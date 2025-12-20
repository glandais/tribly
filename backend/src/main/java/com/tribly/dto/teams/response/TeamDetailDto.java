package com.tribly.dto.teams.response;

import com.tribly.domain.team.Team;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.team.response.TeamAndRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Detailed team information")
public record TeamDetailDto(
    @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Team name", required = true) String name,
    @Schema(description = "Team URL slug", required = true) String slug,
    @Nullable @Schema(description = "Team description", nullable = true) String description,
    @Nullable @Schema(description = "Logo image URL", nullable = true) String logoUrl,
    @Nullable @Schema(description = "Cover image URL", nullable = true) String coverImageUrl,
    @Schema(description = "Whether the team is public", required = true) Visibility visibility,
    @Schema(description = "Number of team members", required = true) long memberCount,
    @Nullable @Schema(description = "Maximum number of members (null = unlimited)", nullable = true)
        Integer maxMembers,
    @Nullable @Schema(description = "Current user's role (null if not a member)", nullable = true)
        TeamRole role,
    @Schema(description = "Team creation timestamp", required = true) String createdAt) {
  public static TeamDetailDto from(TeamAndRole teamAndRole) {
    Team team = teamAndRole.team();
    return new TeamDetailDto(
        TsidUtils.toString(team.getId()),
        team.getName(),
        team.getSlug(),
        team.getDescription(),
        team.getLogoUrl(),
        team.getCoverImageUrl(),
        team.getVisibility(),
        teamAndRole.memberCount(),
        team.getMaxMembers(),
        teamAndRole.teamRole(),
        team.getCreatedAt().toString());
  }
}
