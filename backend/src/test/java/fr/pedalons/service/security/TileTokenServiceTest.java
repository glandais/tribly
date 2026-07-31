package fr.pedalons.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TileTokenServiceTest {

  private static final long USER_ID = 123456789L;
  private static final long DOMAIN_ID = 987654321L;

  @Inject TileTokenService tileTokenService;

  @Test
  void issueThenVerify_shouldYieldTheSameClaims() {
    Instant now = Instant.parse("2026-07-31T10:00:00Z");

    TileToken token = tileTokenService.issue(USER_ID, DOMAIN_ID, now);
    Optional<TileTokenClaims> claims = tileTokenService.verify(token.value(), now);

    assertTrue(claims.isPresent());
    assertEquals(USER_ID, claims.get().userId());
    assertEquals(DOMAIN_ID, claims.get().domainId());
    assertEquals(token.expiresAt(), claims.get().expiresAt());
  }

  @Test
  void issue_shouldExpireAfterTheConfiguredTtl() {
    Instant now = Instant.parse("2026-07-31T10:00:00Z");

    TileToken token = tileTokenService.issue(USER_ID, DOMAIN_ID, now);

    assertEquals(now.plusSeconds(token.expiresInSeconds()), token.expiresAt());
  }

  /**
   * Deterministic within a second, on purpose: the token is part of the tile URL, which is
   * MapLibre's cache key. A token that changed on every call would evict the whole tile cache each
   * time a map asked for one.
   */
  @Test
  void issue_twiceInTheSameSecond_shouldYieldTheSameToken() {
    Instant now = Instant.parse("2026-07-31T10:00:00Z");

    assertEquals(
        tileTokenService.issue(USER_ID, DOMAIN_ID, now).value(),
        tileTokenService.issue(USER_ID, DOMAIN_ID, now).value());
  }

  @Test
  void issue_forDifferentUsers_shouldYieldDifferentTokens() {
    Instant now = Instant.parse("2026-07-31T10:00:00Z");

    assertFalse(
        tileTokenService
            .issue(USER_ID, DOMAIN_ID, now)
            .value()
            .equals(tileTokenService.issue(USER_ID + 1, DOMAIN_ID, now).value()));
  }

  @Test
  void verify_expiredToken_shouldYieldEmpty() {
    Instant issuedAt = Instant.parse("2026-07-31T10:00:00Z");
    TileToken token = tileTokenService.issue(USER_ID, DOMAIN_ID, issuedAt);

    Instant afterExpiry = token.expiresAt().plusSeconds(1);

    assertTrue(tileTokenService.verify(token.value(), afterExpiry).isEmpty());
  }

  @Test
  void verify_atTheExactExpiryInstant_shouldYieldEmpty() {
    Instant issuedAt = Instant.parse("2026-07-31T10:00:00Z");
    TileToken token = tileTokenService.issue(USER_ID, DOMAIN_ID, issuedAt);

    assertTrue(tileTokenService.verify(token.value(), token.expiresAt()).isEmpty());
  }

  @Test
  void verify_tamperedPayload_shouldYieldEmpty() {
    Instant now = Instant.parse("2026-07-31T10:00:00Z");
    String token = tileTokenService.issue(USER_ID, DOMAIN_ID, now).value();

    int dot = token.indexOf('.');
    String tampered = flipOneCharacter(token.substring(0, dot)) + token.substring(dot);

    assertTrue(tileTokenService.verify(tampered, now).isEmpty());
  }

  @Test
  void verify_tamperedMac_shouldYieldEmpty() {
    Instant now = Instant.parse("2026-07-31T10:00:00Z");
    String token = tileTokenService.issue(USER_ID, DOMAIN_ID, now).value();

    int dot = token.indexOf('.');
    String tampered = token.substring(0, dot + 1) + flipOneCharacter(token.substring(dot + 1));

    assertTrue(tileTokenService.verify(tampered, now).isEmpty());
  }

  /**
   * This reads a public query parameter, so it receives garbage as a matter of course. Every one of
   * these must yield empty rather than throw — a malformed token has to produce a 401, never a 500.
   */
  @Test
  void verify_garbage_shouldYieldEmptyAndNeverThrow() {
    Instant now = Instant.parse("2026-07-31T10:00:00Z");

    String[] garbage = {
      "",
      "   ",
      "not-a-token",
      ".",
      "..",
      "a.b",
      "!!!.???",
      "AAAA.BBBB",
      // Well-formed base64url, wrong lengths on both halves.
      "AAAAAAAA.AAAAAAAA",
      "%%%.%%%",
      "abc.def.ghi"
    };

    for (String value : garbage) {
      assertTrue(
          tileTokenService.verify(value, now).isEmpty(), "should have rejected '" + value + "'");
    }
    assertTrue(tileTokenService.verify(null, now).isEmpty());
  }

  /**
   * Flips the first character to a different one, keeping the string base64url-decodable.
   *
   * <p>The <em>first</em>, deliberately: base64url pads to a whole number of 6-bit characters, so
   * the last character of either half carries bits that decoding throws away. Tampering there
   * changes the string without changing the bytes, which would make these tests pass while testing
   * nothing.
   */
  private static String flipOneCharacter(String value) {
    char first = value.charAt(0);
    char replacement = first == 'A' ? 'B' : 'A';
    return replacement + value.substring(1);
  }
}
