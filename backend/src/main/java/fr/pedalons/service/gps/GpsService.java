package fr.pedalons.service.gps;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.InternalException;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.gps.DomainGpsCredential;
import fr.pedalons.domain.gps.GpsOAuthState;
import fr.pedalons.domain.gps.GpsServiceConnection;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.gps.response.GpsOAuthUrlResponse;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.dto.gps.response.RouteUploadResponse;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.GpsConnectReturn;
import fr.pedalons.enums.GpsOAuthVersion;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.infrastructure.gps.GarminClient;
import fr.pedalons.infrastructure.gps.GarminOAuth1Client;
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

  @Inject GarminOAuth1Client garminOAuth1Client;

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
   * The protocol the domain's credential for this service uses. Without a credential, OAuth 2.0:
   * the client then refuses with {@code GPS_SERVICE_NOT_CONFIGURED}, as before.
   */
  private GpsOAuthVersion oauthVersion(GpsServiceType serviceType) {
    return credentialService
        .getCredentials(serviceType)
        .map(DomainGpsCredential::getOauthVersion)
        .orElse(GpsOAuthVersion.OAUTH2);
  }

  /**
   * Initiate OAuth flow for connecting a GPS service.
   * Returns the authorization URL to redirect the user to.
   */
  @Logged
  @Transactional
  public GpsOAuthUrlResponse initiateOAuth(GpsServiceType serviceType) {
    return initiateOAuth(serviceType, GpsConnectReturn.PROFILE);
  }

  /**
   * Initiate OAuth flow for connecting a GPS service, the callback then sending the browser back to
   * {@code returnTo}.
   */
  @Logged
  @Transactional
  public GpsOAuthUrlResponse initiateOAuth(GpsServiceType serviceType, GpsConnectReturn returnTo) {
    // Check if GPS service is configured for this domain
    if (!credentialService.isServiceAvailable(serviceType)) {
      throw new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONFIGURED);
    }

    Long userId = pedalonsContext.getUserId();
    GpsOAuthVersion oauthVersion = oauthVersion(serviceType);

    // Check if already connected. A connection made under the other protocol is useless since the
    // domain switched its credential: the new one replaces it (handleCallback reuses the row).
    connectionRepository
        .findByUserAndService(userId, serviceType)
        .filter(existing -> existing.oauthVersion() == oauthVersion)
        .ifPresent(
            existing -> {
              throw new BusinessException(ErrorCode.GPS_SERVICE_ALREADY_CONNECTED);
            });

    Long domainId = pedalonsContext.getDomainId();

    User user =
        userRepository
            .findActiveByIdAndDomain(domainId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    String redirectUri =
        domainResolver.getEffectiveBaseUrl()
            + "/api/gps/callback/"
            + serviceType.name().toLowerCase();

    if (oauthVersion == GpsOAuthVersion.OAUTH1) {
      return initiateOAuth1(user, serviceType, redirectUri, domainId, returnTo);
    }

    // Generate state for CSRF protection
    byte[] stateBytes = new byte[32];
    new SecureRandom().nextBytes(stateBytes);
    String state = Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);

    GpsServiceClient client = getClient(serviceType);
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

    oauthStateRepository.persist(
        new GpsOAuthState(
            user,
            state,
            serviceType,
            Instant.now().plusSeconds(600),
            codeVerifier,
            redirectUri,
            domainId,
            returnTo));

    return new GpsOAuthUrlResponse(authUrl);
  }

  /**
   * OAuth 1.0a (Garmin only): Garmin's callback names the request token, not a state of ours, so
   * the request token is what the state row is found by — as unguessable, and as single-use.
   */
  private GpsOAuthUrlResponse initiateOAuth1(
      User user,
      GpsServiceType serviceType,
      String redirectUri,
      Long domainId,
      GpsConnectReturn returnTo) {
    requireGarmin(serviceType);
    GarminOAuth1Client.Token requestToken = garminOAuth1Client.requestToken(redirectUri);

    GpsOAuthState oauthState =
        new GpsOAuthState(
            user,
            requestToken.token(),
            serviceType,
            Instant.now().plusSeconds(600),
            null,
            redirectUri,
            domainId,
            returnTo);
    oauthState.setRequestTokenSecretEncrypted(encryptionService.encrypt(requestToken.secret()));
    oauthStateRepository.persist(oauthState);

    return new GpsOAuthUrlResponse(garminOAuth1Client.authorizationUrl(requestToken.token()));
  }

  /**
   * Where the callback carrying {@code state} sends the browser back to — read before
   * {@link #handleCallback} consumes the state, and for a refusal, which never reaches it. An
   * OAuth 1.0a callback passes its request token, which is what its state row is keyed by. An
   * unknown, expired or foreign state, or one written before API-63, gives the profile.
   */
  @Transactional
  @Public
  public GpsConnectReturn findReturnTarget(String state) {
    Long domainId = pedalonsContext.getDomainId();
    return oauthStateRepository
        .findValidByState(state)
        .filter(s -> s.getDomainId().equals(domainId))
        .map(GpsOAuthState::getReturnTo)
        .orElse(GpsConnectReturn.PROFILE);
  }

  /**
   * Handle OAuth callback after user authorizes.
   * Exchanges code for tokens and stores the connection.
   */
  @Transactional
  @Public
  public void handleCallback(GpsServiceType serviceType, String code, String state) {
    GpsOAuthState oauthState = consumeState(serviceType, state);
    // A request token is no OAuth 2.0 state, even though it sits in the same column.
    if (oauthState.getRequestTokenSecretEncrypted() != null) {
      throw new BusinessException(ErrorCode.GPS_INVALID_STATE);
    }

    String redirectUri = oauthState.getRedirectUri();

    // Exchange code for tokens
    GpsServiceClient client = getClient(serviceType);
    TokenResponse tokens;
    if (client.supportsPkce() && oauthState.getCodeVerifier() != null) {
      tokens = client.exchangeCode(code, redirectUri, oauthState.getCodeVerifier());
    } else {
      tokens = client.exchangeCode(code, redirectUri);
    }

    GpsServiceConnection connection = connectionFor(oauthState);

    // Store encrypted tokens
    connection.setAccessTokenEncrypted(encryptionService.encrypt(tokens.accessToken()));
    connection.setAccessTokenSecretEncrypted(null);
    if (tokens.refreshToken() != null) {
      connection.setRefreshTokenEncrypted(encryptionService.encrypt(tokens.refreshToken()));
    }
    if (tokens.expiresIn() != null) {
      connection.setTokenExpiresAt(Instant.now().plusSeconds(tokens.expiresIn()));
    }
    connection.setExternalUserId(tokens.userId());
    connection.setConnectedAt(Instant.now());

    connectionRepository.persist(connection);
    LOG.infof("User %d connected to %s", oauthState.getUser().getId(), serviceType);
  }

  /**
   * Handle the OAuth 1.0a callback (Garmin only): exchanges the request token and its verifier for
   * an access token, and stores the connection.
   */
  @Transactional
  @Public
  public void handleOAuth1Callback(GpsServiceType serviceType, String oauthToken, String verifier) {
    requireGarmin(serviceType);
    GpsOAuthState oauthState = consumeState(serviceType, oauthToken);
    byte[] requestTokenSecret = oauthState.getRequestTokenSecretEncrypted();
    if (requestTokenSecret == null) {
      throw new BusinessException(ErrorCode.GPS_INVALID_STATE);
    }

    GarminOAuth1Client.Token accessToken =
        garminOAuth1Client.accessToken(
            oauthToken, encryptionService.decrypt(requestTokenSecret), verifier);

    GpsServiceConnection connection = connectionFor(oauthState);
    connection.setAccessTokenEncrypted(encryptionService.encrypt(accessToken.token()));
    connection.setAccessTokenSecretEncrypted(encryptionService.encrypt(accessToken.secret()));
    // An OAuth 1.0a token neither expires nor refreshes: nothing of an OAuth 2.0 one may linger.
    connection.setRefreshTokenEncrypted(null);
    connection.setTokenExpiresAt(null);
    connection.setExternalUserId(null);
    connection.setConnectedAt(Instant.now());

    connectionRepository.persist(connection);
    LOG.infof("User %d connected to %s over OAuth 1.0a", oauthState.getUser().getId(), serviceType);
  }

  /**
   * Finds the pending authorization a callback refers to, checks it belongs to this service and
   * domain, and deletes it: it is single-use.
   */
  private GpsOAuthState consumeState(GpsServiceType serviceType, String state) {
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
    return oauthState;
  }

  /** The user's connection to the state's service, existing or new. */
  private GpsServiceConnection connectionFor(GpsOAuthState oauthState) {
    Long userId = oauthState.getUser().getId();
    // Get user from stored userId (user is not authenticated during callback)
    User user =
        userRepository
            .findActiveByIdAndDomain(oauthState.getDomainId(), userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    return connectionRepository
        .findByUserAndService(userId, oauthState.getServiceType())
        .orElseGet(() -> new GpsServiceConnection(user, oauthState.getServiceType()));
  }

  private static void requireGarmin(GpsServiceType serviceType) {
    if (serviceType != GpsServiceType.GARMIN) {
      throw new BusinessException(ErrorCode.GPS_INVALID_STATE);
    }
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

    // The domain switched its credential to the other protocol since this connection was made:
    // its tokens are worthless now. Dropped rather than kept — the user is asked to reconnect, and
    // the failure is returned, not thrown, so that the deletion commits.
    GpsOAuthVersion oauthVersion = oauthVersion(serviceType);
    if (connection.oauthVersion() != oauthVersion) {
      connectionRepository.delete(connection);
      LOG.infof(
          "Dropped user %d's %s connection made over another OAuth version", userId, serviceType);
      return new RouteUploadResponse(false, "Reconnect " + serviceType.name(), null);
    }

    RouteUploadResult result;
    String accessToken = encryptionService.decrypt(connection.getAccessTokenEncrypted());
    byte[] accessTokenSecret = connection.getAccessTokenSecretEncrypted();
    if (accessTokenSecret != null) {
      requireGarmin(serviceType);
      result =
          garminOAuth1Client.uploadRoute(
              accessToken, encryptionService.decrypt(accessTokenSecret), gpxContent, name);
    } else {
      // Refresh token if needed
      if (connection.isTokenExpired() && connection.getRefreshTokenEncrypted() != null) {
        refreshAccessToken(connection);
        accessToken = encryptionService.decrypt(connection.getAccessTokenEncrypted());
      }
      result = getClient(serviceType).uploadRoute(accessToken, gpxContent, name);
    }

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
