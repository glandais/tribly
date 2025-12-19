package com.tribly.domain.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.TriblyPage;
import com.tribly.domain.common.Visibility;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
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
    team = dataService.createTeam("Test Team", "test-team");
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Test
  void find_shouldReturnRoutesByTeam() {
    dataService.createRoute(team, user, "Route 1");
    dataService.createRoute(team, user, "Route 2");

    RouteQuery query = new RouteQuery(team.getId(), 0, 10, null, null);
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(2, result.items().size());
    assertEquals(2, result.total());
  }

  @Test
  void find_shouldFilterByRouteId() {
    Route route1 = dataService.createRoute(team, user, "Route 1");
    dataService.createRoute(team, user, "Route 2");

    RouteQuery query = new RouteQuery(team.getId(), 0, 10, route1.getId(), null);
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals(route1.getId(), result.items().getFirst().getId());
  }

  @Test
  void find_shouldFilterByVisibility() {
    dataService.createRouteWithVisibility(team, user, "Public Route", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, user, "Team Route", Visibility.TEAM);

    RouteQuery query = new RouteQuery(team.getId(), 0, 10, null, Visibility.PUBLIC);
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("Public Route", result.items().getFirst().getName());
  }

  @Test
  void find_shouldIgnoreDeletedRoutes() {
    dataService.createRoute(team, user, "Visible Route");
    Route deletedRoute = dataService.createRoute(team, user, "Deleted Route");
    dataService.deleteRoute(deletedRoute);

    RouteQuery query = new RouteQuery(team.getId(), 0, 10, null, null);
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("Visible Route", result.items().getFirst().getName());
  }

  @Test
  void find_shouldReturnEmptyForDifferentTeam() {
    dataService.createRoute(team, user, "Route 1");
    Team otherTeam = dataService.createTeam("Other Team", "other-team");

    RouteQuery query = new RouteQuery(otherTeam.getId(), 0, 10, null, null);
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(0, result.items().size());
  }

  @Test
  void find_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createRoute(team, user, "Route " + i);
    }

    RouteQuery query = new RouteQuery(team.getId(), 0, 2, null, null);
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(2, result.items().size());
    assertEquals(5, result.total());
  }

  @Test
  void find_shouldCombineFilters() {
    Route targetRoute =
        dataService.createRouteWithVisibility(team, user, "Target Route", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, user, "Other Public Route", Visibility.PUBLIC);
    dataService.createRouteWithVisibility(team, user, "Team Route", Visibility.TEAM);

    RouteQuery query = new RouteQuery(team.getId(), 0, 10, targetRoute.getId(), Visibility.PUBLIC);
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("Target Route", result.items().getFirst().getName());
  }
}
