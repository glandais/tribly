package fr.pedalons.service.bootstrap;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * No value is defaulted here: deployments set {@code PEDALONS_BOOTSTRAP_*}, and {@code %dev} sets
 * the local ones. Optional rather than {@code defaultValue = ""}, which SmallRye converts back to
 * null and then refuses to inject.
 */
@ApplicationScoped
public class BootstrapConfig {

  @ConfigProperty(name = "pedalons.bootstrap.enabled", defaultValue = "true")
  boolean enabled;

  @ConfigProperty(name = "pedalons.bootstrap.domain")
  Optional<String> domain;

  @ConfigProperty(name = "pedalons.bootstrap.domain-name")
  Optional<String> domainName;

  @ConfigProperty(name = "pedalons.bootstrap.base-url")
  Optional<String> baseUrl;

  @ConfigProperty(name = "pedalons.bootstrap.admin-email")
  Optional<String> adminEmail;

  @ConfigProperty(name = "pedalons.bootstrap.admin-display-name")
  Optional<String> adminDisplayName;

  public boolean enabled() {
    return enabled;
  }

  public String domain() {
    return domain.orElse("");
  }

  public String domainName() {
    return domainName.filter(s -> !s.isBlank()).orElseGet(this::domain);
  }

  public String baseUrl() {
    return baseUrl.filter(s -> !s.isBlank()).orElseGet(() -> "https://" + domain());
  }

  public String adminEmail() {
    return adminEmail.orElse("");
  }

  public String adminDisplayName() {
    return adminDisplayName.filter(s -> !s.isBlank()).orElseGet(this::adminEmail);
  }
}
