package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/** A reply was posted; the author of the comment it answers is told. Keyed by the reply. */
public record CommentReplied(long commentId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.COMMENT_REPLY;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + commentId;
  }
}
