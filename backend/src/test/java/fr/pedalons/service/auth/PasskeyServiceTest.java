package fr.pedalons.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.domain.auth.Passkey;
import fr.pedalons.domain.auth.WebAuthnChallenge;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.auth.response.PasskeyDto;
import fr.pedalons.enums.WebAuthnChallengeType;
import fr.pedalons.repository.auth.PasskeyRepository;
import fr.pedalons.repository.auth.WebAuthnChallengeRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PasskeyServiceTest extends AbstractBaseTest {

  @Inject PasskeyService passkeyService;
  @Inject PasskeyRepository passkeyRepository;
  @Inject WebAuthnChallengeRepository challengeRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;
  @Inject PedalonsQueryContext pedalonsContext;

  private Domain domain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createVerifiedUser("passkey@example.com", "Passkey User");
    pedalonsContext.setUserForTest(user);
  }

  // --- generateRegistrationOptions tests ---

  @Test
  void generateRegistrationOptions_shouldReturnValidOptions() {
    Map<String, Object> options = passkeyService.generateRegistrationOptions();

    assertNotNull(options.get("challenge"));
    assertNotNull(options.get("rp"));
    assertNotNull(options.get("user"));
    assertNotNull(options.get("pubKeyCredParams"));
    assertEquals(60000, options.get("timeout"));
    assertEquals("none", options.get("attestation"));

    @SuppressWarnings("unchecked")
    Map<String, Object> rpInfo = (Map<String, Object>) options.get("rp");
    assertNotNull(rpInfo.get("id"));
    assertNotNull(rpInfo.get("name"));

    @SuppressWarnings("unchecked")
    Map<String, Object> userInfo = (Map<String, Object>) options.get("user");
    assertEquals(user.getEmail(), userInfo.get("name"));
    assertEquals(user.getDisplayName(), userInfo.get("displayName"));
  }

  @Test
  void generateRegistrationOptions_shouldCreateChallenge() {
    passkeyService.generateRegistrationOptions();

    Optional<WebAuthnChallenge> challenge =
        challengeRepository.findValidByUserIdAndType(
            user.getId(), WebAuthnChallengeType.REGISTRATION);

    assertTrue(challenge.isPresent());
    assertEquals(WebAuthnChallengeType.REGISTRATION, challenge.get().getChallengeType());
    assertNotNull(challenge.get().getChallenge());
  }

  @Test
  @Transactional
  void generateRegistrationOptions_shouldDeleteOldChallenges() {
    // Create old challenge
    Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
    dataService.createWebAuthnChallenge(
        user, user.getEmail(), "old-challenge", WebAuthnChallengeType.REGISTRATION, expiresAt);

    passkeyService.generateRegistrationOptions();

    // Old challenge should be deleted, only new one exists
    List<WebAuthnChallenge> challenges = challengeRepository.listAll();
    assertEquals(1, challenges.size());
    assertNotEquals("old-challenge", challenges.getFirst().getChallenge());
  }

  @Test
  void generateRegistrationOptions_shouldExcludeExistingCredentials() {
    // Create existing passkey
    dataService.createPasskey(user, "existing-cred".getBytes(), "key".getBytes());

    Map<String, Object> options = passkeyService.generateRegistrationOptions();

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> excludeCredentials =
        (List<Map<String, Object>>) options.get("excludeCredentials");
    assertNotNull(excludeCredentials);
    assertEquals(1, excludeCredentials.size());
    assertEquals("public-key", excludeCredentials.getFirst().get("type"));
  }

  @Test
  void generateRegistrationOptions_shouldNotIncludeExcludeCredentialsIfEmpty() {
    Map<String, Object> options = passkeyService.generateRegistrationOptions();

    assertNull(options.get("excludeCredentials"));
  }

  // --- generateAuthenticationOptions tests ---

  @Test
  void generateAuthenticationOptions_shouldReturnValidOptions() {
    Map<String, Object> options = passkeyService.generateAuthenticationOptions(user.getEmail());

    assertNotNull(options.get("challenge"));
    assertNotNull(options.get("rpId"));
    assertEquals(60000, options.get("timeout"));
    assertEquals("preferred", options.get("userVerification"));
  }

  @Test
  void generateAuthenticationOptions_shouldCreateChallenge() {
    passkeyService.generateAuthenticationOptions(user.getEmail());

    Optional<WebAuthnChallenge> challenge =
        challengeRepository.findValidByEmailAndType(
            user.getEmail(), WebAuthnChallengeType.AUTHENTICATION);

    assertTrue(challenge.isPresent());
    assertEquals(WebAuthnChallengeType.AUTHENTICATION, challenge.get().getChallengeType());
  }

  @Test
  void generateAuthenticationOptions_withNullEmail_shouldWork() {
    Map<String, Object> options = passkeyService.generateAuthenticationOptions(null);

    assertNotNull(options.get("challenge"));
    assertNull(options.get("allowCredentials"));
  }

  @Test
  void generateAuthenticationOptions_shouldIncludeAllowCredentials() {
    dataService.createPasskey(user, "cred-1".getBytes(), "key".getBytes());

    Map<String, Object> options = passkeyService.generateAuthenticationOptions(user.getEmail());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> allowCredentials =
        (List<Map<String, Object>>) options.get("allowCredentials");
    assertNotNull(allowCredentials);
    assertEquals(1, allowCredentials.size());
  }

  @Test
  void generateAuthenticationOptions_shouldNotIncludeAllowCredentialsForUnknownEmail() {
    Map<String, Object> options =
        passkeyService.generateAuthenticationOptions("unknown@example.com");

    assertNull(options.get("allowCredentials"));
  }

  // --- verifyRegistration tests ---

  @Test
  void verifyRegistration_shouldThrowForInvalidChallenge() {
    Map<String, Object> response = Map.of();

    assertThrows(
        BadRequestException.class,
        () -> passkeyService.verifyRegistration(response, "Test Device"));
  }

  @Test
  void verifyRegistration_shouldThrowForExpiredChallenge() {
    dataService.createExpiredWebAuthnChallenge(
        user, user.getEmail(), "expired-challenge", WebAuthnChallengeType.REGISTRATION);
    Map<String, Object> response = Map.of();

    assertThrows(
        BadRequestException.class,
        () -> passkeyService.verifyRegistration(response, "Test Device"));
  }

  @Test
  void verifyRegistration_shouldThrowForWrongChallengeType() {
    // Create authentication challenge instead of registration
    dataService.createWebAuthnChallenge(
        user,
        user.getEmail(),
        "auth-challenge",
        WebAuthnChallengeType.AUTHENTICATION,
        Instant.now().plus(5, ChronoUnit.MINUTES));
    Map<String, Object> response = Map.of();

    assertThrows(
        BadRequestException.class,
        () -> passkeyService.verifyRegistration(response, "Test Device"));
  }

  @Test
  void verifyRegistration_shouldThrowForMalformedResponse() {
    // Create valid challenge
    dataService.createWebAuthnChallenge(
        user,
        user.getEmail(),
        "valid-challenge",
        WebAuthnChallengeType.REGISTRATION,
        Instant.now().plus(5, ChronoUnit.MINUTES));
    // Malformed response missing required fields
    Map<String, Object> response = Map.of("response", Map.of());

    assertThrows(
        BadRequestException.class,
        () -> passkeyService.verifyRegistration(response, "Test Device"));
  }

  @Test
  void verifyRegistration_shouldDeleteChallengeAfterAttempt() {
    dataService.createWebAuthnChallenge(
        user,
        user.getEmail(),
        "one-time-challenge",
        WebAuthnChallengeType.REGISTRATION,
        Instant.now().plus(5, ChronoUnit.MINUTES));
    Map<String, Object> response = Map.of("response", Map.of());

    // First attempt - challenge exists but response is invalid
    assertThrows(
        BadRequestException.class,
        () -> passkeyService.verifyRegistration(response, "Test Device"));

    // Challenge should be deleted, so second attempt fails with "invalid challenge"
    assertThrows(
        BadRequestException.class,
        () -> passkeyService.verifyRegistration(response, "Test Device"));

    // Verify no registration challenges exist
    assertTrue(
        challengeRepository
            .findValidByUserIdAndType(user.getId(), WebAuthnChallengeType.REGISTRATION)
            .isEmpty());
  }

  // --- verifyAuthentication tests ---

  @Test
  void verifyAuthentication_shouldThrowForUnknownCredential() {
    Map<String, Object> response =
        Map.of(
            "id",
            "dW5rbm93bg", // "unknown" in base64
            "response",
            Map.of(
                "clientDataJSON", "e30", // "{}" in base64
                "authenticatorData", "AAAA",
                "signature", "AAAA"));

    assertThrows(NotFoundException.class, () -> passkeyService.verifyAuthentication(response));
  }

  @Test
  void verifyAuthentication_shouldThrowForMalformedResponse() {
    // Create passkey and challenge
    dataService.createPasskey(user, "cred-id".getBytes(), "key".getBytes());
    dataService.createWebAuthnChallenge(
        user,
        user.getEmail(),
        "auth-challenge",
        WebAuthnChallengeType.AUTHENTICATION,
        Instant.now().plus(5, ChronoUnit.MINUTES));

    // Malformed response
    Map<String, Object> response = Map.of("id", "invalid");

    assertThrows(ForbiddenException.class, () -> passkeyService.verifyAuthentication(response));
  }

  @Test
  void verifyAuthentication_shouldThrowForMissingResponseField() {
    Map<String, Object> response = Map.of("id", "dW5rbm93bg");

    assertThrows(ForbiddenException.class, () -> passkeyService.verifyAuthentication(response));
  }

  // --- listPasskeys tests ---

  @Test
  void listPasskeys_shouldReturnUserPasskeys() {
    dataService.createPasskey(user, "cred-1".getBytes(), "key-1".getBytes());
    dataService.createPasskey(user, "cred-2".getBytes(), "key-2".getBytes());

    List<PasskeyDto> passkeys = passkeyService.listPasskeys();

    assertEquals(2, passkeys.size());
  }

  @Test
  void listPasskeys_shouldReturnEmptyForUserWithNoPasskeys() {
    List<PasskeyDto> passkeys = passkeyService.listPasskeys();

    assertTrue(passkeys.isEmpty());
  }

  @Test
  void listPasskeys_shouldIgnoreDeletedPasskeys() {
    dataService.createPasskey(user, "active".getBytes(), "key".getBytes());
    Passkey deletedPasskey =
        dataService.createPasskey(user, "deleted".getBytes(), "key".getBytes());
    dataService.deletePasskey(deletedPasskey);

    List<PasskeyDto> passkeys = passkeyService.listPasskeys();

    assertEquals(1, passkeys.size());
  }

  // --- deletePasskey tests ---

  @Test
  void deletePasskey_shouldSoftDeletePasskey() {
    Passkey passkey = dataService.createPasskey(user, "to-delete".getBytes(), "key".getBytes());

    passkeyService.deletePasskey(passkey.getId());

    assertTrue(passkeyRepository.findByIdAndUserId(passkey.getId(), user.getId()).isEmpty());
  }

  @Test
  void deletePasskey_shouldThrowForNonexistentPasskey() {
    assertThrows(NotFoundException.class, () -> passkeyService.deletePasskey(999999L));
  }

  @Test
  void deletePasskey_shouldThrowForWrongUser() {
    User otherUser = dataService.createVerifiedUser("other@example.com", "Other");
    Passkey passkey = dataService.createPasskey(user, "cred".getBytes(), "key".getBytes());

    pedalonsContext.setUserForTest(otherUser);
    assertThrows(NotFoundException.class, () -> passkeyService.deletePasskey(passkey.getId()));
  }

  @Test
  void deletePasskey_shouldThrowForAlreadyDeletedPasskey() {
    Passkey passkey = dataService.createPasskey(user, "deleted".getBytes(), "key".getBytes());
    dataService.deletePasskey(passkey);

    assertThrows(NotFoundException.class, () -> passkeyService.deletePasskey(passkey.getId()));
  }
}
