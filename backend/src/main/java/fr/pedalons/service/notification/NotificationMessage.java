package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationSubjectType;
import fr.pedalons.enums.NotificationType;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * Everything a channel sender needs to deliver one notification, as plain values.
 *
 * <p>Holds no entity: senders run outside any transaction (a provider call must not hold a JDBC
 * connection), and a detached proxy there would fail on first touch.
 */
public record NotificationMessage(
    Long deliveryId,
    NotificationChannel channel,
    NotificationType type,
    String recipientEmail,
    String recipientName,
    @Nullable String recipientLanguage,
    @Nullable String recipientTimezone,
    @Nullable String actorName,
    String teamSlug,
    String teamName,
    NotificationSubjectType subjectType,
    String subjectSlug,
    String subjectName,
    @Nullable Instant subjectDateTime,
    @Nullable String excerpt,
    String baseUrl,
    String siteName) {

  /** The page the notification opens, on the site of the recipient's team. */
  public String subjectUrl() {
    return baseUrl + NotificationLinks.subjectPath(subjectType, teamSlug, subjectSlug);
  }

  public String preferencesUrl() {
    return baseUrl + NotificationLinks.PREFERENCES_PATH;
  }
}
