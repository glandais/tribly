package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/** A ride became visible to its team. */
public record RidePublished(long rideId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.RIDE_PUBLISHED;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + rideId;
  }
}
