package fr.pedalons.service.social;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Supplies the Strava OAuth application credentials and the base URL they belong to. The decision is
 * a single global Strava app (one client id/secret, one Authorization Callback Domain), so
 * credentials come from configuration rather than per-domain DB rows.
 *
 * <p>The base URL is configurable so the deployment can point at a drop-in proxy (strava-auth-proxy)
 * instead of Strava directly; the proxy speaks the same wire protocol under the same paths, and the
 * credentials configured here are then the virtual ones it issued.
 */
@ApplicationScoped
public class SocialCredentialService {

  private static final String DEFAULT_STRAVA_BASE_URL = "https://www.strava.com";

  @ConfigProperty(name = "pedalons.social.strava.base-url")
  Optional<String> stravaBaseUrl;

  @ConfigProperty(name = "pedalons.social.strava.client-id")
  Optional<String> stravaClientId;

  @ConfigProperty(name = "pedalons.social.strava.client-secret")
  Optional<String> stravaClientSecret;

  public boolean isStravaConfigured() {
    return stravaClientId.filter(s -> !s.isBlank()).isPresent()
        && stravaClientSecret.filter(s -> !s.isBlank()).isPresent();
  }

  /** Base URL of the Strava-compatible endpoint, never with a trailing slash. */
  public String getStravaBaseUrl() {
    return stravaBaseUrl
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(s -> s.endsWith("/") ? s.substring(0, s.length() - 1) : s)
        .orElse(DEFAULT_STRAVA_BASE_URL);
  }

  public Optional<String> getStravaClientId() {
    return stravaClientId.filter(s -> !s.isBlank());
  }

  public Optional<String> getStravaClientSecret() {
    return stravaClientSecret.filter(s -> !s.isBlank());
  }
}
