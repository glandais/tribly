package fr.pedalons.service.asset;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Runs {@link AssetMetadataBackfill} every five minutes until no asset is flagged any more — then
 * each run is one query on an empty partial index. docs/LEDGER_*.md API-43.
 */
@ApplicationScoped
public class AssetMetadataBackfillScheduler {

  static final int BATCH_SIZE = 100;

  @Inject AssetMetadataBackfill backfill;

  /** Where the next batch starts; back to 0 once a batch comes back short. In memory only. */
  private long cursor = 0;

  @Scheduled(every = "5m", delayed = "2m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void run() {
    AssetMetadataBackfill.BatchResult result = backfill.processBatch(cursor, BATCH_SIZE);
    cursor = result.processed() < BATCH_SIZE ? 0 : result.lastId();
  }
}
