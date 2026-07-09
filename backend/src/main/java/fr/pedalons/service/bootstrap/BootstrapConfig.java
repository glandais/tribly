package fr.pedalons.service.bootstrap;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class BootstrapConfig {

  @ConfigProperty(name = "pedalons.bootstrap.enabled", defaultValue = "true")
  boolean enabled;

  @ConfigProperty(name = "pedalons.bootstrap.domain", defaultValue = "")
  String domain;

  @ConfigProperty(name = "pedalons.bootstrap.domain-name", defaultValue = "")
  String domainName;

  @ConfigProperty(name = "pedalons.bootstrap.base-url", defaultValue = "")
  String baseUrl;

  @ConfigProperty(name = "pedalons.bootstrap.admin-email", defaultValue = "")
  String adminEmail;

  @ConfigProperty(name = "pedalons.bootstrap.admin-display-name", defaultValue = "")
  String adminDisplayName;

  public boolean enabled() {
    return enabled;
  }

  public String domain() {
    return domain;
  }

  public String domainName() {
    return domainName.isBlank() ? domain : domainName;
  }

  public String baseUrl() {
    return baseUrl.isBlank() ? "https://" + domain : baseUrl;
  }

  public String adminEmail() {
    return adminEmail;
  }

  public String adminDisplayName() {
    return adminDisplayName.isBlank() ? adminEmail : adminDisplayName;
  }
}
