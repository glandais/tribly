package com.tribly.dto.routes.response;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.addLinearSystem;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.addVerticalSystem;

import com.tribly.domain.route.GpxTrack;
import com.tribly.dto.common.response.GeoJsonLineString;
import com.tribly.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G3D;
import org.geolatte.geom.G3DM;
import org.geolatte.geom.LineString;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.LinearUnit;

/**
 * GPX Track DTO with track points for frontend rendering.
 */
@Schema(description = "GPX track with track points")
@ValidateSchema
public record TrackDto(
    @Schema(implementation = GeoJsonLineString.class, required = true) LineString<G3DM> line,
    @Schema(description = "List of climbs on the route", required = true) List<ClimbDto> climbs) {

  private static final CoordinateReferenceSystem<G3DM> WGS84_3DM =
      addLinearSystem(
          addVerticalSystem(WGS84, G3D.class, LinearUnit.METER), G3DM.class, LinearUnit.METER);

  public static TrackDto from(GpxTrack track) {
    G3DM[] geomPoints =
        track.getTrackPoints().stream()
            .map(p -> g(p.lng(), p.lat(), p.ele(), p.dist()))
            .toArray(G3DM[]::new);
    LineString<G3DM> line = linestring(WGS84_3DM, geomPoints);
    List<ClimbDto> routeClimbDtos = track.getClimbs().stream().map(ClimbDto::from).toList();
    return new TrackDto(line, routeClimbDtos);
  }
}
