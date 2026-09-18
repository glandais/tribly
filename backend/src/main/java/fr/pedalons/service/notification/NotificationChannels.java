package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * The out-of-app channels the server can deliver on right now: a sender bean exists for it and that
 * sender says it is enabled.
 *
 * <p>This is what keeps a half-built channel harmless. {@code PUSH} has a type, defaults and a
 * column value, but no sender yet — so no push delivery is ever queued, and the preferences screen
 * does not offer a switch that would do nothing.
 */
@ApplicationScoped
public class NotificationChannels {

  @Inject Instance<NotificationChannelSender> senders;

  public Set<NotificationChannel> available() {
    Set<NotificationChannel> available = EnumSet.noneOf(NotificationChannel.class);
    for (NotificationChannelSender sender : senders) {
      if (sender.isEnabled()) {
        available.add(sender.channel());
      }
    }
    return available;
  }

  public Optional<NotificationChannelSender> sender(NotificationChannel channel) {
    return senders.stream().filter(s -> s.channel() == channel && s.isEnabled()).findFirst();
  }
}
