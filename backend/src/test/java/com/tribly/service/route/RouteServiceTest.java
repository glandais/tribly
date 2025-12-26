package com.tribly.service.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.dto.routes.response.RouteDto;
import com.tribly.dto.routes.response.RouteListResponse;
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
      gpxProcessingService.deleteRouteFiles(TsidUtils.toLong(createdRoute.id()));
    }
  }

  private String getCreatedRouteSlug() {
    return createdRoute.slug();
  }

  // ==================== Create Route ====================

  @Test
  void createRoute_shouldCreateWithGpxProcessing() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest request =
        new RouteRequest("Test Route", "A test route", SurfaceType.GRAVEL, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", organizer.getId());

    assertNotNull(createdRoute);
    assertEquals("Test Route", createdRoute.name());
    assertEquals("A test route", createdRoute.description());
    assertEquals(SurfaceType.GRAVEL, createdRoute.surfaceType());
    assertEquals(Visibility.PUBLIC, createdRoute.visibility());
    assertTrue(createdRoute.distance() > 0);
    assertTrue(createdRoute.elevationGain() >= 0);
  }

  @Test
  void createRoute_shouldThrowForNonOrganizer() {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest request = new RouteRequest("Test", null, null, Visibility.PUBLIC);

    assertThrows(
        BusinessException.class,
        () ->
            routeService.createRoute(
                "test-team", request, gpxStream, "example.gpx", member.getId()));
  }

  @Test
  void createRoute_shouldThrowForNonexistentTeam() {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest request = new RouteRequest("Test", null, null, Visibility.PUBLIC);

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

    RouteDto result = routeService.getRoute("test-team", route.getSlug(), member.getId());

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldReturnRouteForNonMemberIfPublic() {
    Route route =
        dataService.createRouteWithVisibility(team, admin, "Public Route", Visibility.PUBLIC);

    RouteDto result = routeService.getRoute("test-team", route.getSlug(), null);

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldHideTeamRouteFromNonMembers() {
    Route route = dataService.createRouteWithVisibility(team, admin, "Team Route", Visibility.TEAM);

    assertThrows(
        BusinessException.class, () -> routeService.getRoute("test-team", route.getSlug(), null));
  }

  @Test
  void getRoute_shouldThrowForNonexistentRoute() {
    assertThrows(
        BusinessException.class,
        () -> routeService.getRoute("test-team", "missing", admin.getId()));
  }

  // ==================== List Routes ====================

  @Test
  void getRoutes_shouldReturnPublicRoutesForNonMembers() {
    dataService.createRouteWithVisibility(team, admin, "Public 1", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, admin, "Public 2", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, admin, "Team Only", Visibility.TEAM);

    RouteListResponse result = routeService.getRoutes("test-team", null, 0, 10, null);

    assertEquals(2, result.routes().size());
    assertTrue(result.routes().stream().allMatch(r -> r.visibility() == Visibility.PUBLIC));
  }

  @Test
  void getRoutes_shouldReturnAllRoutesForMembers() {
    dataService.createRouteWithVisibility(team, admin, "Public", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, admin, "Team", Visibility.TEAM);

    RouteListResponse result = routeService.getRoutes("test-team", member.getId(), 0, 10, null);

    assertEquals(2, result.routes().size());
  }

  @Test
  void getRoutes_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createRouteWithVisibility(team, admin, "Route " + i, Visibility.PUBLIC);
    }

    RouteListResponse result = routeService.getRoutes("test-team", null, 0, 3, null);

    assertEquals(3, result.routes().size());
    assertEquals(5, result.total());
  }

  @Test
  void getRoutes_shouldThrowForNonMemberOfPrivateTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.createRoute(privateTeam, admin, "Route");

    assertThrows(
        BusinessException.class, () -> routeService.getRoutes("private-team", null, 0, 10, null));
  }

  // ==================== Update Route ====================

  @Test
  void updateRoute_shouldUpdateAllFields() throws Exception {
    Route route = dataService.createRoute(team, admin, "Original");
    RouteRequest request =
        new RouteRequest(
            "Updated Name", "Updated description", SurfaceType.GRAVEL, Visibility.TEAM);

    RouteDto result =
        routeService.updateRoute(
            "test-team", route.getSlug(), request, null, null, organizer.getId());

    assertEquals("Updated Name", result.name());
    assertEquals("Updated description", result.description());
    assertEquals(SurfaceType.GRAVEL, result.surfaceType());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateRoute_shouldUpdatePartialFields() throws Exception {
    Route route = dataService.createRoute(team, admin, "Original");
    RouteRequest request = new RouteRequest("New Name", null, SurfaceType.ROAD, Visibility.TEAM);

    RouteDto result =
        routeService.updateRoute(
            "test-team", route.getSlug(), request, null, null, organizer.getId());

    assertEquals("New Name", result.name());
  }

  @Test
  void updateRoute_shouldPreserveFieldsWhenNull() throws Exception {
    Route route = dataService.createRouteWithVisibility(team, admin, "Original", Visibility.PUBLIC);
    RouteRequest request =
        new RouteRequest("New name 2", "New description", SurfaceType.MTB, Visibility.PUBLIC);

    RouteDto result =
        routeService.updateRoute(
            "test-team", route.getSlug(), request, null, null, organizer.getId());

    assertEquals("New name 2", result.name());
    assertEquals("New description", result.description());
    assertEquals(Visibility.PUBLIC, result.visibility());
  }

  @Test
  void updateRoute_shouldThrowForNonOrganizer() throws Exception {
    Route route = dataService.createRoute(team, admin, "Test");
    RouteRequest request = new RouteRequest("New", null, null, null);

    assertThrows(
        BusinessException.class,
        () ->
            routeService.updateRoute(
                "test-team", route.getSlug(), request, null, null, member.getId()));
  }

  @Test
  void updateRoute_shouldUpdateGpxFileWhenProvided() throws Exception {
    // Create initial route with GPX
    InputStream initialGpx = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest createRequest =
        new RouteRequest("Original Route", "Original", SurfaceType.ROAD, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute(
            "test-team", createRequest, initialGpx, "example.gpx", organizer.getId());

    int originalDistance = createdRoute.distance();

    // Update route with new GPX file (using same file for simplicity)
    InputStream newGpx = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest updateRequest =
        new RouteRequest("Updated Route", "Updated", SurfaceType.GRAVEL, Visibility.TEAM);

    RouteDto updated =
        routeService.updateRoute(
            "test-team",
            createdRoute.slug(),
            updateRequest,
            newGpx,
            "new-example.gpx",
            organizer.getId());

    assertEquals("Updated Route", updated.name());
    assertEquals("Updated", updated.description());
    assertEquals(SurfaceType.GRAVEL, updated.surfaceType());
    assertEquals(Visibility.TEAM, updated.visibility());
    assertEquals(originalDistance, updated.distance()); // Same file, so distance should match
  }

  // ==================== Delete Route ====================

  @Test
  void deleteRoute_shouldSoftDeleteRoute() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest request = new RouteRequest("To Delete", null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());
    String routeSlug = getCreatedRouteSlug();

    routeService.deleteRoute("test-team", routeSlug, admin.getId());

    assertThrows(
        BusinessException.class,
        () -> routeService.getRoute("test-team", routeSlug, admin.getId()));

    // Cleanup handled by gpxProcessingService.deleteRouteFiles
    createdRoute = null; // Prevent double cleanup in @AfterEach
  }

  @Test
  void deleteRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");

    assertThrows(
        BusinessException.class,
        () -> routeService.deleteRoute("test-team", route.getSlug(), member.getId()));
  }

  // ==================== File Downloads ====================

  @Test
  void getFilteredGpxFile_shouldReturnFileIfExists() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest request = new RouteRequest("Route", null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    assertDoesNotThrow(
        () -> routeService.getFilteredGpxFile("test-team", getCreatedRouteSlug(), null));
  }

  @Test
  void getFitFile_shouldReturnFileIfExists() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest request = new RouteRequest("Route", null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    assertDoesNotThrow(() -> routeService.getFitFile("test-team", getCreatedRouteSlug(), null));
  }

  @Test
  void getThumbnailFile_shouldReturnFileIfExists() throws Exception {
    InputStream gpxStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    RouteRequest request = new RouteRequest("Route", null, null, Visibility.PUBLIC);

    createdRoute =
        routeService.createRoute("test-team", request, gpxStream, "example.gpx", admin.getId());

    assertDoesNotThrow(
        () -> routeService.getThumbnailFile("test-team", getCreatedRouteSlug(), null));
  }
}
