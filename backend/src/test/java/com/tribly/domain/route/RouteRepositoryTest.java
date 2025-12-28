package com.tribly.domain.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Set;
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
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Test
  void find_shouldReturnRoutesByTeam() {
    dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
    dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

    TeamEntityQueryBasic query = TeamEntityQueryBasic.builder().build();
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(2, result.items().size());
    assertEquals(2, result.total());
  }

  @Test
  void find_shouldFilterByRouteId() {
    Route route1 = dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
    dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

    TeamEntityQueryBasic query = TeamEntityQueryBasic.builder().slug(route1.getSlug()).build();
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals(route1.getId(), result.items().getFirst().getId());
  }

  @Test
  void find_shouldFilterByVisibility() {
    dataService.createRoute(team, user, "Public Route", Visibility.PUBLIC);
    dataService.createRoute(team, user, "Team Route", Visibility.TEAM);

    TeamEntityQueryBasic query = TeamEntityQueryBasic.builder().build();
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("Public Route", result.items().getFirst().getName());
  }

  @Test
  void find_shouldIgnoreDeletedRoutes() {
    dataService.createRoute(team, user, "Visible Route", Visibility.PUBLIC);
    Route deletedRoute = dataService.createRoute(team, user, "Deleted Route", Visibility.PUBLIC);
    dataService.deleteRoute(deletedRoute);

    TeamEntityQueryBasic query = TeamEntityQueryBasic.builder().build();
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("Visible Route", result.items().getFirst().getName());
  }

  @Test
  void find_shouldReturnEmptyForDifferentTeam() {
    dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
    Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

    TeamEntityQueryBasic query =
        TeamEntityQueryBasic.builder().teamSlugs(Set.of(otherTeam.getSlug())).build();
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(0, result.items().size());
  }

  @Test
  void find_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createRoute(team, user, "Route " + i, Visibility.PUBLIC);
    }

    TeamEntityQueryBasic query = TeamEntityQueryBasic.builder().size(2).build();
    TriblyPage<Route> result = routeRepository.find(query);

    assertEquals(2, result.items().size());
    assertEquals(5, result.total());
  }
}
