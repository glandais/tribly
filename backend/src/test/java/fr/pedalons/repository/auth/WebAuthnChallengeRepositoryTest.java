package fr.pedalons.repository.auth;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.auth.WebAuthnChallenge;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.WebAuthnChallengeType;
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
class WebAuthnChallengeRepositoryTest extends AbstractBaseTest {

  @Inject WebAuthnChallengeRepository webAuthnChallengeRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Test
  void findValidByChallenge_shouldReturnValidChallenge() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        user, "test@example.com", "challenge-123", WebAuthnChallengeType.REGISTRATION, expiresAt);

    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByChallenge("challenge-123");

    assertTrue(result.isPresent());
    assertEquals("challenge-123", result.get().getChallenge());
  }

  @Test
  void findValidByChallenge_shouldReturnEmptyForNonexistent() {
    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByChallenge("nonexistent");

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByChallenge_shouldIgnoreExpiredChallenges() {
    dataService.createExpiredWebAuthnChallenge(
        user, "test@example.com", "expired-challenge", WebAuthnChallengeType.REGISTRATION);

    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByChallenge("expired-challenge");

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByUserIdAndType_shouldReturnValidChallenge() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        user, "test@example.com", "reg-challenge", WebAuthnChallengeType.REGISTRATION, expiresAt);

    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByUserIdAndType(
            user.getId(), WebAuthnChallengeType.REGISTRATION);

    assertTrue(result.isPresent());
    assertEquals(WebAuthnChallengeType.REGISTRATION, result.get().getChallengeType());
  }

  @Test
  void findValidByUserIdAndType_shouldReturnEmptyForDifferentType() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        user, "test@example.com", "reg-challenge", WebAuthnChallengeType.REGISTRATION, expiresAt);

    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByUserIdAndType(
            user.getId(), WebAuthnChallengeType.AUTHENTICATION);

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByUserIdAndType_shouldIgnoreExpiredChallenges() {
    dataService.createExpiredWebAuthnChallenge(
        user, "test@example.com", "expired", WebAuthnChallengeType.REGISTRATION);

    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByUserIdAndType(
            user.getId(), WebAuthnChallengeType.REGISTRATION);

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByEmailAndType_shouldReturnValidChallenge() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        null,
        "test@example.com",
        "auth-challenge",
        WebAuthnChallengeType.AUTHENTICATION,
        expiresAt);

    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByEmailAndType(
            "test@example.com", WebAuthnChallengeType.AUTHENTICATION);

    assertTrue(result.isPresent());
    assertEquals("test@example.com", result.get().getEmail());
  }

  @Test
  void findValidByEmailAndType_shouldReturnEmptyForDifferentEmail() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        null, "test@example.com", "challenge", WebAuthnChallengeType.AUTHENTICATION, expiresAt);

    Optional<WebAuthnChallenge> result =
        webAuthnChallengeRepository.findValidByEmailAndType(
            "other@example.com", WebAuthnChallengeType.AUTHENTICATION);

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void deleteExpiredChallenges_shouldDeleteExpiredChallenges() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        user, "test@example.com", "valid", WebAuthnChallengeType.REGISTRATION, expiresAt);
    dataService.createExpiredWebAuthnChallenge(
        user, "test@example.com", "expired-1", WebAuthnChallengeType.REGISTRATION);
    dataService.createExpiredWebAuthnChallenge(
        null, "other@example.com", "expired-2", WebAuthnChallengeType.AUTHENTICATION);

    long deletedCount = webAuthnChallengeRepository.deleteExpiredChallenges();

    assertEquals(2, deletedCount);
    assertTrue(webAuthnChallengeRepository.findValidByChallenge("valid").isPresent());
  }

  @Test
  @Transactional
  void deleteByUserId_shouldDeleteUserChallenges() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    User otherUser = dataService.createUser("other@example.com", "Other User");
    dataService.createWebAuthnChallenge(
        user, "test@example.com", "user-challenge", WebAuthnChallengeType.REGISTRATION, expiresAt);
    dataService.createWebAuthnChallenge(
        otherUser,
        "other@example.com",
        "other-challenge",
        WebAuthnChallengeType.REGISTRATION,
        expiresAt);

    long deletedCount = webAuthnChallengeRepository.deleteByUserId(user.getId());

    assertEquals(1, deletedCount);
    assertTrue(webAuthnChallengeRepository.findValidByChallenge("user-challenge").isEmpty());
    assertTrue(webAuthnChallengeRepository.findValidByChallenge("other-challenge").isPresent());
  }

  @Test
  @Transactional
  void deleteByEmail_shouldDeleteEmailChallenges() {
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        null,
        "test@example.com",
        "test-challenge",
        WebAuthnChallengeType.AUTHENTICATION,
        expiresAt);
    dataService.createWebAuthnChallenge(
        null,
        "other@example.com",
        "other-challenge",
        WebAuthnChallengeType.AUTHENTICATION,
        expiresAt);

    long deletedCount = webAuthnChallengeRepository.deleteByEmail("test@example.com");

    assertEquals(1, deletedCount);
    assertTrue(webAuthnChallengeRepository.findValidByChallenge("test-challenge").isEmpty());
    assertTrue(webAuthnChallengeRepository.findValidByChallenge("other-challenge").isPresent());
  }
}
