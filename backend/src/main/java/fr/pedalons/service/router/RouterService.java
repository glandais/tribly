package fr.pedalons.service.router;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.addVerticalSystem;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.router.request.RouterProfile;
import fr.pedalons.dto.router.request.RouterRequest;
import fr.pedalons.dto.router.response.RouterResponse;
import fr.pedalons.infrastructure.valhalla.Polyline6Decoder;
import fr.pedalons.infrastructure.valhalla.ValhallaClient;
import fr.pedalons.infrastructure.valhalla.ValhallaLocation;
import fr.pedalons.infrastructure.valhalla.ValhallaRequest;
import fr.pedalons.infrastructure.valhalla.ValhallaResponse;
import fr.pedalons.service.security.annotation.Logged;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.GPXPathType;
import io.github.glandais.gpx.data.Point;
import io.github.glandais.gpx.filter.GPXFilter;
import io.github.glandais.gpx.filter.GPXPerDistance;
import io.github.glandais.gpx.srtm.GPXElevationFixer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.geolatte.geom.G3D;
import org.geolatte.geom.LineString;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.LinearUnit;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RouterService {

  private static final Logger LOG = Logger.getLogger(RouterService.class);
  private static final CoordinateReferenceSystem<G3D> WGS84_3D =
      addVerticalSystem(WGS84, G3D.class, LinearUnit.METER);

  @Inject @RestClient ValhallaClient valhallaClient;

  @Inject GPXPerDistance gpxPerDistance;

  @Inject GPXElevationFixer gpxElevationFixer;

  @Logged
  public RouterResponse getRoute(RouterRequest routerRequest) {
    ValhallaRequest valhallaRequest = getValhallaRequest(routerRequest);

    ValhallaResponse response;
    try {
      response = valhallaClient.route(valhallaRequest);
    } catch (WebApplicationException e) {
      LOG.warnv("Valhalla routing failed with status {0}", e.getResponse().getStatus());
      throw new BusinessException(ErrorCode.UNKNOWN);
    }

    if (response.trip() == null || response.trip().legs().isEmpty()) {
      return new RouterResponse(linestring(WGS84_3D), 0, 0);
    }

    // Decode all leg shapes and concatenate coordinates
    List<double[]> allCoords =
        response.trip().legs().stream()
            .flatMap(leg -> Polyline6Decoder.decode(leg.shape()).stream())
            .toList();

    if (allCoords.isEmpty()) {
      throw new BusinessException(ErrorCode.UNKNOWN);
    }

    // Convert to LineString<G3D> (elevation = 0, will be fixed by SRTM)
    G3D[] geomPoints = allCoords.stream().map(c -> g(c[0], c[1], 0.0)).toArray(G3D[]::new);
    LineString<G3D> rawRoute = linestring(WGS84_3D, geomPoints);

    // Convert to GPXPath for processing
    GPXPath path = toGpxPath(rawRoute);

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

  private static ValhallaRequest getValhallaRequest(RouterRequest routerRequest) {
    RouterProfile profile = routerRequest.profile();

    Map<String, Map<String, Object>> costingOptions =
        profile.getCostingOptions().isEmpty()
            ? Map.of()
            : Map.of(profile.getCosting(), profile.getCostingOptions());

    return new ValhallaRequest(
        List.of(
            new ValhallaLocation(routerRequest.from().lat(), routerRequest.from().lng()),
            new ValhallaLocation(routerRequest.to().lat(), routerRequest.to().lng())),
        profile.getCosting(),
        costingOptions);
  }

  private GPXPath toGpxPath(LineString<G3D> geometry) {
    GPXPath path = new GPXPath("route", GPXPathType.TRACK);
    var positions = geometry.getPositions();
    for (int i = 0; i < positions.size(); i++) {
      G3D pos = positions.getPositionN(i);
      Point p = new Point();
      p.setLon(Math.toRadians(pos.getLon()));
      p.setLat(Math.toRadians(pos.getLat()));
      p.setEle(pos.getCoordinate(2));
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
