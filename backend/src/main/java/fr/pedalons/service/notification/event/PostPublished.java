package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/** A post became visible to its team. */
public record PostPublished(long postId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.POST_PUBLISHED;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + postId;
  }
}
