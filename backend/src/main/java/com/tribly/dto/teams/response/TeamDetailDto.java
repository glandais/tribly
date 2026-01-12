package com.tribly.dto.teams.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.team.Team;
import com.tribly.dto.common.GeoJsonPoint;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.pages.response.TeamPageSummaryDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.asset.AssetService;
import com.tribly.service.team.response.TeamAndRole;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@Schema(description = "Detailed team information")
@ValidateSchema
public record TeamDetailDto(
    @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Team name", required = true) String name,
    @Schema(description = "Team URL slug", required = true) String slug,
    @Schema(description = "About page content", required = true) MediaDto about,
    @Schema(description = "Additional team pages") List<TeamPageSummaryDto> pages,
    @Schema(description = "Whether the team is public", required = true) Visibility visibility,
    @Schema(description = "Trips enabled", required = true) boolean enableTrips,
    @Schema(description = "Ads enabled", required = true) boolean enableAds,
    @Schema(description = "Number of team members", required = true) long memberCount,
    @Nullable @Schema(description = "Current user's role (null if not a member)") TeamRole role,
    @Schema(description = "Team creation timestamp", required = true) Instant createdAt,
    @Nullable
        @Schema(
            description = "Team location coordinates [longitude, latitude]",
            implementation = GeoJsonPoint.class)
        Point<G2D> geometry) {
  public static TeamDetailDto from(TeamAndRole teamAndRole, AssetService assetService) {
    Team team = teamAndRole.team();
    List<TeamPageSummaryDto> pages =
        team.getAdditionalPages().stream().map(TeamPageSummaryDto::from).toList();
    return new TeamDetailDto(
        TsidUtils.toString(team.getId()),
        team.getName(),
        team.getSlug(),
        MediaDto.from(team.getAboutPage(), assetService),
        pages,
        team.getVisibility(),
        team.isEnableTrips(),
        team.isEnableAds(),
        teamAndRole.memberCount(),
        teamAndRole.teamRole(),
        team.getCreatedAt(),
        team.getGeometry());
  }
}
