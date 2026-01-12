package com.tribly.repository.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.*;
import com.tribly.repository.common.TriblyPage;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteRepositoryTest {

  @Inject RouteRepository routeRepository;
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

  @Nested
  @DisplayName("Basic Find")
  class BasicFind {

    @Test
    void find_shouldReturnRoutesByTeam() {
      dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().build();
      TriblyPage<Route> result = routeRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(2, result.total());
    }

    @Test
    void find_shouldFilterByRouteId() {
      Route route1 = dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().slug(route1.getSlug()).build();
      TriblyPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals(route1.getId(), result.items().getFirst().getId());
    }

    @Test
    void find_shouldFilterByVisibility() {
      dataService.createRoute(team, user, "Public Route", Visibility.PUBLIC);
      dataService.createRoute(team, user, "Team Route", Visibility.TEAM);

      RouteQuery query = RouteQuery.builder().build();
      TriblyPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Public Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldIgnoreDeletedRoutes() {
      dataService.createRoute(team, user, "Visible Route", Visibility.PUBLIC);
      Route deletedRoute = dataService.createRoute(team, user, "Deleted Route", Visibility.PUBLIC);
      dataService.deleteRoute(deletedRoute);

      RouteQuery query = RouteQuery.builder().build();
      TriblyPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Visible Route", result.items().getFirst().getName());
    }

    @Test
    void find_shouldReturnEmptyForDifferentTeam() {
      dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().teamIds(Set.of(otherTeam.getId())).build();
      TriblyPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    void find_shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createRoute(team, user, "Route " + i, Visibility.PUBLIC);
      }

      RouteQuery query = RouteQuery.builder().size(2).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().minDistance(20000.0f).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().maxDistance(10000.0f).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().minDistance(10000.0f).maxDistance(50000.0f).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().minElevationGain(500.0f).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().maxElevationGain(500.0f).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().hilliness(Hilliness.FLAT).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().hilliness(Hilliness.HILLY).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().hilliness(Hilliness.MOUNTAINOUS).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().surfaceType(SurfaceType.GRAVEL).build();
      TriblyPage<Route> result = routeRepository.find(query);

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

      RouteQuery query = RouteQuery.builder().windDirection(WindDirection.NORTH).build();
      TriblyPage<Route> result = routeRepository.find(query);

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
          RouteQuery.builder().sortBy(RouteSortBy.DISTANCE).sortDir(SortDirection.ASC).build();
      TriblyPage<Route> result = routeRepository.find(query);

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
          RouteQuery.builder().sortBy(RouteSortBy.DISTANCE).sortDir(SortDirection.DESC).build();
      TriblyPage<Route> result = routeRepository.find(query);

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
              .sortBy(RouteSortBy.ELEVATION_GAIN)
              .sortDir(SortDirection.ASC)
              .build();
      TriblyPage<Route> result = routeRepository.find(query);

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
          RouteQuery.builder().sortBy(RouteSortBy.HILLINESS).sortDir(SortDirection.ASC).build();
      TriblyPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals("Flat", result.items().get(0).getName());
      assertEquals("Medium", result.items().get(1).getName());
      assertEquals("Hilly", result.items().get(2).getName());
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
          RouteQuery.builder().minDistance(30000.0f).surfaceType(SurfaceType.GRAVEL).build();
      TriblyPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Match", result.items().getFirst().getName());
    }
  }
}
