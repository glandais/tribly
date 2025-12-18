package com.tribly.api.routes;

import com.tribly.domain.route.Route;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Route DTO for detail view with full information.
 */
@Schema(description = "Detailed route information")
public record RouteDetailDto(
        @Schema(description = "Route ID (TSID)")
        String id,

        @Schema(description = "Route name")
        String name,

        @Schema(description = "Route description")
        String description,

        @Schema(description = "Distance in meters")
        Integer distance,

        @Schema(description = "Total elevation gain in meters")
        Integer elevationGain,

        @Schema(description = "Total elevation loss in meters")
        Integer elevationLoss,

        @Schema(description = "Route difficulty")
        String difficulty,

        @Schema(description = "Surface type")
        String surfaceType,

        @Schema(description = "Whether the route is public")
        boolean isPublic,

        @Schema(description = "Thumbnail image URL")
        String thumbnailUrl,

        @Schema(description = "Start point latitude")
        BigDecimal startLat,

        @Schema(description = "Start point longitude")
        BigDecimal startLng,

        @Schema(description = "End point latitude")
        BigDecimal endLat,

        @Schema(description = "End point longitude")
        BigDecimal endLng,

        @Schema(description = "Creator user ID (TSID)")
        String createdById,

        @Schema(description = "Creation timestamp")
        String createdAt,

        @Schema(description = "Last update timestamp")
        String updatedAt
) {
    public static RouteDetailDto from(Route route) {
        return new RouteDetailDto(
                TsidUtils.toString(route.getId()),
                route.getName(),
                route.getDescription(),
                route.getDistance(),
                route.getElevationGain(),
                route.getElevationLoss(),
                route.getDifficulty() != null ? route.getDifficulty().name() : null,
                route.getSurfaceType() != null ? route.getSurfaceType().name() : null,
                route.isPublic(),
                route.getThumbnailUrl(),
                route.getStartLat(),
                route.getStartLng(),
                route.getEndLat(),
                route.getEndLng(),
                TsidUtils.toString(route.getCreatedBy().getId()),
                route.getCreatedAt() != null ? route.getCreatedAt().toString() : null,
                route.getUpdatedAt() != null ? route.getUpdatedAt().toString() : null
        );
    }
}
