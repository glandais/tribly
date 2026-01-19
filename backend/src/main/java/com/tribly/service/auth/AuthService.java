package com.tribly.service.auth;

import static com.tribly.common.TokenUtils.generateSecureToken;
import static com.tribly.common.TokenUtils.hashToken;

import com.tribly.common.exception.BadRequestException;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.common.exception.NotFoundException;
import com.tribly.domain.auth.AuthSession;
import com.tribly.domain.auth.AuthToken;
import com.tribly.domain.platform.Domain;
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
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.Logged;
import com.tribly.service.security.annotation.Public;
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
  @Inject com.tribly.repository.platform.DomainRepository domainRepository;
  @Inject TriblyQueryContext queryContext;

  @ConfigProperty(name = "tribly.auth.refresh-token.expiry-days", defaultValue = "30")
  int refreshTokenExpiryDays;

  @ConfigProperty(name = "tribly.auth.magic-link.expiry-minutes", defaultValue = "15")
  int magicLinkExpiryMinutes;

  @ConfigProperty(name = "tribly.auth.email-verification.expiry-hours", defaultValue = "24")
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
    authTokenRepository.invalidateByEmailAndType(request.email(), AuthTokenType.EMAIL_VERIFICATION);

    // Generate verification token
    String token = generateSecureToken();
    String tokenHash = hashToken(token);

    // Create pending registration token with domain info
    AuthToken authToken =
        new AuthToken(
            request.email(),
            tokenHash,
            AuthTokenType.EMAIL_VERIFICATION,
            Instant.now().plus(Duration.ofHours(emailVerificationExpiryHours)));
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
  public void requestMagicLink(MagicLinkRequest request) {
    Domain domain = domainResolver.getDomain();
    User user = userRepository.findByEmailAndDomain(domain.getId(), request.email()).orElse(null);

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
  @Public
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
            .build();

    return new AuthResult(response, refreshToken);
  }
}
