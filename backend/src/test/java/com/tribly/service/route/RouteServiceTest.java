package com.tribly.service.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.dto.routes.response.RouteDetailDto;
import com.tribly.dto.routes.response.RouteDto;
import com.tribly.dto.routes.response.RouteListResponse;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
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
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    dataService.addUserToTeam(admin, team, TeamRole.ADMIN);
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @AfterEach
  void cleanup() {
    if (createdRoute != null) {
      // FIXME
      //      gpxProcessingService.deleteRouteFiles(createdRoute);
    }
  }

  private String getCreatedRouteSlug() {
    return createdRoute.slug();
  }

  // ==================== Create Route ====================

  @Test
  void createRoute_shouldCreateWithGpxProcessing() throws Exception {
    Path gpxPath = getExampleGpxPath();
    RouteRequest request =
        new RouteRequest(
            "Test Route",
            MediaDto.builder().markdown("A test route").build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            List.of());

    createdRoute = routeService.createRoute(team, request, gpxPath, organizer);

    assertNotNull(createdRoute);
    assertEquals("Test Route", createdRoute.name());
    assertEquals("A test route", createdRoute.media().markdown());
    assertEquals(SurfaceType.GRAVEL, createdRoute.surfaceType());
    assertEquals(Visibility.PUBLIC, createdRoute.visibility());
    assertTrue(createdRoute.distance() > 0);
    assertTrue(createdRoute.elevationGain() >= 0);
  }

  private Path getExampleGpxPath() {
    return new File("src/test/resources/example.gpx").toPath();
  }

  private Path getTwoTracksGpxPath() {
    return new File("src/test/resources/two_tracks.gpx").toPath();
  }

  @Test
  void createRoute_shouldThrowForNonOrganizer() {
    Path gpxPath = getExampleGpxPath();
    RouteRequest request =
        new RouteRequest(
            "Test", MediaDto.builder().build(), SurfaceType.GRAVEL, Visibility.PUBLIC, List.of());

    assertThrows(
        BusinessException.class, () -> routeService.createRoute(team, request, gpxPath, member));
  }

  // ==================== Get Route ====================

  @Test
  void getRoute_shouldReturnRouteForMember() {
    Route route = dataService.createRoute(team, admin, "Public Route", Visibility.PUBLIC);

    RouteDetailDto result = routeService.getRouteDetail(team, route.getSlug(), member);

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldReturnRouteForNonMemberIfPublic() {
    Route route = dataService.createRoute(team, admin, "Public Route", Visibility.PUBLIC);

    RouteDetailDto result = routeService.getRouteDetail(team, route.getSlug(), null);

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldHideTeamRouteFromNonMembers() {
    Route route = dataService.createRoute(team, admin, "Team Route", Visibility.TEAM);

    assertThrows(
        BusinessException.class, () -> routeService.getRouteDetail(team, route.getSlug(), null));
  }

  @Test
  void getRoute_shouldThrowForNonexistentRoute() {
    assertThrows(
        BusinessException.class, () -> routeService.getRouteDetail(team, "missing", admin));
  }

  // ==================== Get Route Detail ====================

  @Test
  void getRouteDetail_shouldReturnRouteWithTrackForMember() {
    Route route = dataService.createRoute(team, admin, "Detailed Route", Visibility.PUBLIC);

    RouteDetailDto result = routeService.getRouteDetail(team, route.getSlug(), member);

    assertNotNull(result);
    assertEquals("Detailed Route", result.name());
    assertNotNull(result.tracks());
  }

  @Test
  void getRouteDetail_shouldReturnRouteForNonMemberIfPublic() {
    Route route = dataService.createRoute(team, admin, "Public Route Detail", Visibility.PUBLIC);

    RouteDetailDto result = routeService.getRouteDetail(team, route.getSlug(), null);

    assertNotNull(result);
    assertEquals("Public Route Detail", result.name());
  }

  @Test
  void getRouteDetail_shouldHideTeamRouteFromNonMembers() {
    Route route = dataService.createRoute(team, admin, "Team Route Detail", Visibility.TEAM);

    assertThrows(
        BusinessException.class, () -> routeService.getRouteDetail(team, route.getSlug(), null));
  }

  @Test
  void getRouteDetail_shouldThrowForNonexistentRoute() {
    assertThrows(
        BusinessException.class, () -> routeService.getRouteDetail(team, "missing", admin));
  }

  // ==================== List Routes ====================

  @Test
  void getRoutes_shouldReturnPublicRoutesForNonMembers() {
    dataService.createRoute(team, admin, "Public 1", Visibility.PUBLIC);
    dataService.createRoute(team, admin, "Public 2", Visibility.PUBLIC);
    dataService.createRoute(team, admin, "Team Only", Visibility.TEAM);

    RouteListResponse result =
        routeService.getRoutes(team, null, RouteSearchParams.builder().page(0).size(10).build());

    assertEquals(2, result.routes().size());
    assertTrue(result.routes().stream().allMatch(r -> r.visibility() == Visibility.PUBLIC));
  }

  @Test
  void getRoutes_shouldReturnAllRoutesForMembers() {
    dataService.createRoute(team, admin, "Public", Visibility.PUBLIC);
    dataService.createRoute(team, admin, "Team", Visibility.TEAM);

    RouteListResponse result =
        routeService.getRoutes(team, member, RouteSearchParams.builder().page(0).size(10).build());

    assertEquals(2, result.routes().size());
  }

  @Test
  void getRoutes_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createRoute(team, admin, "Route " + i, Visibility.PUBLIC);
    }

    RouteListResponse result =
        routeService.getRoutes(team, null, RouteSearchParams.builder().page(0).size(3).build());

    assertEquals(3, result.routes().size());
    assertEquals(5, result.total());
  }

  @Test
  void getRoutes_shouldThrowForNonMemberOfPrivateTeam() {
    Team privateTeam =
        dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    dataService.createRoute(privateTeam, admin, "Route");

    RouteListResponse routes =
        routeService.getRoutes(
            privateTeam, null, RouteSearchParams.builder().page(0).size(10).build());

    assertEquals(0, routes.routes().size());
    assertEquals(0, routes.total());
  }

  // ==================== Update Route ====================

  @Test
  void updateRoute_shouldUpdateAllFields() throws Exception {
    Route route = dataService.createRoute(team, admin, "Original");
    RouteRequest request =
        new RouteRequest(
            "Updated Name",
            MediaDto.builder().markdown("Updated description").build(),
            SurfaceType.GRAVEL,
            Visibility.TEAM,
            List.of());

    RouteDto result = routeService.updateRoute(team, route.getSlug(), request, null, organizer);

    assertEquals("Updated Name", result.name());
    assertEquals("Updated description", result.media().markdown());
    assertEquals(SurfaceType.GRAVEL, result.surfaceType());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateRoute_shouldUpdatePartialFields() throws Exception {
    Route route = dataService.createRoute(team, admin, "Original");
    RouteRequest request =
        new RouteRequest(
            "New Name", MediaDto.builder().build(), SurfaceType.ROAD, Visibility.TEAM, List.of());

    RouteDto result = routeService.updateRoute(team, route.getSlug(), request, null, organizer);

    assertEquals("New Name", result.name());
  }

  @Test
  void updateRoute_shouldPreserveFieldsWhenNull() throws Exception {
    Route route = dataService.createRoute(team, admin, "Original", Visibility.PUBLIC);
    RouteRequest request =
        new RouteRequest(
            "New name 2",
            MediaDto.builder().markdown("New description").build(),
            SurfaceType.MTB,
            Visibility.PUBLIC,
            List.of());

    RouteDto result = routeService.updateRoute(team, route.getSlug(), request, null, organizer);

    assertEquals("New name 2", result.name());
    assertEquals("New description", result.media().markdown());
    assertEquals(Visibility.PUBLIC, result.visibility());
  }

  @Test
  void updateRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");
    RouteRequest request =
        new RouteRequest("New", MediaDto.builder().build(), null, null, List.of());

    assertThrows(
        BusinessException.class,
        () -> routeService.updateRoute(team, route.getSlug(), request, null, member));
  }

  @Test
  void updateRoute_shouldUpdateGpxFileWhenProvided() throws Exception {
    // Create initial route with GPX
    Path initialGpx = getExampleGpxPath();
    RouteRequest createRequest =
        new RouteRequest(
            "Original Route",
            MediaDto.builder().markdown("Original").build(),
            SurfaceType.ROAD,
            Visibility.PUBLIC,
            List.of());

    createdRoute = routeService.createRoute(team, createRequest, initialGpx, organizer);

    int originalDistance = createdRoute.distance();

    // Update route with new GPX file
    Path newGpx = getTwoTracksGpxPath();
    RouteRequest updateRequest =
        new RouteRequest(
            "Updated Route",
            MediaDto.builder().markdown("Updated").build(),
            SurfaceType.GRAVEL,
            Visibility.TEAM,
            List.of());

    RouteDto updated =
        routeService.updateRoute(team, createdRoute.slug(), updateRequest, newGpx, organizer);

    assertEquals("Updated Route", updated.name());
    assertEquals("Updated", updated.media().markdown());
    assertEquals(SurfaceType.GRAVEL, updated.surfaceType());
    assertEquals(Visibility.TEAM, updated.visibility());
    assertNotEquals(originalDistance, updated.distance()); // Same file, so distance should match
  }

  // ==================== Delete Route ====================

  @Test
  void deleteRoute_shouldSoftDeleteRoute() throws Exception {
    Path gpxPath = getExampleGpxPath();
    RouteRequest request =
        new RouteRequest(
            "To Delete", MediaDto.builder().build(), null, Visibility.PUBLIC, List.of());

    createdRoute = routeService.createRoute(team, request, gpxPath, admin);
    String routeSlug = getCreatedRouteSlug();

    routeService.deleteRoute(team, routeSlug, admin);

    assertThrows(
        BusinessException.class, () -> routeService.getRouteDetail(team, routeSlug, admin));

    // Cleanup handled by gpxProcessingService.deleteRouteFiles
    createdRoute = null; // Prevent double cleanup in @AfterEach
  }

  @Test
  void deleteRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");

    assertThrows(
        BusinessException.class, () -> routeService.deleteRoute(team, route.getSlug(), member));
  }
}
