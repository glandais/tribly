package com.tribly.service.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.common.exception.TriblyException;
import com.tribly.domain.asset.Asset;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.service.asset.AssetService;
import com.tribly.service.route.response.TrackMetadata;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.github.glandais.gpx.data.GPX;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.geolatte.geom.G2D;
import org.geolatte.geom.LineString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxProcessingServiceTest {

  @Inject TriblyQueryContext context;
  @Inject GpxProcessingService gpxProcessingService;
  @Inject AssetService assetService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User user;
  private Route route;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    route = dataService.createRoute(team, user, "route", Visibility.PUBLIC);
    route.getTracks().clear();
    dataService.updateRoute(route);
  }

  @AfterEach
  void cleanup() {
    gpxProcessingService.deleteRouteFiles(route);
  }

  // ==================== Create Tracks - Single Track ====================

  @Test
  void createTracks_shouldProcessValidGpx() {
    Path gpxPath = getExampleGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    TrackMetadata result = gpxProcessingService.createTracks(route, gpx);

    assertNotNull(result);
    assertTrue(result.distance() > 0);
    assertTrue(result.elevationGain() >= 0);
    assertNotNull(result.start());
    assertNotNull(result.end());

    List<GpxTrack> tracks = route.getTracks();
    assertFalse(tracks.isEmpty());
    assertEquals(1, tracks.size());
  }

  @Test
  void createTracks_shouldExtractCorrectMetadata() {
    Path gpxPath = getExampleGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    TrackMetadata result = gpxProcessingService.createTracks(route, gpx);

    assertNotNull(result);
    assertTrue(result.distance() > 0, "Distance should be positive");
    assertTrue(result.elevationGain() >= 0, "Elevation gain should be non-negative");
    assertTrue(result.elevationLoss() <= 0, "Elevation loss should be negative");

    // Verify coordinates are in valid ranges
    G2D start = result.start().getPosition();
    G2D end = result.end().getPosition();
    assertTrue(start.getLat() >= -90 && start.getLat() <= 90, "Start latitude should be valid");
    assertTrue(start.getLon() >= -180 && start.getLon() <= 180, "Start longitude should be valid");
    assertTrue(end.getLat() >= -90 && end.getLat() <= 90, "End latitude should be valid");
    assertTrue(end.getLon() >= -180 && end.getLon() <= 180, "End longitude should be valid");
  }

  @Test
  void createTracks_shouldGenerateValidGeometry() {
    Path gpxPath = getExampleGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    gpxProcessingService.createTracks(route, gpx);

    GpxTrack track = route.getTracks().getFirst();
    LineString<G2D> lineString = track.getGeometry();
    assertNotNull(lineString);

    for (G2D point : lineString.getPositions()) {
      double lng = point.getLon();
      double lat = point.getLat();
      assertTrue(lng >= -180 && lng <= 180, "Longitude should be valid");
      assertTrue(lat >= -90 && lat <= 90, "Latitude should be valid");
    }
  }

  @Test
  void createTracks_shouldGenerateTrackPoints() {
    Path gpxPath = getExampleGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    gpxProcessingService.createTracks(route, gpx);

    GpxTrack track = route.getTracks().getFirst();
    List<GpxTrack.TrackPoint> trackPoints = track.getTrackPoints();
    assertFalse(trackPoints.isEmpty(), "Track points should not be empty");

    trackPoints.forEach(
        tp -> {
          assertTrue(tp.lat() >= -90 && tp.lat() <= 90, "Latitude should be valid");
          assertTrue(tp.lng() >= -180 && tp.lng() <= 180, "Longitude should be valid");
          assertTrue(tp.dist() >= 0, "Distance should be non-negative");
        });
  }

  @Test
  void createTracks_shouldStoreTrackMetrics() {
    Path gpxPath = getExampleGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    gpxProcessingService.createTracks(route, gpx);

    GpxTrack track = route.getTracks().getFirst();
    assertTrue(track.getDistance() > 0, "Track distance should be positive");
    assertTrue(track.getElevationGain() >= 0, "Track elevation gain should be non-negative");
    assertTrue(track.getElevationLoss() <= 0, "Track elevation loss should be negative");
    assertNotNull(track.getClimbs(), "Track climbs should not be null");
  }

  @Test
  void createTracks_shouldCreateFiles() {
    Path gpxPath = getExampleGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    gpxProcessingService.createTracks(route, gpx);

    Set<Asset> assets = route.getAssets();
    assertFalse(assets.isEmpty(), "Route should have assets");
    for (Asset asset : assets) {
      File file = assetService.getAssetFile(asset);
      assertTrue(file.exists(), "File should exist: " + asset.getType());
    }
  }

  // ==================== Create Tracks - Multiple Tracks ====================

  @Test
  void createTracks_shouldProcessMultipleTracks() {
    Path gpxPath = getTwoTracksGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    TrackMetadata result = gpxProcessingService.createTracks(route, gpx);

    assertNotNull(result);
    List<GpxTrack> tracks = route.getTracks();
    assertEquals(2, tracks.size(), "Should have 2 tracks");

    // Check each track has valid data
    for (GpxTrack track : tracks) {
      assertNotNull(track.getName(), "Track should have a name");
      assertNotNull(track.getGeometry(), "Track should have geometry");
      assertFalse(track.getTrackPoints().isEmpty(), "Track should have track points");
      assertTrue(track.getDistance() > 0, "Track distance should be positive");
    }
  }

  @Test
  void createTracks_shouldAggregateMetadataFromMultipleTracks() {
    Path gpxPath = getTwoTracksGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    TrackMetadata result = gpxProcessingService.createTracks(route, gpx);

    List<GpxTrack> tracks = route.getTracks();
    float totalDistance = (float) tracks.stream().mapToDouble(GpxTrack::getDistance).sum();
    float totalElevationGain =
        (float) tracks.stream().mapToDouble(GpxTrack::getElevationGain).sum();
    float totalElevationLoss =
        (float) tracks.stream().mapToDouble(GpxTrack::getElevationLoss).sum();

    assertEquals(totalDistance, result.distance(), "Distance should be sum of all tracks");
    assertEquals(
        totalElevationGain, result.elevationGain(), "Elevation gain should be sum of all tracks");
    assertEquals(
        totalElevationLoss, result.elevationLoss(), "Elevation loss should be sum of all tracks");
  }

  @Test
  void createTracks_shouldUseFirstTrackStartAndLastTrackEnd() {
    Path gpxPath = getTwoTracksGpxPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    TrackMetadata result = gpxProcessingService.createTracks(route, gpx);

    List<GpxTrack> tracks = route.getTracks();
    GpxTrack firstTrack = tracks.getFirst();
    GpxTrack lastTrack = tracks.getLast();

    // Start should be from first track's first point
    GpxTrack.TrackPoint firstPoint = firstTrack.getTrackPoints().getFirst();
    assertEquals(firstPoint.lat(), result.start().getPosition().getLat(), 0.0001);
    assertEquals(firstPoint.lng(), result.start().getPosition().getLon(), 0.0001);

    // End should be from last track's last point
    GpxTrack.TrackPoint lastPoint = lastTrack.getTrackPoints().getLast();
    assertEquals(lastPoint.lat(), result.end().getPosition().getLat(), 0.0001);
    assertEquals(lastPoint.lng(), result.end().getPosition().getLon(), 0.0001);
  }

  // ==================== Error Cases ====================

  @Test
  void createTracks_shouldThrowForEmptyGpx() {
    Path gpxPath = new File("src/test/resources/empty.gpx").toPath();

    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    TriblyException exception =
        assertThrows(TriblyException.class, () -> gpxProcessingService.createTracks(route, gpx));

    assertTrue(exception.getMessage().contains("GPX_FAILURE"));
  }

  @Test
  void createTracks_shouldThrowForInvalidGpx() {
    Path gpxPath = new File("src/test/resources/invalid.gpx").toPath();
    assertThrows(TriblyException.class, () -> gpxProcessingService.parseGpx(gpxPath));
  }

  // ==================== File Management ====================

  @Test
  void getFilteredGpxFile_shouldReturnFileIfExists() {
    Path gpxPath = getExampleGpxPath();
    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    gpxProcessingService.createTracks(route, gpx);

    File result = gpxProcessingService.getFilteredGpxFile(route);

    assertNotNull(result);
    assertTrue(result.exists());
  }

  @Test
  void getFilteredGpxFile_shouldThrowIfNotExists() {
    assertThrows(TriblyException.class, () -> gpxProcessingService.getFilteredGpxFile(route));
  }

  @Test
  void getFitFile_shouldReturnFileIfExists() {
    Path gpxPath = getExampleGpxPath();
    GPX gpx = gpxProcessingService.parseGpx(gpxPath);
    context.setUserForTest(user);
    gpxProcessingService.createTracks(route, gpx);

    File result = gpxProcessingService.getFitFile(route);

    assertNotNull(result);
    assertTrue(result.exists());
  }

  @Test
  void getFitFile_shouldThrowIfNotExists() {
    assertThrows(TriblyException.class, () -> gpxProcessingService.getFitFile(route));
  }

  @Test
  void getThumbnailFile_shouldThrowIfNotExists() {
    assertThrows(TriblyException.class, () -> gpxProcessingService.getThumbnailFile(route));
  }

  @Test
  void deleteRouteFiles_shouldNotThrowIfDirectoryNotExists() {
    assertDoesNotThrow(() -> gpxProcessingService.deleteRouteFiles(route));
  }

  // ==================== Helper Methods ====================

  private Path getExampleGpxPath() {
    return new File("src/test/resources/example.gpx").toPath();
  }

  private Path getTwoTracksGpxPath() {
    return new File("src/test/resources/two_tracks.gpx").toPath();
  }
}
