package com.tribly.service.garmin;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.platform.Domain;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.garmin.response.GarminRouteDto;
import com.tribly.dto.garmin.response.GarminRoutesResponse;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GarminRouteServiceTest {

  @Inject GarminRouteService garminRouteService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext triblyContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User admin;
  private User member;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createVerifiedUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    member = dataService.createVerifiedUser("member@example.com", "Member");
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Nested
  class GetRoutesForUser {

    @Test
    void shouldReturnEmptyWhenNoMemberships() {
      User nonMember = dataService.createVerifiedUser("nonmember@example.com", "NonMember");
      triblyContext.setUserForTest(nonMember);

      GarminRoutesResponse response = garminRouteService.getRoutesForUser(null, null);

      assertNotNull(response);
      assertTrue(response.routes().isEmpty());
    }

    @Test
    void shouldReturnRoutesFromUpcomingRides() {
      // Create a route and attach it to an upcoming ride
      Route route = dataService.createRoute(team, admin, "Test Route");
      route.setStatus(Status.PUBLISHED);
      dataService.updateRoute(route);
      Instant tomorrow = Instant.now().plus(1, ChronoUnit.DAYS);
      Ride ride = dataService.createRide(team, admin, "Tomorrow Ride", "tomorrow-ride", tomorrow);
      RideGroup group = dataService.createRideGroup(admin, ride, "Main Group");
      group.setRoute(route);
      dataService.updateRideGroup(group);

      triblyContext.setUserForTest(member);

      GarminRoutesResponse response = garminRouteService.getRoutesForUser(null, null);

      assertNotNull(response);
      assertFalse(response.routes().isEmpty());
    }

    @Test
    void shouldReturnLatestRoutesWhenNoUpcomingRides() {
      // Create published routes without rides
      Route route1 = dataService.createRoute(team, admin, "Route 1");
      route1.setStatus(Status.PUBLISHED);
      dataService.updateRoute(route1);
      Route route2 = dataService.createRoute(team, admin, "Route 2");
      route2.setStatus(Status.PUBLISHED);
      dataService.updateRoute(route2);

      triblyContext.setUserForTest(member);

      GarminRoutesResponse response = garminRouteService.getRoutesForUser(null, null);

      assertNotNull(response);
      assertFalse(response.routes().isEmpty());
    }

    @Test
    void shouldIgnoreDraftRoutes() {
      Route draftRoute = dataService.createRoute(team, admin, "Draft Route");
      draftRoute.setStatus(Status.DRAFT);
      dataService.updateRoute(draftRoute);

      triblyContext.setUserForTest(member);

      GarminRoutesResponse response = garminRouteService.getRoutesForUser(null, null);

      assertNotNull(response);
      // Should be empty since only draft route exists
      assertTrue(response.routes().stream().noneMatch(r -> r.name().equals("Draft Route")));
    }

    @Test
    void shouldCalculateDistanceFromUser() {
      // Create route with known start location (Paris ~48.8, 2.3)
      Route route =
          dataService.createRouteWithProperties(
              team,
              admin,
              "Paris Route",
              Visibility.PUBLIC,
              50000,
              500,
              com.tribly.enums.SurfaceType.ROAD,
              null,
              48.8566,
              2.3522,
              48.9,
              2.4);
      route.setStatus(Status.PUBLISHED);
      dataService.updateRoute(route);

      triblyContext.setUserForTest(member);

      // User is in Lyon (~45.7, 4.8) - about 400km from Paris
      GarminRoutesResponse response = garminRouteService.getRoutesForUser(45.7640, 4.8357);

      assertNotNull(response);
      assertFalse(response.routes().isEmpty());
      GarminRouteDto parisRoute =
          response.routes().stream()
              .filter(r -> r.name().equals("Paris Route"))
              .findFirst()
              .orElse(null);
      assertNotNull(parisRoute);
      assertNotNull(parisRoute.distanceFromUser());
      // Distance should be roughly 400km (400000m)
      assertTrue(parisRoute.distanceFromUser() > 300000);
      assertTrue(parisRoute.distanceFromUser() < 500000);
    }

    @Test
    void shouldPrioritizeRidesOverLatestRoutes() {
      // Create a standalone route
      Route standaloneRoute = dataService.createRoute(team, admin, "Standalone Route");
      standaloneRoute.setStatus(Status.PUBLISHED);
      dataService.updateRoute(standaloneRoute);

      // Create a ride with a route
      Route rideRoute = dataService.createRoute(team, admin, "Ride Route");
      rideRoute.setStatus(Status.PUBLISHED);
      dataService.updateRoute(rideRoute);
      Instant tomorrow = Instant.now().plus(1, ChronoUnit.DAYS);
      Ride ride = dataService.createRide(team, admin, "Tomorrow Ride", "tomorrow-ride", tomorrow);
      RideGroup group = dataService.createRideGroup(admin, ride, "Main Group");
      group.setRoute(rideRoute);
      dataService.updateRideGroup(group);

      triblyContext.setUserForTest(member);

      GarminRoutesResponse response = garminRouteService.getRoutesForUser(null, null);

      assertNotNull(response);
      assertFalse(response.routes().isEmpty());
      // First route should be from the ride (has rideDateTime)
      GarminRouteDto firstRoute = response.routes().get(0);
      assertNotNull(firstRoute.rideDateTime());
    }
  }

  @Nested
  class GetFitFile {

    @Test
    void shouldThrowForNonMemberTeam() {
      User nonMember = dataService.createVerifiedUser("nonmember@example.com", "NonMember");
      Route route = dataService.createRoute(team, admin, "Test Route");
      route.setStatus(Status.PUBLISHED);
      dataService.updateRoute(route);

      triblyContext.setUserForTest(nonMember);

      assertThrows(
          NotFoundException.class,
          () -> garminRouteService.getFitFile(team.getSlug(), route.getSlug()));
    }

    @Test
    void shouldThrowForNonexistentRoute() {
      triblyContext.setUserForTest(member);

      assertThrows(
          NotFoundException.class,
          () -> garminRouteService.getFitFile(team.getSlug(), "nonexistent-route"));
    }

    @Test
    void shouldThrowForNonexistentTeam() {
      triblyContext.setUserForTest(member);

      assertThrows(
          NotFoundException.class,
          () -> garminRouteService.getFitFile("nonexistent-team", "some-route"));
    }
  }
}
