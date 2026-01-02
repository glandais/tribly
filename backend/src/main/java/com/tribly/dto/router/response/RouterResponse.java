package com.tribly.dto.router.response;

import com.tribly.dto.common.response.GeoJsonLineString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G3D;
import org.geolatte.geom.LineString;

public record RouterResponse(
    @Schema(implementation = GeoJsonLineString.class, required = true) LineString<G3D> route,
    double dist,
    double ascend) {}
