package fr.pedalons.repository.auth;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.auth.AuthSession;
import fr.pedalons.domain.user.User;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthSessionRepositoryTest extends AbstractBaseTest {

  @Inject AuthSessionRepository authSessionRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Test
  void findByRefreshTokenHash_shouldReturnActiveSession() {
    Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    dataService.createAuthSession(user, "token-hash-123", expiresAt);

    Optional<AuthSession> result = authSessionRepository.findByRefreshTokenHash("token-hash-123");

    assertTrue(result.isPresent());
    assertEquals("token-hash-123", result.get().getRefreshTokenHash());
    assertEquals(user.getId(), result.get().getUser().getId());
  }

  @Test
  void findByRefreshTokenHash_shouldReturnEmptyForNonexistent() {
    Optional<AuthSession> result = authSessionRepository.findByRefreshTokenHash("nonexistent");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByRefreshTokenHash_shouldIgnoreRevokedSessions() {
    Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    AuthSession session = dataService.createAuthSession(user, "revoked-hash", expiresAt);
    dataService.revokeAuthSession(session);

    Optional<AuthSession> result = authSessionRepository.findByRefreshTokenHash("revoked-hash");

    assertTrue(result.isEmpty());
  }

  @Test
  void findActiveByUserId_shouldReturnActiveSessions() {
    Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    dataService.createAuthSession(user, "session-1", expiresAt);
    dataService.createAuthSession(user, "session-2", expiresAt);

    List<AuthSession> results = authSessionRepository.findActiveByUserId(user.getId());

    assertEquals(2, results.size());
  }

  @Test
  void findActiveByUserId_shouldIgnoreRevokedSessions() {
    Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    dataService.createAuthSession(user, "active-session", expiresAt);
    AuthSession revokedSession = dataService.createAuthSession(user, "revoked-session", expiresAt);
    dataService.revokeAuthSession(revokedSession);

    List<AuthSession> results = authSessionRepository.findActiveByUserId(user.getId());

    assertEquals(1, results.size());
    assertEquals("active-session", results.get(0).getRefreshTokenHash());
  }

  @Test
  void findActiveByUserId_shouldReturnEmptyForUserWithNoSessions() {
    User otherUser = dataService.createUser("other@example.com", "Other User");

    List<AuthSession> results = authSessionRepository.findActiveByUserId(otherUser.getId());

    assertTrue(results.isEmpty());
  }

  @Test
  @Transactional
  void revokeAllByUserId_shouldRevokeAllUserSessions() {
    Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    dataService.createAuthSession(user, "session-1", expiresAt);
    dataService.createAuthSession(user, "session-2", expiresAt);

    int revokedCount = authSessionRepository.revokeAllByUserId(user.getId());

    assertEquals(2, revokedCount);
    assertTrue(authSessionRepository.findActiveByUserId(user.getId()).isEmpty());
  }

  @Test
  @Transactional
  void revokeAllByUserId_shouldNotAffectOtherUsers() {
    User otherUser = dataService.createUser("other@example.com", "Other User");
    Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    dataService.createAuthSession(user, "user-session", expiresAt);
    dataService.createAuthSession(otherUser, "other-session", expiresAt);

    authSessionRepository.revokeAllByUserId(user.getId());

    assertTrue(authSessionRepository.findActiveByUserId(user.getId()).isEmpty());
    assertEquals(1, authSessionRepository.findActiveByUserId(otherUser.getId()).size());
  }

  @Test
  @Transactional
  void deleteExpiredSessions_shouldDeleteExpiredAndRevokedSessions() {
    Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    dataService.createAuthSession(user, "active-session", expiresAt);
    dataService.createExpiredAuthSession(user, "expired-session");
    AuthSession revokedSession = dataService.createAuthSession(user, "revoked-session", expiresAt);
    dataService.revokeAuthSession(revokedSession);

    long deletedCount = authSessionRepository.deleteExpiredSessions();

    assertEquals(2, deletedCount);
    assertEquals(1, authSessionRepository.findActiveByUserId(user.getId()).size());
  }
}
