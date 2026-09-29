package fr.pedalons.dto.moderation.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ModerationAction;
import fr.pedalons.enums.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

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
        ModerationAction action,
    @Nullable
        @Schema(
            description =
                "Slug of the team the reports were filed in — the item's teamSlug. A member can be"
                    + " reported in several teams, one queue item per team: on the platform queue,"
                    + " this decides that item only. Omitted there, the decision applies to the"
                    + " target's open reports in every team. Ignored by a team's queue, whose path"
                    + " names the team.",
            examples = "velo-club")
        String teamSlug) {

  /** Without a team: the shape this record had before MOD-1 (docs/LEDGER_*.md MOD-1). */
  public ModerationDecisionRequest(
      ReportTargetType targetType, String targetId, ModerationAction action) {
    this(targetType, targetId, action, null);
  }
}
