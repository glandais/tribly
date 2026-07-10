package fr.pedalons.dto.routes.response;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.addLinearSystem;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.addVerticalSystem;

import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.dto.common.GeoJsonLineString;
import fr.pedalons.dto.validation.ValidateSchema;
import io.github.glandais.gpx.climb.Climb;
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
    return of(track.getTrackPoints(), track.getClimbs());
  }

  /** Builds a track DTO straight from computed points and climbs, with no entity involved. */
  public static TrackDto of(List<GpxTrack.TrackPoint> trackPoints, List<Climb> climbs) {
    G3DM[] geomPoints =
        trackPoints.stream().map(p -> g(p.lng(), p.lat(), p.ele(), p.dist())).toArray(G3DM[]::new);
    LineString<G3DM> line = linestring(WGS84_3DM, geomPoints);
    return new TrackDto(line, climbs.stream().map(ClimbDto::from).toList());
  }
}
