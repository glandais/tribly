package fr.pedalons.dto.routes.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Geographic bounding box, in WGS84 degrees")
@ValidateSchema
public record BoundsDto(
    @Schema(description = "Western edge", required = true) double minLon,
    @Schema(description = "Southern edge", required = true) double minLat,
    @Schema(description = "Eastern edge", required = true) double maxLon,
    @Schema(description = "Northern edge", required = true) double maxLat) {}
