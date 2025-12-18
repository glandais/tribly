package com.tribly.api.routes;

import com.tribly.domain.route.Route;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Route DTO for list view.
 */
@Schema(description = "Route summary data")
public record RouteDto(
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

        @Schema(description = "Route difficulty", enumeration = {"EASY", "MODERATE", "HARD", "VERY_HARD"})
        String difficulty,

        @Schema(description = "Surface type", enumeration = {"ASPHALT", "GRAVEL", "MIXED"})
        String surfaceType,

        @Schema(description = "Whether the route is public")
        boolean isPublic,

        @Schema(description = "Thumbnail image URL")
        String thumbnailUrl,

        @Schema(description = "Creation timestamp")
        String createdAt
) {
    public static RouteDto from(Route route) {
        return new RouteDto(
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
                route.getCreatedAt() != null ? route.getCreatedAt().toString() : null
        );
    }
}
