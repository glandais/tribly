package fr.pedalons.service.auth;

import static fr.pedalons.common.TokenUtils.generateOtpCode;
import static fr.pedalons.common.TokenUtils.generateSecureToken;
import static fr.pedalons.common.TokenUtils.hashToken;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.common.exception.InternalException;
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
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

  /**
   * Wrong codes a login OTP survives. With {@code otp.max-attempts} codes per window, a guesser gets
   * 3 × 5 tries out of a million every 5 minutes instead of an unbounded run.
   */
  @ConfigProperty(name = "pedalons.auth.otp.max-verify-attempts", defaultValue = "5")
  int otpMaxVerifyAttempts;

  @ConfigProperty(name = "pedalons.auth.email-verification.expiry-hours", defaultValue = "24")
  int emailVerificationExpiryHours;

  @ConfigProperty(name = "pedalons.auth.password-reset.expiry-hours", defaultValue = "1")
  int passwordResetExpiryHours;

  /**
   * Not {@code @Transactional}, like the three other methods that e-mail a token: the token is
   * committed first and the mail sent after. Sent inside the transaction, a slow SMTP relay held it
   * open until the reaper aborted it, and the visitor got a 500 for a failure that was not theirs.
   * A delivery failure still reaches the caller here — a sign-up whose mail never left must say so,
   * and trying again invalidates the orphaned token.
   */
  @Public
  public void register(RegisterRequest request) {
    String token =
        QuarkusTransaction.joiningExisting().call(() -> createPendingRegistration(request));
    sendVerification(request.email(), request.displayName(), token);
  }

  /**
   * The verification mail of a sign-up or an address change, whose failure the visitor must hear
   * about — named, not as a bare 500: nothing was created (the account only exists once the address
   * is verified), and trying again works, the new token replacing the orphaned one.
   */
  private void sendVerification(String email, String displayName, String token) {
    try {
      authEmailService.sendVerificationEmail(email, displayName, token);
    } catch (RuntimeException e) {
      throw new InternalException(ErrorCode.EMAIL_NOT_SENT, e);
    }
  }

  private String createPendingRegistration(RegisterRequest request) {
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
    authToken.setPendingPasswordHash(BcryptUtil.bcryptHash(request.password()));
    authToken.setPendingDomainId(domain.getId());
    // Bean Validation refuses a request without acceptTerms: reaching here means they were
    // accepted.
    authToken.setPendingTermsAcceptedAt(authToken.getCreatedAt());
    authTokenRepository.persist(authToken);
    return token;
  }

  @Transactional
  @Public
  public AuthResult verifyEmail(String token, String userAgent, String ipAddress) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        authTokenRepository
            .findValidByTokenHash(tokenHash)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    // Email-change (account recovery): verifies a new real email on an existing user.
    if (authToken.getTokenType() == AuthTokenType.EMAIL_CHANGE) {
      return verifyEmailChange(authToken, userAgent, ipAddress);
    }

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
    user.setPasswordHash(authToken.getPendingPasswordHash());
    user.markEmailVerified();
    user.recordLogin();
    // Accepted on the sign-up form, recorded on the token by register. A token issued before the
    // form asked carries none, and the account records none.
    user.setTermsAcceptedAt(authToken.getPendingTermsAcceptedAt());
    userRepository.persist(user);

    return createAuthResult(user, userAgent, ipAddress);
  }

  /**
   * Starts collecting a real email for the current user (e.g. a migrated account with a
   * placeholder address). Sends a verification link to the new address; the change is only applied
   * once that link is followed. Rejects an address already used by another account in the domain.
   *
   * <p>The mail leaves after the commit, and its failure reaches the caller — see {@link
   * #register}.
   */
  @Logged
  public void requestEmailChange(String newEmail) {
    String normalized = newEmail.toLowerCase(java.util.Locale.ROOT).trim();
    EmailChange change =
        QuarkusTransaction.joiningExisting().call(() -> createEmailChangeToken(normalized));
    if (change != null) {
      sendVerification(normalized, change.displayName(), change.token());
    }
  }

  private record EmailChange(String displayName, String token) {}

  /** Null when the rate limit withholds the mail. */
  private @Nullable EmailChange createEmailChangeToken(String normalized) {
    User user = queryContext.getUser();
    Long domainId = user.getDomain().getId();

    // Collision: reject if the address belongs to a different account (decision: no auto-merge).
    userRepository
        .findByEmailAndDomain(domainId, normalized)
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(user.getId())) {
                throw new fr.pedalons.common.exception.ConflictException(
                    ErrorCode.EMAIL_ALREADY_EXISTS);
              }
            });

    // Rate limit (mirrors OTP/password-reset). Silent to avoid probing which emails are taken.
    long recentCount =
        authTokenRepository.countRecentByEmailAndType(
            normalized, AuthTokenType.EMAIL_CHANGE, otpRateLimitWindowMinutes, domainId);
    if (recentCount >= otpMaxAttempts) {
      Log.warnf("Email-change rate limit exceeded for user=%d domain=%d", user.getId(), domainId);
      return null;
    }

    authTokenRepository.invalidateByEmailAndType(normalized, AuthTokenType.EMAIL_CHANGE, domainId);

    String token = generateSecureToken();
    AuthToken authToken =
        new AuthToken(
            user,
            normalized,
            hashToken(token),
            AuthTokenType.EMAIL_CHANGE,
            Instant.now().plus(Duration.ofHours(emailVerificationExpiryHours)),
            domainId);
    authTokenRepository.persist(authToken);
    return new EmailChange(user.getDisplayName(), token);
  }

  private AuthResult verifyEmailChange(AuthToken authToken, String userAgent, String ipAddress) {
    authToken.markUsed();

    User user = authToken.getUser();
    if (user == null) {
      throw new BadRequestException(ErrorCode.USER_NOT_FOUND);
    }

    Long domainId = user.getDomain().getId();
    String newEmail = authToken.getEmail();

    // Re-check collision at verify time (another account may have claimed it meanwhile).
    userRepository
        .findByEmailAndDomain(domainId, newEmail)
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(user.getId())) {
                throw new fr.pedalons.common.exception.ConflictException(
                    ErrorCode.EMAIL_ALREADY_EXISTS);
              }
            });

    user.setEmail(newEmail);
    user.markEmailVerified();
    user.recordLogin();
    userRepository.persist(user);

    return createAuthResult(user, userAgent, ipAddress);
  }

  /**
   * The code is committed before the mail leaves, and a delivery failure is only logged: the answer
   * is the same whether or not the address has an account (anti-enumeration), and the visitor can
   * ask for another code. Sent inside the transaction, a slow SMTP relay would let the reaper abort
   * it — a 500 despite the catch, and no code at all.
   */
  @Public
  public void requestOtp(OtpRequest request) {
    String otpCode = QuarkusTransaction.joiningExisting().call(() -> createOtp(request));
    if (otpCode == null) {
      return;
    }
    try {
      authEmailService.sendOtpEmail(request.email(), otpCode);
    } catch (Exception e) {
      Log.errorf(
          e,
          "Failed to send OTP email to email=%s domain=%d",
          request.email(),
          domainResolver.getDomain().getId());
    }
  }

  /** Null when no code is issued: unknown or unverified address, or rate limit. */
  private @Nullable String createOtp(OtpRequest request) {
    Domain domain = domainResolver.getDomain();
    User user = userRepository.findByEmailAndDomain(domain.getId(), request.email()).orElse(null);

    // Always respond success to prevent email enumeration
    if (user == null || !user.isEmailVerified()) {
      return null;
    }

    // Rate limiting: check if too many OTP requests in the window
    long recentCount =
        authTokenRepository.countRecentByEmailAndType(
            request.email(), AuthTokenType.OTP, otpRateLimitWindowMinutes, domain.getId());
    if (recentCount >= otpMaxAttempts) {
      Log.warnf("OTP rate limit exceeded for email=%s domain=%d", request.email(), domain.getId());
      return null;
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
    return otpCode;
  }

  // A wrong code throws, and the default rollback would erase the failed attempt it just counted.
  @Transactional(dontRollbackOn = BadRequestException.class)
  @Public
  public AuthResult verifyOtp(String email, String code, String userAgent, String ipAddress) {
    Long domainId = domainResolver.getDomainId();
    String tokenHash = hashToken(code);
    AuthToken authToken =
        authTokenRepository
            .findValidByEmailAndType(email, AuthTokenType.OTP, domainId)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    // Verify the code matches (constant-time comparison to prevent timing attacks)
    if (!MessageDigest.isEqual(
        authToken.getTokenHash().getBytes(StandardCharsets.UTF_8),
        tokenHash.getBytes(StandardCharsets.UTF_8))) {
      authToken.recordFailedAttempt(otpMaxVerifyAttempts);
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    authToken.markUsed();

    User user = authToken.getUser();
    if (user == null) {
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    userRepository.recordLogin(user.getId());

    return createAuthResult(user, userAgent, ipAddress);
  }

  @Transactional
  @Public
  public AuthResult loginWithPassword(
      String email, String password, String userAgent, String ipAddress) {
    Domain domain = domainResolver.getDomain();
    User user =
        userRepository
            .findByEmailAndDomain(domain.getId(), email)
            .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_CREDENTIALS));

    if (user.getPasswordHash() == null) {
      // Return INVALID_CREDENTIALS to avoid leaking whether the account exists
      // (same response as user-not-found above)
      throw new BadRequestException(ErrorCode.INVALID_CREDENTIALS);
    }

    if (!BcryptUtil.matches(password, user.getPasswordHash())) {
      throw new BadRequestException(ErrorCode.INVALID_CREDENTIALS);
    }

    userRepository.recordLogin(user.getId());
    return createAuthResult(user, userAgent, ipAddress);
  }

  /** Same shape as {@link #requestOtp}: token committed first, delivery failure only logged. */
  @Public
  public void requestPasswordReset(String email) {
    String token = QuarkusTransaction.joiningExisting().call(() -> createPasswordResetToken(email));
    if (token == null) {
      return;
    }
    try {
      authEmailService.sendPasswordResetEmail(email, token);
    } catch (Exception e) {
      Log.errorf(
          e,
          "Failed to send password reset email to email=%s domain=%d",
          email,
          domainResolver.getDomain().getId());
    }
  }

  /** Null when no token is issued: unknown or unverified address, or rate limit. */
  private @Nullable String createPasswordResetToken(String email) {
    Domain domain = domainResolver.getDomain();
    User user = userRepository.findByEmailAndDomain(domain.getId(), email).orElse(null);

    // Always respond success to prevent email enumeration
    if (user == null || !user.isEmailVerified()) {
      return null;
    }

    // Rate limit
    long recentCount =
        authTokenRepository.countRecentByEmailAndType(
            email, AuthTokenType.PASSWORD_RESET, otpRateLimitWindowMinutes, domain.getId());
    if (recentCount >= otpMaxAttempts) {
      Log.warnf("Password reset rate limit exceeded for email=%s domain=%d", email, domain.getId());
      return null;
    }

    // Invalidate existing reset tokens
    authTokenRepository.invalidateByEmailAndType(
        email, AuthTokenType.PASSWORD_RESET, domain.getId());

    String token = generateSecureToken();
    String tokenHash = hashToken(token);

    AuthToken authToken =
        new AuthToken(
            user,
            email,
            tokenHash,
            AuthTokenType.PASSWORD_RESET,
            Instant.now().plus(Duration.ofHours(passwordResetExpiryHours)),
            domain.getId());
    authTokenRepository.persist(authToken);
    return token;
  }

  @Transactional
  @Public
  public AuthResult resetPassword(
      String token, String newPassword, String userAgent, String ipAddress) {
    String tokenHash = hashToken(token);

    AuthToken authToken =
        authTokenRepository
            .findValidByTokenHash(tokenHash)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    if (authToken.getTokenType() != AuthTokenType.PASSWORD_RESET) {
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    authToken.markUsed();

    User user = authToken.getUser();
    if (user == null) {
      throw new BadRequestException(ErrorCode.USER_NOT_FOUND);
    }

    user.setPasswordHash(BcryptUtil.bcryptHash(newPassword));
    user.recordLogin();

    return createAuthResult(user, userAgent, ipAddress);
  }

  @Transactional
  @Public
  public AuthResult authenticateWithPasskey(
      Map<String, Object> response, String userAgent, String ipAddress) {
    User user = passkeyService.verifyAuthentication(response);
    userRepository.recordLogin(user.getId());
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

    // A refresh is not a login, and it must not write a versioned or whole row: concurrent
    // refreshes of the same session (several tabs, app resume) would otherwise fail on the
    // optimistic lock, and a full-row flush could resurrect a session revoked meanwhile.
    authSessionRepository.markUsed(session.getId());

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
