package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/** A trip became visible to its team. */
public record TripPublished(long tripId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.TRIP_PUBLISHED;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + tripId;
  }
}
