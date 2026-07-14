package fr.pedalons.service.social;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Supplies the Strava OAuth application credentials. The decision is a single global Strava app (one
 * client id/secret, one Authorization Callback Domain), so credentials come from configuration
 * rather than per-domain DB rows.
 */
@ApplicationScoped
public class SocialCredentialService {

  @ConfigProperty(name = "pedalons.social.strava.client-id")
  Optional<String> stravaClientId;

  @ConfigProperty(name = "pedalons.social.strava.client-secret")
  Optional<String> stravaClientSecret;

  public boolean isStravaConfigured() {
    return stravaClientId.filter(s -> !s.isBlank()).isPresent()
        && stravaClientSecret.filter(s -> !s.isBlank()).isPresent();
  }

  public Optional<String> getStravaClientId() {
    return stravaClientId.filter(s -> !s.isBlank());
  }

  public Optional<String> getStravaClientSecret() {
    return stravaClientSecret.filter(s -> !s.isBlank());
  }
}
