package fr.pedalons.service.migration;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Triggers the biketeam → tribly migration once at application startup, only when {@code
 * pedalons.migration.biketeam.enabled=true}. Failures are logged but do NOT abort boot.
 *
 * <p>Unless {@code exit-when-done} says otherwise, the application then stops: this is a one-shot
 * job, and its container has no reason to linger. The exit code tells a `docker compose run` whether
 * every team made it through.
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
    LOG.infof("Biketeam migration starting (team=%s)", config.getTeamId().orElse("<all>"));
    int exitCode = 0;
    try {
      int failed = service.run();
      if (failed > 0) {
        exitCode = 1;
        LOG.errorf("Biketeam migration completed, %d team(s) failed", failed);
      } else {
        LOG.info("Biketeam migration completed");
      }
    } catch (Exception e) {
      exitCode = 1;
      LOG.error("Biketeam migration failed", e);
    }
    if (config.isExitWhenDone()) {
      LOG.infof("Stopping with exit code %d", exitCode);
      // Safe from a StartupEvent observer: exit() raises `shutdownRequested` under the same lock
      // waitForExit() reads it under, so the main thread cannot miss the signal by arriving late.
      Quarkus.asyncExit(exitCode);
    }
  }
}
