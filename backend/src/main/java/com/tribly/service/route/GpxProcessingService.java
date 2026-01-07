package com.tribly.service.route;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import com.tribly.common.GeoPoint;
import com.tribly.domain.asset.Asset;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.GpxWaypoint;
import com.tribly.domain.route.Route;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.enums.AllEntityType;
import com.tribly.enums.AssetType;
import com.tribly.enums.WindDirection;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.service.asset.AssetService;
import com.tribly.service.asset.response.AssetWithFile;
import com.tribly.service.route.response.TrackMetadata;
import io.github.glandais.gpx.climb.Climb;
import io.github.glandais.gpx.climb.ClimbDetector;
import io.github.glandais.gpx.data.*;
import io.github.glandais.gpx.filter.GPXFilter;
import io.github.glandais.gpx.filter.GPXPerDistance;
import io.github.glandais.gpx.io.read.GPXFileReader;
import io.github.glandais.gpx.io.write.FitFileWriter;
import io.github.glandais.gpx.io.write.GPXFileWriter;
import io.github.glandais.gpx.map.TileMapProducer;
import io.github.glandais.gpx.srtm.GPXElevationFixer;
import io.github.glandais.gpx.util.GPXDataComputer;
import io.github.glandais.gpx.util.Vector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.geolatte.geom.G2D;
import org.geolatte.geom.LineString;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Service for processing GPX files for route management.
 * Handles the complete GPX processing pipeline from upload to storage.
 */
@ApplicationScoped
public class GpxProcessingService {

  private static final Logger LOG = Logger.getLogger(GpxProcessingService.class);

  @Inject GPXFileReader gpxFileReader;

  @Inject GPXDataComputer gpxDataComputer;

  @Inject GPXPerDistance gpxPerDistance;

  @Inject GPXElevationFixer gpxElevationFixer;

  @Inject ClimbDetector climbDetector;

  @Inject GPXFileWriter gpxFileWriter;

  @Inject FitFileWriter fitFileWriter;

  @Inject TileMapProducer tileMapProducer;

  @Inject AssetService assetService;

  @ConfigProperty(name = "mapbox.api.key")
  private String mapboxApiKey;

  public GPX parseGpx(Path path) {
    // Step 1: Parse GPX
    LOG.infov("Processing GPX file");
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      return gpxFileReader.parseGPX(fis);
    } catch (Exception e) {
      LOG.errorv("Failed to parse GPX file", e);
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
  }

  public GPX fromPoints(String name, List<GeoPoint> points) {
    GPXPath gpxPath = new GPXPath(name, GPXPathType.TRACK);
    points.stream().map(this::createGpxPoint).forEach(gpxPath::addPoint);
    gpxPath.computeArrays();
    return new GPX(name, List.of(gpxPath), List.of());
  }

  private Point createGpxPoint(GeoPoint geoPoint) {
    Point p = new Point();
    p.setLon(Math.toRadians(geoPoint.lng()));
    p.setLat(Math.toRadians(geoPoint.lat()));
    p.setEle(0.0);
    p.setInstant(null, Instant.EPOCH);
    return p;
  }

  @Transactional
  public TrackMetadata createTracks(User creator, Route route, GPX gpx) {
    Long routeId = route.getId();
    try {
      if (gpx.paths().isEmpty()) {
        throw new BusinessException(ErrorCode.GPX_EMPTY);
      }

      // Save original GPX (parsed, before filtering)
      AssetWithFile gpxAssetFile =
          createAsset(creator, route, AssetType.ROUTE_ORIGINAL_GPX, "original.gpx");
      File originalFile = gpxAssetFile.file();
      gpxFileWriter.writeGPX(gpx, originalFile, false);
      LOG.infov("Saved original GPX to {0}", originalFile);

      for (GPXWaypoint waypoint : gpx.waypoints()) {
        GpxWaypoint gpxWaypoint =
            new GpxWaypoint(
                creator,
                waypoint.name(),
                point(WGS84, g(waypoint.point().getLonDeg(), waypoint.point().getLatDeg())));
        route.addWaypoint(gpxWaypoint);
      }

      List<TrackMetadata> tracksMetadata = new ArrayList<>();
      for (GPXPath path : gpx.paths()) {
        // Step 2: Resample to 10m intervals
        gpxPerDistance.computeOnePointPerDistance(path, 10.0);
        LOG.infov("Resampled to {0} points (10m intervals)", path.getPoints().size());

        // Step 3: Fix elevation with SRTM (with graceful degradation)
        try {
          gpxElevationFixer.fixElevation(path);
          LOG.infov("Fixed elevation with SRTM data");
        } catch (Exception e) {
          LOG.warnv(
              "SRTM elevation fix failed for route {0}, using original elevations: {1}",
              routeId, e);
        }

        // Step 4: Simplify with Douglas-Peucker
        GPXFilter.filterPointsDouglasPeucker(path);
        LOG.infov("Simplified to {0} points (Douglas-Peucker)", path.getPoints().size());

        // Step 5: Detect climbs
        List<Climb> climbs = new ArrayList<>(climbDetector.getClimbs(path));
        LOG.infov("Detected {0} climbs", climbs.size());

        // Step 7: Convert to Geometry
        LineString<G2D> lineString = toLineString(path);

        // Step 8: Convert to JSONB track points for frontend
        List<GpxTrack.TrackPoint> trackPoints = toTrackPoints(path);

        TrackMetadata metadata = extractMetadata(path);

        GpxTrack track =
            new GpxTrack(
                creator,
                path.getName(),
                lineString,
                trackPoints,
                climbs,
                metadata.distance(),
                metadata.elevationGain(),
                metadata.elevationLoss());
        route.addTrack(track);

        tracksMetadata.add(metadata);
      }

      Vector wind = gpxDataComputer.getWind(gpx);
      WindDirection windDirection = findDirectionFromVector(wind);

      // Save filtered GPX
      AssetWithFile filteredAssetFile =
          createAsset(creator, route, AssetType.ROUTE_FILTERED_GPX, "filtered.gpx");
      File filteredFile = filteredAssetFile.file();
      gpxFileWriter.writeGPX(gpx, filteredFile, true);
      LOG.infov("Saved filtered GPX to {0}", filteredFile);

      // Save FIT file
      AssetWithFile fitAssetFile = createAsset(creator, route, AssetType.ROUTE_FIT, "route.fit");
      File fitFile = fitAssetFile.file();
      fitFileWriter.writeGPX(gpx, fitFile);
      LOG.infov("Saved FIT file to {0}", fitFile);

      // Generate thumbnail map
      AssetWithFile thumbnailAssetFile =
          createAsset(creator, route, AssetType.ROUTE_THUMBNAIL, "thumbnail.png");
      File thumbnailFile = thumbnailAssetFile.file();
      try {
        // Use OpenStreetMap tiles, 512x512 max size, 0.1 margin
        tileMapProducer.createTileMap(
            thumbnailFile,
            gpx,
            "https://api.mapbox.com/styles/v1/mapbox/outdoors-v11/tiles/256/{z}/{x}/{y}?access_token="
                + mapboxApiKey,
            0.1,
            512,
            512);
        LOG.infov("Generated thumbnail to {0}", thumbnailFile);
      } catch (Exception e) {
        LOG.warnv("Thumbnail generation failed for route {0}: {1}", routeId, e);
      }

      LOG.infov("GPX processing complete for route}");

      int distance = tracksMetadata.stream().mapToInt(TrackMetadata::distance).sum();
      int elevationGain = tracksMetadata.stream().mapToInt(TrackMetadata::elevationGain).sum();
      return new TrackMetadata(
          distance,
          elevationGain,
          getHilliness(distance, elevationGain),
          tracksMetadata.stream().mapToInt(TrackMetadata::elevationLoss).sum(),
          tracksMetadata.getFirst().start(),
          tracksMetadata.getLast().end(),
          windDirection);
    } catch (Exception e) {
      LOG.errorv("GPX processing failed for route", e);
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
  }

  @Nullable
  public static WindDirection findDirectionFromVector(Vector windVector) {
    return Stream.of(WindDirection.values())
        .map(
            wd -> {
              double angle = Math.toRadians(90 - wd.getAngle());
              double x1 = Math.cos(angle);
              double y1 = Math.sin(angle) * -1;
              double x2 = windVector.x();
              double y2 = windVector.y();
              double dotProduct = (x1 * x2) + (y1 * y2);
              return new WindScore(wd, 0.5 + (0.5 * dotProduct));
            })
        .sorted(Comparator.comparing(WindScore::score))
        .map(WindScore::wd)
        .findFirst()
        .orElse(null);
  }

  record WindScore(WindDirection wd, double score) {}

  private AssetWithFile createAsset(User user, Route route, AssetType assetType, String fileName)
      throws IOException {
    Asset existingAsset = getAsset(route, assetType);
    if (existingAsset != null) {
      route.getAssets().remove(existingAsset);
    }
    AssetWithFile assetFile = assetService.addAsset(user, route, assetType, fileName);
    route.getAssets().add(assetFile.asset());
    return assetFile;
  }

  /**
   * Convert GPXPath to PostGIS LineString in WKT format.
   * Format: "LINESTRING(lng lat, lng lat, ...)"
   */
  private LineString<G2D> toLineString(GPXPath path) {
    G2D[] geomPoints =
        path.getPoints().stream()
            .map(p -> g(Math.toDegrees(p.getLon()), Math.toDegrees(p.getLat())))
            .toArray(G2D[]::new);
    return linestring(WGS84, geomPoints);
  }

  /**
   * Convert GPXPath to simplified track points for frontend JSONB storage.
   */
  private List<GpxTrack.TrackPoint> toTrackPoints(GPXPath path) {
    List<GpxTrack.TrackPoint> trackPoints = new ArrayList<>();
    List<Point> points = path.getPoints();

    for (Point p : points) {
      trackPoints.add(
          new GpxTrack.TrackPoint(
              Math.toDegrees(p.getLat()), Math.toDegrees(p.getLon()), p.getEle(), p.getDist()));
    }

    return trackPoints;
  }

  /**
   * Extract route metadata from processed GPXPath.
   */
  private TrackMetadata extractMetadata(GPXPath path) {
    List<Point> points = path.getPoints();
    Point start = points.getFirst();
    Point end = points.getLast();

    int distance = (int) Math.round(path.getDist());
    int elevationGain = (int) Math.round(path.getTotalElevation());
    return new TrackMetadata(
        distance,
        elevationGain,
        getHilliness(distance, elevationGain),
        (int) Math.round(path.getTotalElevationNegative()),
        point(WGS84, g(start.getLonDeg(), start.getLatDeg())),
        point(WGS84, g(end.getLonDeg(), end.getLatDeg())),
        null);
  }

  private int getHilliness(int distance, int elevationGain) {
    if (distance == 0) {
      return 0;
    }
    return (1000 * elevationGain) / distance;
  }

  /**
   * Delete all files for a route (cleanup on error or deletion).
   */
  public void deleteRouteFiles(Route route) {
    // FIXME
  }

  /**
   * Get file path for filtered GPX download.
   */
  public File getFilteredGpxFile(Route route) {
    return getFile(route, AssetType.ROUTE_FILTERED_GPX);
  }

  /**
   * Get file path for FIT download.
   */
  public File getFitFile(Route route) {
    return getFile(route, AssetType.ROUTE_FIT);
  }

  /**
   * Get file path for thumbnail image.
   */
  public File getThumbnailFile(Route route) {
    return getFile(route, AssetType.ROUTE_THUMBNAIL);
  }

  private File getFile(Route route, AssetType assetType) {
    Asset matching = getAsset(route, assetType);
    if (matching != null) {
      return assetService.getAssetFile(matching);
    } else {
      throw new NotFoundException(AllEntityType.ASSET, "forRoute-" + route.getId());
    }
  }

  private @Nullable Asset getAsset(Route route, AssetType assetType) {
    Asset matching = null;
    for (Asset asset : route.getAssets()) {
      if (asset.getType() == assetType) {
        matching = asset;
      }
    }
    return matching;
  }
}
