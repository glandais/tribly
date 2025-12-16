package com.tribly.api.routes;

import com.tribly.domain.route.Route;
import com.tribly.infrastructure.id.TsidUtils;

/**
 * Route DTO for list view.
 */
public record RouteDto(
        String id,
        String name,
        String description,
        Integer distance,
        Integer elevationGain,
        Integer elevationLoss,
        String difficulty,
        String surfaceType,
        boolean isPublic,
        String thumbnailUrl,
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
