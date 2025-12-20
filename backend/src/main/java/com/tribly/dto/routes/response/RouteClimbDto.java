package com.tribly.dto.routes.response;

import com.tribly.domain.route.RouteClimb;
import com.tribly.enums.ClimbCategory;
import com.tribly.infrastructure.id.TsidUtils;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * Climb DTO for climb segments.
 */
@Schema(description = "Climb segment information")
public record RouteClimbDto(
    @Schema(description = "Climb ID (TSID)", required = true) String id,
    @Nullable @Schema(description = "Climb name (if named)", nullable = true) String name,
    @Schema(description = "Start distance from route start in meters", required = true)
        Integer startDistance,
    @Schema(description = "End distance from route start in meters", required = true)
        Integer endDistance,
    @Schema(description = "Elevation gain in meters", required = true) Integer elevationGain,
    @Schema(description = "Average gradient percentage", required = true)
        BigDecimal averageGradient,
    @Schema(description = "Maximum gradient percentage", required = true) BigDecimal maxGradient,
    @Nullable @Schema(description = "Climb category (HC, 1, 2, 3, 4)", nullable = true)
        ClimbCategory category) {
  public static RouteClimbDto from(RouteClimb climb) {
    return new RouteClimbDto(
        TsidUtils.toString(climb.getId()),
        climb.getName(),
        climb.getStartDistance(),
        climb.getEndDistance(),
        climb.getElevationGain(),
        climb.getAverageGradient(),
        climb.getMaxGradient(),
        climb.getCategory());
  }
}
