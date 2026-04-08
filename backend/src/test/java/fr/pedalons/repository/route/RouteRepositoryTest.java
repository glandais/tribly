package fr.pedalons.repository.route;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.enums.*;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteRepositoryTest extends AbstractBaseTest {

  @Inject RouteRepository routeRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Nested
  @DisplayName("Basic Find")
  class BasicFind {

    @Test
    void find_shouldReturnRoutesByTeam() {
      dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(2, result.total());
    }

    @Test
    void find_shouldFilterByRouteId() {
      Route route1 = dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).slug(route1.getSlug()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals(route1.getId(), result.items().getFirst().getId());
    }

    @Test
    void find_shouldFilterByVisibility() {
      dataService.createRoute(team, user, "Public Route", Visibility.PUBLIC);
      dataService.createRoute(team, user, "Team Route", Visibility.TEAM);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Public Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldIgnoreDeletedRoutes() {
      dataService.createRoute(team, user, "Visible Route", Visibility.PUBLIC);
      Route deletedRoute = dataService.createRoute(team, user, "Deleted Route", Visibility.PUBLIC);
      dataService.deleteRoute(deletedRoute);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Visible Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldReturnEmptyForDifferentTeam() {
      dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).teamIds(Set.of(otherTeam.getId())).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    void find_shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createRoute(team, user, "Route " + i, Visibility.PUBLIC);
      }

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).size(2).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(5, result.total());
    }
  }

  @Nested
  @DisplayName("Distance Range Filter")
  class DistanceRangeFilter {

    @Test
    void find_shouldFilterByMinDistance() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Short Route",
          Visibility.PUBLIC,
          5000,
          100,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.1,
          6.1);
      dataService.createRouteWithProperties(
          team,
          user,
          "Long Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).minDistance(20000.0f).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Long Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldFilterByMaxDistance() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Short Route",
          Visibility.PUBLIC,
          5000,
          100,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.1,
          6.1);
      dataService.createRouteWithProperties(
          team,
          user,
          "Long Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).maxDistance(10000.0f).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Short Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldFilterByDistanceRange() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Short",
          Visibility.PUBLIC,
          5000,
          100,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.1,
          6.1);
      dataService.createRouteWithProperties(
          team,
          user,
          "Medium",
          Visibility.PUBLIC,
          30000,
          300,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.3,
          6.3);
      dataService.createRouteWithProperties(
          team,
          user,
          "Long",
          Visibility.PUBLIC,
          80000,
          800,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.8,
          6.8);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .minDistance(10000.0f)
              .maxDistance(50000.0f)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Medium", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Elevation Gain Range Filter")
  class ElevationGainRangeFilter {

    @Test
    void find_shouldFilterByMinElevationGain() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Flat Route",
          Visibility.PUBLIC,
          50000,
          100,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "Hilly Route",
          Visibility.PUBLIC,
          50000,
          1000,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).minElevationGain(500.0f).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Hilly Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldFilterByMaxElevationGain() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Flat Route",
          Visibility.PUBLIC,
          50000,
          100,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "Hilly Route",
          Visibility.PUBLIC,
          50000,
          1000,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).maxElevationGain(500.0f).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Flat Route", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Hilliness Preset Filter")
  class HillinessPresetFilter {

    @Test
    void find_shouldFilterByHillinessFlat() {
      // Flat: < 8 m/km -> 50km with 300m gain = 6 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Flat Route",
          Visibility.PUBLIC,
          50000,
          300,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      // Hilly: 8-15 m/km -> 50km with 600m gain = 12 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Hilly Route",
          Visibility.PUBLIC,
          50000,
          600,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).hilliness(Hilliness.FLAT).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Flat Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldFilterByHillinessHilly() {
      // Flat: 6 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Flat Route",
          Visibility.PUBLIC,
          50000,
          300,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      // Hilly: 12 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Hilly Route",
          Visibility.PUBLIC,
          50000,
          600,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      // Mountainous: 20 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Mountain Route",
          Visibility.PUBLIC,
          50000,
          1000,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).hilliness(Hilliness.HILLY).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Hilly Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldFilterByHillinessMountainous() {
      // Hilly: 12 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Hilly Route",
          Visibility.PUBLIC,
          50000,
          600,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      // Mountainous: 20 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Mountain Route",
          Visibility.PUBLIC,
          50000,
          1000,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).hilliness(Hilliness.MOUNTAINOUS).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Mountain Route", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Surface Type Filter")
  class SurfaceTypeFilter {

    @Test
    void find_shouldFilterBySurfaceType() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Road Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "Gravel Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.GRAVEL,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "MTB Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.MTB,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).surfaceType(SurfaceType.GRAVEL).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Gravel Route", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Wind Direction Filter")
  class WindDirectionFilter {

    @Test
    void find_shouldFilterByWindDirection() {
      dataService.createRouteWithProperties(
          team,
          user,
          "North Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "South Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.SOUTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).windDirection(WindDirection.NORTH).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("North Route", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Sorting")
  class Sorting {

    @Test
    void find_shouldSortByDistanceAscending() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Long",
          Visibility.PUBLIC,
          80000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.8,
          6.8);
      dataService.createRouteWithProperties(
          team,
          user,
          "Short",
          Visibility.PUBLIC,
          20000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.2,
          6.2);
      dataService.createRouteWithProperties(
          team,
          user,
          "Medium",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .sortBy(RouteSortBy.DISTANCE)
              .sortDir(SortDirection.ASC)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals("Short", result.items().get(0).getName());
      assertEquals("Medium", result.items().get(1).getName());
      assertEquals("Long", result.items().get(2).getName());
    }

    @Test
    void find_shouldSortByDistanceDescending() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Long",
          Visibility.PUBLIC,
          80000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.8,
          6.8);
      dataService.createRouteWithProperties(
          team,
          user,
          "Short",
          Visibility.PUBLIC,
          20000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.2,
          6.2);
      dataService.createRouteWithProperties(
          team,
          user,
          "Medium",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .sortBy(RouteSortBy.DISTANCE)
              .sortDir(SortDirection.DESC)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals("Long", result.items().get(0).getName());
      assertEquals("Medium", result.items().get(1).getName());
      assertEquals("Short", result.items().get(2).getName());
    }

    @Test
    void find_shouldSortByElevationGain() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Flat",
          Visibility.PUBLIC,
          50000,
          200,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "Hilly",
          Visibility.PUBLIC,
          50000,
          800,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "Medium",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .sortBy(RouteSortBy.ELEVATION_GAIN)
              .sortDir(SortDirection.ASC)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals("Flat", result.items().get(0).getName());
      assertEquals("Medium", result.items().get(1).getName());
      assertEquals("Hilly", result.items().get(2).getName());
    }

    @Test
    void find_shouldSortByHilliness() {
      // 4 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Flat",
          Visibility.PUBLIC,
          50000,
          200,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      // 16 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Hilly",
          Visibility.PUBLIC,
          50000,
          800,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      // 10 m/km
      dataService.createRouteWithProperties(
          team,
          user,
          "Medium",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .sortBy(RouteSortBy.HILLINESS)
              .sortDir(SortDirection.ASC)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals("Flat", result.items().get(0).getName());
      assertEquals("Medium", result.items().get(1).getName());
      assertEquals("Hilly", result.items().get(2).getName());
    }
  }

  @Nested
  @DisplayName("Geographic Proximity Filter")
  class GeographicProximityFilter {

    @Test
    void find_shouldFilterByNearStart() {
      // Route starting near Paris (48.8566, 2.3522)
      dataService.createRouteWithProperties(
          team,
          user,
          "Paris Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          48.8566,
          2.3522,
          48.9,
          2.4);
      // Route starting near Lyon (45.7640, 4.8357) - ~400km from Paris
      dataService.createRouteWithProperties(
          team,
          user,
          "Lyon Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.7640,
          4.8357,
          45.8,
          4.9);

      // Search within 50km of Paris
      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .nearLat(48.8566)
              .nearLon(2.3522)
              .nearRadius(50000.0)
              .nearType(NearType.START)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Paris Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldFilterByNearEnd() {
      // Route ending near Paris
      dataService.createRouteWithProperties(
          team,
          user,
          "To Paris",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          48.5,
          2.0,
          48.8566,
          2.3522);
      // Route ending near Lyon
      dataService.createRouteWithProperties(
          team,
          user,
          "To Lyon",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.5,
          4.5,
          45.7640,
          4.8357);

      // Search within 50km of Paris for routes ending there
      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .nearLat(48.8566)
              .nearLon(2.3522)
              .nearRadius(50000.0)
              .nearType(NearType.END)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("To Paris", result.items().getFirst().getName());
    }

    @Test
    void find_shouldFilterByNearStartOrEnd() {
      // Route starting near Paris, ending elsewhere
      dataService.createRouteWithProperties(
          team,
          user,
          "From Paris",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          48.8566,
          2.3522,
          49.0,
          2.5);
      // Route ending near Paris, starting elsewhere
      dataService.createRouteWithProperties(
          team,
          user,
          "To Paris",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          48.5,
          2.0,
          48.8566,
          2.3522);
      // Route far from Paris
      dataService.createRouteWithProperties(
          team,
          user,
          "Lyon Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.7640,
          4.8357,
          45.8,
          4.9);

      // Search within 50km of Paris for routes starting OR ending there
      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .nearLat(48.8566)
              .nearLon(2.3522)
              .nearRadius(50000.0)
              .nearType(NearType.START_OR_END)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(2, result.items().size());
    }

    @Test
    void find_shouldUseDefaultRadiusAndType() {
      // Route starting near Paris
      dataService.createRouteWithProperties(
          team,
          user,
          "Paris Route",
          Visibility.PUBLIC,
          50000,
          500,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          48.8566,
          2.3522,
          48.9,
          2.4);

      // Search near Paris without specifying radius/type (defaults: 25km, START_OR_END)
      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).nearLat(48.8566).nearLon(2.3522).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Paris Route", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Combined Filters")
  class CombinedFilters {

    @Test
    void find_shouldCombineMultipleFilters() {
      dataService.createRouteWithProperties(
          team,
          user,
          "Match",
          Visibility.PUBLIC,
          50000,
          600,
          SurfaceType.GRAVEL,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);
      dataService.createRouteWithProperties(
          team,
          user,
          "Too Short",
          Visibility.PUBLIC,
          10000,
          600,
          SurfaceType.GRAVEL,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.1,
          6.1);
      dataService.createRouteWithProperties(
          team,
          user,
          "Wrong Surface",
          Visibility.PUBLIC,
          50000,
          600,
          SurfaceType.ROAD,
          WindDirection.NORTH,
          45.0,
          6.0,
          45.5,
          6.5);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .minDistance(30000.0f)
              .surfaceType(SurfaceType.GRAVEL)
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Match", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Query Builder Methods")
  class QueryBuilderMethods {

    @Test
    void getQuerySlug_shouldBuildCorrectQuery() {
      RouteQuery query =
          routeRepository.getQuerySlug(
              domain.getId(), team.getId(), user.getId(), "test-slug", false, false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    void getQuerySlug_shouldWorkWithNullUserId() {
      RouteQuery query =
          routeRepository.getQuerySlug(
              domain.getId(), team.getId(), null, "test-slug", false, false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    void getQueryId_shouldBuildCorrectQuery() {
      RouteQuery query =
          routeRepository.getQueryId(
              domain.getId(), team.getId(), user.getId(), 12345L, false, false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals(12345L, query.id());
    }

    @Test
    void getQueryId_shouldWorkWithNullUserId() {
      RouteQuery query =
          routeRepository.getQueryId(domain.getId(), team.getId(), null, 12345L, false, false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals(12345L, query.id());
    }
  }

  @Nested
  @DisplayName("Entity Type Methods")
  class EntityTypeMethods {

    @Test
    void getEntityType_shouldReturnRoute() {
      assertEquals(TeamEntityType.ROUTE, routeRepository.getEntityType());
    }

    @Test
    void getAllEntityType_shouldReturnRoute() {
      assertEquals(EntityType.ROUTE, routeRepository.getAllEntityType());
    }
  }
}
