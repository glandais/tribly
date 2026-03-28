package fr.pedalons.dto.routes.response;

import fr.pedalons.domain.route.GpxWaypoint;
import fr.pedalons.dto.common.GeoJsonPoint;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

public record WaypointDto(
    @Schema(implementation = GeoJsonPoint.class, required = true) Point<G2D> geometry,
    String name) {
  public static WaypointDto from(GpxWaypoint waypoint) {
    return new WaypointDto(waypoint.getGeometry(), waypoint.getName());
  }
}
