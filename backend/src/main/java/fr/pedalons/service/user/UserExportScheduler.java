package fr.pedalons.service.user;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/** Drains the GDPR data-export queue. */
@ApplicationScoped
public class UserExportScheduler {

  private static final Logger LOG = Logger.getLogger(UserExportScheduler.class);

  @Inject UserExportService userExportService;

  /**
   * One job per tick, so a single heavy export cannot monopolise the worker.
   *
   * <p>{@code SKIP} means a build running longer than the interval never overlaps itself — the
   * strongest guard while the deployment is a single replica. The compare-and-set claim inside
   * {@code UserExportService} is what makes it safe with more than one.
   *
   * <p>No {@code @Transactional} here, unlike the other cleanup schedulers: the work below spans
   * minutes of storage I/O and manages its own short transactions.
   */
  @Scheduled(every = "30s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void processPendingExports() {
    try {
      userExportService.processOnePendingExport();
      userExportService.retryPendingEmails();
    } catch (Exception e) {
      // A scheduler that throws stops logging usefully; the job row already records the failure.
      LOG.error("Export scheduler tick failed", e);
    }
  }
}
