package fr.pedalons.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.auth.AuthSession;
import fr.pedalons.domain.auth.AuthToken;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AuthTokenType;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.repository.auth.AuthSessionRepository;
import fr.pedalons.repository.auth.AuthTokenRepository;
import fr.pedalons.repository.gps.GpsOAuthStateRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthCleanupSchedulerTest extends AbstractBaseTest {

  @Inject AuthCleanupScheduler authCleanupScheduler;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject GpsOAuthStateRepository gpsOAuthStateRepository;
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
      dataService.createExpiredAuthToken("expired@example.com", "expired-hash", AuthTokenType.OTP);
      // Create valid token
      Instant futureExpiry = Instant.now().plus(24, ChronoUnit.HOURS);
      dataService.createAuthToken(
          "valid@example.com", "valid-hash", AuthTokenType.OTP, futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(1, authTokenRepository.count());
    }

    @Test
    void shouldDeleteUsedTokens() {
      // Create and use a token
      Instant futureExpiry = Instant.now().plus(24, ChronoUnit.HOURS);
      AuthToken usedToken =
          dataService.createAuthToken(
              "used@example.com", "used-hash", AuthTokenType.OTP, futureExpiry);
      dataService.markAuthTokenUsed(usedToken);
      // Create valid token
      dataService.createAuthToken(
          "valid@example.com", "valid-hash", AuthTokenType.OTP, futureExpiry);

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
      dataService.createAuthToken("user1@example.com", "hash-1", AuthTokenType.OTP, futureExpiry);
      dataService.createAuthToken(
          "user2@example.com", "hash-2", AuthTokenType.EMAIL_VERIFICATION, futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(2, authTokenRepository.count());
    }
  }

  @Nested
  class CleanupExpiredGpsOAuthStates {

    @Test
    void shouldDeleteExpiredGpsOAuthStates() {
      Instant futureExpiry = Instant.now().plus(10, ChronoUnit.MINUTES);
      dataService.createExpiredGpsOAuthState(user, "expired-state", GpsServiceType.HAMMERHEAD);
      dataService.createGpsOAuthState(user, "valid-state", GpsServiceType.HAMMERHEAD, futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(1, gpsOAuthStateRepository.count());
    }

    @Test
    void shouldNotDeleteValidGpsOAuthStates() {
      Instant futureExpiry = Instant.now().plus(10, ChronoUnit.MINUTES);
      dataService.createGpsOAuthState(user, "state-1", GpsServiceType.HAMMERHEAD, futureExpiry);
      dataService.createGpsOAuthState(user, "state-2", GpsServiceType.GARMIN, futureExpiry);

      authCleanupScheduler.cleanupExpiredAuthData();

      assertEquals(2, gpsOAuthStateRepository.count());
    }
  }
}
