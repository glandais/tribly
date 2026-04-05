package fr.pedalons.service.auth;

import static fr.pedalons.common.TokenUtils.generateOtpCode;
import static fr.pedalons.common.TokenUtils.generateSecureToken;
import static fr.pedalons.common.TokenUtils.hashToken;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.domain.auth.AuthSession;
import fr.pedalons.domain.auth.AuthToken;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.auth.request.OtpRequest;
import fr.pedalons.dto.auth.request.RegisterRequest;
import fr.pedalons.dto.auth.response.AuthResponse;
import fr.pedalons.dto.auth.response.AuthResult;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.enums.AuthTokenType;
import fr.pedalons.repository.auth.AuthSessionRepository;
import fr.pedalons.repository.auth.AuthTokenRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AuthService {

  @Inject UserRepository userRepository;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject JwtService jwtService;
  @Inject AuthEmailService authEmailService;
  @Inject PasskeyService passkeyService;
  @Inject DomainResolver domainResolver;
  @Inject fr.pedalons.repository.platform.DomainRepository domainRepository;
  @Inject PedalonsQueryContext queryContext;

  @ConfigProperty(name = "pedalons.auth.refresh-token.expiry-days", defaultValue = "30")
  int refreshTokenExpiryDays;

  @ConfigProperty(name = "pedalons.auth.otp.expiry-minutes", defaultValue = "5")
  int otpExpiryMinutes;

  @ConfigProperty(name = "pedalons.auth.otp.max-attempts", defaultValue = "3")
  int otpMaxAttempts;

  @ConfigProperty(name = "pedalons.auth.otp.rate-limit-window-minutes", defaultValue = "5")
  int otpRateLimitWindowMinutes;

  @ConfigProperty(name = "pedalons.auth.email-verification.expiry-hours", defaultValue = "24")
  int emailVerificationExpiryHours;

  @Transactional
  @Public
  public void register(RegisterRequest request) {
    Domain domain = domainResolver.getDomain();

    // Check if email already exists in this domain
    if (userRepository.findByEmailAndDomain(domain.getId(), request.email()).isPresent()) {
      throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    // Invalidate any existing verification tokens for this email
    authTokenRepository.invalidateByEmailAndType(
        request.email(), AuthTokenType.EMAIL_VERIFICATION, domain.getId());

    // Generate verification token
    String token = generateSecureToken();
    String tokenHash = hashToken(token);

    // Create pending registration token with domain info
    AuthToken authToken =
        new AuthToken(
            request.email(),
            tokenHash,
            AuthTokenType.EMAIL_VERIFICATION,
            Instant.now().plus(Duration.ofHours(emailVerificationExpiryHours)),
            domain.getId());
    authToken.setPendingDisplayName(request.displayName());
    authToken.setPendingDomainId(domain.getId());
    authTokenRepository.persist(authToken);

    // Send verification email
    authEmailService.sendVerificationEmail(request.email(), request.displayName(), token);
  }

  @Transactional
  @Public
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

    // Get the domain from the token
    Long domainId =
        Objects.requireNonNull(
            authToken.getPendingDomainId(), "Pending domain ID should not be null");
    Domain domain =
        domainRepository
            .findByIdOptional(domainId)
            .orElseThrow(() -> new BadRequestException(ErrorCode.DOMAIN_NOT_FOUND));

    // Create user from pending data
    String displayName =
        Objects.requireNonNull(
            authToken.getPendingDisplayName(), "Pending display name should not be null");
    User user = new User(domain, authToken.getEmail(), displayName);
    user.markEmailVerified();
    user.recordLogin();
    userRepository.persist(user);

    return createAuthResult(user, userAgent, ipAddress);
  }

  @Transactional
  @Public
  public void requestOtp(OtpRequest request) {
    Domain domain = domainResolver.getDomain();
    User user = userRepository.findByEmailAndDomain(domain.getId(), request.email()).orElse(null);

    // Always respond success to prevent email enumeration
    if (user == null || !user.isEmailVerified()) {
      return;
    }

    // Rate limiting: check if too many OTP requests in the window
    long recentCount =
        authTokenRepository.countRecentByEmailAndType(
            request.email(), AuthTokenType.OTP, otpRateLimitWindowMinutes, domain.getId());
    if (recentCount >= otpMaxAttempts) {
      // Silent return to prevent enumeration - rate limited
      return;
    }

    // Invalidate any existing OTPs for this email
    authTokenRepository.invalidateByEmailAndType(
        request.email(), AuthTokenType.OTP, domain.getId());

    // Generate 6-digit OTP code
    String otpCode = generateOtpCode();
    String tokenHash = hashToken(otpCode);

    AuthToken authToken =
        new AuthToken(
            user,
            request.email(),
            tokenHash,
            AuthTokenType.OTP,
            Instant.now().plus(Duration.ofMinutes(otpExpiryMinutes)),
            domain.getId());
    authTokenRepository.persist(authToken);

    // Send OTP email
    authEmailService.sendOtpEmail(request.email(), otpCode);
  }

  @Transactional
  @Public
  public AuthResult verifyOtp(String email, String code, String userAgent, String ipAddress) {
    Long domainId = domainResolver.getDomainId();
    String tokenHash = hashToken(code);
    AuthToken authToken =
        authTokenRepository
            .findValidByEmailAndType(email, AuthTokenType.OTP, domainId)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    // Verify the code matches
    if (!authToken.getTokenHash().equals(tokenHash)) {
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
  @Public
  public AuthResult authenticateWithPasskey(
      Map<String, Object> response, String userAgent, String ipAddress) {
    User user = passkeyService.verifyAuthentication(response);
    user.recordLogin();
    return createAuthResult(user, userAgent, ipAddress);
  }

  @Transactional
  @Public
  public AuthResponse refreshToken(String refreshToken) {
    Domain domain = domainResolver.getDomain();
    String tokenHash = hashToken(refreshToken);
    AuthSession session =
        authSessionRepository
            .findByRefreshTokenHash(tokenHash)
            .orElseThrow(ForbiddenException::new);

    if (!session.isValid()) {
      throw new BadRequestException(ErrorCode.SESSION_EXPIRED);
    }

    User user = session.getUser();

    // Validate that the user belongs to the current domain
    if (!user.getDomain().getId().equals(domain.getId())) {
      throw new ForbiddenException();
    }

    // Update session usage
    session.markUsed();

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
  @Public
  public void logout(@Nullable String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }

    String tokenHash = hashToken(refreshToken);
    authSessionRepository.findByRefreshTokenHash(tokenHash).ifPresent(AuthSession::revoke);
  }

  @Transactional
  @Logged
  public void logoutAll() {
    Long userId = queryContext.getUserId();
    authSessionRepository.revokeAllByUserId(userId);
  }

  public User getUserByEmail(String email) {
    Domain domain = domainResolver.getDomain();
    return userRepository
        .findByEmailAndDomain(domain.getId(), email)
        .orElseThrow(NotFoundException::new);
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
            .refreshToken(refreshToken)
            .build();

    return new AuthResult(response, refreshToken);
  }
}
