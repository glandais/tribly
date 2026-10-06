package fr.pedalons.dto.dashboard.response;

import fr.pedalons.domain.moderation.ContentReport;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportTargetType;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * The reports tile: how many targets wait for a decision, and the latest report. Never says who
 * reported — reporters stay anonymous in a team's queue.
 */
@Schema(
    description =
        "The reports tile of a team dashboard. The full queue is GET"
            + " /api/teams/{teamSlug}/reports?status=OPEN; reporters are never named.")
@ValidateSchema
public record TeamDashboardReportsDto(
    @Schema(
            description =
                "Reported targets waiting for a decision — the number of items of the open queue",
            required = true)
        long openCount,
    @Nullable @Schema(description = "Reason of the most recent open report, null when none")
        ReportReason latestReason,
    @Nullable @Schema(description = "What the most recent open report is about, null when none")
        ReportTargetType latestTargetType,
    @Nullable
        @Schema(
            description =
                "The reported text as it was when the most recent open report was filed, null"
                    + " when none")
        String latestExcerpt,
    @Nullable @Schema(description = "When the most recent open report was filed, null when none")
        Instant latestReportedAt) {

  public static TeamDashboardReportsDto from(long openCount, @Nullable ContentReport latest) {
    if (latest == null) {
      return new TeamDashboardReportsDto(openCount, null, null, null, null);
    }
    return new TeamDashboardReportsDto(
        openCount,
        latest.getReason(),
        latest.getTargetType(),
        latest.getExcerpt(),
        latest.getCreatedAt());
  }
}
