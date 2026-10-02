package fr.pedalons.infrastructure.gps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

/** OAuth 1.0a signing, against a published vector (docs/LEDGER_*.md API-62). */
class OAuth1SignerTest {

  // Twitter's « Creating a signature » example: request parameters, oauth_* parameters and secrets.
  private static final String URL = "https://api.twitter.com/1.1/statuses/update.json";
  private static final String CONSUMER_KEY = "xvz1evFS4wEEPTGEFPHBog";
  private static final String CONSUMER_SECRET = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
  private static final String TOKEN = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb";
  private static final String TOKEN_SECRET = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";
  private static final String NONCE = "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg";
  private static final long TIMESTAMP = 1318622958L;
  private static final Map<String, String> REQUEST_PARAMS =
      Map.of(
          "include_entities", "true",
          "status", "Hello Ladies + Gentlemen, a signed OAuth request!");

  @Test
  void signsThePublishedExample() {
    String signature =
        OAuth1Signer.signature(
            "POST",
            URL,
            Map.of(
                "include_entities",
                "true",
                "status",
                "Hello Ladies + Gentlemen, a signed OAuth request!",
                "oauth_consumer_key",
                CONSUMER_KEY,
                "oauth_nonce",
                NONCE,
                "oauth_signature_method",
                "HMAC-SHA1",
                "oauth_timestamp",
                Long.toString(TIMESTAMP),
                "oauth_token",
                TOKEN,
                "oauth_version",
                "1.0"),
            CONSUMER_SECRET,
            TOKEN_SECRET);

    assertEquals("hCtSmYh+iHYCEqBWrE7C7hYmtUk=", signature);
  }

  @Test
  void headerCarriesTheProtocolParametersAndTheSignature() {
    String header =
        OAuth1Signer.authorizationHeader(
            "POST",
            URL,
            CONSUMER_KEY,
            CONSUMER_SECRET,
            TOKEN,
            TOKEN_SECRET,
            Map.of(),
            REQUEST_PARAMS,
            NONCE,
            TIMESTAMP);

    assertTrue(header.startsWith("OAuth "));
    assertTrue(header.contains("oauth_consumer_key=\"" + CONSUMER_KEY + "\""));
    assertTrue(header.contains("oauth_token=\"" + TOKEN + "\""));
    assertTrue(header.contains("oauth_signature_method=\"HMAC-SHA1\""));
    // The signature is percent-encoded in the header: + and = are not left raw.
    assertTrue(header.contains("oauth_signature=\"hCtSmYh%2BiHYCEqBWrE7C7hYmtUk%3D\""));
    // Request parameters are signed, not sent in the header.
    assertFalse(header.contains("include_entities"));
  }

  @Test
  void requestTokenLegHasNoTokenAndSignsTheCallback() {
    String header =
        OAuth1Signer.authorizationHeader(
            "POST",
            "https://connectapi.garmin.com/oauth-service/oauth/request_token",
            "key",
            "secret",
            null,
            null,
            Map.of("oauth_callback", "https://example.com/api/gps/callback/garmin"),
            Map.of(),
            "nonce",
            1L);

    assertFalse(header.contains("oauth_token="));
    assertTrue(
        header.contains(
            "oauth_callback=\"https%3A%2F%2Fexample.com%2Fapi%2Fgps%2Fcallback%2Fgarmin\""));
  }

  @Test
  void encodesPerRfc3986() {
    assertEquals("a%20b%2A~-._%2B", OAuth1Signer.encode("a b*~-._+"));
  }

  @Test
  void parsesTokenResponses() {
    Map<String, String> fields =
        GarminOAuth1Client.parseForm(
            "oauth_token=abc&oauth_token_secret=d%2Fe&oauth_callback_confirmed=true\n");

    assertEquals("abc", fields.get("oauth_token"));
    assertEquals("d/e", fields.get("oauth_token_secret"));
  }
}
