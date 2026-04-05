package fr.pedalons.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.domain.auth.AuthToken;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.auth.request.OtpRequest;
import fr.pedalons.dto.auth.request.RegisterRequest;
import fr.pedalons.dto.auth.response.AuthResponse;
import fr.pedalons.dto.auth.response.AuthResult;
import fr.pedalons.enums.AuthTokenType;
import fr.pedalons.repository.auth.AuthSessionRepository;
import fr.pedalons.repository.auth.AuthTokenRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthServiceTest extends AbstractBaseTest {

  @Inject AuthService authService;
  @Inject UserRepository userRepository;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject MockMailbox mailbox;
  @Inject DomainResolver domainResolver;
  @Inject PedalonsQueryContext pedalonsContext;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    mailbox.clear();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
  }

  // --- Register tests ---

  @Test
  void register_shouldCreateVerificationToken() {
    RegisterRequest request = new RegisterRequest("new@example.com", "New User");

    authService.register(request);

    var tokens = authTokenRepository.listAll();
    assertEquals(1, tokens.size());
    assertEquals("new@example.com", tokens.getFirst().getEmail());
    assertEquals(AuthTokenType.EMAIL_VERIFICATION, tokens.getFirst().getTokenType());
    assertEquals("New User", tokens.getFirst().getPendingDisplayName());
  }

  @Test
  void register_shouldSendVerificationEmail() {
    RegisterRequest request = new RegisterRequest("new@example.com", "New User");

    authService.register(request);

    var sent = mailbox.getMailsSentTo("new@example.com");
    assertEquals(1, sent.size());
    assertTrue(sent.getFirst().getSubject().contains("Confirmez"));
  }

  @Test
  void register_shouldThrowIfEmailExists() {
    dataService.createVerifiedUser("existing@example.com", "Existing User");
    RegisterRequest request = new RegisterRequest("existing@example.com", "New User");

    assertThrows(BadRequestException.class, () -> authService.register(request));
  }

  @Test
  @Transactional
  void register_shouldInvalidateExistingTokens() {
    // Create existing token
    Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
    dataService.createAuthToken(
        "test@example.com", "old-hash", AuthTokenType.EMAIL_VERIFICATION, expiresAt);

    RegisterRequest request = new RegisterRequest("test@example.com", "Test User");
    authService.register(request);

    // Old token should be invalidated
    assertTrue(authTokenRepository.findValidByTokenHash("old-hash").isEmpty());
  }

  // --- VerifyEmail tests ---

  @Test
  void verifyEmail_shouldCreateUserAndSession() {
    // Create a token manually with known hash
    createVerificationToken("verify@example.com", "Verified User", "test-token");

    AuthResult result = authService.verifyEmail("test-token", "Test Agent", "127.0.0.1");

    assertNotNull(result.response().accessToken());
    assertEquals("verify@example.com", result.response().user().email());
    assertEquals("Verified User", result.response().user().displayName());

    // User should exist and be verified
    User user =
        userRepository.findByEmailAndDomain(domain.getId(), "verify@example.com").orElseThrow();
    assertTrue(user.isEmailVerified());
  }

  @Test
  void verifyEmail_shouldThrowForInvalidToken() {
    assertThrows(
        BadRequestException.class, () -> authService.verifyEmail("invalid", "Agent", "IP"));
  }

  @Test
  void verifyEmail_shouldThrowForExpiredToken() {
    createExpiredVerificationToken("expired@example.com", "User", "expired-token");

    assertThrows(
        BadRequestException.class, () -> authService.verifyEmail("expired-token", "Agent", "IP"));
  }

  // --- RequestOtp tests ---

  @Test
  void requestOtp_shouldCreateTokenAndSendEmail() {
    dataService.createVerifiedUser("otp@example.com", "OTP User");
    OtpRequest request = new OtpRequest("otp@example.com");

    authService.requestOtp(request);

    var tokens =
        authTokenRepository.findValidByEmailAndType(
            "otp@example.com", AuthTokenType.OTP, domain.getId());
    assertTrue(tokens.isPresent());

    assertEquals(1, mailbox.getMailsSentTo("otp@example.com").size());
  }

  @Test
  void requestOtp_shouldNotThrowForNonexistentUser() {
    OtpRequest request = new OtpRequest("nonexistent@example.com");

    // Should not throw - prevents email enumeration
    assertDoesNotThrow(() -> authService.requestOtp(request));
    assertEquals(0, mailbox.getTotalMessagesSent());
  }

  @Test
  void requestOtp_shouldNotSendForUnverifiedUser() {
    dataService.createUser("unverified@example.com", "Unverified User");
    OtpRequest request = new OtpRequest("unverified@example.com");

    authService.requestOtp(request);

    assertEquals(0, mailbox.getTotalMessagesSent());
  }

  // --- VerifyOtp tests ---

  @Test
  void verifyOtp_shouldReturnAuthResult() {
    User user = dataService.createVerifiedUser("otp@example.com", "OTP User");
    createOtpToken(user, "123456");

    AuthResult result = authService.verifyOtp("otp@example.com", "123456", "Agent", "IP");

    assertNotNull(result.response().accessToken());
    assertEquals("otp@example.com", result.response().user().email());
  }

  @Test
  void verifyOtp_shouldThrowForInvalidCode() {
    User user = dataService.createVerifiedUser("otp@example.com", "OTP User");
    createOtpToken(user, "123456");

    assertThrows(
        BadRequestException.class,
        () -> authService.verifyOtp("otp@example.com", "000000", "Agent", "IP"));
  }

  // --- RefreshToken tests ---

  @Test
  void refreshToken_shouldReturnNewAccessToken() {
    User user = dataService.createVerifiedUser("refresh@example.com", "Refresh User");
    String refreshToken = dataService.createRefreshTokenForUser(user);

    AuthResponse response = authService.refreshToken(refreshToken);

    assertNotNull(response.accessToken());
    assertEquals("refresh@example.com", response.user().email());
  }

  @Test
  void refreshToken_shouldThrowForInvalidToken() {
    assertThrows(ForbiddenException.class, () -> authService.refreshToken("invalid-token"));
  }

  @Test
  void refreshToken_shouldThrowForRevokedSession() {
    User user = dataService.createVerifiedUser("revoked@example.com", "User");
    String refreshToken = dataService.createRefreshTokenForUser(user);
    authService.logout(refreshToken);

    assertThrows(ForbiddenException.class, () -> authService.refreshToken(refreshToken));
  }

  // --- Logout tests ---

  @Test
  void logout_shouldRevokeSession() {
    User user = dataService.createVerifiedUser("logout@example.com", "Logout User");
    String refreshToken = dataService.createRefreshTokenForUser(user);

    authService.logout(refreshToken);

    assertThrows(ForbiddenException.class, () -> authService.refreshToken(refreshToken));
  }

  @Test
  void logout_shouldHandleNullToken() {
    assertDoesNotThrow(() -> authService.logout(null));
  }

  @Test
  void logout_shouldHandleBlankToken() {
    assertDoesNotThrow(() -> authService.logout(""));
    assertDoesNotThrow(() -> authService.logout("   "));
  }

  // --- LogoutAll tests ---

  @Test
  void logoutAll_shouldRevokeAllUserSessions() {
    User user = dataService.createVerifiedUser("logoutall@example.com", "User");
    String token1 = dataService.createRefreshTokenForUser(user);
    String token2 = dataService.createRefreshTokenForUser(user);

    pedalonsContext.setUserForTest(user);
    authService.logoutAll();

    assertThrows(ForbiddenException.class, () -> authService.refreshToken(token1));
    assertThrows(ForbiddenException.class, () -> authService.refreshToken(token2));
  }

  // --- AuthenticateWithPasskey tests ---

  @Test
  void authenticateWithPasskey_shouldThrowForInvalidResponse() {
    // Invalid passkey response (no valid credential)
    var response =
        java.util.Map.of(
            "id",
            "dW5rbm93bg", // "unknown" in base64
            "response",
            java.util.Map.of(
                "clientDataJSON", "e30",
                "authenticatorData", "AAAA",
                "signature", "AAAA"));

    assertThrows(
        NotFoundException.class,
        () -> authService.authenticateWithPasskey(response, "Test Agent", "127.0.0.1"));
  }

  @Test
  void authenticateWithPasskey_shouldThrowForMalformedResponse() {
    var response = java.util.Map.<String, Object>of("invalid", "data");

    assertThrows(
        ForbiddenException.class,
        () -> authService.authenticateWithPasskey(response, "Test Agent", "127.0.0.1"));
  }

  // --- GetUserByEmail tests ---

  @Test
  void getUserByEmail_shouldReturnUser() {
    dataService.createUser("find@example.com", "Find User");

    User user = authService.getUserByEmail("find@example.com");

    assertEquals("find@example.com", user.getEmail());
  }

  @Test
  void getUserByEmail_shouldThrowForNonexistent() {
    assertThrows(NotFoundException.class, () -> authService.getUserByEmail("nonexistent@test.com"));
  }

  // --- Helper methods ---

  @Transactional
  void createVerificationToken(String email, String displayName, String token) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        new AuthToken(
            email,
            tokenHash,
            AuthTokenType.EMAIL_VERIFICATION,
            Instant.now().plus(24, ChronoUnit.HOURS),
            domain.getId());
    authToken.setPendingDisplayName(displayName);
    authToken.setPendingDomainId(domain.getId());
    authTokenRepository.persist(authToken);
  }

  @Transactional
  void createExpiredVerificationToken(String email, String displayName, String token) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        new AuthToken(
            email,
            tokenHash,
            AuthTokenType.EMAIL_VERIFICATION,
            Instant.now().minus(1, ChronoUnit.HOURS),
            domain.getId());
    authToken.setPendingDisplayName(displayName);
    authToken.setPendingDomainId(domain.getId());
    authTokenRepository.persist(authToken);
  }

  @Transactional
  void createOtpToken(User user, String code) {
    String tokenHash = hashToken(code);
    AuthToken authToken =
        new AuthToken(
            user,
            user.getEmail(),
            tokenHash,
            AuthTokenType.OTP,
            Instant.now().plus(5, ChronoUnit.MINUTES),
            domain.getId());
    authTokenRepository.persist(authToken);
  }

  private String hashToken(String token) {
    try {
      java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      return java.util.Base64.getEncoder().encodeToString(hash);
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }
}
