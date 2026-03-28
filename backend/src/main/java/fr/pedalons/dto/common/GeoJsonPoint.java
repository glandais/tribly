package fr.pedalons.dto.common;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "GeoJsonPoint", description = "GeoJSON Point geometry")
@ValidateSchema
public record GeoJsonPoint(
    @Schema(
            examples = "Point",
            enumeration = {"Point"},
            required = true)
        String type,
    @Schema(
            examples = "[2.3522, 48.8566]",
            description = "Coordinates [longitude, latitude]",
            required = true)
        double[] coordinates) {}
