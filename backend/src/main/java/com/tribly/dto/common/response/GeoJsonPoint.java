package com.tribly.dto.common.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "GeoJsonPoint", description = "GeoJSON Point geometry")
public record GeoJsonPoint(
    @Schema(
            examples = "Point",
            enumeration = {"Point"})
        String type,
    @Schema(examples = "[2.3522, 48.8566]", description = "Coordinates [longitude, latitude]")
        double[] coordinates) {}
