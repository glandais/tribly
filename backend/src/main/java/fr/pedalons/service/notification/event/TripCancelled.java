package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/** A trip was cancelled; its registered riders are told. */
public record TripCancelled(long tripId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.TRIP_CANCELLED;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + tripId;
  }
}
