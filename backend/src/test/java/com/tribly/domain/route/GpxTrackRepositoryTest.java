package com.tribly.domain.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.repository.GpxTrackRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxTrackRepositoryTest {

  @Inject GpxTrackRepository gpxTrackRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;
  private Route route;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    team = dataService.createTeam("Test Team", "test-team");
    user = dataService.createUser("test@example.com", "Test User");
    route = dataService.createRoute(team, user, "Test Route");
  }

  @Test
  void findByRoute_shouldReturnTrackWhenExists() {
    List<GpxTrack.TrackPoint> trackPoints =
        List.of(
            new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
            new GpxTrack.TrackPoint(45.1, 6.1, 600.0, 1000.0),
            new GpxTrack.TrackPoint(45.2, 6.2, 700.0, 2000.0));
    String geometry = "LINESTRING(6 45,6.1 45.1,6.2 45.2)";

    dataService.createGpxTrack(route, geometry, trackPoints);

    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(route.getId());

    assertTrue(result.isPresent());
    assertEquals(route.getId(), result.get().getRoute().getId());
    assertEquals(geometry, result.get().getGeometry());
    assertEquals(3, result.get().getTrackPoints().size());
    assertEquals(45.0, result.get().getTrackPoints().get(0).lat());
    assertEquals(6.0, result.get().getTrackPoints().get(0).lng());
  }

  @Test
  void findByRoute_shouldReturnEmptyWhenNoTrack() {
    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(route.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByRoute_shouldReturnEmptyForNonexistentRoute() {
    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(999999L);

    assertTrue(result.isEmpty());
  }

  @Test
  void findByRoute_shouldIgnoreDeletedTracks() {
    List<GpxTrack.TrackPoint> trackPoints = List.of(new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0));
    String geometry = "LINESTRING(6.0 45.0, 6.1 45.1)";

    GpxTrack track = dataService.createGpxTrack(route, geometry, trackPoints);
    dataService.deleteGpxTrack(track);

    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(route.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByRoute_shouldReturnTrackWithAllFields() {
    List<GpxTrack.TrackPoint> trackPoints =
        List.of(
            new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
            new GpxTrack.TrackPoint(45.1, 6.1, 600.0, 1500.0));
    String geometry = "LINESTRING(6.0 45.0, 6.1 45.1)";

    dataService.createGpxTrackWithName(
        route, "Mountain Route", geometry, trackPoints, "mountain_route.gpx");

    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(route.getId());

    assertTrue(result.isPresent());
    assertEquals("Mountain Route", result.get().getName());
    assertEquals("mountain_route.gpx", result.get().getOriginalFileName());
    assertNotNull(result.get().getProcessedAt());
    assertEquals(2, result.get().getTrackPoints().size());
  }

  @Test
  void findByRoute_shouldReturnOnlyTrackForSpecificRoute() {
    Route otherRoute = dataService.createRoute(team, user, "Other Route");
    List<GpxTrack.TrackPoint> trackPoints = List.of(new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0));
    String geometry = "LINESTRING(6 45,6.1 45.1)";

    dataService.createGpxTrack(route, geometry, trackPoints);
    dataService.createGpxTrack(otherRoute, "LINESTRING(7.0 46.0, 7.1 46.1)", trackPoints);

    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(route.getId());

    assertTrue(result.isPresent());
    assertEquals(route.getId(), result.get().getRoute().getId());
    assertEquals(geometry, result.get().getGeometry());
  }

  @Test
  void findByRoute_shouldHandleComplexGeometry() {
    List<GpxTrack.TrackPoint> trackPoints =
        List.of(
            new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
            new GpxTrack.TrackPoint(45.1, 6.1, 600.0, 1000.0),
            new GpxTrack.TrackPoint(45.2, 6.2, 700.0, 2000.0),
            new GpxTrack.TrackPoint(45.3, 6.3, 800.0, 3000.0),
            new GpxTrack.TrackPoint(45.4, 6.4, 900.0, 4000.0));
    String geometry = "LINESTRING(6.0 45.0, 6.1 45.1, 6.2 45.2, 6.3 45.3, 6.4 45.4)";

    dataService.createGpxTrack(route, geometry, trackPoints);

    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(route.getId());

    assertTrue(result.isPresent());
    assertEquals(5, result.get().getTrackPoints().size());
    assertEquals(0.0, result.get().getTrackPoints().get(0).dist());
    assertEquals(4000.0, result.get().getTrackPoints().get(4).dist());
    assertEquals(500.0, result.get().getTrackPoints().get(0).ele());
    assertEquals(900.0, result.get().getTrackPoints().get(4).ele());
  }

  @Test
  void findByRoute_shouldHandleTrackWithoutOptionalFields() {
    List<GpxTrack.TrackPoint> trackPoints = List.of(new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0));
    String geometry = "LINESTRING(6.0 45.0, 6.1 45.1)";

    dataService.createGpxTrack(route, geometry, trackPoints);

    Optional<GpxTrack> result = gpxTrackRepository.findByRoute(route.getId());

    assertTrue(result.isPresent());
    assertNull(result.get().getName());
    assertNull(result.get().getOriginalFileName());
    assertNotNull(result.get().getProcessedAt());
  }
}
