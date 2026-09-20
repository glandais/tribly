package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationChannel;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/** Drives stages 2 and 3 of the notification pipeline, their recovery and their housekeeping. */
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

  /**
   * Picks up what a crash — a deploy mid-tick — left claimed. Every few minutes rather than nightly:
   * a cancellation stuck overnight would be dropped as past once recovered.
   */
  @Scheduled(every = "5m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void recoverStuck() {
    try {
      int events = dispatchService.recoverStuck();
      int deliveries = deliveryService.recoverStuck();
      if (events > 0 || deliveries > 0) {
        LOG.warnf(
            "Notification recovery: %d stuck event(s), %d stuck delivery(ies) requeued",
            events, deliveries);
      }
    } catch (Exception e) {
      LOG.error("Notification recovery failed", e);
    }
  }

  @Scheduled(cron = "0 15 4 * * ?")
  void housekeeping() {
    try {
      int purged = retentionService.purgeExpired();
      LOG.infof("Notification housekeeping: %d expired event(s) purged", purged);
    } catch (Exception e) {
      LOG.error("Notification housekeeping failed", e);
    }
  }
}
