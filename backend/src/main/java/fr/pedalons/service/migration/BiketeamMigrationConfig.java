package fr.pedalons.service.migration;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
public class BiketeamMigrationConfig {

  @ConfigProperty(name = "pedalons.migration.biketeam.enabled", defaultValue = "false")
  boolean enabled;

  /**
   * The single biketeam team to migrate; every team is migrated when absent. Biketeam team ids are
   * already slugs, so this doubles as the target tribly team slug.
   */
  @ConfigProperty(name = "pedalons.migration.biketeam.team-id")
  Optional<String> teamId;

  @ConfigProperty(name = "pedalons.migration.biketeam.target-domain", defaultValue = "")
  String targetDomain;

  @ConfigProperty(name = "pedalons.migration.biketeam.target-domain-name", defaultValue = "")
  String targetDomainName;

  @ConfigProperty(name = "pedalons.migration.biketeam.target-domain-base-url", defaultValue = "")
  String targetDomainBaseUrl;

  // Optional, not defaultValue="": SmallRye converts an empty String back to null and refuses to
  // inject it. Absent when the migration runs without the biketeam data export.
  @ConfigProperty(name = "pedalons.migration.biketeam.data-dir")
  Optional<String> dataDir;

  @ConfigProperty(name = "pedalons.migration.biketeam.admin-email", defaultValue = "")
  String adminEmail;

  /** Mail domain of the placeholder addresses given to biketeam accounts that had no email. */
  @ConfigProperty(
      name = "pedalons.migration.biketeam.placeholder-email-domain",
      defaultValue = "pedalons.fr")
  String placeholderEmailDomain;

  /** Empty when the biketeam data export isn't mounted — GPX tracks and images are then skipped. */
  public String getDataDir() {
    return dataDir.orElse("");
  }

  /** Falls back to the hostname when left unset. */
  public String getTargetDomainName() {
    return targetDomainName.isBlank() ? targetDomain : targetDomainName;
  }

  /** Falls back to {@code https://{targetDomain}} when left unset. */
  public String getTargetDomainBaseUrl() {
    return targetDomainBaseUrl.isBlank() ? "https://" + targetDomain : targetDomainBaseUrl;
  }
}
