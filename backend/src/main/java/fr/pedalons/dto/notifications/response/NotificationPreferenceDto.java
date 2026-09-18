package fr.pedalons.dto.notifications.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "One cell of the notification preferences: a type on a channel")
@ValidateSchema
public record NotificationPreferenceDto(
    @Schema(description = "Notification type", required = true) NotificationType type,
    @Schema(description = "Delivery channel", required = true) NotificationChannel channel,
    @Schema(description = "Whether it is delivered, as currently in effect", required = true)
        boolean enabled,
    @Schema(
            description =
                "What applies when the user never touched this cell. Lets a client offer a"
                    + " 'restore defaults' without hard-coding them.",
            required = true)
        boolean enabledByDefault) {}
