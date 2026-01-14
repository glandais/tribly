package com.tribly.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.common.exception.BadRequestException;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.common.exception.NotFoundException;
import com.tribly.domain.auth.AuthToken;
import com.tribly.domain.user.User;
import com.tribly.dto.auth.request.MagicLinkRequest;
import com.tribly.dto.auth.request.RegisterRequest;
import com.tribly.dto.auth.response.AuthResponse;
import com.tribly.dto.auth.response.AuthResult;
import com.tribly.enums.AuthTokenType;
import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.repository.auth.AuthTokenRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthServiceTest {

  @Inject AuthService authService;
  @Inject UserRepository userRepository;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject MockMailbox mailbox;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    mailbox.clear();
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
    assertTrue(sent.getFirst().getSubject().contains("Verify"));
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
    User user = userRepository.findByEmail("verify@example.com").orElseThrow();
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

  // --- RequestMagicLink tests ---

  @Test
  void requestMagicLink_shouldCreateTokenAndSendEmail() {
    dataService.createVerifiedUser("magic@example.com", "Magic User");
    MagicLinkRequest request = new MagicLinkRequest("magic@example.com");

    authService.requestMagicLink(request);

    var tokens =
        authTokenRepository.findValidByEmailAndType("magic@example.com", AuthTokenType.MAGIC_LINK);
    assertTrue(tokens.isPresent());

    assertEquals(1, mailbox.getMailsSentTo("magic@example.com").size());
  }

  @Test
  void requestMagicLink_shouldNotThrowForNonexistentUser() {
    MagicLinkRequest request = new MagicLinkRequest("nonexistent@example.com");

    // Should not throw - prevents email enumeration
    assertDoesNotThrow(() -> authService.requestMagicLink(request));
    assertEquals(0, mailbox.getTotalMessagesSent());
  }

  @Test
  void requestMagicLink_shouldNotSendForUnverifiedUser() {
    dataService.createUser("unverified@example.com", "Unverified User");
    MagicLinkRequest request = new MagicLinkRequest("unverified@example.com");

    authService.requestMagicLink(request);

    assertEquals(0, mailbox.getTotalMessagesSent());
  }

  // --- VerifyMagicLink tests ---

  @Test
  void verifyMagicLink_shouldReturnAuthResult() {
    User user = dataService.createVerifiedUser("magic@example.com", "Magic User");
    createMagicLinkToken(user, "magic-token");

    AuthResult result = authService.verifyMagicLink("magic-token", "Agent", "IP");

    assertNotNull(result.response().accessToken());
    assertEquals("magic@example.com", result.response().user().email());
  }

  @Test
  void verifyMagicLink_shouldThrowForInvalidToken() {
    assertThrows(
        BadRequestException.class, () -> authService.verifyMagicLink("invalid", "Agent", "IP"));
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

    authService.logoutAll(user.getId());

    assertThrows(ForbiddenException.class, () -> authService.refreshToken(token1));
    assertThrows(ForbiddenException.class, () -> authService.refreshToken(token2));
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
            Instant.now().plus(24, ChronoUnit.HOURS));
    authToken.setPendingDisplayName(displayName);
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
            Instant.now().minus(1, ChronoUnit.HOURS));
    authToken.setPendingDisplayName(displayName);
    authTokenRepository.persist(authToken);
  }

  @Transactional
  void createMagicLinkToken(User user, String token) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        new AuthToken(
            user,
            user.getEmail(),
            tokenHash,
            AuthTokenType.MAGIC_LINK,
            Instant.now().plus(15, ChronoUnit.MINUTES));
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
