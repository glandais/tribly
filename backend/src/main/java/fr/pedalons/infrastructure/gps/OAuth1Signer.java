package fr.pedalons.infrastructure.gps;

import fr.pedalons.common.exception.InternalException;
import fr.pedalons.dto.error.ErrorCode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.jspecify.annotations.Nullable;

/**
 * Signs requests with OAuth 1.0a, HMAC-SHA1 (RFC 5849), the way Garmin's legacy programme expects:
 * every protocol parameter travels in the {@code Authorization} header. Only parameters in that
 * header or passed as {@code requestParams} enter the signature — a JSON body never does.
 */
public final class OAuth1Signer {

  private static final SecureRandom RANDOM = new SecureRandom();

  private OAuth1Signer() {}

  /**
   * The {@code Authorization} header for one request.
   *
   * @param method HTTP method
   * @param url request URL, without query string
   * @param consumerKey the application's consumer key
   * @param consumerSecret the application's consumer secret
   * @param token the request or access token, null when asking for a request token
   * @param tokenSecret that token's secret
   * @param extraOAuthParams other protocol parameters ({@code oauth_callback}, {@code
   *     oauth_verifier})
   */
  public static String authorizationHeader(
      String method,
      String url,
      String consumerKey,
      String consumerSecret,
      @Nullable String token,
      @Nullable String tokenSecret,
      Map<String, String> extraOAuthParams) {
    byte[] nonce = new byte[16];
    RANDOM.nextBytes(nonce);
    return authorizationHeader(
        method,
        url,
        consumerKey,
        consumerSecret,
        token,
        tokenSecret,
        extraOAuthParams,
        Map.of(),
        HexFormat.of().formatHex(nonce),
        Instant.now().getEpochSecond());
  }

  /** Same, with the nonce, timestamp and signed request parameters fixed: for tests. */
  static String authorizationHeader(
      String method,
      String url,
      String consumerKey,
      String consumerSecret,
      @Nullable String token,
      @Nullable String tokenSecret,
      Map<String, String> extraOAuthParams,
      Map<String, String> requestParams,
      String nonce,
      long timestamp) {
    Map<String, String> oauthParams = new LinkedHashMap<>();
    oauthParams.put("oauth_consumer_key", consumerKey);
    oauthParams.put("oauth_nonce", nonce);
    oauthParams.put("oauth_signature_method", "HMAC-SHA1");
    oauthParams.put("oauth_timestamp", Long.toString(timestamp));
    if (token != null) {
      oauthParams.put("oauth_token", token);
    }
    oauthParams.put("oauth_version", "1.0");
    oauthParams.putAll(extraOAuthParams);

    Map<String, String> signed = new LinkedHashMap<>(requestParams);
    signed.putAll(oauthParams);
    oauthParams.put("oauth_signature", signature(method, url, signed, consumerSecret, tokenSecret));

    return "OAuth "
        + oauthParams.entrySet().stream()
            .map(e -> encode(e.getKey()) + "=\"" + encode(e.getValue()) + "\"")
            .collect(Collectors.joining(", "));
  }

  static String signature(
      String method,
      String url,
      Map<String, String> params,
      String consumerSecret,
      @Nullable String tokenSecret) {
    // Parameters sorted by encoded name (names are unique here, so values never need comparing).
    TreeMap<String, String> encoded = new TreeMap<>();
    params.forEach((k, v) -> encoded.put(encode(k), encode(v)));
    String normalized =
        encoded.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
    String baseString = method.toUpperCase() + "&" + encode(url) + "&" + encode(normalized);
    String key = encode(consumerSecret) + "&" + (tokenSecret == null ? "" : encode(tokenSecret));
    try {
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
      return Base64.getEncoder()
          .encodeToString(mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8)));
    } catch (GeneralSecurityException e) {
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    }
  }

  /** RFC 3986 percent-encoding, which OAuth 1.0a requires (not form encoding). */
  static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8)
        .replace("+", "%20")
        .replace("*", "%2A")
        .replace("%7E", "~");
  }
}
