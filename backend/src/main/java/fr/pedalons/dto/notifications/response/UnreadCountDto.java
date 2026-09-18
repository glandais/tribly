package fr.pedalons.dto.notifications.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Number of unread notifications — the badge on the bell")
@ValidateSchema
public record UnreadCountDto(
    @Schema(description = "Unread notifications", required = true) long count) {}
