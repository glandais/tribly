package fr.pedalons.service.asset;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Runs {@link AssetSizeBackfill} every minute. docs/LEDGER_*.md API-7.
 *
 * <p>The cursor only moves forward: an asset whose file is missing is looked at once per process,
 * not once a minute forever. The rows a previous release writes during a rolling deploy come after
 * it all the same — TSIDs grow with time. Once the first pass is done, each run is one query on an
 * empty range of a partial index.
 */
@ApplicationScoped
public class AssetSizeBackfillScheduler {

  static final int BATCH_SIZE = 200;

  @Inject AssetSizeBackfill backfill;

  /** The highest asset id looked at. In memory only: a restart makes one full pass again. */
  private long cursor = 0;

  @Scheduled(every = "1m", delayed = "1m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void run() {
    cursor = backfill.processBatch(cursor, BATCH_SIZE).lastId();
  }
}
