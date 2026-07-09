package fr.pedalons.service.migration;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Triggers the biketeam → tribly migration once at application startup, only when
 * {@code pedalons.migration.biketeam.enabled=true}. Failures are logged but do NOT abort boot.
 */
@ApplicationScoped
public class BiketeamMigrationRunner {

  private static final Logger LOG = Logger.getLogger(BiketeamMigrationRunner.class);

  @Inject BiketeamMigrationConfig config;
  @Inject BiketeamMigrationService service;

  void onStart(@Observes StartupEvent ev) {
    if (!config.isEnabled()) {
      LOG.debug("Biketeam migration disabled");
      return;
    }
    LOG.infof(
        "Biketeam migration starting (sourceTeam=%s → domain=%s teamSlug=%s)",
        config.getSourceTeamId(), config.getTargetDomain(), config.getTargetTeamSlug());
    try {
      service.run();
      LOG.info("Biketeam migration completed");
    } catch (Exception e) {
      LOG.error("Biketeam migration failed", e);
    }
  }
}
