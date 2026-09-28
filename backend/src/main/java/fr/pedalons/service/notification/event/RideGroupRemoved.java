package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;
import java.util.List;

/**
 * A group was removed from a published ride while riders were registered to it: their
 * registrations went with it. Unlike the other records it carries more than an identifier — by the
 * time the dispatcher reads it the group and its registrations are gone, so the group's name and
 * who was registered have to travel with the event.
 */
public record RideGroupRemoved(long rideId, long groupId, String groupName, List<Long> userIds)
    implements NotificationEvent {

  @Override
  public NotificationType type() {
    return NotificationType.RIDE_GROUP_REMOVED;
  }

  @Override
  public String dedupKey() {
    return type().name() + ":" + groupId;
  }
}
