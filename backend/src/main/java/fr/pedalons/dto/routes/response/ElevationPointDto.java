package fr.pedalons.dto.routes.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** One sample of an elevation profile: where, how high, and how steep the way in was. */
@Schema(description = "One point of an elevation profile")
@ValidateSchema
public record ElevationPointDto(
    @Schema(
            description = "Cumulative distance from the start of the route, in meters",
            required = true)
        double distance,
    @Schema(description = "Elevation above sea level, in meters", required = true) double elevation,
    @Schema(
            description =
                "Grade of the segment ending at this point, in percent. Zero on the first point,"
                    + " which ends no segment.",
            required = true)
        double grade) {}
