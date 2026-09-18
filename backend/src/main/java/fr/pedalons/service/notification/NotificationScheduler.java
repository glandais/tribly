package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationChannel;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/** Drives stages 2 and 3 of the notification pipeline, and their nightly housekeeping. */
@ApplicationScoped
public class NotificationScheduler {

  private static final Logger LOG = Logger.getLogger(NotificationScheduler.class);

  /** Events fanned out per tick at most, so a backlog cannot starve the senders below. */
  private static final int MAX_EVENTS_PER_TICK = 20;

  @Inject NotificationDispatchService dispatchService;
  @Inject NotificationDeliveryService deliveryService;
  @Inject NotificationRetentionService retentionService;

  /**
   * {@code SKIP}: a slow tick never overlaps the next. The claims below are what make more than one
   * replica safe, as for the export queue.
   */
  @Scheduled(every = "15s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void tick() {
    try {
      for (int i = 0; i < MAX_EVENTS_PER_TICK && dispatchService.dispatchOne(); i++) {
        // drain
      }
    } catch (Exception e) {
      LOG.error("Notification dispatch tick failed", e);
    }
    for (NotificationChannel channel : NotificationChannel.values()) {
      if (channel == NotificationChannel.IN_APP) {
        continue; // the inbox row is the delivery
      }
      try {
        deliveryService.sendDue(channel);
      } catch (Exception e) {
        LOG.errorf(e, "Notification delivery tick failed on %s", channel);
      }
    }
  }

  @Scheduled(cron = "0 15 4 * * ?")
  void housekeeping() {
    try {
      int events = dispatchService.recoverStuck();
      int deliveries = deliveryService.recoverStuck();
      int purged = retentionService.purgeExpired();
      LOG.infof(
          "Notification housekeeping: %d stuck event(s), %d stuck delivery(ies) requeued, %d"
              + " expired event(s) purged",
          events, deliveries, purged);
    } catch (Exception e) {
      LOG.error("Notification housekeeping failed", e);
    }
  }
}
