package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/**
 * An invitation to a team was sent. Queued for every invitation, whether or not an account holds
 * the address: the resolver looks the account up, off the request, so the inviter learns nothing
 * about who has one.
 */
public record TeamInvited(long invitationId) implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.TEAM_INVITATION;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + invitationId;
  }
}
