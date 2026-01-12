package com.tribly.dto.routes.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.route.Route;
import com.tribly.dto.common.GeoJsonPoint;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.service.asset.AssetService;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

/**
 * Route DTO for detail view with full information.
 */
@Schema(description = "Detailed route information")
@ValidateSchema
public record RouteDetailDto(
    @Schema(description = "Route ID (TSID)", required = true) String id,
    @Schema(description = "Route slug", required = true) String slug,
    @Schema(description = "Team", required = true) TeamPublicationDto team,
    @Schema(description = "Route name", required = true) String name,
    @Schema(description = "Media", required = true) MediaDto media,
    @Schema(description = "Distance in meters", required = true) Float distance,
    @Schema(description = "Total elevation gain in meters", required = true) Float elevationGain,
    @Schema(description = "Total elevation loss in meters", required = true) Float elevationLoss,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is public", required = true) Visibility visibility,
    @Schema(implementation = GeoJsonPoint.class) Point<G2D> start,
    @Schema(implementation = GeoJsonPoint.class) Point<G2D> end,
    @Schema(description = "Creator user", required = true) PublicUserDto createdBy,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt,
    @Schema(description = "Last update timestamp", required = true) Instant updatedAt,
    @Schema(description = "Tracks", required = true) List<TrackDto> tracks,
    @Schema(description = "Waypoints", required = true) List<WaypointDto> waypoints) {
  public static RouteDetailDto from(Route route, AssetService assetService) {
    return new RouteDetailDto(
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
        route.getStart(),
        route.getEnd(),
        PublicUserDto.from(route.getCreatedBy()),
        route.getCreatedAt(),
        route.getUpdatedAt(),
        route.getTracks().stream().map(TrackDto::from).toList(),
        route.getWaypoints().stream().map(WaypointDto::from).toList());
  }
}
