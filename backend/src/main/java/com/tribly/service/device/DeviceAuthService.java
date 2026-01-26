package com.tribly.service.device;

import com.tribly.common.exception.BadRequestException;
import com.tribly.domain.auth.AuthSession;
import com.tribly.domain.auth.DeviceCode;
import com.tribly.domain.user.User;
import com.tribly.dto.device.response.DeviceCodeResponse;
import com.tribly.dto.device.response.DeviceTokenResponse;
import com.tribly.dto.error.ErrorCode;
import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.repository.auth.DeviceCodeRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import org.jboss.logging.Logger;

/**
 * Service handling device code OAuth flow (RFC 8628) for GPS devices (Karoo, Garmin, etc.).
 *
 * <p>Flow:
 *
 * <ol>
 *   <li>Device calls POST /api/device/oauth/device → gets device_code + user_code
 *   <li>Device displays user_code and QR for verification_uri_complete
 *   <li>User scans QR → opens /karoo?code=ABC123 on phone
 *   <li>User authenticates via OTP email on frontend
 *   <li>Frontend calls POST /api/device/oauth/complete to mark code as authorized
 *   <li>Device polls POST /api/device/oauth/token until authorized
 *   <li>Device receives access + refresh tokens
 * </ol>
 */
@ApplicationScoped
public class DeviceAuthService {

  private static final Logger LOG = Logger.getLogger(DeviceAuthService.class);
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private static final int DEVICE_CODE_EXPIRY_MINUTES = 10;
  private static final int POLLING_INTERVAL_SECONDS = 5;
  private static final String USER_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  private static final int USER_CODE_LENGTH = 6;

  @Inject DeviceJwtService deviceJwtService;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject DeviceCodeRepository deviceCodeRepository;
  @Inject UserRepository userRepository;
  @Inject DomainResolver domainResolver;

  @Public
  public String getFrontendBaseUrl() {
    return domainResolver.getDomain().getBaseUrl();
  }

  /**
   * Start the device code flow. Returns codes for the device to display and poll with.
   *
   * @param clientId The client identifier (e.g., "karoo", "garmin")
   * @return Device code response with user code and verification URLs
   */
  @Public
  @Transactional
  public DeviceCodeResponse initiateDeviceCodeFlow(String clientId) {
    Long domainId = domainResolver.getDomain().getId();

    // Generate device code (long, for polling)
    String deviceCode = generateSecureToken();
    String deviceCodeHash = hashToken(deviceCode);

    // Generate user code (short, human-readable)
    String userCode = generateUserCode();

    // Build verification URLs
    String baseUrl = getFrontendBaseUrl();
    String verificationUri = baseUrl + "/karoo";
    String verificationUriComplete = verificationUri + "?code=" + userCode;

    // Store device code in database
    Instant expiresAt = Instant.now().plus(Duration.ofMinutes(DEVICE_CODE_EXPIRY_MINUTES));
    DeviceCode entity = new DeviceCode(deviceCodeHash, userCode, clientId, domainId, expiresAt);
    deviceCodeRepository.persist(entity);

    LOG.infov("Initiated device code flow for client={0}, user_code={1}", clientId, userCode);

    return DeviceCodeResponse.builder()
        .deviceCode(deviceCode)
        .userCode(userCode)
        .verificationUri(verificationUri)
        .verificationUriComplete(verificationUriComplete)
        .expiresIn(DEVICE_CODE_EXPIRY_MINUTES * 60)
        .interval(POLLING_INTERVAL_SECONDS)
        .build();
  }

  /**
   * Complete the device code flow after user authenticates. Called by frontend after OTP
   * verification.
   *
   * @param userCode The user code displayed on device
   * @param userId The authenticated user's ID
   * @param domainId The domain ID for verification
   */
  @Transactional
  @Public
  public void completeDeviceCodeFlow(String userCode, Long userId, Long domainId) {
    DeviceCode deviceCode =
        deviceCodeRepository
            .findValidByUserCode(userCode)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    if (!deviceCode.isValid()) {
      throw new BadRequestException(ErrorCode.TOKEN_EXPIRED);
    }

    // Verify domain matches
    if (!deviceCode.getDomainId().equals(domainId)) {
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    // Get user and authorize device code
    User user =
        userRepository
            .findActiveByIdAndDomain(domainId, userId)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    deviceCode.authorize(user);

    LOG.infov(
        "Device code authorized for user {0}, client={1}, user_code={2}",
        userId, deviceCode.getClientId(), userCode);
  }

  /**
   * Exchange device code for tokens. Returns error codes per RFC 8628 if not yet authorized.
   *
   * @param deviceCodeValue The device code to exchange
   * @return Token response if authorized
   * @throws BadRequestException with specific error codes for polling states
   */
  @Transactional
  @Public
  public DeviceTokenResponse exchangeDeviceCode(String deviceCodeValue) {
    String deviceCodeHash = hashToken(deviceCodeValue);
    DeviceCode deviceCode =
        deviceCodeRepository
            .findByDeviceCodeHash(deviceCodeHash)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    if (!deviceCode.isValid()) {
      // Expired - delete it
      deviceCodeRepository.delete(deviceCode);
      throw new BadRequestException(ErrorCode.TOKEN_EXPIRED);
    }

    if (!deviceCode.isAuthorized() || deviceCode.getUser() == null) {
      // Not yet authorized - return authorization_pending error per RFC 8628
      throw new BadRequestException(ErrorCode.AUTHORIZATION_PENDING);
    }

    // Authorization successful - get user and delete code
    User user = deviceCode.getUser();
    String clientId = deviceCode.getClientId();
    deviceCodeRepository.delete(deviceCode);

    return createTokenResponse(user, clientId);
  }

  /**
   * Refresh an access token using a refresh token.
   *
   * @param refreshToken The refresh token
   * @param clientId The client identifier for the new access token
   * @return Token response with new access token
   */
  @Transactional
  @Public
  public DeviceTokenResponse refreshToken(String refreshToken, String clientId) {
    String tokenHash = hashToken(refreshToken);
    AuthSession session =
        authSessionRepository
            .findByRefreshTokenHash(tokenHash)
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    if (!session.isValid()) {
      throw new BadRequestException(ErrorCode.SESSION_EXPIRED);
    }

    // Update session usage
    session.markUsed();

    User user = session.getUser();
    user.recordLogin();

    // Generate new access token only (refresh token stays the same)
    String accessToken = deviceJwtService.generateAccessToken(user, clientId);

    return DeviceTokenResponse.builder()
        .accessToken(accessToken)
        .tokenType("Bearer")
        .expiresIn(deviceJwtService.getAccessTokenExpirySeconds())
        .refreshToken(null)
        .build();
  }

  /**
   * Look up device code by user code (for frontend display during verification).
   *
   * @param userCode The user code
   * @return Device code entity if valid, empty otherwise
   */
  @Public
  public Optional<DeviceCode> getDeviceCodeByUserCode(String userCode) {
    return deviceCodeRepository.findValidByUserCode(userCode);
  }

  /**
   * Check if a user code exists and get its authorization status.
   *
   * @param userCode The user code to verify
   * @return Optional containing the authorization status if code is valid, empty if not found
   */
  @Public
  public Optional<Boolean> isUserCodeAuthorized(String userCode) {
    return deviceCodeRepository.findValidByUserCode(userCode).map(DeviceCode::isAuthorized);
  }

  private DeviceTokenResponse createTokenResponse(User user, String clientId) {
    // Generate tokens
    String accessToken = deviceJwtService.generateAccessToken(user, clientId);
    String refreshToken = generateSecureToken();
    String refreshTokenHash = hashToken(refreshToken);

    // Create session with device-specific expiry
    AuthSession session =
        new AuthSession(
            user,
            refreshTokenHash,
            Instant.now().plus(Duration.ofDays(deviceJwtService.getRefreshTokenExpiryDays())));
    session.setUserAgent(clientId + " Device");
    session.setIpAddress("device");
    authSessionRepository.persist(session);

    user.recordLogin();

    return DeviceTokenResponse.builder()
        .accessToken(accessToken)
        .tokenType("Bearer")
        .expiresIn(deviceJwtService.getAccessTokenExpirySeconds())
        .refreshToken(refreshToken)
        .build();
  }

  private String generateSecureToken() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String generateUserCode() {
    StringBuilder code = new StringBuilder(USER_CODE_LENGTH);
    for (int i = 0; i < USER_CODE_LENGTH; i++) {
      int index = SECURE_RANDOM.nextInt(USER_CODE_CHARS.length());
      code.append(USER_CODE_CHARS.charAt(index));
    }
    return code.toString();
  }

  private String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }
}
