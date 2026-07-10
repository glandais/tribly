package fr.pedalons.service.route;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import fr.pedalons.common.GeoPoint;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.domain.route.GpxWaypoint;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.WindDirection;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.route.response.TrackMetadata;
import fr.pedalons.service.security.PedalonsQueryContext;
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
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

  /** Sibling of the asset temp dir, so temp files can be moved across without copying bytes. */
  private static final String TEMP_DIR = "pedalons-gpx";

  @Inject GPXFileReader gpxFileReader;

  @Inject GPXDataComputer gpxDataComputer;

  @Inject GPXPerDistance gpxPerDistance;

  @Inject GPXElevationFixer gpxElevationFixer;

  @Inject ClimbDetector climbDetector;

  @Inject GPXFileWriter gpxFileWriter;

  @Inject FitFileWriter fitFileWriter;

  @Inject TileMapProducer tileMapProducer;

  @Inject AssetService assetService;

  @Inject StorageService storageService;

  @Inject PedalonsQueryContext pedalonsContext;

  @ConfigProperty(name = "tileserver.url")
  private String tileserverUrl;

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

  /** A single track, fully computed but not yet bound to any entity. */
  public record ComputedTrack(
      String name,
      LineString<G2D> line,
      List<GpxTrack.TrackPoint> trackPoints,
      List<Climb> climbs,
      TrackMetadata metadata) {}

  /** A single waypoint, fully computed but not yet bound to any entity. */
  public record ComputedWaypoint(String name, org.geolatte.geom.Point<G2D> location) {}

  /**
   * Outcome of {@link #computeGpx(GPX)}: the two GPX serializations as temp files, plus the
   * computed tracks, waypoints and aggregated metadata.
   *
   * <p>Closing deletes any temp file still present. {@link #processGpxData} consumes the files by
   * moving them, so closing is then a no-op — but on the error path it releases them.
   */
  public record ComputedGpx(
      File originalGpx,
      File filteredGpx,
      List<ComputedTrack> tracks,
      List<ComputedWaypoint> waypoints,
      TrackMetadata aggregated)
      implements AutoCloseable {

    @Override
    public void close() {
      deleteQuietly(originalGpx);
      deleteQuietly(filteredGpx);
    }
  }

  private static void deleteQuietly(@Nullable File file) {
    if (file != null && file.exists() && !file.delete()) {
      LOG.warnf("Failed to delete temp GPX file %s — may leak on disk", file.getAbsolutePath());
    }
  }

  /**
   * The pure GPX pipeline: resample to one point per 10m, fix elevation with SRTM data, simplify
   * with Douglas-Peucker, detect climbs, and aggregate metadata.
   *
   * <p>No database, no S3, no entities, no {@link Team} — which is what lets GPX previews (which
   * belong to no team) reuse it. Callers own the returned temp files.
   *
   * <p>The GPX object is mutated in place by the pipeline, so {@code original.gpx} must be written
   * before the loop and {@code filtered.gpx} after it. Keeping both serializations here makes that
   * ordering a local property of this method rather than a rule callers must remember.
   */
  public ComputedGpx computeGpx(GPX gpx) {
    if (gpx.paths().isEmpty()) {
      throw new BusinessException(ErrorCode.GPX_EMPTY);
    }
    File original = null;
    File filtered = null;
    try {
      original = writeGpxToTempFile(gpx, false);

      List<ComputedWaypoint> waypoints = new ArrayList<>();
      for (GPXWaypoint waypoint : gpx.waypoints()) {
        waypoints.add(
            new ComputedWaypoint(
                waypoint.name(),
                point(WGS84, g(waypoint.point().getLonDeg(), waypoint.point().getLatDeg()))));
      }

      List<ComputedTrack> tracks = new ArrayList<>();
      List<TrackMetadata> tracksMetadata = new ArrayList<>();
      for (GPXPath path : gpx.paths()) {
        gpxPerDistance.computeOnePointPerDistance(path, 10.0);
        LOG.infov("Resampled to {0} points (10m intervals)", path.getPoints().size());

        try {
          gpxElevationFixer.fixElevation(path);
          LOG.infov("Fixed elevation with SRTM data");
        } catch (Exception e) {
          LOG.warnf(e, "SRTM elevation fix failed for %s, using original elevations", gpx.name());
        }

        GPXFilter.filterPointsDouglasPeucker(path);
        LOG.infov("Simplified to {0} points (Douglas-Peucker)", path.getPoints().size());

        List<Climb> climbs = new ArrayList<>(climbDetector.getClimbs(path));
        LOG.infov("Detected {0} climbs", climbs.size());

        TrackMetadata metadata = extractMetadata(path);
        tracks.add(
            new ComputedTrack(
                path.getName(), toLineString(path), toTrackPoints(path), climbs, metadata));
        tracksMetadata.add(metadata);
      }

      filtered = writeGpxToTempFile(gpx, true);

      Vector wind = gpxDataComputer.getWind(gpx);
      WindDirection windDirection = findDirectionFromVector(wind);

      float distance = (float) tracksMetadata.stream().mapToDouble(TrackMetadata::distance).sum();
      float elevationGain =
          (float) tracksMetadata.stream().mapToDouble(TrackMetadata::elevationGain).sum();
      float elevationLoss =
          (float) tracksMetadata.stream().mapToDouble(TrackMetadata::elevationLoss).sum();
      TrackMetadata aggregated =
          new TrackMetadata(
              distance,
              elevationGain,
              getHilliness(distance, elevationGain),
              elevationLoss,
              tracksMetadata.getFirst().start(),
              tracksMetadata.getLast().end(),
              windDirection);

      return new ComputedGpx(original, filtered, tracks, waypoints, aggregated);
    } catch (PedalonsException e) {
      deleteQuietly(original);
      deleteQuietly(filtered);
      throw e;
    } catch (Exception e) {
      deleteQuietly(original);
      deleteQuietly(filtered);
      LOG.errorv("GPX computation failed for {0}", gpx.name(), e);
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
  }

  private File writeGpxToTempFile(GPX gpx, boolean filtered) throws IOException {
    Path tempDir = Files.createDirectories(Path.of(System.getProperty("java.io.tmpdir"), TEMP_DIR));
    File file = Files.createTempFile(tempDir, "gpx-", ".gpx").toFile();
    try {
      gpxFileWriter.writeGPX(gpx, file, filtered);
    } catch (Exception e) {
      deleteQuietly(file);
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
    return file;
  }

  /** Result of the CPU/S3 processing phase, passed to the DB persist phase. */
  record PreparedAsset(AssetType type, String fileName, long fileId, String contentType) {}

  record GpxProcessingResult(
      TrackMetadata metadata,
      List<GpxTrack> tracks,
      List<GpxWaypoint> waypoints,
      List<PreparedAsset> uploadedAssets) {}

  /**
   * Phase 1: CPU and S3 work — runs with the caller's transaction SUSPENDED so the DB connection
   * is released during the heavy processing (SRTM, file generation, 5 S3 uploads).
   *
   * <p>All entities returned are transient (not yet attached to Hibernate session).
   */
  @Transactional(Transactional.TxType.NOT_SUPPORTED)
  public GpxProcessingResult processGpxData(Route route, GPX gpx) {
    User creator = pedalonsContext.getUser();
    Long routeId = route.getId();
    Team team = route.getTeam();
    try (ComputedGpx computed = computeGpx(gpx)) {
      List<PreparedAsset> uploadedAssets = new ArrayList<>();

      uploadedAssets.add(
          prepareAndUpload(
              team,
              AssetType.ROUTE_ORIGINAL_GPX,
              "original.gpx",
              tmp -> moveInto(computed.originalGpx(), tmp)));
      LOG.infov("Saved original GPX to S3");

      uploadedAssets.add(
          prepareAndUpload(
              team,
              AssetType.ROUTE_FILTERED_GPX,
              "filtered.gpx",
              tmp -> moveInto(computed.filteredGpx(), tmp)));
      LOG.infov("Saved filtered GPX to S3");

      // Save FIT file
      uploadedAssets.add(
          prepareAndUpload(
              team,
              AssetType.ROUTE_FIT,
              "route.fit",
              tmp -> {
                fitFileWriter.writeGPX(gpx, tmp);
              }));
      LOG.infov("Saved FIT file to S3");

      // Generate thumbnails (failures are non-fatal)
      generateThumbnailAsset(
          team,
          gpx,
          routeId,
          "colorful",
          AssetType.ROUTE_THUMBNAIL_LIGHT,
          "thumbnail-light.png",
          uploadedAssets);
      generateThumbnailAsset(
          team,
          gpx,
          routeId,
          "eclipse",
          AssetType.ROUTE_THUMBNAIL_DARK,
          "thumbnail-dark.png",
          uploadedAssets);

      LOG.infov("GPX processing complete for route {0}", routeId);

      List<GpxWaypoint> waypoints =
          computed.waypoints().stream()
              .map(w -> new GpxWaypoint(creator, w.name(), w.location()))
              .toList();

      List<GpxTrack> tracks =
          computed.tracks().stream()
              .map(
                  t ->
                      new GpxTrack(
                          creator,
                          t.name(),
                          t.line(),
                          t.trackPoints(),
                          t.climbs(),
                          t.metadata().distance(),
                          t.metadata().elevationGain(),
                          t.metadata().elevationLoss()))
              .toList();

      return new GpxProcessingResult(computed.aggregated(), tracks, waypoints, uploadedAssets);
    } catch (PedalonsException e) {
      throw e;
    } catch (Exception e) {
      LOG.errorv("GPX processing failed for route {0}", routeId, e);
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
  }

  /** Hands a temp file over to {@link #prepareAndUpload}'s destination without copying bytes. */
  private static void moveInto(File source, File destination) throws IOException {
    Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
  }

  @FunctionalInterface
  private interface FileWriter {
    void write(File file) throws Exception;
  }

  private PreparedAsset prepareAndUpload(
      Team team, AssetType type, String fileName, FileWriter writer) throws IOException {
    long fileId = TSID.Factory.getTsid().toLong();
    File tmp = assetService.createTempFile(fileId);
    try {
      writer.write(tmp);
    } catch (Exception e) {
      tmp.delete();
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
    String contentType = assetService.uploadTempFileToS3(team, type, fileId, fileName);
    return new PreparedAsset(type, fileName, fileId, contentType);
  }

  /**
   * Phase 2: DB-only work — persists the results of {@link #processGpxData} within the caller's
   * transaction (short burst, no heavy I/O).
   */
  @Transactional(Transactional.TxType.REQUIRED)
  public TrackMetadata persistGpxData(Route route, GpxProcessingResult result) {
    route.getAssets().removeIf(a -> ROUTE_ASSET_TYPES.contains(a.getType()));

    for (PreparedAsset pa : result.uploadedAssets()) {
      assetService.persistAsset(
          route.getTeam(), route, pa.type(), pa.fileId(), pa.fileName(), pa.contentType());
    }

    result.waypoints().forEach(route::addWaypoint);
    result.tracks().forEach(route::addTrack);

    return result.metadata();
  }

  /**
   * Cleans up S3 objects from a partially completed {@link #processGpxData} call. Call this in
   * error handlers when the DB persist phase has not yet run (assets not in DB yet).
   */
  public void cleanupUploadedAssets(GpxProcessingResult result, Team team) {
    for (PreparedAsset pa : result.uploadedAssets()) {
      try {
        storageService.delete(assetService.getAssetKey(team, pa.fileId()));
      } catch (Exception e) {
        LOG.warnf(e, "S3 cleanup failed for fileId %s", pa.fileId());
      }
    }
  }

  /**
   * Convenience facade: runs both phases sequentially. Used by tests and by the {@code createTracks}
   * legacy call sites until they are migrated.
   */
  @Transactional
  public TrackMetadata createTracks(Route route, GPX gpx) {
    GpxProcessingResult result = processGpxData(route, gpx);
    return persistGpxData(route, result);
  }

  private void generateThumbnailAsset(
      Team team,
      GPX gpx,
      Long routeId,
      String style,
      AssetType assetType,
      String fileName,
      List<PreparedAsset> uploadedAssets) {
    try {
      PreparedAsset pa =
          prepareAndUpload(
              team,
              assetType,
              fileName,
              tmp -> {
                String tileUrl = tileserverUrl + "/styles/" + style + "/256/{z}/{x}/{y}.png";
                tileMapProducer.createTileMap(tmp, gpx, tileUrl, 0.1, 512, 512);
              });
      uploadedAssets.add(pa);
      LOG.infov("Generated and saved {0} thumbnail to S3", style);
    } catch (Exception e) {
      LOG.warnv("Thumbnail generation ({0}) failed for route {1}: {2}", style, routeId, e);
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

    float distance = (float) path.getDist();
    float elevationGain = (float) path.getTotalElevation();
    return new TrackMetadata(
        distance,
        elevationGain,
        getHilliness(distance, elevationGain),
        (int) Math.round(path.getTotalElevationNegative()),
        point(WGS84, g(start.getLonDeg(), start.getLatDeg())),
        point(WGS84, g(end.getLonDeg(), end.getLatDeg())),
        null);
  }

  private float getHilliness(float distance, float elevationGain) {
    if (distance == 0) {
      return 0;
    }
    return (1000.0f * elevationGain) / distance;
  }

  private static final List<AssetType> ROUTE_ASSET_TYPES =
      List.of(
          AssetType.ROUTE_ORIGINAL_GPX,
          AssetType.ROUTE_FILTERED_GPX,
          AssetType.ROUTE_FIT,
          AssetType.ROUTE_THUMBNAIL_LIGHT,
          AssetType.ROUTE_THUMBNAIL_DARK);

  /**
   * Delete all files for a route (cleanup on error or deletion).
   * S3 cleanup is handled automatically by AssetRemoveListener via orphanRemoval.
   */
  public void deleteRouteFiles(Route route) {
    route.getAssets().removeIf(asset -> ROUTE_ASSET_TYPES.contains(asset.getType()));
  }

  /**
   * Get InputStream for filtered GPX download.
   */
  public InputStream getFilteredGpxContent(Route route) {
    return getAssetContent(route, AssetType.ROUTE_FILTERED_GPX);
  }

  /**
   * Get InputStream for FIT download.
   */
  public InputStream getFitContent(Route route) {
    return getAssetContent(route, AssetType.ROUTE_FIT);
  }

  private InputStream getAssetContent(Route route, AssetType assetType) {
    Asset matching = getAsset(route, assetType);
    if (matching != null) {
      return assetService.getAssetContent(matching);
    } else {
      throw new NotFoundException(EntityType.ASSET, "forRoute-" + route.getId());
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
