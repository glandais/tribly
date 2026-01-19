package com.tribly.service.garmin;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.BadRequestException;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.domain.auth.AuthSession;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.garmin.response.GarminTokenResponse;
import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.annotation.Public;
import io.smallrye.jwt.auth.principal.JWTParser;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

/**
 * Service handling OAuth flow for Garmin Connect IQ devices.
 *
 * <p>OAuth Flow: 1. Device calls /authorize → redirects to frontend /garmin/login 2. User logs in
 * via magic link/passkey on frontend 3. Frontend POSTs to /callback with user token 4. Backend
 * generates auth code, redirects to connectiq://oauth?code=... 5. Device exchanges code for tokens
 * via /token
 */
@ApplicationScoped
public class GarminAuthService {

  private static final Logger LOG = Logger.getLogger(GarminAuthService.class);
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  // In-memory store for auth codes (short-lived, 5 minutes)
  // In production, consider using Redis or database
  private final Map<String, AuthCodeData> authCodes = new ConcurrentHashMap<>();

  @Inject GarminJwtService garminJwtService;
  @Inject AuthSessionRepository authSessionRepository;
  @Inject UserRepository userRepository;
  @Inject JWTParser jwtParser;
  @Inject DomainResolver domainResolver;

  @Public
  public String getFrontendBaseUrl() {
    return domainResolver.getDomain().getBaseUrl();
  }

  public record AuthCodeData(Long userId, Long domainId, Instant expiresAt, String redirectUri) {}

  /**
   * Generate an authorization code for the OAuth flow. Called after user authenticates on frontend.
   */
  @Transactional
  @Public
  public String generateAuthCode(String accessToken, String redirectUri) {
    // Validate the access token and extract user with domain
    UserWithDomain userWithDomain = validateAccessTokenAndGetUser(accessToken);

    // Generate auth code
    String code = generateSecureToken();

    // Store with 5 minute expiry (including domainId for multi-tenant isolation)
    authCodes.put(
        code,
        new AuthCodeData(
            userWithDomain.user().getId(),
            userWithDomain.domainId(),
            Instant.now().plus(Duration.ofMinutes(5)),
            redirectUri));

    // Clean up expired codes periodically
    cleanupExpiredCodes();

    LOG.infov("Generated auth code for user {0}", userWithDomain.user().getEmail());
    return code;
  }

  private record UserWithDomain(User user, Long domainId) {}

  /** Exchange authorization code for access and refresh tokens. */
  @Transactional
  @Public
  public GarminTokenResponse exchangeAuthCode(String code) {
    AuthCodeData codeData = authCodes.remove(code);

    if (codeData == null || codeData.expiresAt().isBefore(Instant.now())) {
      throw new BadRequestException(ErrorCode.TOKEN_INVALID);
    }

    // Use domain-aware lookup for multi-tenant isolation
    User user =
        userRepository
            .findActiveByIdAndDomain(codeData.domainId(), codeData.userId())
            .orElseThrow(() -> new BadRequestException(ErrorCode.TOKEN_INVALID));

    return createTokenResponse(user);
  }

  /** Refresh an access token using a refresh token. */
  @Transactional
  @Public
  public GarminTokenResponse refreshToken(String refreshToken) {
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

    // Generate new access token only (refresh token stays the same)
    String accessToken = garminJwtService.generateAccessToken(user);

    return GarminTokenResponse.builder()
        .accessToken(accessToken)
        .tokenType("Bearer")
        .expiresIn(garminJwtService.getAccessTokenExpirySeconds())
        .refreshToken(null) // Don't return refresh token on refresh
        .build();
  }

  private GarminTokenResponse createTokenResponse(User user) {
    // Generate tokens
    String accessToken = garminJwtService.generateAccessToken(user);
    String refreshToken = generateSecureToken();
    String refreshTokenHash = hashToken(refreshToken);

    // Create session with Garmin-specific expiry
    AuthSession session =
        new AuthSession(
            user,
            refreshTokenHash,
            Instant.now().plus(Duration.ofDays(garminJwtService.getRefreshTokenExpiryDays())));
    session.setUserAgent("Garmin Connect IQ");
    session.setIpAddress("device");
    authSessionRepository.persist(session);

    user.recordLogin();

    return GarminTokenResponse.builder()
        .accessToken(accessToken)
        .tokenType("Bearer")
        .expiresIn(garminJwtService.getAccessTokenExpirySeconds())
        .refreshToken(refreshToken)
        .build();
  }

  private UserWithDomain validateAccessTokenAndGetUser(String accessToken) {
    try {
      JsonWebToken jwt = jwtParser.parse(accessToken);
      String email = jwt.getSubject();
      String domainIdStr = jwt.getClaim("domainId");
      if (domainIdStr == null) {
        LOG.warn("Access token missing domainId claim");
        throw new ForbiddenException();
      }
      Long domainId = TsidUtils.toLong(domainIdStr);
      User user =
          userRepository.findByEmailAndDomain(domainId, email).orElseThrow(ForbiddenException::new);
      return new UserWithDomain(user, domainId);
    } catch (ForbiddenException e) {
      throw e;
    } catch (Exception e) {
      LOG.warnv("Failed to validate access token: {0}", e.getMessage());
      throw new ForbiddenException();
    }
  }

  private void cleanupExpiredCodes() {
    Instant now = Instant.now();
    authCodes.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
  }

  private String generateSecureToken() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
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
