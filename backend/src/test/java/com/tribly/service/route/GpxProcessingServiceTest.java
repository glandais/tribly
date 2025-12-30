package com.tribly.service.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ClimbCategory;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.asset.AssetService;
import com.tribly.service.route.response.ProcessedGpx;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.github.glandais.gpx.climb.Climb;
import io.github.glandais.gpx.climb.ClimbParts;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.geolatte.geom.G2D;
import org.geolatte.geom.LineString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxProcessingServiceTest {

  @Inject GpxProcessingService gpxProcessingService;
  @Inject AssetService assetService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;
  private Route route;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user, team, TeamRole.ADMIN);
    route = dataService.createRoute(team, user, "route", Visibility.PUBLIC);
  }

  @AfterEach
  void cleanup() {
    gpxProcessingService.deleteRouteFiles(route);
  }

  // ==================== Process GPX Upload ====================

  @Test
  void processGpxUpload_shouldProcessValidGpx() throws IOException {
    InputStream gpxStream = getExampleGpxStream();

    ProcessedGpx result = gpxProcessingService.processGpxUpload(user, route, gpxStream);

    assertNotNull(result);
    assertNotNull(result.geometry());
    assertFalse(result.trackPoints().isEmpty());
    assertNotNull(result.metadata());
    assertTrue(result.metadata().distance() > 0);
    assertTrue(result.metadata().elevationGain() >= 0);
  }

  private InputStream getExampleGpxStream() {
    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    assertNotNull(resourceAsStream, "example.gpx not found in test resources");
    return resourceAsStream;
  }

  @Test
  void processGpxUpload_shouldExtractCorrectMetadata() throws IOException {
    InputStream gpxStream = getExampleGpxStream();

    ProcessedGpx result = gpxProcessingService.processGpxUpload(user, route, gpxStream);

    // Verify metadata structure
    assertNotNull(result.metadata());
    assertTrue(result.metadata().distance() > 0, "Distance should be positive");
    assertTrue(result.metadata().elevationGain() >= 0, "Elevation gain should be non-negative");
    assertTrue(result.metadata().elevationLoss() <= 0, "Elevation loss should be non-negative");

    // Verify coordinates are in valid ranges
    G2D start = result.metadata().start().getPosition();
    G2D end = result.metadata().end().getPosition();
    assertTrue(start.getLat() >= -90 && start.getLat() <= 90, "Start latitude should be valid");
    assertTrue(start.getLon() >= -180 && start.getLon() <= 180, "Start longitude should be valid");
    assertTrue(end.getLat() >= -90 && end.getLat() <= 90, "End latitude should be valid");
    assertTrue(end.getLon() >= -180 && end.getLon() <= 180, "End longitude should be valid");
  }

  @Test
  void processGpxUpload_shouldGenerateValidWKT() throws IOException {
    InputStream gpxStream = getExampleGpxStream();

    ProcessedGpx result = gpxProcessingService.processGpxUpload(user, route, gpxStream);

    LineString<G2D> lineString = result.geometry();
    assertNotNull(lineString);

    for (G2D point : lineString.getPositions()) {
      double lng = point.getLon();
      double lat = point.getLat();
      assertTrue(lng >= -180 && lng <= 180, "Longitude should be valid");
      assertTrue(lat >= -90 && lat <= 90, "Latitude should be valid");
    }
  }

  @Test
  void processGpxUpload_shouldGenerateTrackPoints() throws IOException {
    InputStream gpxStream = getExampleGpxStream();

    ProcessedGpx result = gpxProcessingService.processGpxUpload(user, route, gpxStream);

    assertFalse(result.trackPoints().isEmpty(), "Track points should not be empty");

    result
        .trackPoints()
        .forEach(
            tp -> {
              assertTrue(tp.lat() >= -90 && tp.lat() <= 90, "Latitude should be valid");
              assertTrue(tp.lng() >= -180 && tp.lng() <= 180, "Longitude should be valid");
              assertTrue(tp.dist() >= 0, "Distance should be non-negative");
            });
  }

  @Test
  void processGpxUpload_shouldCreateFiles() throws IOException {
    InputStream gpxStream = getExampleGpxStream();

    gpxProcessingService.processGpxUpload(user, route, gpxStream);

    Set<Asset> assets = route.getAssets();
    for (Asset asset : assets) {
      File file = assetService.getAssetFile(asset);
      assertTrue(file.exists(), "File should exists");
    }
  }

  @Test
  void processGpxUpload_shouldThrowForEmptyGpx() {
    String emptyGpx =
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <gpx version="1.1" xmlns="http://www.topografix.com/GPX/1/1">
        </gpx>
        """;
    InputStream gpxStream = new ByteArrayInputStream(emptyGpx.getBytes());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> gpxProcessingService.processGpxUpload(user, route, gpxStream));

    assertTrue(exception.getMessage().contains("no tracks"));
  }

  @Test
  void processGpxUpload_shouldThrowForInvalidGpx() {
    String invalidGpx = "not a valid gpx file";
    InputStream gpxStream = new ByteArrayInputStream(invalidGpx.getBytes());

    assertThrows(
        BusinessException.class,
        () -> gpxProcessingService.processGpxUpload(user, route, gpxStream));
  }

  // ==================== Categorize Climb ====================

  @Test
  void categorizeClimb_shouldReturnHCForHighElevation() {
    Climb climb = createMockClimb(1600, 0, 5.0); // > 1500m elevation

    ClimbCategory result = gpxProcessingService.categorizeClimb(climb);

    assertEquals(ClimbCategory.HC, result);
  }

  @Test
  void categorizeClimb_shouldReturnHCForHighElevationAndGrade() {
    Climb climb = createMockClimb(1100, 0, 8.5); // > 1000m and > 8% grade

    ClimbCategory result = gpxProcessingService.categorizeClimb(climb);

    assertEquals(ClimbCategory.HC, result);
  }

  @Test
  void categorizeClimb_shouldReturnCAT1() {
    Climb climb = createMockClimb(900, 0, 5.0); // 800-1500m

    ClimbCategory result = gpxProcessingService.categorizeClimb(climb);

    assertEquals(ClimbCategory.CAT1, result);
  }

  @Test
  void categorizeClimb_shouldReturnCAT2() {
    Climb climb = createMockClimb(600, 0, 5.0); // 500-800m

    ClimbCategory result = gpxProcessingService.categorizeClimb(climb);

    assertEquals(ClimbCategory.CAT2, result);
  }

  @Test
  void categorizeClimb_shouldReturnCAT3() {
    Climb climb = createMockClimb(400, 0, 5.0); // 300-500m

    ClimbCategory result = gpxProcessingService.categorizeClimb(climb);

    assertEquals(ClimbCategory.CAT3, result);
  }

  @Test
  void categorizeClimb_shouldReturnCAT4() {
    Climb climb = createMockClimb(200, 0, 5.0); // < 300m

    ClimbCategory result = gpxProcessingService.categorizeClimb(climb);

    assertEquals(ClimbCategory.CAT4, result);
  }

  // ==================== File Management ====================

  @Test
  void getFilteredGpxFile_shouldReturnFileIfExists() throws IOException {
    InputStream gpxStream = getExampleGpxStream();
    gpxProcessingService.processGpxUpload(user, route, gpxStream);

    File result = gpxProcessingService.getFilteredGpxFile(route);

    assertNotNull(result);
    assertTrue(result.exists());
  }

  @Test
  void getFilteredGpxFile_shouldThrowIfNotExists() {
    assertThrows(BusinessException.class, () -> gpxProcessingService.getFilteredGpxFile(route));
  }

  @Test
  void getFitFile_shouldReturnFileIfExists() throws IOException {
    InputStream gpxStream = getExampleGpxStream();
    gpxProcessingService.processGpxUpload(user, route, gpxStream);

    File result = gpxProcessingService.getFitFile(route);

    assertNotNull(result);
    assertTrue(result.exists());
  }

  @Test
  void getFitFile_shouldThrowIfNotExists() {
    assertThrows(BusinessException.class, () -> gpxProcessingService.getFitFile(route));
  }

  @Test
  void getThumbnailFile_shouldThrowIfNotExists() {
    assertThrows(BusinessException.class, () -> gpxProcessingService.getThumbnailFile(route));
  }

  @Test
  void deleteRouteFiles_shouldNotThrowIfDirectoryNotExists() {
    assertDoesNotThrow(() -> gpxProcessingService.deleteRouteFiles(route));
  }

  // ==================== Helper Methods ====================

  private Climb createMockClimb(double positiveElevation, double negativeElevation, double grade) {
    return new Climb(
        0, 0, grade, 0, 0, 0, positiveElevation, negativeElevation, grade, 0, new ClimbParts());
  }
}
