package fr.pedalons.dto.moderation.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "A report of a comment, a publication, an ad, a route or a member")
@ValidateSchema
public record ReportRequest(
    @Schema(
            description =
                "The team the target belongs to. Its organizers and administrators moderate the"
                    + " report.",
            required = true)
        @NotBlank
        String teamSlug,
    @Schema(description = "What is reported", required = true) ReportTargetType targetType,
    @Schema(
            description =
                "ID (TSID) of the comment, publication, ad or route — or of the user for a MEMBER",
            required = true)
        @NotBlank
        String targetId,
    @Schema(description = "Why", required = true) ReportReason reason,
    @Nullable
        @Schema(description = "Optional free text for the moderators, up to 500 characters")
        @Size(max = 500)
        String message) {}
