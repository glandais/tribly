package com.tribly.dto.routes.response;

import com.tribly.domain.route.Route;
import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * Route DTO for list view.
 */
@Schema(description = "Route summary data")
public record RouteDto(
    @Schema(description = "Route ID (TSID)", required = true) String id,
    @Schema(description = "Route name", required = true) String name,
    @Nullable @Schema(description = "Route description", nullable = true) String description,
    @Schema(description = "Distance in meters", required = true) Integer distance,
    @Schema(description = "Total elevation gain in meters", required = true) Integer elevationGain,
    @Schema(description = "Total elevation loss in meters", required = true) Integer elevationLoss,
    @Schema(description = "Route difficulty", required = true) RouteDifficulty difficulty,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is public", required = true) Visibility visibility,
    @Schema(description = "Thumbnail image URL", required = true) String thumbnailUrl,
    @Schema(description = "Creation timestamp", required = true) String createdAt) {
  public static RouteDto from(Route route) {
    return new RouteDto(
        TsidUtils.toString(route.getId()),
        route.getName(),
        route.getDescription(),
        route.getDistance(),
        route.getElevationGain(),
        route.getElevationLoss(),
        route.getDifficulty(),
        route.getSurfaceType(),
        route.getVisibility(),
        route.getThumbnailUrl(),
        route.getCreatedAt().toString());
  }
}
