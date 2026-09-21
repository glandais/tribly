package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;
import java.time.Instant;

/**
 * A ride its registered riders are reminded of, the day before. Keyed by the ride <em>and</em> its
 * date: a ride moved to another day earns a reminder of its own, and the resolver drops the one
 * whose date the ride no longer has.
 */
public record RideReminder(long rideId, Instant dateTime) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.RIDE_REMINDER;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + rideId + ":" + dateTime.getEpochSecond();
  }
}
