package fr.pedalons.service.gps;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.InternalException;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.gps.GpsOAuthState;
import fr.pedalons.domain.gps.GpsServiceConnection;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.gps.response.GpsOAuthUrlResponse;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.dto.gps.response.RouteUploadResponse;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.infrastructure.gps.GarminClient;
import fr.pedalons.infrastructure.gps.GpsServiceClient;
import fr.pedalons.infrastructure.gps.HammerheadClient;
import fr.pedalons.infrastructure.gps.PkceUtils;
import fr.pedalons.infrastructure.gps.RouteUploadResult;
import fr.pedalons.infrastructure.gps.TokenResponse;
import fr.pedalons.infrastructure.gps.WahooClient;
import fr.pedalons.infrastructure.security.TokenEncryptionService;
import fr.pedalons.repository.gps.GpsOAuthStateRepository;
import fr.pedalons.repository.gps.GpsServiceConnectionRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import org.jboss.logging.Logger;

/**
 * Main service for GPS device integrations.
 * Handles OAuth flow, token management, and route uploads.
 */
@ApplicationScoped
public class GpsService {

  private static final Logger LOG = Logger.getLogger(GpsService.class);

  @Inject GpsOAuthStateRepository oauthStateRepository;

  @Inject GpsServiceConnectionRepository connectionRepository;

  @Inject UserRepository userRepository;

  @Inject TokenEncryptionService encryptionService;

  @Inject HammerheadClient hammerheadClient;

  @Inject GarminClient garminClient;

  @Inject WahooClient wahooClient;

  @Inject PedalonsQueryContext pedalonsContext;

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
      case WAHOO -> wahooClient;
    };
  }

  /**
   * Initiate OAuth flow for connecting a GPS service.
   * Returns the authorization URL to redirect the user to.
   */
  @Logged
  @Transactional
  public GpsOAuthUrlResponse initiateOAuth(GpsServiceType serviceType) {
    // Check if GPS service is configured for this domain
    if (!credentialService.isServiceAvailable(serviceType)) {
      throw new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONFIGURED);
    }

    Long userId = pedalonsContext.getUserId();

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
        domainResolver.getEffectiveBaseUrl()
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

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    Long domainId = pedalonsContext.getDomainId();

    oauthStateRepository.persist(
        new GpsOAuthState(
            user,
            state,
            serviceType,
            Instant.now().plusSeconds(600),
            codeVerifier,
            redirectUri,
            domainId));

    return new GpsOAuthUrlResponse(authUrl);
  }

  /**
   * Handle OAuth callback after user authorizes.
   * Exchanges code for tokens and stores the connection.
   */
  @Transactional
  @Public
  public void handleCallback(GpsServiceType serviceType, String code, String state) {
    // Validate state (findValidByState filters expired states)
    GpsOAuthState oauthState =
        oauthStateRepository
            .findValidByState(state)
            .orElseThrow(() -> new BusinessException(ErrorCode.GPS_INVALID_STATE));

    if (oauthState.getServiceType() != serviceType) {
      throw new BusinessException(ErrorCode.GPS_INVALID_STATE);
    }

    Long currentDomainId = pedalonsContext.getDomainId();
    if (!oauthState.getDomainId().equals(currentDomainId)) {
      throw new BusinessException(ErrorCode.GPS_INVALID_STATE);
    }

    // Delete state immediately after validation (single use)
    oauthStateRepository.delete(oauthState);

    Long userId = oauthState.getUser().getId();
    String redirectUri = oauthState.getRedirectUri();

    // Exchange code for tokens
    GpsServiceClient client = getClient(serviceType);
    TokenResponse tokens;
    if (client.supportsPkce() && oauthState.getCodeVerifier() != null) {
      tokens = client.exchangeCode(code, redirectUri, oauthState.getCodeVerifier());
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
    Long userId = pedalonsContext.getUserId();
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
  @Logged
  public List<GpsServiceConnectionDto> getConnectionsForUser() {
    Long userId = pedalonsContext.getUserId();
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
    Route route = routeService.get(teamSlug, routeSlug);
    return uploadGpx(serviceType, getRouteGpxContent(route), route.getName());
  }

  /**
   * Uploads GPX bytes to the current user's connection on a GPS service. The bytes may come from a
   * route's stored asset or from a GPX preview, which owns no asset.
   */
  @Transactional
  @Logged
  public RouteUploadResponse uploadGpx(GpsServiceType serviceType, byte[] gpxContent, String name) {
    Long userId = pedalonsContext.getUserId();

    // Get connection
    GpsServiceConnection connection =
        connectionRepository
            .findByUserAndService(userId, serviceType)
            .orElseThrow(() -> new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONNECTED));

    // Refresh token if needed
    if (connection.isTokenExpired() && connection.getRefreshTokenEncrypted() != null) {
      refreshAccessToken(connection);
    }

    // Upload to service
    String accessToken = encryptionService.decrypt(connection.getAccessTokenEncrypted());
    GpsServiceClient client = getClient(serviceType);
    RouteUploadResult result = client.uploadRoute(accessToken, gpxContent, name);

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
      throw new InternalException(ErrorCode.GPX_FAILURE, e);
    }
  }

  @Public
  public String getFrontendBaseUrl() {
    return domainResolver.getEffectiveBaseUrl();
  }
}
