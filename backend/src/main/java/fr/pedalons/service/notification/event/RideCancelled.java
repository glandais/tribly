package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/** A ride was cancelled; its registered riders are told. */
public record RideCancelled(long rideId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.RIDE_CANCELLED;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + rideId;
  }
}
