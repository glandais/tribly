package fr.pedalons.service.user;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Nightly retention sweep for GDPR data exports.
 *
 * <p>Separate from the auth and asset sweeps because {@link UserExportService#retryStuckJobs()} is
 * job-queue recovery, which has no analogue in either of those.
 */
@ApplicationScoped
public class UserExportCleanupScheduler {

  private static final Logger LOG = Logger.getLogger(UserExportCleanupScheduler.class);

  @Inject UserExportService userExportService;

  /** 04:30, after the GPX preview purge at 04:00. */
  @Scheduled(cron = "0 30 4 * * ?")
  void cleanupExports() {
    int expired = userExportService.purgeExpired();
    int stuck = userExportService.retryStuckJobs();
    long deleted = userExportService.deleteOldRows();
    if (expired > 0 || stuck > 0 || deleted > 0) {
      LOG.infof(
          "Export cleanup: %d expired, %d stuck jobs recovered, %d rows deleted",
          expired, stuck, deleted);
    }
  }
}
