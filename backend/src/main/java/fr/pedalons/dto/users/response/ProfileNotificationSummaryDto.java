package fr.pedalons.dto.users.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.NotificationChannel;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The notification shortcut's state line. {@code channels} is the same list as {@code GET
 * /api/notifications/preferences}: a channel the server cannot deliver on (e-mail turned off, no
 * push credentials) is absent from both lists, and the digest then reads false.
 */
@Schema(description = "Where the current user's notifications go, summed up")
@ValidateSchema
public record ProfileNotificationSummaryDto(
    @Schema(
            description = "Channels that can be configured on this server, in display order",
            required = true)
        List<NotificationChannel> channels,
    @Schema(
            description =
                "Among channels, those on which at least one notification type is turned on for"
                    + " the user (their choices, or the defaults they never changed)",
            required = true)
        List<NotificationChannel> enabledChannels,
    @Schema(
            description =
                "Whether non-urgent e-mails are held for a daily digest. Always false when EMAIL is"
                    + " not among channels.",
            required = true)
        boolean emailDigest) {}
