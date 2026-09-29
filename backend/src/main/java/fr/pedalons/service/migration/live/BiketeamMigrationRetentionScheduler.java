package fr.pedalons.service.migration.live;

import fr.pedalons.repository.migration.BiketeamMigrationJobRepository;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Deletes the {@code biketeam_migrations} rows that ended more than the retention ago (privacy
 * policy §6, one year): finished jobs and lapsed grants. They are the team's migration history and
 * hold the account that confirmed it; nothing else references them, and a re-migration relies on
 * {@code biketeam_migration_map}, which this leaves alone.
 *
 * <p>Separate from {@link BiketeamLiveMigrationWorker}: its jobs stop when the migration is disabled,
 * and the rows it left behind still have to go.
 */
@ApplicationScoped
public class BiketeamMigrationRetentionScheduler {

  @Inject BiketeamMigrationJobRepository jobRepository;

  @ConfigProperty(name = "pedalons.biketeam.retention-days", defaultValue = "365")
  int retentionDays;

  @Scheduled(cron = "0 50 3 * * ?")
  @Transactional
  void purgeEndedMigrations() {
    long purged =
        jobRepository.deleteEndedBefore(Instant.now().minus(Duration.ofDays(retentionDays)));
    if (purged > 0) {
      Log.infof(
          "Biketeam migration purge: %d rows ended over %d days ago deleted",
          purged, retentionDays);
    }
  }
}
