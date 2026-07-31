package fr.pedalons.service.security;

import fr.pedalons.domain.user.User;
import fr.pedalons.dto.tiles.response.TileTokenDto;
import fr.pedalons.service.security.annotation.Logged;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

/**
 * Mints and verifies the short-lived token that authorizes a vector tile request.
 *
 * <p>It exists for one reason: a map renderer fetches tiles through its own HTTP stack, so it
 * cannot carry an {@code Authorization} header. The browser gets away with it because the session
 * cookie rides along on same-origin requests; MapLibre on mobile has no cookie and no way to add a
 * header (the Dart binding exposes neither {@code transformRequest} nor custom headers), which
 * leaves the query string as the only channel.
 *
 * <h2>Why an HMAC and not a JWT</h2>
 *
 * <p>{@link PedalonsQueryContext#doInit()} resolves the caller as soon as the principal is a {@code
 * JsonWebToken}, without looking at scopes. A "tile-scoped" JWT would therefore identify its bearer
 * on every {@code @PermitAll} endpoint — the exact opposite of what this token is for. A private
 * scheme verified by one caller only ({@link TileTokenFilter}) is structurally unable to
 * authenticate anywhere else.
 *
 * <h2>Why not an opaque token in the database</h2>
 *
 * <p>Tiles arrive in bursts of ten to forty per camera move, on a path that already runs an {@code
 * ST_AsMVT}. One SELECT per tile is one too many. Revocation, the only real advantage of a stored
 * token, is bought here with a short TTL instead.
 *
 * <h2>Key separation</h2>
 *
 * <p>The signing key is <em>derived</em> from {@code pedalons.encryption.key}, never the key
 * itself: compromising one does not hand over the other, and bumping {@link #KDF_INFO} invalidates
 * every outstanding token at once. The {@link #PURPOSE} prefix goes into the MAC without travelling
 * in the URL, so a token minted for some future use — signed image URLs, say — can never be
 * presented as a tile token even under the same key.
 */
@ApplicationScoped
public class TileTokenService {

  /** Domain separation. Part of the MAC input, never part of the token. */
  private static final byte[] PURPOSE = "tile:v1".getBytes(StandardCharsets.UTF_8);

  /** Key-derivation label. Changing it invalidates every outstanding token. */
  private static final byte[] KDF_INFO = "pedalons/tile-token/v1".getBytes(StandardCharsets.UTF_8);

  private static final String HMAC_ALGORITHM = "HmacSHA256";

  /** userId (8) + domainId (8) + expiry epoch-seconds (8). */
  private static final int PAYLOAD_BYTES = 24;

  /**
   * MAC truncated to 128 bits — RFC 2104 §5 allows down to half the output, and forging a signature
   * that protects fifteen minutes of map tiles is not a realistic threat. The length matters
   * because the token sits in a URL, which is both MapLibre's cache key and one proxy log line per
   * tile.
   */
  private static final int MAC_BYTES = 16;

  private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

  @ConfigProperty(name = "pedalons.encryption.key", defaultValue = "")
  String masterKeyBase64;

  @ConfigProperty(name = "pedalons.tiles.token.ttl-seconds")
  int ttlSeconds;

  @Inject PedalonsQueryContext pedalonsQueryContext;

  private SecretKeySpec tileKey;

  @PostConstruct
  void init() {
    if (masterKeyBase64.isEmpty()) {
      throw new IllegalStateException("Encryption key not configured (pedalons.encryption.key)");
    }
    byte[] masterKey = Base64.getDecoder().decode(masterKeyBase64);
    if (masterKey.length != 32) {
      throw new IllegalStateException("Encryption key must be 256 bits (32 bytes) base64 encoded");
    }
    byte[] derived = hmac(new SecretKeySpec(masterKey, HMAC_ALGORITHM), KDF_INFO);
    this.tileKey = new SecretKeySpec(derived, HMAC_ALGORITHM);
  }

  /**
   * Mints a token for the caller of the current request.
   *
   * <p>Lives here rather than in the resource because the API layer orchestrates nothing: it may
   * not reach a domain entity, and every service method it calls must carry one of the security
   * annotations. {@code @Logged} is the right one — the token is bound to a user, so there has to
   * be one, and there is no entity to check access against.
   */
  @Logged
  public TileTokenDto mintForCurrentUser() {
    User user = pedalonsQueryContext.getUser();
    TileToken token = issue(user.getId(), user.getDomain().getId(), Instant.now());
    return new TileTokenDto(token.value(), token.expiresAt(), token.expiresInSeconds());
  }

  /**
   * Mints a token authorizing {@code userId} to read tiles on {@code domainId}.
   *
   * <p>Deterministic within a second: two calls in the same second yield the same string. That is
   * wanted, not incidental — the token is part of the tile URL, so a token that changed on every
   * call would evict MapLibre's whole tile cache each time a map asked for one.
   */
  public TileToken issue(long userId, long domainId, Instant now) {
    Instant expiresAt =
        now.plusSeconds(ttlSeconds).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    byte[] payload =
        ByteBuffer.allocate(PAYLOAD_BYTES)
            .putLong(userId)
            .putLong(domainId)
            .putLong(expiresAt.getEpochSecond())
            .array();
    byte[] mac = sign(payload);
    return new TileToken(
        ENCODER.encodeToString(payload) + "." + ENCODER.encodeToString(mac), expiresAt, ttlSeconds);
  }

  /**
   * Verifies a token, or yields empty.
   *
   * <p>Never throws. This reads a public query parameter, so it receives garbage as a matter of
   * course, and a malformed value must produce a 401 rather than a 500.
   */
  public Optional<TileTokenClaims> verify(@Nullable String token, Instant now) {
    if (token == null || token.isBlank()) {
      return Optional.empty();
    }
    try {
      int separator = token.indexOf('.');
      if (separator < 0) {
        return Optional.empty();
      }
      byte[] payload = DECODER.decode(token.substring(0, separator));
      byte[] mac = DECODER.decode(token.substring(separator + 1));
      if (payload.length != PAYLOAD_BYTES || mac.length != MAC_BYTES) {
        return Optional.empty();
      }
      // Constant-time: a byte-by-byte comparison leaks the expected MAC through response timing.
      if (!MessageDigest.isEqual(sign(payload), mac)) {
        return Optional.empty();
      }
      ByteBuffer buffer = ByteBuffer.wrap(payload);
      long userId = buffer.getLong();
      long domainId = buffer.getLong();
      Instant expiresAt = Instant.ofEpochSecond(buffer.getLong());
      if (!expiresAt.isAfter(now)) {
        return Optional.empty();
      }
      return Optional.of(new TileTokenClaims(userId, domainId, expiresAt));
    } catch (RuntimeException e) {
      return Optional.empty();
    }
  }

  private byte[] sign(byte[] payload) {
    byte[] input = new byte[PURPOSE.length + payload.length];
    System.arraycopy(PURPOSE, 0, input, 0, PURPOSE.length);
    System.arraycopy(payload, 0, input, PURPOSE.length, payload.length);
    return Arrays.copyOf(hmac(tileKey, input), MAC_BYTES);
  }

  private static byte[] hmac(SecretKeySpec key, byte[] input) {
    try {
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      mac.init(key);
      return mac.doFinal(input);
    } catch (java.security.GeneralSecurityException e) {
      throw new IllegalStateException("HMAC-SHA256 unavailable", e);
    }
  }
}
