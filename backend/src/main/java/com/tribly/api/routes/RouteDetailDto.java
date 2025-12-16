package com.tribly.api.routes;

import com.tribly.domain.route.Route;
import com.tribly.infrastructure.id.TsidUtils;

import java.math.BigDecimal;

/**
 * Route DTO for detail view with full information.
 */
public record RouteDetailDto(
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
        BigDecimal startLat,
        BigDecimal startLng,
        BigDecimal endLat,
        BigDecimal endLng,
        String createdById,
        String createdAt,
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
