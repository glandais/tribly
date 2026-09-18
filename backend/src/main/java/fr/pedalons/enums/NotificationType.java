package fr.pedalons.enums;

import static fr.pedalons.enums.NotificationChannel.EMAIL;
import static fr.pedalons.enums.NotificationChannel.IN_APP;
import static fr.pedalons.enums.NotificationChannel.PUSH;

import java.util.EnumSet;
import java.util.Set;

/**
 * The closed set of things a member can be notified about.
 *
 * <p>Stored by name in {@code notification_events.type} and {@code notification_preferences.type}:
 * renaming a constant is a data migration, adding one is not. Each constant has exactly one event
 * record in {@code fr.pedalons.service.notification.event}, and the resolver switches exhaustively
 * over those records — a type added here without its handling does not compile.
 *
 * <p>{@link #defaultChannels} is what a member receives until they touch their preferences. Only
 * the overrides are stored, so changing a default here changes it for everyone who never did.
 */
public enum NotificationType {
  /** A ride became visible to the team: created published, published from draft, or auto-published. */
  RIDE_PUBLISHED(EnumSet.of(IN_APP, PUSH)),
  /** A ride the recipient is registered to was cancelled. */
  RIDE_CANCELLED(EnumSet.of(IN_APP, EMAIL, PUSH)),
  /** A trip became visible to the team. Rarer and weightier than a ride, hence the e-mail. */
  TRIP_PUBLISHED(EnumSet.of(IN_APP, EMAIL, PUSH)),
  /** A trip the recipient is registered to was cancelled. */
  TRIP_CANCELLED(EnumSet.of(IN_APP, EMAIL, PUSH)),
  /** A post became visible to the team. */
  POST_PUBLISHED(EnumSet.of(IN_APP)),
  /** Someone replied to the recipient's comment. */
  COMMENT_REPLY(EnumSet.of(IN_APP, PUSH));

  private final Set<NotificationChannel> defaultChannels;

  NotificationType(Set<NotificationChannel> defaultChannels) {
    this.defaultChannels = Set.copyOf(defaultChannels);
  }

  public boolean isEnabledByDefault(NotificationChannel channel) {
    return defaultChannels.contains(channel);
  }
}
