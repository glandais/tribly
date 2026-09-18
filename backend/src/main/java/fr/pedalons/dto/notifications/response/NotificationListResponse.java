package fr.pedalons.dto.notifications.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "A page of the current user's notifications, newest first")
@ValidateSchema
public record NotificationListResponse(
    @Schema(description = "The notifications of this page", required = true)
        List<NotificationDto> items,
    @Schema(
            description = "How many notifications match the query (unread only, if asked)",
            required = true)
        long total,
    @Schema(description = "How many notifications are unread, whatever the filter", required = true)
        long unreadCount,
    @Schema(description = "Page number (0-indexed)", required = true) int page,
    @Schema(description = "Page size applied", required = true) int size) {}
