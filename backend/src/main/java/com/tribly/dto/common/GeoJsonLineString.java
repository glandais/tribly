package com.tribly.dto.common;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "GeoJsonLineString")
@ValidateSchema
public record GeoJsonLineString(
    @Schema(
            enumeration = {"LineString"},
            required = true)
        String type,
    @Schema(description = "Array of [lon, lat] coordinates", required = true)
        double[][] coordinates) {}
