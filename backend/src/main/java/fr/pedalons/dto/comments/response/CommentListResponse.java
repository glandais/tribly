package fr.pedalons.dto.comments.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * One answer of {@code GET …/comments}.
 *
 * <p>{@code total} keeps the meaning it always had — every comment on the entity, replies included.
 * Re-pointing it at the paginated items would have been a silent behaviour change for the web
 * client, which shows it as "N commentaires"; the number a paginator needs went into {@code
 * itemTotal} instead.
 */
@Schema(description = "List of comments response")
@ValidateSchema
public record CommentListResponse(
    @Schema(description = "List of comments (top-level only, with nested replies)", required = true)
        List<CommentDto> items,
    @Schema(description = "Total count including replies", required = true) int total,
    @Schema(
            description =
                "How many items exist in the mode that was asked for: top-level comments normally,"
                    + " or replies of the requested parentId. This is what page/size iterate over.",
            required = true)
        int itemTotal,
    @Schema(description = "Page number of items (0-indexed)", required = true) int page,
    @Schema(
            description =
                "Page size applied to items. Equals itemTotal when the caller passed no pagination"
                    + " parameter, since the whole tree is then returned.",
            required = true)
        int size) {}
