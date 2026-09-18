package fr.pedalons.dto.notifications.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Switch one notification type on or off on one channel")
@ValidateSchema
public record NotificationPreferenceUpdate(
    @Schema(description = "Notification type", required = true) @NotNull NotificationType type,
    @Schema(
            description = "Delivery channel. IN_APP is refused: the inbox is always on.",
            required = true)
        @NotNull
        NotificationChannel channel,
    @Schema(description = "Whether to deliver it", required = true) boolean enabled) {}
