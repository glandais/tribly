package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/**
 * Someone registered to a group of a ride; its organisers are told. Keyed by the registration, which
 * is deleted when the rider leaves: leaving before the dispatch notifies nobody.
 */
public record RideJoined(long participationId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.RIDE_JOINED;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + participationId;
  }
}
