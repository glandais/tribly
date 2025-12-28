package com.tribly.domain.route;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.repository.RouteClimbRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ClimbCategory;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteClimbRepositoryTest {

  @Inject RouteClimbRepository routeClimbRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;
  private Route route;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    route = dataService.createRoute(team, user, "Test Route");
  }

  @Test
  void findByRoute_shouldReturnClimbsOrderedByStartDistance() {
    dataService.createRouteClimb(
        user, route, 5000, 6000, 100, new BigDecimal("5.0"), new BigDecimal("8.5"));
    dataService.createRouteClimb(
        user, route, 1000, 2000, 80, new BigDecimal("4.0"), new BigDecimal("7.0"));
    dataService.createRouteClimb(
        user, route, 10000, 11500, 150, new BigDecimal("6.0"), new BigDecimal("10.0"));

    List<RouteClimb> result = routeClimbRepository.findByRoute(route.getId());

    assertEquals(3, result.size());
    assertEquals(1000, result.get(0).getStartDistance());
    assertEquals(5000, result.get(1).getStartDistance());
    assertEquals(10000, result.get(2).getStartDistance());
  }

  @Test
  void findByRoute_shouldIgnoreDeletedClimbs() {
    dataService.createRouteClimb(
        user, route, 1000, 2000, 80, new BigDecimal("4.0"), new BigDecimal("7.0"));
    RouteClimb deletedClimb =
        dataService.createRouteClimb(
            user, route, 5000, 6000, 100, new BigDecimal("5.0"), new BigDecimal("8.5"));
    dataService.deleteRouteClimb(deletedClimb);

    List<RouteClimb> result = routeClimbRepository.findByRoute(route.getId());

    assertEquals(1, result.size());
    assertEquals(1000, result.get(0).getStartDistance());
  }

  @Test
  void findByRoute_shouldReturnEmptyForNonexistentRoute() {
    List<RouteClimb> result = routeClimbRepository.findByRoute(999999L);

    assertEquals(0, result.size());
  }

  @Test
  void findByRoute_shouldHandleClimbsWithAndWithoutCategory() {
    dataService.createRouteClimb(
        user,
        route,
        "Col du Tourmalet",
        1000,
        5000,
        1404,
        new BigDecimal("7.4"),
        new BigDecimal("10.2"),
        ClimbCategory.HC);
    dataService.createRouteClimb(
        user, route, 8000, 9000, 50, new BigDecimal("2.5"), new BigDecimal("5.0"));
    dataService.createRouteClimb(
        user,
        route,
        "Small Hill",
        12000,
        12800,
        80,
        new BigDecimal("3.2"),
        new BigDecimal("6.0"),
        ClimbCategory.CAT4);

    List<RouteClimb> result = routeClimbRepository.findByRoute(route.getId());

    assertEquals(3, result.size());
    assertEquals("Col du Tourmalet", result.get(0).getName());
    assertEquals(ClimbCategory.HC, result.get(0).getCategory());
    assertNull(result.get(1).getName());
    assertNull(result.get(1).getCategory());
    assertEquals("Small Hill", result.get(2).getName());
    assertEquals(ClimbCategory.CAT4, result.get(2).getCategory());
  }

  @Test
  void findByRoute_shouldReturnEmptyWhenNoClimbs() {
    List<RouteClimb> result = routeClimbRepository.findByRoute(route.getId());

    assertEquals(0, result.size());
  }

  @Test
  void findByRoute_shouldOnlyReturnClimbsForSpecificRoute() {
    Route otherRoute = dataService.createRoute(team, user, "Other Route");
    dataService.createRouteClimb(
        user, route, 1000, 2000, 80, new BigDecimal("4.0"), new BigDecimal("7.0"));
    dataService.createRouteClimb(
        user, otherRoute, 3000, 4000, 100, new BigDecimal("5.0"), new BigDecimal("8.0"));

    List<RouteClimb> result = routeClimbRepository.findByRoute(route.getId());

    assertEquals(1, result.size());
    assertEquals(1000, result.get(0).getStartDistance());
  }

  @Test
  void findByRoute_shouldHandleAllClimbCategories() {
    dataService.createRouteClimb(
        user,
        route,
        "HC Climb",
        1000,
        5000,
        1500,
        new BigDecimal("7.5"),
        new BigDecimal("12.0"),
        ClimbCategory.HC);
    dataService.createRouteClimb(
        user,
        route,
        "Cat 1",
        6000,
        8000,
        800,
        new BigDecimal("6.0"),
        new BigDecimal("9.0"),
        ClimbCategory.CAT1);
    dataService.createRouteClimb(
        user,
        route,
        "Cat 2",
        9000,
        10500,
        600,
        new BigDecimal("5.0"),
        new BigDecimal("8.0"),
        ClimbCategory.CAT2);
    dataService.createRouteClimb(
        user,
        route,
        "Cat 3",
        11000,
        12000,
        400,
        new BigDecimal("4.0"),
        new BigDecimal("7.0"),
        ClimbCategory.CAT3);
    dataService.createRouteClimb(
        user,
        route,
        "Cat 4",
        13000,
        13800,
        200,
        new BigDecimal("3.0"),
        new BigDecimal("5.0"),
        ClimbCategory.CAT4);

    List<RouteClimb> result = routeClimbRepository.findByRoute(route.getId());

    assertEquals(5, result.size());
    assertEquals(ClimbCategory.HC, result.get(0).getCategory());
    assertEquals(ClimbCategory.CAT1, result.get(1).getCategory());
    assertEquals(ClimbCategory.CAT2, result.get(2).getCategory());
    assertEquals(ClimbCategory.CAT3, result.get(3).getCategory());
    assertEquals(ClimbCategory.CAT4, result.get(4).getCategory());
  }
}
