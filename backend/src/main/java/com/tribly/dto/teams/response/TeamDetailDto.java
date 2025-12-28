package com.tribly.dto.teams.response;

import com.tribly.domain.team.Team;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.AssetService;
import com.tribly.service.team.response.TeamAndRole;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Detailed team information")
public record TeamDetailDto(
    @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Team name", required = true) String name,
    @Schema(description = "Team URL slug", required = true) String slug,
    @Schema(description = "Team description", required = true) MediaDto media,
    @Schema(description = "Whether the team is public", required = true) Visibility visibility,
    @Schema(description = "Number of team members", required = true) long memberCount,
    @Nullable @Schema(description = "Current user's role (null if not a member)") TeamRole role,
    @Schema(description = "Team creation timestamp", required = true) Instant createdAt) {
  public static TeamDetailDto from(TeamAndRole teamAndRole, AssetService assetService) {
    Team team = teamAndRole.team();
    return new TeamDetailDto(
        TsidUtils.toString(team.getId()),
        team.getName(),
        team.getSlug(),
        MediaDto.from(team.getTeamDescription(), assetService),
        team.getVisibility(),
        teamAndRole.memberCount(),
        teamAndRole.teamRole(),
        team.getCreatedAt());
  }
}
