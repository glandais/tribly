package fr.pedalons.service.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Builds the {@code refresh_token} cookie. Single source of truth for its attributes, shared by
 * {@code AuthResource}, {@code StravaAuthResource} and {@code PasskeyResource}.
 *
 * <p>The cookie is scoped to {@code /} — not {@code /api} — because the SSR server needs to see it
 * on the HTML document request in order to render the page for the logged-in user. Same reason for
 * {@code SameSite=Lax} (see {@code pedalons.auth.cookie.same-site}): {@code Strict} would withhold
 * the cookie on any navigation coming from another site, and those arrivals would render anonymous.
 *
 * <p>Relaxing to {@code Lax} removes what used to be the only CSRF defence, hence {@link
 * fr.pedalons.service.security.CrossSiteRequestFilter}.
 */
@ApplicationScoped
public class RefreshTokenCookieFactory {

  public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  /** Path the cookie used to be issued on, before SSR needed to read it. */
  private static final String LEGACY_PATH = "/api";

  private static final String PATH = "/";

  @ConfigProperty(name = "pedalons.auth.refresh-token.expiry-days", defaultValue = "30")
  int refreshTokenExpiryDays;

  @ConfigProperty(name = "pedalons.auth.cookie.secure", defaultValue = "true")
  boolean cookieSecure;

  @ConfigProperty(name = "pedalons.auth.cookie.same-site", defaultValue = "lax")
  String cookieSameSite;

  /**
   * The cookies to emit when issuing a session: the token itself, plus the removal of any
   * {@code path=/api} cookie left over from before the path change. Without that removal the
   * browser would send two same-named cookies on {@code /api/*} and {@code @CookieParam} would pick
   * one arbitrarily.
   */
  public NewCookie[] issue(String refreshToken) {
    // Removal first, real cookie last: browsers key cookies by name+path+domain so order is
    // irrelevant to them, but clients that index by name alone (RestAssured, some HTTP libraries)
    // keep the last one — which must be the one carrying the token.
    return new NewCookie[] {legacyRemoval(), build(refreshToken, PATH, expirySeconds())};
  }

  /** The cookies to emit when ending a session — both paths, so nothing survives a logout. */
  public NewCookie[] revoke() {
    return new NewCookie[] {legacyRemoval(), build("", PATH, 0)};
  }

  private NewCookie legacyRemoval() {
    return build("", LEGACY_PATH, 0);
  }

  private int expirySeconds() {
    return refreshTokenExpiryDays * 24 * 60 * 60;
  }

  private NewCookie build(String value, String path, int maxAge) {
    return new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
        .value(value)
        .path(path)
        .maxAge(maxAge)
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(parsedSameSite())
        .build();
  }

  private NewCookie.SameSite parsedSameSite() {
    try {
      return NewCookie.SameSite.valueOf(cookieSameSite.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(
          "Invalid pedalons.auth.cookie.same-site value: '"
              + cookieSameSite
              + "'. Valid values: STRICT, LAX, NONE",
          e);
    }
  }
}
