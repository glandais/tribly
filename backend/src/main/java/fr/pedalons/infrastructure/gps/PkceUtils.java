package fr.pedalons.infrastructure.gps;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for PKCE (Proof Key for Code Exchange) OAuth 2.0 extension.
 */
public final class PkceUtils {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private PkceUtils() {}

  /**
   * Generate a random code verifier for PKCE.
   * Length is 43-128 characters using unreserved URI characters.
   *
   * @return Random code verifier string
   */
  public static String generateCodeVerifier() {
    byte[] randomBytes = new byte[64];
    SECURE_RANDOM.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  /**
   * Generate a code challenge from a code verifier using SHA-256.
   *
   * @param codeVerifier The code verifier
   * @return Base64url-encoded SHA-256 hash of the verifier
   */
  public static String generateCodeChallenge(String codeVerifier) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}
