package com.tribly.service.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.routes.request.CreateRouteRequest;
import com.tribly.dto.routes.request.UpdateRouteRequest;
import com.tribly.dto.routes.response.ClimbListResponse;
import com.tribly.dto.routes.response.GpxTrackDto;
import com.tribly.dto.routes.response.RouteDto;
import com.tribly.dto.routes.response.RouteListResponse;
import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteServiceTest {

  @Inject RouteService routeService;
  @Inject GpxProcessingService gpxProcessingService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User admin;
  private User organizer;
  private User member;
  private RouteDto createdRoute;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    team = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    admin = dataService.createUser("admin@example.com", "Admin");
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    dataService.addUserToTeam(admin, team, TeamRole.ADMIN);
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @AfterEach
  void cleanup() {
    if (createdRoute != null) {
      gpxProcessingService.deleteRouteFiles(getCreatedRouteId());
    }
  }

  private Long getCreatedRouteId() {
    return TsidUtils.toLong(createdRoute.id());
  }

  // ==================== Create Route ====================

  @Test
  void createRoute_shouldCreateWithGpxProcessing() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest(
            "Test Route",
            "A test route",
            RouteDifficulty.MODERATE,
            SurfaceType.GRAVEL,
            Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", organizer.getId());

    assertNotNull(createdRoute);
    assertEquals("Test Route", createdRoute.name());
    assertEquals("A test route", createdRoute.description());
    assertEquals(RouteDifficulty.MODERATE, createdRoute.difficulty());
    assertEquals(SurfaceType.GRAVEL, createdRoute.surfaceType());
    assertEquals(Visibility.PUBLIC, createdRoute.visibility());
    assertTrue(createdRoute.distance() > 0);
    assertTrue(createdRoute.elevationGain() >= 0);
    assertNotNull(createdRoute.thumbnailUrl());
  }

  @Test
  void createRoute_shouldSaveGpxTrackAndClimbs() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Route with Track", null, null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", organizer.getId());

    // Verify GPX track was saved
    GpxTrackDto track = routeService.getTrack("test-team", getCreatedRouteId(), organizer.getId());
    assertNotNull(track);
    assertEquals("Route with Track", track.name());
    assertNotNull(track.trackPoints());
    assertFalse(track.trackPoints().isEmpty());

    // Verify climbs were saved
    ClimbListResponse climbs =
        routeService.getClimbs("test-team", getCreatedRouteId(), organizer.getId());
    assertNotNull(climbs);
    // example.gpx may or may not have climbs, just verify method works
  }

  @Test
  void createRoute_shouldThrowForNonOrganizer() {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Test", null, null, null, Visibility.PUBLIC);

    assertThrows(
        BusinessException.class,
        () ->
            routeService.createRoute(
                "test-team", request, gpxStream, "example.gpx", member.getId()));
  }

  @Test
  void createRoute_shouldThrowForNonexistentTeam() {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Test", null, null, null, Visibility.PUBLIC);

    assertThrows(
        BusinessException.class,
        () ->
            routeService.createRoute(
                "nonexistent", request, gpxStream, "example.gpx", admin.getId()));
  }

  // ==================== Get Route ====================

  @Test
  void getRoute_shouldReturnRouteForMember() {
    Route route =
        dataService.createRouteWithVisibility(team, admin, "Public Route", Visibility.PUBLIC);

    RouteDto result = routeService.getRoute("test-team", route.getId(), member.getId());

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldReturnRouteForNonMemberIfPublic() {
    Route route =
        dataService.createRouteWithVisibility(team, admin, "Public Route", Visibility.PUBLIC);

    RouteDto result = routeService.getRoute("test-team", route.getId(), null);

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldHideTeamRouteFromNonMembers() {
    Route route = dataService.createRouteWithVisibility(team, admin, "Team Route", Visibility.TEAM);

    assertThrows(
        BusinessException.class, () -> routeService.getRoute("test-team", route.getId(), null));
  }

  @Test
  void getRoute_shouldThrowForNonexistentRoute() {
    assertThrows(
        BusinessException.class, () -> routeService.getRoute("test-team", 999999L, admin.getId()));
  }

  // ==================== List Routes ====================

  @Test
  void getRoutes_shouldReturnPublicRoutesForNonMembers() {
    dataService.createRouteWithVisibility(team, admin, "Public 1", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, admin, "Public 2", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, admin, "Team Only", Visibility.TEAM);

    RouteListResponse result = routeService.getRoutes("test-team", null, 0, 10);

    assertEquals(2, result.routes().size());
    assertTrue(result.routes().stream().allMatch(r -> r.visibility() == Visibility.PUBLIC));
  }

  @Test
  void getRoutes_shouldReturnAllRoutesForMembers() {
    dataService.createRouteWithVisibility(team, admin, "Public", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, admin, "Team", Visibility.TEAM);

    RouteListResponse result = routeService.getRoutes("test-team", member.getId(), 0, 10);

    assertEquals(2, result.routes().size());
  }

  @Test
  void getRoutes_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createRouteWithVisibility(team, admin, "Route " + i, Visibility.PUBLIC);
    }

    RouteListResponse result = routeService.getRoutes("test-team", null, 0, 3);

    assertEquals(3, result.routes().size());
    assertEquals(5, result.total());
  }

  @Test
  void getRoutes_shouldThrowForNonMemberOfPrivateTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.createRoute(privateTeam, admin, "Route");

    assertThrows(
        BusinessException.class, () -> routeService.getRoutes("private-team", null, 0, 10));
  }

  // ==================== Update Route ====================

  @Test
  void updateRoute_shouldUpdateAllFields() {
    Route route = dataService.createRoute(team, admin, "Original");
    UpdateRouteRequest request =
        new UpdateRouteRequest(
            "Updated Name",
            "Updated description",
            RouteDifficulty.HARD,
            SurfaceType.GRAVEL,
            Visibility.TEAM);

    RouteDto result =
        routeService.updateRoute("test-team", route.getId(), request, organizer.getId());

    assertEquals("Updated Name", result.name());
    assertEquals("Updated description", result.description());
    assertEquals(RouteDifficulty.HARD, result.difficulty());
    assertEquals(SurfaceType.GRAVEL, result.surfaceType());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateRoute_shouldUpdatePartialFields() {
    Route route = dataService.createRoute(team, admin, "Original");
    UpdateRouteRequest request = new UpdateRouteRequest("New Name", null, null, null, null);

    RouteDto result =
        routeService.updateRoute("test-team", route.getId(), request, organizer.getId());

    assertEquals("New Name", result.name());
  }

  @Test
  void updateRoute_shouldPreserveFieldsWhenNull() {
    Route route = dataService.createRouteWithVisibility(team, admin, "Original", Visibility.PUBLIC);
    UpdateRouteRequest request = new UpdateRouteRequest(null, "New description", null, null, null);

    RouteDto result =
        routeService.updateRoute("test-team", route.getId(), request, organizer.getId());

    assertEquals("Original", result.name());
    assertEquals("New description", result.description());
    assertEquals(Visibility.PUBLIC, result.visibility());
  }

  @Test
  void updateRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");
    UpdateRouteRequest request = new UpdateRouteRequest("New", null, null, null, null);

    assertThrows(
        BusinessException.class,
        () -> routeService.updateRoute("test-team", route.getId(), request, member.getId()));
  }

  // ==================== Delete Route ====================

  @Test
  void deleteRoute_shouldSoftDeleteRoute() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("To Delete", null, null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());
    Long routeId = getCreatedRouteId();

    routeService.deleteRoute("test-team", routeId, admin.getId());

    assertThrows(
        BusinessException.class, () -> routeService.getRoute("test-team", routeId, admin.getId()));

    // Cleanup handled by gpxProcessingService.deleteRouteFiles
    createdRoute = null; // Prevent double cleanup in @AfterEach
  }

  @Test
  void deleteRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");

    assertThrows(
        BusinessException.class,
        () -> routeService.deleteRoute("test-team", route.getId(), member.getId()));
  }

  // ==================== Get Climbs ====================

  @Test
  void getClimbs_shouldReturnClimbsForRoute() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Route", null, null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    ClimbListResponse climbs =
        routeService.getClimbs("test-team", getCreatedRouteId(), member.getId());

    assertNotNull(climbs);
    // example.gpx may or may not have climbs, just verify method works
  }

  // ==================== Get Track ====================

  @Test
  void getTrack_shouldReturnGpxTrack() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Route", null, null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    GpxTrackDto track = routeService.getTrack("test-team", getCreatedRouteId(), member.getId());

    assertNotNull(track);
    assertNotNull(track.trackPoints());
    assertFalse(track.trackPoints().isEmpty());
  }

  @Test
  void getTrack_shouldThrowForNonexistentTrack() {
    Route route = dataService.createRoute(team, admin, "No Track");

    assertThrows(
        BusinessException.class,
        () -> routeService.getTrack("test-team", route.getId(), member.getId()));
  }

  // ==================== File Downloads ====================

  @Test
  void getFilteredGpxFile_shouldReturnFileIfExists() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Route", null, null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    assertDoesNotThrow(
        () -> routeService.getFilteredGpxFile("test-team", getCreatedRouteId(), null));
  }

  @Test
  void getFitFile_shouldReturnFileIfExists() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Route", null, null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    assertDoesNotThrow(() -> routeService.getFitFile("test-team", getCreatedRouteId(), null));
  }

  @Test
  void getThumbnailFile_shouldReturnFileIfExists() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    CreateRouteRequest request =
        new CreateRouteRequest("Route", null, null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    assertDoesNotThrow(() -> routeService.getThumbnailFile("test-team", getCreatedRouteId(), null));
  }
}
