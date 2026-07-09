package fr.pedalons.service.migration;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
public class BiketeamMigrationConfig {

  @ConfigProperty(name = "pedalons.migration.biketeam.enabled", defaultValue = "false")
  boolean enabled;

  @ConfigProperty(name = "pedalons.migration.biketeam.team-id", defaultValue = "")
  String sourceTeamId;

  @ConfigProperty(name = "pedalons.migration.biketeam.target-domain", defaultValue = "")
  String targetDomain;

  @ConfigProperty(name = "pedalons.migration.biketeam.target-domain-name", defaultValue = "")
  String targetDomainName;

  @ConfigProperty(name = "pedalons.migration.biketeam.target-domain-base-url", defaultValue = "")
  String targetDomainBaseUrl;

  @ConfigProperty(name = "pedalons.migration.biketeam.target-team-slug", defaultValue = "")
  String targetTeamSlug;

  @ConfigProperty(name = "pedalons.migration.biketeam.data-dir", defaultValue = "")
  String dataDir;

  @ConfigProperty(name = "pedalons.migration.biketeam.admin-email", defaultValue = "")
  String adminEmail;

  /** Falls back to the hostname when left unset. */
  public String getTargetDomainName() {
    return targetDomainName.isBlank() ? targetDomain : targetDomainName;
  }

  /** Falls back to {@code https://{targetDomain}} when left unset. */
  public String getTargetDomainBaseUrl() {
    return targetDomainBaseUrl.isBlank() ? "https://" + targetDomain : targetDomainBaseUrl;
  }
}
