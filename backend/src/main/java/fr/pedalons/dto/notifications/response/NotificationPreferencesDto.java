package fr.pedalons.dto.notifications.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.NotificationChannel;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The preference matrix — every type on every channel the server can deliver on today — plus the
 * per-team mute switches and the e-mail digest. A channel
 * that is not implemented or not enabled is absent, so a client never renders a dead switch. The
 * inbox is not a channel here: it always receives everything.
 */
@Schema(description = "The current user's notification preferences")
@ValidateSchema
public record NotificationPreferencesDto(
    @Schema(
            description = "Channels that can be configured on this server, in display order",
            required = true)
        List<NotificationChannel> channels,
    @Schema(description = "One cell per type and configurable channel", required = true)
        List<NotificationPreferenceDto> preferences,
    @Schema(
            description =
                "The user's teams on this site, each with its mute switch. Offered whatever the"
                    + " channels: muting also keeps the team's announcements out of the inbox.",
            required = true)
        List<NotificationTeamPreferenceDto> teams,
    @Schema(
            description =
                "Non-urgent e-mails are held and sent as one digest a day, at 7:00 in the user's"
                    + " time zone. Cancellations, changes and reminders still leave at once. Only"
                    + " meaningful when EMAIL is among the channels.",
            required = true)
        boolean emailDigest) {}
