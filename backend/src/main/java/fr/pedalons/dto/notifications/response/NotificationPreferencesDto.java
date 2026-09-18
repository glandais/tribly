package fr.pedalons.dto.notifications.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.NotificationChannel;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The preference matrix: every type on every channel the server can deliver on today. A channel
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
        List<NotificationPreferenceDto> preferences) {}
