package fr.pedalons.repository.auth;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.auth.AuthToken;
import fr.pedalons.enums.AuthTokenType;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthTokenRepositoryTest extends AbstractBaseTest {

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
    dataService.createAuthToken("test@example.com", "magic-hash", AuthTokenType.OTP, expiresAt);

    Optional<AuthToken> result =
        authTokenRepository.findValidByEmailAndType("test@example.com", AuthTokenType.OTP);

    assertTrue(result.isPresent());
    assertEquals(AuthTokenType.OTP, result.get().getTokenType());
  }

  @Test
  void findValidByEmailAndType_shouldReturnEmptyForDifferentType() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken("test@example.com", "magic-hash", AuthTokenType.OTP, expiresAt);

    Optional<AuthToken> result =
        authTokenRepository.findValidByEmailAndType(
            "test@example.com", AuthTokenType.EMAIL_VERIFICATION);

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByEmailAndType_shouldIgnoreExpiredTokens() {
    dataService.createExpiredAuthToken("test@example.com", "expired-hash", AuthTokenType.OTP);

    Optional<AuthToken> result =
        authTokenRepository.findValidByEmailAndType("test@example.com", AuthTokenType.OTP);

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void invalidateByEmailAndType_shouldMarkTokensAsUsed() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken("test@example.com", "token-1", AuthTokenType.OTP, expiresAt);
    dataService.createAuthToken("test@example.com", "token-2", AuthTokenType.OTP, expiresAt);

    int invalidatedCount =
        authTokenRepository.invalidateByEmailAndType("test@example.com", AuthTokenType.OTP);

    assertEquals(2, invalidatedCount);
    assertTrue(
        authTokenRepository
            .findValidByEmailAndType("test@example.com", AuthTokenType.OTP)
            .isEmpty());
  }

  @Test
  @Transactional
  void invalidateByEmailAndType_shouldNotAffectOtherTypes() {
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken("test@example.com", "magic-hash", AuthTokenType.OTP, expiresAt);
    dataService.createAuthToken(
        "test@example.com", "verify-hash", AuthTokenType.EMAIL_VERIFICATION, expiresAt);

    authTokenRepository.invalidateByEmailAndType("test@example.com", AuthTokenType.OTP);

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
    dataService.createExpiredAuthToken("test@example.com", "expired-hash", AuthTokenType.OTP);
    AuthToken usedToken =
        dataService.createAuthToken("test@example.com", "used-hash", AuthTokenType.OTP, expiresAt);
    dataService.markAuthTokenUsed(usedToken);

    long deletedCount = authTokenRepository.deleteExpiredTokens();

    assertEquals(2, deletedCount);
    assertTrue(authTokenRepository.findValidByTokenHash("valid-hash").isPresent());
  }
}
