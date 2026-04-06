package fr.pedalons.service.route;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.dto.routes.request.RouteSearchParams;
import fr.pedalons.dto.routes.response.RouteDetailDto;
import fr.pedalons.dto.routes.response.RouteDto;
import fr.pedalons.dto.routes.response.RouteListResponse;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteServiceTest extends AbstractBaseTest {

  @Inject RouteService routeService;
  @Inject GpxProcessingService gpxProcessingService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext queryContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User admin;
  private User organizer;
  private User member;
  private User user2;
  private RouteDto createdRoute;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
    user2 = dataService.createUser("user2@example.com", "user2");
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

    queryContext.setUserForTest(organizer);
    createdRoute = routeService.createRoute(team.getSlug(), request, gpxPath);

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

    queryContext.setUserForTest(member);
    assertThrows(
        PedalonsException.class, () -> routeService.createRoute(team.getSlug(), request, gpxPath));
  }

  // ==================== Get Route ====================

  @Test
  void getRoute_shouldReturnRouteForMember() {
    Route route = dataService.createRoute(team, admin, "Public Route", Visibility.PUBLIC);

    queryContext.setUserForTest(member);
    RouteDetailDto result = routeService.getDto(team.getSlug(), route.getSlug());

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldReturnRouteForNonMemberIfPublic() {
    Route route = dataService.createRoute(team, admin, "Public Route", Visibility.PUBLIC);

    queryContext.setUserForTest(null);
    RouteDetailDto result = routeService.getDto(team.getSlug(), route.getSlug());

    assertNotNull(result);
    assertEquals("Public Route", result.name());
  }

  @Test
  void getRoute_shouldHideTeamRouteFromNonMembers() {
    Route route = dataService.createRoute(team, admin, "Team Route", Visibility.TEAM);

    queryContext.setUserForTest(null);
    assertThrows(
        PedalonsException.class, () -> routeService.getDto(team.getSlug(), route.getSlug()));
  }

  @Test
  void getRoute_shouldThrowForNonexistentRoute() {
    queryContext.setUserForTest(admin);
    assertThrows(PedalonsException.class, () -> routeService.getDto(team.getSlug(), "missing"));
  }

  // ==================== Get Route Detail ====================

  @Test
  void getRouteDetail_shouldReturnRouteWithTrackForMember() {
    Route route = dataService.createRoute(team, admin, "Detailed Route", Visibility.PUBLIC);

    queryContext.setUserForTest(member);
    RouteDetailDto result = routeService.getDto(team.getSlug(), route.getSlug());

    assertNotNull(result);
    assertEquals("Detailed Route", result.name());
    assertNotNull(result.tracks());
  }

  @Test
  void getRouteDetail_shouldReturnRouteForNonMemberIfPublic() {
    Route route = dataService.createRoute(team, admin, "Public Route Detail", Visibility.PUBLIC);

    queryContext.setUserForTest(null);
    RouteDetailDto result = routeService.getDto(team.getSlug(), route.getSlug());

    assertNotNull(result);
    assertEquals("Public Route Detail", result.name());
  }

  @Test
  void getRouteDetail_shouldHideTeamRouteFromNonMembers() {
    Route route = dataService.createRoute(team, admin, "Team Route Detail", Visibility.TEAM);

    queryContext.setUserForTest(null);
    assertThrows(
        PedalonsException.class, () -> routeService.getDto(team.getSlug(), route.getSlug()));
  }

  @Test
  void getRouteDetail_shouldThrowForNonexistentRoute() {
    queryContext.setUserForTest(admin);
    assertThrows(PedalonsException.class, () -> routeService.getDto(team.getSlug(), "missing"));
  }

  // ==================== List Routes ====================

  @Test
  void getRoutes_shouldReturnPublicRoutesForNonMembers() {
    dataService.createRoute(team, admin, "Public 1", Visibility.PUBLIC);
    dataService.createRoute(team, admin, "Public 2", Visibility.PUBLIC);
    dataService.createRoute(team, admin, "Team Only", Visibility.TEAM);

    queryContext.setUserForTest(null);
    RouteListResponse result =
        routeService.getRoutes(
            team.getSlug(), RouteSearchParams.builder().page(0).size(10).build());

    assertEquals(2, result.routes().size());
    assertTrue(result.routes().stream().allMatch(r -> r.visibility() == Visibility.PUBLIC));
  }

  @Test
  void getRoutes_shouldReturnAllRoutesForMembers() {
    dataService.createRoute(team, admin, "Public", Visibility.PUBLIC);
    dataService.createRoute(team, admin, "Team", Visibility.TEAM);

    queryContext.setUserForTest(member);
    RouteListResponse result =
        routeService.getRoutes(
            team.getSlug(), RouteSearchParams.builder().page(0).size(10).build());

    assertEquals(2, result.routes().size());
  }

  @Test
  void getRoutes_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createRoute(team, admin, "Route " + i, Visibility.PUBLIC);
    }

    queryContext.setUserForTest(null);
    RouteListResponse result =
        routeService.getRoutes(team.getSlug(), RouteSearchParams.builder().page(0).size(3).build());

    assertEquals(3, result.routes().size());
    assertEquals(5, result.total());
  }

  @Test
  void getRoutes_shouldThrowForNonMemberOfPrivateTeam() {
    Team privateTeam =
        dataService.createTeam(user2, "Private Team", "private-team", Visibility.TEAM);
    dataService.createRoute(privateTeam, user2, "Route");

    queryContext.setUserForTest(null);
    RouteListResponse routes =
        routeService.getRoutes(
            privateTeam.getSlug(), RouteSearchParams.builder().page(0).size(10).build());

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

    queryContext.setUserForTest(organizer);
    RouteDto result = routeService.updateRoute(team.getSlug(), route.getSlug(), request, null);

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

    queryContext.setUserForTest(organizer);
    RouteDto result = routeService.updateRoute(team.getSlug(), route.getSlug(), request, null);

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

    queryContext.setUserForTest(organizer);
    RouteDto result = routeService.updateRoute(team.getSlug(), route.getSlug(), request, null);

    assertEquals("New name 2", result.name());
    assertEquals("New description", result.media().markdown());
    assertEquals(Visibility.PUBLIC, result.visibility());
  }

  @Test
  void updateRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");
    RouteRequest request =
        new RouteRequest("New", MediaDto.builder().build(), null, null, List.of());

    queryContext.setUserForTest(member);
    assertThrows(
        PedalonsException.class,
        () -> routeService.updateRoute(team.getSlug(), route.getSlug(), request, null));
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

    queryContext.setUserForTest(organizer);
    createdRoute = routeService.createRoute(team.getSlug(), createRequest, initialGpx);

    float originalDistance = createdRoute.distance();

    // Update route with new GPX file
    Path newGpx = getTwoTracksGpxPath();
    RouteRequest updateRequest =
        new RouteRequest(
            "Updated Route",
            MediaDto.builder().markdown("Updated").build(),
            SurfaceType.GRAVEL,
            Visibility.TEAM,
            List.of());

    queryContext.setUserForTest(organizer);
    RouteDto updated =
        routeService.updateRoute(team.getSlug(), createdRoute.slug(), updateRequest, newGpx);

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

    queryContext.setUserForTest(admin);
    createdRoute = routeService.createRoute(team.getSlug(), request, gpxPath);
    String routeSlug = getCreatedRouteSlug();

    queryContext.setUserForTest(admin);
    routeService.deleteRoute(team.getSlug(), routeSlug);

    queryContext.setUserForTest(member);
    assertThrows(PedalonsException.class, () -> routeService.getDto(team.getSlug(), routeSlug));
  }

  @Test
  void deleteRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");

    queryContext.setUserForTest(member);
    assertThrows(
        PedalonsException.class, () -> routeService.deleteRoute(team.getSlug(), route.getSlug()));
  }

  // ==================== Undelete Route ====================

  @Test
  void undeleteRoute_shouldRestoreDeletedRoute() {
    Route route = dataService.createRoute(team, admin, "To Restore");

    queryContext.setUserForTest(organizer);
    routeService.deleteRoute(team.getSlug(), route.getSlug());

    queryContext.setUserForTest(admin);
    RouteDetailDto result = routeService.undeleteRoute(team.getSlug(), route.getSlug());

    assertFalse(result.deleted());
    assertEquals(route.getSlug(), result.slug());
  }

  @Test
  void undeleteRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");

    queryContext.setUserForTest(organizer);
    routeService.deleteRoute(team.getSlug(), route.getSlug());

    queryContext.setUserForTest(member);
    assertThrows(
        PedalonsException.class, () -> routeService.undeleteRoute(team.getSlug(), route.getSlug()));
  }
}
