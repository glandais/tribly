package com.tribly.api.routes;

import com.tribly.domain.route.RouteClimb;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Climb DTO for climb segments.
 */
@Schema(description = "Climb segment information")
public record RouteClimbDto(
        @Schema(description = "Climb ID (TSID)")
        String id,

        @Schema(description = "Climb name (if named)")
        String name,

        @Schema(description = "Start distance from route start in meters")
        Integer startDistance,

        @Schema(description = "End distance from route start in meters")
        Integer endDistance,

        @Schema(description = "Elevation gain in meters")
        Integer elevationGain,

        @Schema(description = "Average gradient percentage")
        BigDecimal averageGradient,

        @Schema(description = "Maximum gradient percentage")
        BigDecimal maxGradient,

        @Schema(description = "Climb category (HC, 1, 2, 3, 4)")
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
