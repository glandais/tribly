package com.tribly.dto.routes.response;

import com.tribly.domain.route.GpxWaypoint;
import org.geolatte.geom.G2D;

public record WaypointDto(double lon, double lat, String name) {
  public static WaypointDto from(GpxWaypoint waypoint) {
    G2D position = waypoint.getGeometry().getPosition();
    return new WaypointDto(position.getLon(), position.getLat(), waypoint.getName());
  }
}
