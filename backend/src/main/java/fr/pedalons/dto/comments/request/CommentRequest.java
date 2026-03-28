package fr.pedalons.dto.comments.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Comment creation request")
@ValidateSchema
public record CommentRequest(
    @Schema(description = "Comment content", required = true) @NotBlank @Size(max = 5000)
        String content,
    @Nullable @Schema(description = "Parent comment ID for replies (optional)") String parentId) {}
