package com.tribly.service.route;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.Route;
import com.tribly.domain.user.User;
import com.tribly.enums.AssetType;
import com.tribly.enums.ClimbCategory;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.asset.AssetService;
import com.tribly.service.asset.response.AssetWithFile;
import com.tribly.service.route.response.ProcessedGpx;
import com.tribly.service.route.response.RouteMetadata;
import io.github.glandais.gpx.climb.Climb;
import io.github.glandais.gpx.climb.ClimbDetector;
import io.github.glandais.gpx.climb.Climbs;
import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.Point;
import io.github.glandais.gpx.filter.GPXFilter;
import io.github.glandais.gpx.filter.GPXPerDistance;
import io.github.glandais.gpx.io.read.GPXFileReader;
import io.github.glandais.gpx.io.write.FitFileWriter;
import io.github.glandais.gpx.io.write.GPXFileWriter;
import io.github.glandais.gpx.map.TileMapProducer;
import io.github.glandais.gpx.srtm.GPXElevationFixer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

  @Inject GPXPerDistance gpxPerDistance;

  @Inject GPXElevationFixer gpxElevationFixer;

  @Inject ClimbDetector climbDetector;

  @Inject GPXFileWriter gpxFileWriter;

  @Inject FitFileWriter fitFileWriter;

  @Inject TileMapProducer tileMapProducer;

  @Inject AssetService assetService;

  /**
   * Process uploaded GPX file through complete pipeline.
   * <p>
   * Pipeline steps:
   * 1. Parse GPX
   * 2. Resample to 10m intervals
   * 3. Fix elevation with SRTM data
   * 4. Simplify with Douglas-Peucker algorithm
   * 5. Detect climbs
   * 6. Generate files (original, filtered, FIT, thumbnail)
   * 7. Convert to PostGIS WKT and JSONB track points
   *
   * @param route        Route
   * @param gpxInputStream Input stream of uploaded GPX file
   * @return ProcessedGpx containing all extracted data
   */
  @Transactional
  public ProcessedGpx processGpxUpload(User user, Route route, InputStream gpxInputStream) {
    Long routeId = route.getId();
    try {
      // Step 1: Parse GPX
      LOG.infov("Processing GPX file for route {1}", routeId);
      GPX gpx = gpxFileReader.parseGPX(gpxInputStream);

      if (gpx.paths().isEmpty()) {
        throw BusinessException.validation("GPX file contains no tracks or routes");
      }

      GPXPath path = gpx.paths().getFirst();
      LOG.infov("Parsed GPX with {0} points", path.getPoints().size());

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
            routeId, e.getMessage());
      }

      // Step 4: Simplify with Douglas-Peucker
      GPXFilter.filterPointsDouglasPeucker(path);
      LOG.infov("Simplified to {0} points (Douglas-Peucker)", path.getPoints().size());

      // Step 5: Detect climbs
      Climbs climbs = climbDetector.getClimbs(path);
      LOG.infov("Detected {0} climbs", climbs.size());

      // Save original GPX (parsed, before filtering)
      AssetWithFile gpxAssetFile =
          createAsset(user, route, AssetType.ROUTE_ORIGINAL_GPX, "original.gpx");
      File originalFile = gpxAssetFile.file();
      gpxFileWriter.writeGPX(gpx, originalFile, false);
      LOG.infov("Saved original GPX to {0}", originalFile);

      // Save filtered GPX
      AssetWithFile filteredAssetFile =
          createAsset(user, route, AssetType.ROUTE_FILTERED_GPX, "filtered.gpx");
      File filteredFile = filteredAssetFile.file();
      gpxFileWriter.writeGPX(gpx, filteredFile, true);
      LOG.infov("Saved filtered GPX to {0}", filteredFile);

      // Save FIT file
      AssetWithFile fitAssetFile = createAsset(user, route, AssetType.ROUTE_FIT, "route.fit");
      File fitFile = fitAssetFile.file();
      fitFileWriter.writeGPX(gpx, fitFile);
      LOG.infov("Saved FIT file to {0}", fitFile);

      // Generate thumbnail map
      AssetWithFile thumbnailAssetFile =
          createAsset(user, route, AssetType.ROUTE_THUMBNAIL, "thumbnail.png");
      File thumbnailFile = thumbnailAssetFile.file();
      try {
        // Use OpenStreetMap tiles, 512x512 max size, 0.1 margin
        tileMapProducer.createTileMap(
            thumbnailFile, gpx, "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png", 0.1, 512);
        LOG.infov("Generated thumbnail to {0}", thumbnailFile);
      } catch (Exception e) {
        LOG.warnv("Thumbnail generation failed for route {0}: {1}", routeId, e.getMessage());
      }

      // Step 7: Convert to PostGIS WKT
      String wkt = toLineStringWKT(path);

      // Step 8: Convert to JSONB track points for frontend
      List<GpxTrack.TrackPoint> trackPoints = toTrackPoints(path);

      // Step 9: Extract metadata
      RouteMetadata metadata = extractMetadata(path);

      LOG.infov("GPX processing complete for route {0}", routeId);
      return new ProcessedGpx(wkt, trackPoints, climbs, metadata);

    } catch (Exception e) {
      LOG.errorv("GPX processing failed for route {0}", routeId);
      throw BusinessException.conflict("GPX processing failed: " + e.getMessage());
    }
  }

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
  private String toLineStringWKT(GPXPath path) {
    StringBuilder wkt = new StringBuilder("LINESTRING(");
    List<Point> points = path.getPoints();

    for (int i = 0; i < points.size(); i++) {
      Point p = points.get(i);
      // PostGIS format: longitude first, then latitude
      wkt.append(Math.toDegrees(p.getLon())).append(" ").append(Math.toDegrees(p.getLat()));
      if (i < points.size() - 1) {
        wkt.append(",");
      }
    }

    wkt.append(")");
    return wkt.toString();
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
  private RouteMetadata extractMetadata(GPXPath path) {
    List<Point> points = path.getPoints();
    Point start = points.getFirst();
    Point end = points.getLast();

    return new RouteMetadata(
        (int) Math.round(path.getDist()),
        (int) Math.round(path.getTotalElevation()),
        (int) Math.round(path.getTotalElevationNegative()),
        Math.toDegrees(start.getLat()),
        Math.toDegrees(start.getLon()),
        Math.toDegrees(end.getLat()),
        Math.toDegrees(end.getLon()));
  }

  /**
   * Categorize climb based on elevation gain and average gradient.
   * <p>
   * Categories:
   * - HC (Hors Catégorie): > 1500m elevation or (> 1000m and > 8% grade)
   * - CAT1: 800-1500m elevation
   * - CAT2: 500-800m elevation
   * - CAT3: 300-500m elevation
   * - CAT4: < 300m elevation
   */
  public ClimbCategory categorizeClimb(Climb climb) {
    int elevationGain = (int) Math.round(climb.positiveElevation());
    double avgGrade = climb.grade();

    if (elevationGain > 1500 || (elevationGain > 1000 && avgGrade > 8.0)) {
      return ClimbCategory.HC;
    } else if (elevationGain > 800) {
      return ClimbCategory.CAT1;
    } else if (elevationGain > 500) {
      return ClimbCategory.CAT2;
    } else if (elevationGain > 300) {
      return ClimbCategory.CAT3;
    } else {
      return ClimbCategory.CAT4;
    }
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
      throw BusinessException.notFound("Asset not found for route " + route.getId());
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
