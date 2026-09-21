package fr.pedalons.service.notification;

import fr.pedalons.domain.ride.Ride;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.service.notification.event.RideReminder;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.jboss.logging.Logger;

/**
 * The first scheduled producer: reminds the registered riders of a ride the day before.
 *
 * <p>Every hour, the rides that start 20 to 24 hours from now. The window is four ticks wide so a
 * tick missed to a deploy loses nothing; the dedup key — the ride and its date — is what keeps the
 * four from reminding four times, and what gives a ride moved to another day a reminder of its own.
 * A ride created less than 20 hours before it starts gets none: whoever joins it has just seen it.
 */
@ApplicationScoped
public class RideReminderScheduler {

  private static final Logger LOG = Logger.getLogger(RideReminderScheduler.class);

  static final Duration WINDOW_START = Duration.ofHours(20);
  static final Duration WINDOW_END = Duration.ofHours(24);

  @Inject RideRepository rideRepository;
  @Inject NotificationPublisher publisher;

  @Scheduled(cron = "0 7 * * * ?", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void remind() {
    try {
      int queued = queueReminders(Instant.now());
      if (queued > 0) {
        LOG.infof("Ride reminders: %d ride(s) queued", queued);
      }
    } catch (Exception e) {
      LOG.error("Ride reminders failed", e);
    }
  }

  /** Queues a reminder for every ride due one. Returns how many rides were considered. */
  @Transactional
  public int queueReminders(Instant now) {
    List<Ride> rides = rideRepository.findToRemind(now.plus(WINDOW_START), now.plus(WINDOW_END));
    for (Ride ride : rides) {
      // No actor: nobody did anything, and the organiser registered to their own ride is reminded
      // like everyone else.
      publisher.publish(new RideReminder(ride.getId(), ride.getDateTime()), ride.getTeam(), null);
    }
    return rides.size();
  }
}
