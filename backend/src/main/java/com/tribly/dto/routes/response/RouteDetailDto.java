package com.tribly.dto.routes.response;

import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.RouteClimb;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.AssetService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Route DTO for detail view with full information.
 */
@Schema(description = "Detailed route information")
public record RouteDetailDto(
    @Schema(description = "Route ID (TSID)", required = true) String id,
    @Schema(description = "Route slug", required = true) String slug,
    @Schema(description = "Route name", required = true) String name,
    @Schema(description = "Media", required = true) MediaDto media,
    @Schema(description = "Distance in meters", required = true) Integer distance,
    @Schema(description = "Total elevation gain in meters", required = true) Integer elevationGain,
    @Schema(description = "Total elevation loss in meters", required = true) Integer elevationLoss,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is public", required = true) Visibility visibility,
    @Schema(description = "Start point latitude", required = true) BigDecimal startLat,
    @Schema(description = "Start point longitude", required = true) BigDecimal startLng,
    @Schema(description = "End point latitude", required = true) BigDecimal endLat,
    @Schema(description = "End point longitude", required = true) BigDecimal endLng,
    @Schema(description = "Creator user", required = true) PublicUserDto createdBy,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt,
    @Schema(description = "Last update timestamp", required = true) Instant updatedAt,
    @Schema(description = "List of climbs on the route", required = true)
        List<RouteClimbDto> climbs,
    @Schema(description = "Geometry details", required = true) GpxTrackDto track) {
  public static RouteDetailDto from(
      Route route, List<RouteClimb> climbs, GpxTrack track, AssetService assetService) {
    List<RouteClimbDto> routeClimbDtos = climbs.stream().map(RouteClimbDto::from).toList();

    return new RouteDetailDto(
        TsidUtils.toString(route.getId()),
        route.getSlug(),
        route.getName(),
        MediaDto.from(route, assetService),
        route.getDistance(),
        route.getElevationGain(),
        route.getElevationLoss(),
        route.getSurfaceType(),
        route.getVisibility(),
        route.getStartLat(),
        route.getStartLng(),
        route.getEndLat(),
        route.getEndLng(),
        PublicUserDto.from(route.getCreatedBy()),
        route.getCreatedAt(),
        route.getUpdatedAt(),
        routeClimbDtos,
        GpxTrackDto.from(track));
  }
}
