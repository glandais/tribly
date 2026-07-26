package fr.pedalons.dto.config;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Where a map opens before it knows what it is showing.
 *
 * <p>Deliberately not a {@code GeoJsonPoint}: a camera is a position <em>and</em> a zoom, and a
 * GeoJSON geometry cannot carry the zoom.
 */
@Schema(description = "Initial map camera")
@ValidateSchema
public record MapCenterDto(
    @Schema(description = "Latitude in WGS84 degrees", examples = "46.6", required = true)
        double lat,
    @Schema(description = "Longitude in WGS84 degrees", examples = "2.3", required = true)
        double lon,
    @Schema(description = "Initial zoom level", examples = "5", required = true) double zoom) {}
