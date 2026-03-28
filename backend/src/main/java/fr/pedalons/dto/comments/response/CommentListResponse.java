package fr.pedalons.dto.comments.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "List of comments response")
@ValidateSchema
public record CommentListResponse(
    @Schema(description = "List of comments (top-level only, with nested replies)", required = true)
        List<CommentDto> items,
    @Schema(description = "Total count including replies", required = true) int total) {}
