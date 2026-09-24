package fr.pedalons.dto.moderation.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "A moderation queue: every open target, or the 100 most recently decided ones, one item"
            + " per target")
@ValidateSchema
public record ModerationQueueResponse(
    @Schema(description = "One item per reported target", required = true)
        List<ModerationItemDto> items,
    @Schema(description = "How many items", required = true) int total) {}
