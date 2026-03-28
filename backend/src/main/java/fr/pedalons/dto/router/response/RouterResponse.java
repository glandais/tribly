package fr.pedalons.dto.router.response;

import fr.pedalons.dto.common.GeoJsonLineString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G3D;
import org.geolatte.geom.LineString;

public record RouterResponse(
    @Schema(implementation = GeoJsonLineString.class, required = true) LineString<G3D> route,
    double dist,
    double ascend) {}
