package fr.pedalons.service.social;

import static fr.pedalons.common.TokenUtils.generateSecureToken;
import static fr.pedalons.common.TokenUtils.hashToken;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.domain.social.SocialLoginCode;
import fr.pedalons.domain.social.SocialOAuthState;
import fr.pedalons.domain.social.UserSocialIdentity;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.auth.response.AuthResult;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.social.response.StravaAuthUrlResponse;
import fr.pedalons.enums.SocialAuthPurpose;
import fr.pedalons.enums.SocialProvider;
import fr.pedalons.infrastructure.social.StravaAthlete;
import fr.pedalons.infrastructure.social.StravaClient;
import fr.pedalons.repository.social.SocialLoginCodeRepository;
import fr.pedalons.repository.social.SocialOAuthStateRepository;
import fr.pedalons.repository.social.UserSocialIdentityRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.auth.AuthService;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.security.annotation.Public;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Strava authentication: start OAuth (login or link), handle the callback (bind/recover accounts),
 * and exchange the one-time hand-off code for a real session.
 *
 * <p>The callback is unauthenticated (browser redirect) and, with a single global Strava app, may
 * land on a central host that differs from the tenant host. It therefore mints a short-lived,
 * single-use code and redirects to the tenant SPA, which exchanges it for a first-party session.
 */
@ApplicationScoped
public class StravaAuthService {

  private static final String CALLBACK_PATH = "/api/auth/strava/callback";

  @Inject SocialOAuthStateRepository oauthStateRepository;
  @Inject SocialLoginCodeRepository loginCodeRepository;
  @Inject UserSocialIdentityRepository identityRepository;
  @Inject UserRepository userRepository;
  @Inject StravaClient stravaClient;
  @Inject SocialCredentialService credentialService;
  @Inject DomainResolver domainResolver;
  @Inject PedalonsQueryContext pedalonsContext;
  @Inject AuthService authService;

  @ConfigProperty(name = "pedalons.social.callback-base-url")
  Optional<String> callbackBaseUrl;

  @ConfigProperty(name = "pedalons.social.login-code.expiry-seconds", defaultValue = "60")
  int loginCodeExpirySeconds;

  @ConfigProperty(name = "pedalons.social.oauth-state.expiry-minutes", defaultValue = "10")
  int oauthStateExpiryMinutes;

  @ConfigProperty(name = "pedalons.social.placeholder-email-domain", defaultValue = "pedalons.fr")
  String placeholderEmailDomain;

  /** Base URL of the tenant SPA, used for redirects and error landings. */
  @Public
  public String getFrontendBaseUrl() {
    return domainResolver.getEffectiveBaseUrl();
  }

  /** Start a Strava login/recovery flow (no authenticated user yet). */
  @Public
  @Transactional
  public StravaAuthUrlResponse initiateLogin() {
    requireConfigured();
    return persistStateAndBuildUrl(null, SocialAuthPurpose.LOGIN);
  }

  /** Start a "link Strava" flow from account settings (must be logged in). */
  @Logged
  @Transactional
  public StravaAuthUrlResponse initiateLink() {
    requireConfigured();
    User user = pedalonsContext.getUser();
    identityRepository
        .findByUserAndProvider(user.getId(), SocialProvider.STRAVA)
        .ifPresent(
            existing -> {
              throw new ConflictException(ErrorCode.SOCIAL_IDENTITY_ALREADY_LINKED);
            });
    return persistStateAndBuildUrl(user, SocialAuthPurpose.LINK);
  }

  private StravaAuthUrlResponse persistStateAndBuildUrl(User user, SocialAuthPurpose purpose) {
    String state = generateSecureToken();
    String returnBaseUrl = domainResolver.getEffectiveBaseUrl();
    String redirectUri =
        callbackBaseUrl.filter(s -> !s.isBlank()).orElse(returnBaseUrl) + CALLBACK_PATH;
    Long domainId = domainResolver.getDomainId();

    oauthStateRepository.persist(
        new SocialOAuthState(
            user,
            state,
            SocialProvider.STRAVA,
            purpose,
            Instant.now().plus(Duration.ofMinutes(oauthStateExpiryMinutes)),
            redirectUri,
            returnBaseUrl,
            domainId));

    return new StravaAuthUrlResponse(stravaClient.getAuthorizationUrl(state, redirectUri));
  }

  /**
   * Handle the Strava OAuth callback. Validates the state, exchanges the code for the athlete id,
   * resolves/binds the account, and returns the tenant SPA redirect URL. Throws {@link
   * BusinessException} with {@link ErrorCode#SOCIAL_NO_ACCOUNT} when no account can be recovered.
   */
  @Public
  @Transactional
  public String handleCallback(String code, String state) {
    SocialOAuthState oauthState =
        oauthStateRepository
            .findValidByState(state)
            .orElseThrow(() -> new BadRequestException(ErrorCode.SOCIAL_INVALID_STATE));

    Long currentDomainId = domainResolver.getDomainId();
    if (oauthState.getProvider() != SocialProvider.STRAVA
        || !oauthState.getDomainId().equals(currentDomainId)) {
      throw new BadRequestException(ErrorCode.SOCIAL_INVALID_STATE);
    }

    // Single use — delete immediately after validation.
    String redirectUri = oauthState.getRedirectUri();
    String returnBaseUrl = oauthState.getReturnBaseUrl();
    SocialAuthPurpose purpose = oauthState.getPurpose();
    Long domainId = oauthState.getDomainId();
    User stateUser = oauthState.getUser();
    oauthStateRepository.delete(oauthState);

    StravaAthlete athlete = stravaClient.exchangeCode(code, redirectUri);
    String athleteId = athlete.athleteId();

    if (purpose == SocialAuthPurpose.LINK) {
      return handleLink(stateUser, domainId, athleteId, returnBaseUrl);
    }
    return handleLogin(domainId, athleteId, returnBaseUrl);
  }

  private String handleLink(User stateUser, Long domainId, String athleteId, String returnBaseUrl) {
    if (stateUser == null) {
      throw new BadRequestException(ErrorCode.SOCIAL_INVALID_STATE);
    }
    User user =
        userRepository
            .findActiveById(stateUser.getId())
            .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

    Optional<UserSocialIdentity> existing =
        identityRepository.findByProviderAndExternalId(domainId, SocialProvider.STRAVA, athleteId);
    if (existing.isPresent()) {
      if (!existing.get().getUser().getId().equals(user.getId())) {
        throw new ConflictException(ErrorCode.SOCIAL_IDENTITY_ALREADY_LINKED);
      }
      // Already linked to this user — idempotent success.
      return returnBaseUrl + "/profile?strava_linked=1";
    }

    identityRepository.persist(
        new UserSocialIdentity(user, domainId, SocialProvider.STRAVA, athleteId));
    Log.infof("User %d linked Strava athlete %s", user.getId(), athleteId);
    return returnBaseUrl + "/profile?strava_linked=1";
  }

  private String handleLogin(Long domainId, String athleteId, String returnBaseUrl) {
    Optional<UserSocialIdentity> identity =
        identityRepository.findByProviderAndExternalId(domainId, SocialProvider.STRAVA, athleteId);

    User user;
    if (identity.isPresent()) {
      // Case (a): already bound.
      user =
          userRepository
              .findActiveById(identity.get().getUser().getId())
              .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_NO_ACCOUNT));
      identity.get().recordLogin();
    } else {
      // Case (b): a migrated placeholder account for this athlete.
      String placeholderEmail =
          ("strava_" + athleteId + "@" + placeholderEmailDomain).toLowerCase(Locale.ROOT);
      user =
          userRepository
              .findByEmailAndDomain(domainId, placeholderEmail)
              .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_NO_ACCOUNT));
      identityRepository.persist(
          new UserSocialIdentity(user, domainId, SocialProvider.STRAVA, athleteId));
      Log.infof("Recovered migrated account %d via Strava athlete %s", user.getId(), athleteId);
    }

    user.recordLogin();
    boolean needsEmail = isPlaceholderEmail(user);
    String rawCode = mintLoginCode(user, domainId, needsEmail);

    return returnBaseUrl
        + "/strava/callback?code="
        + urlEncode(rawCode)
        + "&needs_email="
        + (needsEmail ? "1" : "0");
  }

  private String mintLoginCode(User user, Long domainId, boolean needsEmail) {
    String rawCode = generateSecureToken();
    loginCodeRepository.persist(
        new SocialLoginCode(
            user,
            domainId,
            hashToken(rawCode),
            needsEmail,
            Instant.now().plus(Duration.ofSeconds(loginCodeExpirySeconds))));
    return rawCode;
  }

  /** Exchange a one-time login code (from the callback redirect) for a real session. */
  @Public
  @Transactional
  public AuthResult exchangeLoginCode(String code, String userAgent, String ipAddress) {
    Long currentDomainId = domainResolver.getDomainId();
    SocialLoginCode loginCode =
        loginCodeRepository
            .findValidByCodeHash(hashToken(code))
            .orElseThrow(() -> new BadRequestException(ErrorCode.SOCIAL_LOGIN_CODE_INVALID));

    if (!loginCode.getDomainId().equals(currentDomainId)) {
      throw new BadRequestException(ErrorCode.SOCIAL_LOGIN_CODE_INVALID);
    }

    loginCode.markUsed();
    User user =
        userRepository
            .findActiveById(loginCode.getUser().getId())
            .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    user.recordLogin();
    return authService.createAuthResult(user, userAgent, ipAddress);
  }

  /** Unlink Strava from the current user, refusing to strip their only usable login method. */
  @Logged
  @Transactional
  public void unlinkStrava() {
    User user = pedalonsContext.getUser();
    UserSocialIdentity identity =
        identityRepository
            .findByUserAndProvider(user.getId(), SocialProvider.STRAVA)
            .orElseThrow(() -> new BadRequestException(ErrorCode.SOCIAL_IDENTITY_ALREADY_LINKED));

    // If the user has no password and no verified email, Strava is their only way in.
    if (user.getPasswordHash() == null && !user.isEmailVerified()) {
      throw new BusinessException(ErrorCode.SOCIAL_LAST_LOGIN_METHOD);
    }

    identityRepository.delete(identity);
    Log.infof("User %d unlinked Strava", user.getId());
  }

  private void requireConfigured() {
    if (!credentialService.isStravaConfigured()) {
      throw new BusinessException(ErrorCode.SOCIAL_SERVICE_NOT_CONFIGURED);
    }
  }

  /** True when the email is a migration-synthesized placeholder (undeliverable, unverified). */
  private boolean isPlaceholderEmail(User user) {
    if (user.isEmailVerified()) {
      return false;
    }
    String email = user.getEmail().toLowerCase(Locale.ROOT);
    int at = email.indexOf('@');
    if (at < 0) {
      return false;
    }
    String local = email.substring(0, at);
    String host = email.substring(at + 1);
    return host.equals(placeholderEmailDomain.toLowerCase(Locale.ROOT))
        && (local.startsWith("strava_")
            || local.startsWith("facebook_")
            || local.startsWith("google_"));
  }

  private String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
