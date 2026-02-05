package com.tribly.service.asset;

import com.tribly.domain.asset.Asset;
import com.tribly.repository.asset.AssetRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.jboss.logging.Logger;

/**
 * Scheduled job to clean up orphaned assets (uploaded but never attached to an entity).
 *
 * <p>Runs daily at 3:30 AM. Deletes assets where {@code teamEntity} is null and {@code updatedAt}
 * is older than 1 day. Uses entity-level delete so {@code @PreRemove} listener cleans up S3 files.
 */
@ApplicationScoped
public class AssetCleanupScheduler {

  private static final Logger LOG = Logger.getLogger(AssetCleanupScheduler.class);

  @Inject AssetRepository assetRepository;

  @Scheduled(cron = "0 30 3 * * ?")
  @Transactional
  void cleanupOrphanedAssets() {
    Instant cutoff = Instant.now().minus(1, ChronoUnit.DAYS);
    List<Asset> orphaned = assetRepository.findOrphanedAssets(cutoff);

    for (Asset asset : orphaned) {
      assetRepository.delete(asset);
    }

    if (!orphaned.isEmpty()) {
      LOG.infof("Asset cleanup completed: %d orphaned assets deleted", orphaned.size());
    }
  }
}
