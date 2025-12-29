package com.tribly.dto.common.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "GeoJsonLineString")
public record GeoJsonLineString(
    @Schema(enumeration = {"LineString"}) String type,
    @Schema(description = "Array of [lon, lat] coordinates") double[][] coordinates) {}
