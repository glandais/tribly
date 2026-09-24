package fr.pedalons.dto.moderation.response;

import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportStatus;
import fr.pedalons.enums.ReportTargetType;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "One reported target in a moderation queue, with all its reports grouped")
@ValidateSchema
public record ModerationItemDto(
    @Schema(description = "Type of the reported target", required = true)
        ReportTargetType targetType,
    @Schema(description = "ID (TSID) of the reported target", required = true) String targetId,
    @Schema(description = "Slug of the team the reports were filed in", required = true)
        String teamSlug,
    @Schema(description = "Name of that team", required = true) String teamName,
    @Schema(
            description = "Who the moderation is about: the author of the content, or the member",
            required = true)
        PublicUserDto targetUser,
    @Nullable
        @Schema(
            description =
                "Name of the publication — of the commented one for a comment. Null for a member,"
                    + " or when the content is gone.")
        String contentName,
    @Nullable
        @Schema(
            description =
                "Type of the content to open: the publication itself, or the one a comment is on"
                    + " (POST, RIDE, TRIP, ROUTE or AD). Null for a member, or when the content is"
                    + " gone.")
        ReportTargetType contentType,
    @Nullable @Schema(description = "Slug of the content to open, with contentType")
        String contentSlug,
    @Nullable
        @Schema(
            description =
                "The reported text as it was when first reported (comment text, name and start of"
                    + " the description, or member name)")
        String excerpt,
    @Schema(description = "How many reports this target gathered", required = true) int reportCount,
    @Schema(description = "The distinct reasons given", required = true) List<ReportReason> reasons,
    @Schema(description = "The non-empty free texts of the reports", required = true)
        List<String> messages,
    @Schema(description = "When the first report was filed", required = true)
        Instant firstReportedAt,
    @Schema(description = "When the last report was filed", required = true) Instant lastReportedAt,
    @Schema(
            description =
                "Whether the content is currently hidden from members, having gathered enough"
                    + " reports",
            required = true)
        boolean hidden,
    @Schema(
            description =
                "OPEN while waiting; REMOVED or DISMISSED once decided (the latest decision)",
            required = true)
        ReportStatus status,
    @Nullable
        @Schema(
            description =
                "Who reported. Only in the platform queue: always null in a team's queue, where"
                    + " reporters stay anonymous.")
        List<PublicUserDto> reporters) {}
