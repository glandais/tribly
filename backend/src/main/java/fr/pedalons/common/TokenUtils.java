package fr.pedalons.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for secure token generation and hashing.
 *
 * <p>Used for authentication tokens (refresh tokens, magic links, email verification).
 */
public final class TokenUtils {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private TokenUtils() {
    // Utility class
  }

  /**
   * Generates a cryptographically secure random token encoded as URL-safe Base64.
   *
   * @return a 32-byte random token encoded as URL-safe Base64 without padding
   */
  public static String generateSecureToken() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  /**
   * Generates a 6-digit OTP code for authentication.
   *
   * @return a zero-padded 6-digit string
   */
  public static String generateOtpCode() {
    return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
  }

  /**
   * Hashes a token using SHA-256 for secure storage.
   *
   * @param token the raw token to hash
   * @return the SHA-256 hash encoded as Base64
   */
  public static String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }
}
