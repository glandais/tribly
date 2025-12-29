package com.tribly.domain.route;

import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.repository.GpxTrackRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.geolatte.geom.codec.Wkt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxTrackRepositoryTest {

  @Inject GpxTrackRepository gpxTrackRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Test
  void findByRoute_shouldReturnTrackWhenExists() {
    List<GpxTrack.TrackPoint> trackPoints =
        List.of(
            new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
            new GpxTrack.TrackPoint(45.1, 6.1, 600.0, 1000.0),
            new GpxTrack.TrackPoint(45.2, 6.2, 700.0, 2000.0));
    String geometry = "LINESTRING(6 45,6.1 45.1,6.2 45.2)";

    Route route =
        dataService.createRoute(team, user, "test", Visibility.PUBLIC, geometry, trackPoints);

    GpxTrack result = gpxTrackRepository.findByRoute(route.getId());

    assertNotNull(result);
    assertEquals(route.getId(), result.getRoute().getId());
    assertEquals(Wkt.fromWkt(geometry, WGS84), result.getGeometry());
    assertEquals(3, result.getTrackPoints().size());
    assertEquals(45.0, result.getTrackPoints().get(0).lat());
    assertEquals(6.0, result.getTrackPoints().get(0).lng());
  }

  @Test
  void findByRoute_shouldReturnTrackWithAllFields() {
    List<GpxTrack.TrackPoint> trackPoints =
        List.of(
            new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
            new GpxTrack.TrackPoint(45.1, 6.1, 600.0, 1500.0));
    String geometry = "LINESTRING(6.0 45.0, 6.1 45.1)";

    Route route =
        dataService.createRoute(
            team, user, "Mountain Route", Visibility.PUBLIC, geometry, trackPoints);

    GpxTrack result = gpxTrackRepository.findByRoute(route.getId());

    assertNotNull(result);
    assertNotNull(result.getProcessedAt());
    assertEquals(2, result.getTrackPoints().size());
  }

  @Test
  void findByRoute_shouldReturnOnlyTrackForSpecificRoute() {
    List<GpxTrack.TrackPoint> trackPoints = List.of(new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0));
    String geometry = "LINESTRING(6 45,6.1 45.1)";

    Route route =
        dataService.createRoute(team, user, "route1", Visibility.PUBLIC, geometry, trackPoints);
    dataService.createRoute(team, user, "route2", Visibility.PUBLIC, geometry, trackPoints);

    GpxTrack result = gpxTrackRepository.findByRoute(route.getId());

    assertNotNull(result);
    assertEquals(route.getId(), result.getRoute().getId());
    assertEquals(Wkt.fromWkt(geometry, WGS84), result.getGeometry());
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

    Route route =
        dataService.createRoute(team, user, "route1", Visibility.PUBLIC, geometry, trackPoints);

    GpxTrack result = gpxTrackRepository.findByRoute(route.getId());

    assertNotNull(result);
    assertEquals(5, result.getTrackPoints().size());
    assertEquals(0.0, result.getTrackPoints().get(0).dist());
    assertEquals(4000.0, result.getTrackPoints().get(4).dist());
    assertEquals(500.0, result.getTrackPoints().get(0).ele());
    assertEquals(900.0, result.getTrackPoints().get(4).ele());
  }
}
