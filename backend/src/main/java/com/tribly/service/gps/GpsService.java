package com.tribly.service.gps;

import com.tribly.common.exception.BusinessException;
import com.tribly.domain.asset.Asset;
import com.tribly.domain.gps.GpsServiceConnection;
import com.tribly.domain.route.Route;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.gps.response.GpsOAuthUrlResponse;
import com.tribly.dto.gps.response.GpsServiceConnectionDto;
import com.tribly.dto.gps.response.RouteUploadResponse;
import com.tribly.enums.AssetType;
import com.tribly.enums.GpsServiceType;
import com.tribly.infrastructure.gps.GarminClient;
import com.tribly.infrastructure.gps.GpsServiceClient;
import com.tribly.infrastructure.gps.HammerheadClient;
import com.tribly.infrastructure.gps.PkceUtils;
import com.tribly.infrastructure.gps.RouteUploadResult;
import com.tribly.infrastructure.gps.TokenResponse;
import com.tribly.infrastructure.security.TokenEncryptionService;
import com.tribly.repository.gps.GpsServiceConnectionRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.asset.AssetService;
import com.tribly.service.route.RouteService;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.Logged;
import com.tribly.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.logging.Logger;

/**
 * Main service for GPS device integrations.
 * Handles OAuth flow, token management, and route uploads.
 */
@ApplicationScoped
public class GpsService {

  private static final Logger LOG = Logger.getLogger(GpsService.class);

  // In-memory state storage for OAuth flow (consider Redis for production multi-instance)
  private final Map<String, OAuthState> pendingStates = new ConcurrentHashMap<>();

  @Inject GpsServiceConnectionRepository connectionRepository;

  @Inject UserRepository userRepository;

  @Inject TokenEncryptionService encryptionService;

  @Inject HammerheadClient hammerheadClient;

  @Inject GarminClient garminClient;

  @Inject TriblyQueryContext triblyContext;

  @Inject RouteService routeService;

  @Inject AssetService assetService;

  @Inject DomainGpsCredentialService credentialService;

  @Inject DomainResolver domainResolver;

  /**
   * Get the appropriate client for a service type.
   */
  private GpsServiceClient getClient(GpsServiceType serviceType) {
    return switch (serviceType) {
      case HAMMERHEAD -> hammerheadClient;
      case GARMIN -> garminClient;
    };
  }

  /**
   * Initiate OAuth flow for connecting a GPS service.
   * Returns the authorization URL to redirect the user to.
   */
  @Logged
  public GpsOAuthUrlResponse initiateOAuth(GpsServiceType serviceType) {
    // Check if GPS service is configured for this domain
    if (!credentialService.isServiceAvailable(serviceType)) {
      throw new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONFIGURED);
    }

    Long userId = triblyContext.getUserId();

    // Check if already connected
    connectionRepository
        .findByUserAndService(userId, serviceType)
        .ifPresent(
            existing -> {
              throw new BusinessException(ErrorCode.GPS_SERVICE_ALREADY_CONNECTED);
            });

    // Generate state for CSRF protection
    byte[] stateBytes = new byte[32];
    new SecureRandom().nextBytes(stateBytes);
    String state = Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);

    GpsServiceClient client = getClient(serviceType);
    String redirectUri =
        triblyContext.getDomain().getBaseUrl()
            + "/api/gps/callback/"
            + serviceType.name().toLowerCase();
    String authUrl;
    String codeVerifier = null;

    if (client.supportsPkce()) {
      // Generate PKCE code verifier and challenge
      codeVerifier = PkceUtils.generateCodeVerifier();
      String codeChallenge = PkceUtils.generateCodeChallenge(codeVerifier);
      authUrl = client.getAuthorizationUrl(state, redirectUri, codeChallenge);
    } else {
      authUrl = client.getAuthorizationUrl(state, redirectUri);
    }

    // Store state with user ID, expiry, code verifier, and redirect URI
    pendingStates.put(
        state,
        new OAuthState(
            userId, serviceType, Instant.now().plusSeconds(600), codeVerifier, redirectUri));

    return new GpsOAuthUrlResponse(authUrl);
  }

  /**
   * Handle OAuth callback after user authorizes.
   * Exchanges code for tokens and stores the connection.
   */
  @Transactional
  @Public
  public void handleCallback(GpsServiceType serviceType, String code, String state) {
    // Validate state
    OAuthState oauthState = pendingStates.remove(state);
    if (oauthState == null) {
      throw new BusinessException(ErrorCode.GPS_INVALID_STATE);
    }
    if (oauthState.expiresAt().isBefore(Instant.now())) {
      throw new BusinessException(ErrorCode.GPS_STATE_EXPIRED);
    }
    if (oauthState.serviceType() != serviceType) {
      throw new BusinessException(ErrorCode.GPS_INVALID_STATE);
    }

    Long userId = oauthState.userId();
    String redirectUri = oauthState.redirectUri();

    // Exchange code for tokens
    GpsServiceClient client = getClient(serviceType);
    TokenResponse tokens;
    if (client.supportsPkce() && oauthState.codeVerifier() != null) {
      tokens = client.exchangeCode(code, redirectUri, oauthState.codeVerifier());
    } else {
      tokens = client.exchangeCode(code, redirectUri);
    }

    // Get user from stored userId (user is not authenticated during callback)
    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // Create or update connection
    GpsServiceConnection connection =
        connectionRepository
            .findByUserAndService(userId, serviceType)
            .orElseGet(() -> new GpsServiceConnection(user, serviceType));

    // Store encrypted tokens
    connection.setAccessTokenEncrypted(encryptionService.encrypt(tokens.accessToken()));
    if (tokens.refreshToken() != null) {
      connection.setRefreshTokenEncrypted(encryptionService.encrypt(tokens.refreshToken()));
    }
    if (tokens.expiresIn() != null) {
      connection.setTokenExpiresAt(Instant.now().plusSeconds(tokens.expiresIn()));
    }
    connection.setExternalUserId(tokens.userId());
    connection.setConnectedAt(Instant.now());

    connectionRepository.persist(connection);
    LOG.infof("User %d connected to %s", userId, serviceType);
  }

  /**
   * Disconnect a GPS service.
   */
  @Transactional
  @Logged
  public void disconnect(GpsServiceType serviceType) {
    Long userId = triblyContext.getUserId();
    GpsServiceConnection connection =
        connectionRepository
            .findByUserAndService(userId, serviceType)
            .orElseThrow(() -> new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONNECTED));

    connectionRepository.delete(connection);
    LOG.infof("User %d disconnected from %s", userId, serviceType);
  }

  /**
   * Get all GPS service connections for the current user.
   */
  public List<GpsServiceConnectionDto> getConnectionsForUser() {
    Long userId = triblyContext.getUserId();
    return connectionRepository.findByUser(userId).stream()
        .map(GpsServiceConnectionDto::from)
        .toList();
  }

  /**
   * Get all GPS service types that are available (configured) for the current domain.
   */
  @Logged
  public List<GpsServiceType> getAvailableServices() {
    return credentialService.getAvailableServices();
  }

  /**
   * Upload a route to a connected GPS service.
   */
  @Transactional
  @Logged
  public RouteUploadResponse uploadRoute(
      GpsServiceType serviceType, String teamSlug, String routeSlug) {
    Long userId = triblyContext.getUserId();

    // Get connection
    GpsServiceConnection connection =
        connectionRepository
            .findByUserAndService(userId, serviceType)
            .orElseThrow(() -> new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONNECTED));

    // Refresh token if needed
    if (connection.isTokenExpired() && connection.getRefreshTokenEncrypted() != null) {
      refreshAccessToken(connection);
    }

    // Get route and GPX file
    Route route = routeService.get(teamSlug, routeSlug);
    byte[] gpxContent = getRouteGpxContent(route);

    // Upload to service
    String accessToken = encryptionService.decrypt(connection.getAccessTokenEncrypted());
    GpsServiceClient client = getClient(serviceType);
    RouteUploadResult result = client.uploadRoute(accessToken, gpxContent, route.getName());

    // Update last used
    connection.markUsed();
    connectionRepository.persist(connection);

    return new RouteUploadResponse(result.success(), result.message(), result.externalRouteId());
  }

  private void refreshAccessToken(GpsServiceConnection connection) {
    String refreshToken = encryptionService.decrypt(connection.getRefreshTokenEncrypted());
    GpsServiceClient client = getClient(connection.getServiceType());

    TokenResponse tokens = client.refreshToken(refreshToken);

    connection.setAccessTokenEncrypted(encryptionService.encrypt(tokens.accessToken()));
    if (tokens.refreshToken() != null) {
      connection.setRefreshTokenEncrypted(encryptionService.encrypt(tokens.refreshToken()));
    }
    if (tokens.expiresIn() != null) {
      connection.setTokenExpiresAt(Instant.now().plusSeconds(tokens.expiresIn()));
    }

    connectionRepository.persist(connection);
    LOG.infof(
        "Refreshed access token for user %d on %s",
        connection.getUser().getId(), connection.getServiceType());
  }

  private byte[] getRouteGpxContent(Route route) {
    // Find the filtered GPX asset
    Asset gpxAsset =
        route.getAssets().stream()
            .filter(a -> a.getType() == AssetType.ROUTE_FILTERED_GPX)
            .findFirst()
            .orElseGet(
                () ->
                    route.getAssets().stream()
                        .filter(a -> a.getType() == AssetType.ROUTE_ORIGINAL_GPX)
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(ErrorCode.GPX_NOT_FOUND)));

    try (InputStream is = assetService.getAssetContent(gpxAsset)) {
      return is.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read GPX file", e);
    }
  }

  @Public
  public String getFrontendBaseUrl() {
    return domainResolver.getDomain().getBaseUrl();
  }

  private record OAuthState(
      Long userId,
      GpsServiceType serviceType,
      Instant expiresAt,
      String codeVerifier,
      String redirectUri) {}
}
