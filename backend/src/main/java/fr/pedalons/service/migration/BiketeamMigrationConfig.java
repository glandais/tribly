package fr.pedalons.service.migration;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * The target domain and the platform admin are not configured here — they belong to {@code
 * pedalons.bootstrap.*}, which the migration reuses through {@code BootstrapService}.
 */
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

  // Optional, not defaultValue="": SmallRye converts an empty String back to null and refuses to
  // inject it. Absent when the migration runs without the biketeam data export.
  @ConfigProperty(name = "pedalons.migration.biketeam.data-dir")
  Optional<String> dataDir;

  /** Mail domain of the placeholder addresses given to biketeam accounts that had no email. */
  @ConfigProperty(
      name = "pedalons.migration.biketeam.placeholder-email-domain",
      defaultValue = "pedalons.fr")
  String placeholderEmailDomain;

  /** Empty when the biketeam data export isn't mounted — GPX tracks and images are then skipped. */
  public String getDataDir() {
    return dataDir.orElse("");
  }
}
