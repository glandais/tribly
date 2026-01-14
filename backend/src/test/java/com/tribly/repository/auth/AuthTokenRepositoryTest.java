package com.tribly.repository.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.auth.AuthToken;
import com.tribly.enums.AuthTokenType;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthTokenRepositoryTest {

  @Inject AuthTokenRepository authTokenRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  @Test
  void findValidByTokenHash_shouldReturnValidToken() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken(
        "test@example.com", "token-hash-123", AuthTokenType.EMAIL_VERIFICATION, expiresAt);

    Optional<AuthToken> result = authTokenRepository.findValidByTokenHash("token-hash-123");

    assertTrue(result.isPresent());
    assertEquals("token-hash-123", result.get().getTokenHash());
    assertEquals("test@example.com", result.get().getEmail());
  }

  @Test
  void findValidByTokenHash_shouldReturnEmptyForNonexistent() {
    Optional<AuthToken> result = authTokenRepository.findValidByTokenHash("nonexistent");

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByTokenHash_shouldIgnoreExpiredTokens() {
    dataService.createExpiredAuthToken(
        "test@example.com", "expired-hash", AuthTokenType.EMAIL_VERIFICATION);

    Optional<AuthToken> result = authTokenRepository.findValidByTokenHash("expired-hash");

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByTokenHash_shouldIgnoreUsedTokens() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    AuthToken token =
        dataService.createAuthToken(
            "test@example.com", "used-hash", AuthTokenType.EMAIL_VERIFICATION, expiresAt);
    dataService.markAuthTokenUsed(token);

    Optional<AuthToken> result = authTokenRepository.findValidByTokenHash("used-hash");

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByEmailAndType_shouldReturnValidToken() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken(
        "test@example.com", "magic-hash", AuthTokenType.MAGIC_LINK, expiresAt);

    Optional<AuthToken> result =
        authTokenRepository.findValidByEmailAndType("test@example.com", AuthTokenType.MAGIC_LINK);

    assertTrue(result.isPresent());
    assertEquals(AuthTokenType.MAGIC_LINK, result.get().getTokenType());
  }

  @Test
  void findValidByEmailAndType_shouldReturnEmptyForDifferentType() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken(
        "test@example.com", "magic-hash", AuthTokenType.MAGIC_LINK, expiresAt);

    Optional<AuthToken> result =
        authTokenRepository.findValidByEmailAndType(
            "test@example.com", AuthTokenType.EMAIL_VERIFICATION);

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByEmailAndType_shouldIgnoreExpiredTokens() {
    dataService.createExpiredAuthToken(
        "test@example.com", "expired-hash", AuthTokenType.MAGIC_LINK);

    Optional<AuthToken> result =
        authTokenRepository.findValidByEmailAndType("test@example.com", AuthTokenType.MAGIC_LINK);

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void invalidateByEmailAndType_shouldMarkTokensAsUsed() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken("test@example.com", "token-1", AuthTokenType.MAGIC_LINK, expiresAt);
    dataService.createAuthToken("test@example.com", "token-2", AuthTokenType.MAGIC_LINK, expiresAt);

    int invalidatedCount =
        authTokenRepository.invalidateByEmailAndType("test@example.com", AuthTokenType.MAGIC_LINK);

    assertEquals(2, invalidatedCount);
    assertTrue(
        authTokenRepository
            .findValidByEmailAndType("test@example.com", AuthTokenType.MAGIC_LINK)
            .isEmpty());
  }

  @Test
  @Transactional
  void invalidateByEmailAndType_shouldNotAffectOtherTypes() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken(
        "test@example.com", "magic-hash", AuthTokenType.MAGIC_LINK, expiresAt);
    dataService.createAuthToken(
        "test@example.com", "verify-hash", AuthTokenType.EMAIL_VERIFICATION, expiresAt);

    authTokenRepository.invalidateByEmailAndType("test@example.com", AuthTokenType.MAGIC_LINK);

    assertTrue(
        authTokenRepository
            .findValidByEmailAndType("test@example.com", AuthTokenType.EMAIL_VERIFICATION)
            .isPresent());
  }

  @Test
  @Transactional
  void deleteExpiredTokens_shouldDeleteExpiredAndUsedTokens() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken(
        "test@example.com", "valid-hash", AuthTokenType.EMAIL_VERIFICATION, expiresAt);
    dataService.createExpiredAuthToken(
        "test@example.com", "expired-hash", AuthTokenType.MAGIC_LINK);
    AuthToken usedToken =
        dataService.createAuthToken(
            "test@example.com", "used-hash", AuthTokenType.MAGIC_LINK, expiresAt);
    dataService.markAuthTokenUsed(usedToken);

    long deletedCount = authTokenRepository.deleteExpiredTokens();

    assertEquals(2, deletedCount);
    assertTrue(authTokenRepository.findValidByTokenHash("valid-hash").isPresent());
  }
}
