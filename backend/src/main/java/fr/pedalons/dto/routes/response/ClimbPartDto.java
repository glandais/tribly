package fr.pedalons.dto.routes.response;

import fr.pedalons.dto.validation.ValidateSchema;
import io.github.glandais.gpx.climb.ClimbPart;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Climb part DTO for a single gradient segment within a climb.
 */
@Schema(description = "Climb part information")
@ValidateSchema
public record ClimbPartDto(
    @Schema(description = "Start distance from route start in meters", required = true)
        Integer startDistance,
    @Schema(description = "End distance from route start in meters", required = true)
        Integer endDistance,
    @Schema(description = "Elevation gain in meters", required = true) Integer elevationGain,
    @Schema(description = "Gradient percentage", required = true) BigDecimal grade) {
  public static ClimbPartDto from(ClimbPart part) {
    return new ClimbPartDto(
        (int) Math.round(part.startDist()),
        (int) Math.round(part.endDist()),
        (int) Math.round(part.ele()),
        BigDecimal.valueOf(part.grade()));
  }
}
