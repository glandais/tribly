package com.tribly.dto.routes.response;

import com.tribly.domain.route.Route;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.AssetService;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Route DTO for list view.
 */
@Schema(description = "Route summary data")
@ValidateSchema
public record RouteDto(
    @Schema(description = "Route ID (TSID)", required = true) String id,
    @Schema(description = "Route slug", required = true) String slug,
    @Schema(description = "Team", required = true) TeamPublicationDto team,
    @Schema(description = "Route name", required = true) String name,
    @Schema(description = "Route description", required = true) MediaDto media,
    @Schema(description = "Distance in meters", required = true) Integer distance,
    @Schema(description = "Total elevation gain in meters", required = true) Integer elevationGain,
    @Schema(description = "Total elevation loss in meters", required = true) Integer elevationLoss,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is public", required = true) Visibility visibility,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt) {
  public static RouteDto from(Route route, AssetService assetService) {
    return new RouteDto(
        TsidUtils.toString(route.getId()),
        route.getSlug(),
        TeamPublicationDto.from(route.getTeam()),
        route.getName(),
        MediaDto.from(route, assetService),
        route.getDistance(),
        route.getElevationGain(),
        route.getElevationLoss(),
        route.getSurfaceType(),
        route.getVisibility(),
        route.getCreatedAt());
  }
}
