package com.tribly.infrastructure.security;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * AES-256-GCM encryption service for securing OAuth tokens at rest.
 */
@ApplicationScoped
public class TokenEncryptionService {

  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final int GCM_TAG_LENGTH = 128;
  private static final int GCM_IV_LENGTH = 12;

  @ConfigProperty(name = "tribly.encryption.key", defaultValue = "")
  String encryptionKeyBase64;

  private SecretKey secretKey;

  @PostConstruct
  void init() {
    if (encryptionKeyBase64.isEmpty()) {
      throw new IllegalStateException("Encryption key not configured (tribly.encryption.key)");
    }
    byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);
    if (keyBytes.length != 32) {
      throw new IllegalStateException("Encryption key must be 256 bits (32 bytes) base64 encoded");
    }
    this.secretKey = new SecretKeySpec(keyBytes, "AES");
  }

  /**
   * Encrypts a token string using AES-256-GCM.
   * The returned bytes contain: IV (12 bytes) + ciphertext + auth tag.
   */
  public byte[] encrypt(String plaintext) {
    try {
      byte[] iv = new byte[GCM_IV_LENGTH];
      new SecureRandom().nextBytes(iv);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

      byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

      // Prepend IV to ciphertext
      byte[] result = new byte[iv.length + ciphertext.length];
      System.arraycopy(iv, 0, result, 0, iv.length);
      System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);

      return result;
    } catch (Exception e) {
      throw new RuntimeException("Failed to encrypt token", e);
    }
  }

  /**
   * Decrypts bytes encrypted by this service.
   * Expects: IV (12 bytes) + ciphertext + auth tag.
   */
  public String decrypt(byte[] encrypted) {
    try {
      // Extract IV from beginning
      byte[] iv = new byte[GCM_IV_LENGTH];
      System.arraycopy(encrypted, 0, iv, 0, iv.length);

      // Extract ciphertext
      byte[] ciphertext = new byte[encrypted.length - iv.length];
      System.arraycopy(encrypted, iv.length, ciphertext, 0, ciphertext.length);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

      byte[] plaintext = cipher.doFinal(ciphertext);
      return new String(plaintext, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decrypt token", e);
    }
  }
}
