package fr.pedalons.dto.posts.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated post list response")
@ValidateSchema
public record PostListResponse(
    @Schema(description = "List of posts", required = true) List<PostDto> posts,
    @Schema(description = "Total number of posts", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
