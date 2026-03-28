package fr.pedalons.service.brouter;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.addVerticalSystem;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.router.request.RouterRequest;
import fr.pedalons.dto.router.response.RouterResponse;
import fr.pedalons.infrastructure.brouter.BRouterClient;
import fr.pedalons.infrastructure.brouter.ResultFeature;
import fr.pedalons.infrastructure.brouter.RouterResult;
import fr.pedalons.service.security.annotation.Logged;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.GPXPathType;
import io.github.glandais.gpx.data.Point;
import io.github.glandais.gpx.filter.GPXFilter;
import io.github.glandais.gpx.filter.GPXPerDistance;
import io.github.glandais.gpx.srtm.GPXElevationFixer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.geolatte.geom.G3D;
import org.geolatte.geom.LineString;
import org.geolatte.geom.PositionSequence;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.LinearUnit;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BRouterService {

  private static final Logger LOG = Logger.getLogger(BRouterService.class);
  private static final CoordinateReferenceSystem<G3D> WGS84_3D =
      addVerticalSystem(WGS84, G3D.class, LinearUnit.METER);

  @Inject @RestClient BRouterClient bRouterClient;

  @Inject GPXPerDistance gpxPerDistance;

  @Inject GPXElevationFixer gpxElevationFixer;

  @Logged
  public RouterResponse getRoute(RouterRequest routerRequest) {
    String lonlats =
        routerRequest.from().lng()
            + ","
            + routerRequest.from().lat()
            + "|"
            + routerRequest.to().lng()
            + ","
            + routerRequest.to().lat();
    RouterResult geojson =
        bRouterClient.route(lonlats, routerRequest.profile().getProfileName(), 0, "geojson");
    List<ResultFeature> features = geojson.features();
    if (features.isEmpty()) {
      throw new BusinessException(ErrorCode.UNKNOWN);
    }
    ResultFeature feature = features.getFirst();

    // Convert to GPXPath for processing
    GPXPath path = toGpxPath(feature.geometry());

    // Step 1: Resample to 10m intervals
    gpxPerDistance.computeOnePointPerDistance(path, 10.0);
    LOG.debugv("Resampled to {0} points (10m intervals)", path.getPoints().size());

    // Step 2: Fix elevation with SRTM (with graceful degradation)
    try {
      gpxElevationFixer.fixElevation(path);
      LOG.debugv("Fixed elevation with SRTM data");
    } catch (Exception e) {
      LOG.warnv("SRTM elevation fix failed, using original elevations: {0}", e.getMessage());
    }

    // Step 3: Simplify with Douglas-Peucker
    GPXFilter.filterPointsDouglasPeucker(path);
    LOG.debugv("Simplified to {0} points (Douglas-Peucker)", path.getPoints().size());

    // Convert back to LineString and compute metrics
    LineString<G3D> filteredRoute = toLineString3D(path);
    double dist = path.getDist();
    double ascend = path.getTotalElevation();

    return new RouterResponse(filteredRoute, dist, ascend);
  }

  private GPXPath toGpxPath(LineString<G3D> geometry) {
    GPXPath path = new GPXPath("route", GPXPathType.TRACK);
    PositionSequence<G3D> positions = geometry.getPositions();
    for (int i = 0; i < positions.size(); i++) {
      G3D pos = positions.getPositionN(i);
      Point p = new Point();
      p.setLon(Math.toRadians(pos.getLon()));
      p.setLat(Math.toRadians(pos.getLat()));
      p.setEle(pos.getCoordinate(2)); // Z coordinate is elevation
      p.setInstant(null, Instant.EPOCH);
      path.addPoint(p);
    }
    path.computeArrays();
    return path;
  }

  private LineString<G3D> toLineString3D(GPXPath path) {
    G3D[] geomPoints =
        path.getPoints().stream()
            .map(p -> g(Math.toDegrees(p.getLon()), Math.toDegrees(p.getLat()), p.getEle()))
            .toArray(G3D[]::new);
    return linestring(WGS84_3D, geomPoints);
  }
}
