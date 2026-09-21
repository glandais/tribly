package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/** A top-level comment was posted; the author of what it comments on is told. */
public record CommentOnPublication(long commentId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.COMMENT_ON_MY_PUBLICATION;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + commentId;
  }
}
