package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationChannel;

/**
 * Delivers notifications on one out-of-app channel. One {@code @ApplicationScoped} bean per channel;
 * {@link NotificationChannels} discovers them.
 *
 * <p>{@link #send} runs outside any transaction and may throw: the delivery is then retried with
 * backoff, up to the configured number of attempts.
 */
public interface NotificationChannelSender {

  NotificationChannel channel();

  /**
   * Whether this channel may be used right now — its configuration switch, its credentials. A
   * disabled channel creates no deliveries and is not offered in the preferences.
   */
  boolean isEnabled();

  void send(NotificationMessage message) throws Exception;
}
