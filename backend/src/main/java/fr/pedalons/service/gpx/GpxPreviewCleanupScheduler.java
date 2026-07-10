package fr.pedalons.service.gpx;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Scheduled job that enforces the GPX preview retention window.
 *
 * <p>Runs daily at 4 AM, after the asset cleanup. Deletes previews older than {@link
 * GpxPreviewService#RETENTION_DAYS} days along with their S3 objects.
 */
@ApplicationScoped
public class GpxPreviewCleanupScheduler {

  private static final Logger LOG = Logger.getLogger(GpxPreviewCleanupScheduler.class);

  @Inject GpxPreviewService gpxPreviewService;

  @Scheduled(cron = "0 0 4 * * ?")
  void cleanupExpiredPreviews() {
    int deleted = gpxPreviewService.purgeExpired();
    if (deleted > 0) {
      LOG.infof("GPX preview cleanup completed: %d expired previews deleted", deleted);
    }
  }
}
