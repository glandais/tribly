package fr.pedalons.dto.notifications.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.notification.Notification;
import fr.pedalons.domain.notification.NotificationEventEntry;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.NotificationSubjectType;
import fr.pedalons.enums.NotificationType;
import java.time.Instant;
import java.util.Objects;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * One inbox entry.
 *
 * <p>Structured, not pre-rendered: the client picks wording, icon and route from {@code type} and
 * {@code subjectType}, in its own language — the same contract as the error codes. The fields
 * describe the subject as it was when the notification was sent; the slugs still resolve after a
 * rename, through the slug redirects.
 */
@Schema(description = "A notification in the current user's inbox")
@ValidateSchema
public record NotificationDto(
    @Schema(description = "Notification identifier", required = true) String id,
    @Schema(description = "What happened", required = true) NotificationType type,
    @Schema(description = "Whether the user has read it", required = true) boolean read,
    @Schema(description = "When it was created", required = true) Instant createdAt,
    @Schema(
            description =
                "Display name of whoever caused it, when someone did (a scheduled publication has"
                    + " no actor)")
        @Nullable String actorName,
    @Schema(description = "Slug of the team it happened in", required = true) String teamSlug,
    @Schema(description = "Name of the team it happened in", required = true) String teamName,
    @Schema(description = "Kind of page the notification opens", required = true)
        NotificationSubjectType subjectType,
    @Schema(description = "Slug of the ride, trip, post or route", required = true)
        String subjectSlug,
    @Schema(description = "Name of the ride, trip, post or route", required = true)
        String subjectName,
    @Schema(description = "Date of the ride or trip, publication date of a post")
        @Nullable Instant subjectDateTime,
    @Schema(description = "A short quote — the reply, for COMMENT_REPLY")
        @Nullable String excerpt) {

  /** Expects the event join-fetched, and fanned out — only then is its snapshot set. */
  public static NotificationDto from(Notification notification) {
    NotificationEventEntry event = notification.getEvent();
    return new NotificationDto(
        TsidUtils.toString(notification.getId()),
        notification.getType(),
        notification.getReadAt() != null,
        notification.getCreatedAt(),
        event.getActorName(),
        Objects.requireNonNull(event.getTeamSlug()),
        Objects.requireNonNull(event.getTeamName()),
        Objects.requireNonNull(event.getSubjectType()),
        Objects.requireNonNull(event.getSubjectSlug()),
        Objects.requireNonNull(event.getSubjectName()),
        event.getSubjectDateTime(),
        event.getExcerpt());
  }
}
