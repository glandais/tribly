package com.tribly.service.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.Route;
import com.tribly.enums.ClimbCategory;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.route.response.ProcessedGpx;
import io.github.glandais.gpx.climb.Climb;
import io.github.glandais.gpx.climb.ClimbParts;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxProcessingServiceTest {

  @Inject GpxProcessingService gpxProcessingService;

  // FIXME
  private static final Route route = new Route();

  @AfterEach
  void cleanup() {
    gpxProcessingService.deleteRouteFiles(route);
  }

  // ==================== Process GPX Upload ====================

  @Test
  void processGpxUpload_shouldProcessValidGpx() throws IOException {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    assertNotNull(gpxStream, "example.gpx not found in test resources");

    ProcessedGpx result = gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

    assertNotNull(result);
    assertNotNull(result.wkt());
    assertTrue(result.wkt().startsWith("LINESTRING("));
    assertTrue(result.wkt().endsWith(")"));
    assertFalse(result.trackPoints().isEmpty());
    assertNotNull(result.metadata());
    assertTrue(result.metadata().distance() > 0);
    assertTrue(result.metadata().elevationGain() >= 0);
  }

  @Test
  void processGpxUpload_shouldExtractCorrectMetadata() throws IOException {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");

    ProcessedGpx result = gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

    // Verify metadata structure
    assertNotNull(result.metadata());
    assertTrue(result.metadata().distance() > 0, "Distance should be positive");
    assertTrue(result.metadata().elevationGain() >= 0, "Elevation gain should be non-negative");
    assertTrue(result.metadata().elevationLoss() <= 0, "Elevation loss should be non-negative");

    // Verify coordinates are in valid ranges
    assertTrue(
        result.metadata().startLat() >= -90 && result.metadata().startLat() <= 90,
        "Start latitude should be valid");
    assertTrue(
        result.metadata().startLng() >= -180 && result.metadata().startLng() <= 180,
        "Start longitude should be valid");
    assertTrue(
        result.metadata().endLat() >= -90 && result.metadata().endLat() <= 90,
        "End latitude should be valid");
    assertTrue(
        result.metadata().endLng() >= -180 && result.metadata().endLng() <= 180,
        "End longitude should be valid");
  }

  @Test
  void processGpxUpload_shouldGenerateValidWKT() throws IOException {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");

    ProcessedGpx result = gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

    String wkt = result.wkt();
    assertTrue(wkt.startsWith("LINESTRING("));
    assertTrue(wkt.endsWith(")"));
    assertTrue(wkt.contains(" "), "WKT should contain space-separated coordinates");
    assertTrue(wkt.contains(","), "WKT should contain comma-separated points");

    // Verify WKT contains valid longitude/latitude pairs
    String coords = wkt.substring("LINESTRING(".length(), wkt.length() - 1);
    String[] points = coords.split(",");
    assertTrue(points.length > 0, "WKT should contain at least one point");

    for (String point : points) {
      String[] lngLat = point.trim().split(" ");
      assertEquals(2, lngLat.length, "Each point should have longitude and latitude");
      double lng = Double.parseDouble(lngLat[0]);
      double lat = Double.parseDouble(lngLat[1]);
      assertTrue(lng >= -180 && lng <= 180, "Longitude should be valid");
      assertTrue(lat >= -90 && lat <= 90, "Latitude should be valid");
    }
  }

  @Test
  void processGpxUpload_shouldGenerateTrackPoints() throws IOException {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");

    ProcessedGpx result = gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

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
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");

    gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

    Path routeDir = Path.of("/tmp", "tribly-data-gpx-test", "routes", route.toString());
    assertTrue(Files.exists(routeDir), "Route directory should exist");
    assertTrue(Files.exists(routeDir.resolve("original.gpx")), "Original GPX should exist");
    assertTrue(Files.exists(routeDir.resolve("filtered.gpx")), "Filtered GPX should exist");
    assertTrue(Files.exists(routeDir.resolve("route.fit")), "FIT file should exist");
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
            () -> gpxProcessingService.processGpxUpload(route, gpxStream, "empty.gpx"));

    assertTrue(exception.getMessage().contains("no tracks"));
  }

  @Test
  void processGpxUpload_shouldThrowForInvalidGpx() {
    String invalidGpx = "not a valid gpx file";
    InputStream gpxStream = new ByteArrayInputStream(invalidGpx.getBytes());

    assertThrows(
        BusinessException.class,
        () -> gpxProcessingService.processGpxUpload(route, gpxStream, "invalid.gpx"));
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
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

    File result = gpxProcessingService.getFilteredGpxFile(route);

    assertNotNull(result);
    assertTrue(result.exists());
    assertTrue(result.getName().equals("filtered.gpx"));
  }

  @Test
  void getFilteredGpxFile_shouldThrowIfNotExists() {
    assertThrows(BusinessException.class, () -> gpxProcessingService.getFilteredGpxFile(route));
  }

  @Test
  void getFitFile_shouldReturnFileIfExists() throws IOException {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

    File result = gpxProcessingService.getFitFile(route);

    assertNotNull(result);
    assertTrue(result.exists());
    assertTrue(result.getName().equals("route.fit"));
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
  void deleteRouteFiles_shouldDeleteAllFiles() throws IOException {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    gpxProcessingService.processGpxUpload(route, gpxStream, "example.gpx");

    Path routeDir = Path.of("/tmp", "tribly-data-gpx-test", "routes", route.toString());
    assertTrue(Files.exists(routeDir), "Route directory should exist before deletion");

    gpxProcessingService.deleteRouteFiles(route);

    assertFalse(Files.exists(routeDir), "Route directory should not exist after deletion");
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
