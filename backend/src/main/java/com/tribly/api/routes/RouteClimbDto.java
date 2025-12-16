package com.tribly.api.routes;

import com.tribly.domain.route.RouteClimb;
import com.tribly.infrastructure.id.TsidUtils;

import java.math.BigDecimal;

/**
 * Climb DTO for climb segments.
 */
public record RouteClimbDto(
        String id,
        String name,
        Integer startDistance,
        Integer endDistance,
        Integer elevationGain,
        BigDecimal averageGradient,
        BigDecimal maxGradient,
        String category
) {
    public static RouteClimbDto from(RouteClimb climb) {
        return new RouteClimbDto(
                TsidUtils.toString(climb.getId()),
                climb.getName(),
                climb.getStartDistance(),
                climb.getEndDistance(),
                climb.getElevationGain(),
                climb.getAverageGradient(),
                climb.getMaxGradient(),
                climb.getCategory() != null ? climb.getCategory().name() : null
        );
    }
}
