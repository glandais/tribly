package fr.pedalons.dto.moderation.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ModerationAction;
import fr.pedalons.enums.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "A moderator's decision, applied to every open report of one target")
@ValidateSchema
public record ModerationDecisionRequest(
    @Schema(description = "Type of the reported target", required = true)
        ReportTargetType targetType,
    @Schema(description = "ID (TSID) of the reported target", required = true) @NotBlank
        String targetId,
    @Schema(
            description =
                "REMOVE_CONTENT deletes the content (not allowed on a MEMBER); DISMISS keeps it and"
                    + " shows it again if reports had hidden it",
            required = true)
        ModerationAction action) {}
