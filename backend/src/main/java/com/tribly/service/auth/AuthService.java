package com.tribly.service.auth;

import com.tribly.common.exception.BadRequestException;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.common.exception.NotFoundException;
import com.tribly.domain.auth.AuthSession;
import com.tribly.domain.auth.AuthToken;
import com.tribly.domain.user.User;
import com.tribly.dto.auth.request.MagicLinkRequest;
import com.tribly.dto.auth.request.RegisterRequest;
import com.tribly.dto.auth.response.AuthResponse;
import com.tribly.dto.auth.response.AuthResult;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.users.response.UserDto;
import com.tribly.enums.AuthTokenType;
import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.repository.auth.AuthTokenRepository;
import com.tribly.repository.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AuthService {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  @Inject UserRepository userRepository;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject JwtService jwtService;
  @Inject AuthEmailService authEmailService;
  @Inject PasskeyService passkeyService;

  @ConfigProperty(name = "tribly.auth.refresh-token.expiry-days", defaultValue = "30")
  int refreshTokenExpiryDays;

  @ConfigProperty(name = "tribly.auth.magic-link.expiry-minutes", defaultValue = "15")
  int magicLinkExpiryMinutes;

  @ConfigProperty(name = "tribly.auth.email-verification.expiry-hours", defaultValue = "24")
  int emailVerificationExpiryHours;

  @Transactional
  public void register(RegisterRequest request) {
    // Check if email already exists
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    // Invalidate any existing verification tokens for this email
    authTokenRepository.invalidateByEmailAndType(request.email(), AuthTokenType.EMAIL_VERIFICATION);

    // Generate verification token
    String token = generateSecureToken();
    String tokenHash = hashToken(token);

    // Create pending registration token
    AuthToken authToken =
        new AuthToken(
            request.email(),
            tokenHash,
            AuthTokenType.EMAIL_VERIFICATION,
            Instant.now().plus(Duration.ofHours(emailVerificationExpiryHours)));
    authToken.setPendingDisplayName(request.displayName());
    authTokenRepository.persist(authToken);

    // Send verification email
    authEmailService.sendVerificationEmail(request.email(), request.displayName(), token);
  }

  @Transactional
  public AuthResult verifyEmail(String token, String userAgent, String ipAddress) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        authTokenRepository
            .findValidByTokenHash(tokenHash)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    if (authToken.getTokenType() != AuthTokenType.EMAIL_VERIFICATION) {
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    authToken.markUsed();

    // Create user from pending data
    String displayName =
        Objects.requireNonNull(
            authToken.getPendingDisplayName(), "Pending display name should not be null");
    User user = new User(authToken.getEmail(), displayName);
    user.markEmailVerified();
    user.recordLogin();
    userRepository.persist(user);

    return createAuthResult(user, userAgent, ipAddress);
  }

  @Transactional
  public void requestMagicLink(MagicLinkRequest request) {
    User user = userRepository.findByEmail(request.email()).orElse(null);

    // Always respond success to prevent email enumeration
    if (user == null || !user.isEmailVerified()) {
      return;
    }

    // Invalidate any existing magic links for this email
    authTokenRepository.invalidateByEmailAndType(request.email(), AuthTokenType.MAGIC_LINK);

    // Generate magic link token
    String token = generateSecureToken();
    String tokenHash = hashToken(token);

    AuthToken authToken =
        new AuthToken(
            user,
            request.email(),
            tokenHash,
            AuthTokenType.MAGIC_LINK,
            Instant.now().plus(Duration.ofMinutes(magicLinkExpiryMinutes)));
    authTokenRepository.persist(authToken);

    // Send magic link email
    authEmailService.sendMagicLinkEmail(request.email(), token);
  }

  @Transactional
  public AuthResult verifyMagicLink(String token, String userAgent, String ipAddress) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        authTokenRepository
            .findValidByTokenHash(tokenHash)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    if (authToken.getTokenType() != AuthTokenType.MAGIC_LINK) {
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    authToken.markUsed();

    User user = authToken.getUser();
    if (user == null) {
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    user.recordLogin();

    return createAuthResult(user, userAgent, ipAddress);
  }

  @Transactional
  public AuthResult authenticateWithPasskey(
      Map<String, Object> response, String userAgent, String ipAddress) {
    User user = passkeyService.verifyAuthentication(response);
    user.recordLogin();
    return createAuthResult(user, userAgent, ipAddress);
  }

  @Transactional
  public AuthResponse refreshToken(String refreshToken) {
    String tokenHash = hashToken(refreshToken);
    AuthSession session =
        authSessionRepository
            .findByRefreshTokenHash(tokenHash)
            .orElseThrow(ForbiddenException::new);

    if (!session.isValid()) {
      throw new BadRequestException(ErrorCode.SESSION_EXPIRED);
    }

    // Update session usage
    session.markUsed();

    User user = session.getUser();
    user.recordLogin();

    // Generate new access token
    String accessToken = jwtService.generateAccessToken(user);

    return AuthResponse.builder()
        .accessToken(accessToken)
        .expiresIn(jwtService.getAccessTokenExpirySeconds())
        .user(UserDto.from(user))
        .build();
  }

  @Transactional
  public void logout(@Nullable String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }

    String tokenHash = hashToken(refreshToken);
    authSessionRepository.findByRefreshTokenHash(tokenHash).ifPresent(AuthSession::revoke);
  }

  @Transactional
  public void logoutAll(Long userId) {
    authSessionRepository.revokeAllByUserId(userId);
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
  }

  /**
   * Creates an auth result for the given user, including access token, refresh token, and session.
   */
  public AuthResult createAuthResult(User user, String userAgent, String ipAddress) {
    // Generate tokens
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = generateSecureToken();
    String refreshTokenHash = hashToken(refreshToken);

    // Create session
    AuthSession session =
        new AuthSession(
            user, refreshTokenHash, Instant.now().plus(Duration.ofDays(refreshTokenExpiryDays)));
    session.setUserAgent(userAgent);
    session.setIpAddress(ipAddress);
    authSessionRepository.persist(session);

    AuthResponse response =
        AuthResponse.builder()
            .accessToken(accessToken)
            .expiresIn(jwtService.getAccessTokenExpirySeconds())
            .user(UserDto.from(user))
            .build();

    return new AuthResult(response, refreshToken);
  }

  private String generateSecureToken() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String hashToken(String token) {
    // Use SHA-256 for token hashing (fast, secure for tokens)
    try {
      java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }
}
