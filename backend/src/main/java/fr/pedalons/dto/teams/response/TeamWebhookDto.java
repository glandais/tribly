package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.NotificationDeliveryStatus;
import fr.pedalons.enums.TeamWebhookKind;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * A team's webhook as its administrators see it. The URL is a secret — whoever has a Slack or
 * Discord webhook URL can post to the channel — so it only ever comes back masked.
 */
@Schema(description = "The team's outgoing webhook")
@ValidateSchema
public record TeamWebhookDto(
    @Schema(description = "Whether the team has a webhook at all", required = true)
        boolean configured,
    @Schema(
            description = "The URL, masked: scheme, host and the last characters only",
            examples = "https://hooks.slack.com/…XXXX")
        @Nullable String maskedUrl,
    @Schema(description = "Message format, read from the URL") @Nullable TeamWebhookKind kind,
    @Schema(description = "Language the messages are written in", examples = "fr")
        @Nullable String language,
    @Schema(description = "Whether announcements are posted", required = true) boolean enabled,
    @Schema(description = "Outcome of the latest attempt")
        @Nullable NotificationDeliveryStatus lastStatus,
    @Schema(description = "Why the latest attempt failed, when it did") @Nullable String lastError,
    @Schema(description = "When the latest attempt was made") @Nullable Instant lastAttemptAt) {

  public static TeamWebhookDto none() {
    return new TeamWebhookDto(false, null, null, null, false, null, null, null);
  }
}
