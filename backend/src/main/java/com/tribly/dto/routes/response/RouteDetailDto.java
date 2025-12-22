package com.tribly.dto.routes.response;

import com.tribly.domain.route.Route;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * Route DTO for detail view with full information.
 */
@Schema(description = "Detailed route information")
public record RouteDetailDto(
    @Schema(description = "Route ID (TSID)", required = true) String id,
    @Schema(description = "Route name", required = true) String name,
    @Nullable @Schema(description = "Route description", required = true) String description,
    @Schema(description = "Distance in meters", required = true) Integer distance,
    @Schema(description = "Total elevation gain in meters", required = true) Integer elevationGain,
    @Schema(description = "Total elevation loss in meters", required = true) Integer elevationLoss,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is public", required = true) Visibility visibility,
    @Schema(description = "Start point latitude", required = true) BigDecimal startLat,
    @Schema(description = "Start point longitude", required = true) BigDecimal startLng,
    @Schema(description = "End point latitude", required = true) BigDecimal endLat,
    @Schema(description = "End point longitude", required = true) BigDecimal endLng,
    @Schema(description = "Creator user ID (TSID)", required = true) String createdById,
    @Schema(description = "Creation timestamp", required = true) String createdAt,
    @Schema(description = "Last update timestamp", required = true) String updatedAt) {
  public static RouteDetailDto from(Route route) {
    return new RouteDetailDto(
        TsidUtils.toString(route.getId()),
        route.getName(),
        route.getDescription(),
        route.getDistance(),
        route.getElevationGain(),
        route.getElevationLoss(),
        route.getSurfaceType(),
        route.getVisibility(),
        route.getStartLat(),
        route.getStartLng(),
        route.getEndLat(),
        route.getEndLng(),
        TsidUtils.toString(route.getCreatedBy().getId()),
        route.getCreatedAt().toString(),
        route.getUpdatedAt().toString());
  }
}
