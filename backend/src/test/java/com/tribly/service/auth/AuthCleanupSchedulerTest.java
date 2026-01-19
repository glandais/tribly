package com.tribly.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.auth.AuthSession;
import com.tribly.domain.auth.AuthToken;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.user.User;
import com.tribly.enums.AuthTokenType;
import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.repository.auth.AuthTokenRepository;
import com.tribly.service.security.DomainResolver;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthCleanupSchedulerTest {

  @Inject AuthCleanupScheduler authCleanupScheduler;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createVerifiedUser("cleanup@example.com", "Cleanup User");
  }

  @Nested
  class CleanupExpiredAuthData {

    @Test
    void shouldDeleteExpiredSessions() {
      // Create expired session
      dataService.createExpiredAuthSession(user, "expired-hash");
      // Create valid session
      Instant futureExpiry = Instant.now().plus(7, ChronoUnit.DAYS);
      dataService.createAuthSession(user, "valid-hash", futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(1, authSessionRepository.count());
    }

    @Test
    void shouldDeleteRevokedSessions() {
      // Create and revoke a session
      Instant futureExpiry = Instant.now().plus(7, ChronoUnit.DAYS);
      AuthSession session = dataService.createAuthSession(user, "revoked-hash", futureExpiry);
      dataService.revokeAuthSession(session);
      // Create valid session
      dataService.createAuthSession(user, "valid-hash", futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(1, authSessionRepository.count());
    }

    @Test
    void shouldDeleteExpiredTokens() {
      // Create expired token
      dataService.createExpiredAuthToken(
          "expired@example.com", "expired-hash", AuthTokenType.MAGIC_LINK);
      // Create valid token
      Instant futureExpiry = Instant.now().plus(24, ChronoUnit.HOURS);
      dataService.createAuthToken(
          "valid@example.com", "valid-hash", AuthTokenType.MAGIC_LINK, futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(1, authTokenRepository.count());
    }

    @Test
    void shouldDeleteUsedTokens() {
      // Create and use a token
      Instant futureExpiry = Instant.now().plus(24, ChronoUnit.HOURS);
      AuthToken usedToken =
          dataService.createAuthToken(
              "used@example.com", "used-hash", AuthTokenType.MAGIC_LINK, futureExpiry);
      dataService.markAuthTokenUsed(usedToken);
      // Create valid token
      dataService.createAuthToken(
          "valid@example.com", "valid-hash", AuthTokenType.MAGIC_LINK, futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(1, authTokenRepository.count());
    }

    @Test
    void shouldHandleNoDataToClean() {
      // No sessions or tokens
      assertDoesNotThrow(() -> authCleanupScheduler.cleanupExpiredAuthData());
    }

    @Test
    void shouldCleanBothSessionsAndTokens() {
      // Create expired session
      dataService.createExpiredAuthSession(user, "expired-session");
      // Create expired token
      dataService.createExpiredAuthToken(
          "expired@example.com", "expired-token", AuthTokenType.EMAIL_VERIFICATION);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(0, authSessionRepository.count());
      assertEquals(0, authTokenRepository.count());
    }

    @Test
    void shouldNotDeleteValidSessions() {
      Instant futureExpiry = Instant.now().plus(7, ChronoUnit.DAYS);
      dataService.createAuthSession(user, "valid-1", futureExpiry);
      dataService.createAuthSession(user, "valid-2", futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(2, authSessionRepository.count());
    }

    @Test
    void shouldNotDeleteValidTokens() {
      Instant futureExpiry = Instant.now().plus(24, ChronoUnit.HOURS);
      dataService.createAuthToken(
          "user1@example.com", "hash-1", AuthTokenType.MAGIC_LINK, futureExpiry);
      dataService.createAuthToken(
          "user2@example.com", "hash-2", AuthTokenType.EMAIL_VERIFICATION, futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(2, authTokenRepository.count());
    }
  }
}
