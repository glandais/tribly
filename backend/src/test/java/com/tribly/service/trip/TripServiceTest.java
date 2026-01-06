package com.tribly.service.trip;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripStage;
import com.tribly.domain.user.User;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.trips.request.StageRequest;
import com.tribly.dto.trips.request.TripRequest;
import com.tribly.dto.trips.response.TripDto;
import com.tribly.dto.trips.response.TripListResponse;
import com.tribly.dto.trips.response.TripParticipationDto;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TripServiceTest {

  @Inject TripService tripService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User admin;
  private User organizer;
  private User member;

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

  // ==================== List Trips ====================

  @Nested
  class ListTrips {

    @Test
    void shouldReturnPublishedTripsForNonMembers() {
      dataService.createTrip(
          team, admin, "Public Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);
      dataService.createTrip(
          team, admin, "Team Trip", Instant.now(), Visibility.TEAM, Status.PUBLISHED, null);

      TripListResponse result = tripService.listTrips(team, null, null, null, null, 0, 10);

      assertEquals(1, result.trips().size());
      assertEquals("Public Trip", result.trips().getFirst().getName());
    }

    @Test
    void shouldReturnTeamTripsForMembers() {
      dataService.createTrip(
          team, admin, "Public Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);
      dataService.createTrip(
          team, admin, "Team Trip", Instant.now(), Visibility.TEAM, Status.PUBLISHED, null);

      TripListResponse result = tripService.listTrips(team, member, null, null, null, 0, 10);

      assertEquals(2, result.trips().size());
    }

    @Test
    void shouldShowDraftsToOrganizers() {
      dataService.createTrip(
          team, admin, "Draft Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);

      TripListResponse result = tripService.listTrips(team, organizer, null, null, null, 0, 10);

      assertEquals(1, result.trips().size());
      assertEquals(Status.DRAFT, result.trips().getFirst().getStatus());
    }

    @Test
    void shouldHideDraftsFromMembers() {
      dataService.createTrip(
          team, admin, "Draft Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);

      TripListResponse result = tripService.listTrips(team, member, null, null, null, 0, 10);

      assertEquals(0, result.trips().size());
    }

    @Test
    void shouldFilterByDateRange() {
      Instant today = Instant.now();
      Instant tomorrow = today.plusSeconds(24 * 3600);
      Instant nextWeek = today.plusSeconds(24 * 3600 * 7);
      dataService.createTrip(team, admin, "Today Trip", today);
      dataService.createTrip(team, admin, "Tomorrow Trip", tomorrow);
      dataService.createTrip(team, admin, "Next Week Trip", nextWeek);

      TripListResponse result = tripService.listTrips(team, null, null, today, tomorrow, 0, 10);

      assertEquals(2, result.trips().size());
    }

    @Test
    void shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createTrip(team, admin, "Trip " + i, Instant.now());
      }

      TripListResponse result = tripService.listTrips(team, null, null, null, null, 0, 3);

      assertEquals(3, result.trips().size());
      assertEquals(5, result.total());
    }
  }

  // ==================== Get Trip Detail ====================

  @Nested
  class GetTripDetail {

    @Test
    void shouldReturnTrip() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      TripDto result = tripService.getTripDetail(team, trip.getSlug(), null);

      assertEquals("Test Trip", result.getName());
      assertEquals(trip.getSlug(), result.getSlug());
    }

    @Test
    void shouldThrowForNonexistent() {
      assertThrows(
          BusinessException.class, () -> tripService.getTripDetail(team, "nonexistent", null));
    }

    @Test
    void shouldShowDraftToOrganizer() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Draft Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);

      TripDto result = tripService.getTripDetail(team, trip.getSlug(), organizer);

      assertEquals(Status.DRAFT, result.getStatus());
    }

    @Test
    void shouldHideDraftFromMember() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Draft Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);

      assertThrows(
          BusinessException.class, () -> tripService.getTripDetail(team, trip.getSlug(), member));
    }

    @Test
    void shouldIncludeStages() {
      Trip trip = dataService.createTrip(team, admin, "Trip with Stages", Instant.now());
      dataService.createTripStage(admin, trip, "Stage 1", 0);
      dataService.createTripStage(admin, trip, "Stage 2", 1);

      TripDto result = tripService.getTripDetail(team, trip.getSlug(), organizer);

      assertEquals(2, result.getStages().size());
      assertEquals(2, result.getStageCount());
    }
  }

  // ==================== Create Trip ====================

  @Nested
  class CreateTrip {

    @Test
    void shouldCreateWithSlug() {
      TripRequest request =
          new TripRequest(
              "Summer Tour",
              MediaDto.builder().markdown("A nice trip").build(),
              Instant.now().plusSeconds(24 * 3600 * 7),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      TripDto result = tripService.createTrip(team, request, organizer);

      assertNotNull(result);
      assertEquals("Summer Tour", result.getName());
      assertEquals("summer-tour", result.getSlug());
      assertEquals(Status.DRAFT, result.getStatus());
    }

    @Test
    void shouldHandleSlugCollision() {
      dataService.createTrip(team, admin, "Test Trip", Instant.now());
      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      TripDto result = tripService.createTrip(team, request, organizer);

      assertNotEquals("test-trip", result.getSlug());
      assertTrue(result.getSlug().startsWith("test-trip-"));
    }

    @Test
    void shouldCreateWithRoute() {
      var route = dataService.createRoute(team, admin, "Test Route", Visibility.PUBLIC);

      TripRequest request =
          new TripRequest(
              "Trip with Route",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              route.getSlug(),
              null,
              List.of());

      TripDto result = tripService.createTrip(team, request, organizer);

      assertNotNull(result);
      assertEquals(route.getSlug(), result.getRouteSlug());
    }

    @Test
    void shouldCreateWithStages() {
      StageRequest stage1 =
          StageRequest.builder()
              .name("Stage 1")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .build();
      StageRequest stage2 =
          StageRequest.builder()
              .name("Stage 2")
              .dateTime(Instant.now().plusSeconds(48 * 3600))
              .media(MediaDto.builder().build())
              .build();

      TripRequest request =
          new TripRequest(
              "Multi-Stage Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage1, stage2));

      TripDto result = tripService.createTrip(team, request, organizer);

      assertEquals(2, result.getStages().size());
      assertEquals("Stage 1", result.getStages().get(0).name());
      assertEquals("Stage 2", result.getStages().get(1).name());
    }

    @Test
    void shouldThrowForNonOrganizer() {
      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      assertThrows(BusinessException.class, () -> tripService.createTrip(team, request, member));
    }

    @Test
    void shouldThrowForPublicTripInTeamVisibilityTeam() {
      Team privateTeam =
          dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
      dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

      TripRequest request =
          new TripRequest(
              "Public Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      BusinessException exception =
          assertThrows(
              BusinessException.class,
              () -> tripService.createTrip(privateTeam, request, organizer));

      assertTrue(exception.getMessage().contains("Private teams can only have team-only items"));
    }

    @Test
    void shouldThrowForRouteFromDifferentTeam() {
      Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
      var foreignRoute =
          dataService.createRoute(otherTeam, admin, "Foreign Route", Visibility.PUBLIC);

      TripRequest request =
          new TripRequest(
              "Trip with Foreign Route",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              foreignRoute.getSlug(),
              null,
              List.of());

      assertThrows(BusinessException.class, () -> tripService.createTrip(team, request, organizer));
    }

    @Test
    void shouldThrowForPrivateRouteOnPublicTrip() {
      var privateRoute = dataService.createRoute(team, admin, "Private Route", Visibility.TEAM);

      TripRequest request =
          new TripRequest(
              "Public Trip with Private Route",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              privateRoute.getSlug(),
              null,
              List.of());

      BusinessException exception =
          assertThrows(
              BusinessException.class, () -> tripService.createTrip(team, request, organizer));

      assertTrue(exception.getMessage().contains("private route"));
    }

    @Test
    void shouldAllowTeamRouteOnTeamTrip() {
      var teamRoute = dataService.createRoute(team, admin, "Team Route", Visibility.TEAM);

      TripRequest request =
          new TripRequest(
              "Team Trip with Team Route",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.TEAM,
              teamRoute.getSlug(),
              null,
              List.of());

      TripDto result = tripService.createTrip(team, request, organizer);

      assertNotNull(result);
      assertEquals(teamRoute.getSlug(), result.getRouteSlug());
    }

    @Test
    void shouldThrowForInvalidRouteSlug() {
      TripRequest request =
          new TripRequest(
              "Trip with Invalid Route",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              "nonexistent-route",
              null,
              List.of());

      assertThrows(BusinessException.class, () -> tripService.createTrip(team, request, organizer));
    }

    @Test
    void shouldAllowTeamTripInTeamTeam() {
      Team privateTeam =
          dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
      dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

      TripRequest request =
          new TripRequest(
              "Team Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.TEAM,
              null,
              null,
              List.of());

      TripDto result = tripService.createTrip(privateTeam, request, organizer);

      assertNotNull(result);
      assertEquals(Visibility.TEAM, result.getVisibility());
    }

    @Test
    void shouldCreateStageWithStartAndEndPlaces() {
      var startPlace = dataService.createPlace(team, admin, "Start Location", true, false);
      var endPlace = dataService.createPlace(team, admin, "End Location", false, true);

      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Places")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .startPlaceId(TsidUtils.toString(startPlace.getId()))
              .endPlaceId(TsidUtils.toString(endPlace.getId()))
              .build();

      TripRequest request =
          new TripRequest(
              "Trip with Stage Places",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      TripDto result = tripService.createTrip(team, request, organizer);

      assertNotNull(result);
      assertEquals(1, result.getStages().size());
      assertNotNull(result.getStages().get(0).startPlace());
      assertNotNull(result.getStages().get(0).endPlace());
      assertEquals("Start Location", result.getStages().get(0).startPlace().name());
      assertEquals("End Location", result.getStages().get(0).endPlace().name());
    }

    @Test
    void shouldThrowForStageWithInvalidStartPlaceId() {
      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Invalid Start")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .startPlaceId(TsidUtils.toString(9999L))
              .build();

      TripRequest request =
          new TripRequest(
              "Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      assertThrows(BusinessException.class, () -> tripService.createTrip(team, request, organizer));
    }

    @Test
    void shouldThrowForStageWithInvalidEndPlaceId() {
      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Invalid End")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .endPlaceId(TsidUtils.toString(9999L))
              .build();

      TripRequest request =
          new TripRequest(
              "Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      assertThrows(BusinessException.class, () -> tripService.createTrip(team, request, organizer));
    }

    @Test
    void shouldThrowForStageWithPlaceFromDifferentTeam() {
      Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
      var foreignPlace = dataService.createPlace(otherTeam, admin, "Foreign Place");

      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Foreign Place")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .startPlaceId(TsidUtils.toString(foreignPlace.getId()))
              .build();

      TripRequest request =
          new TripRequest(
              "Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      assertThrows(BusinessException.class, () -> tripService.createTrip(team, request, organizer));
    }
  }

  // ==================== Update Trip ====================

  @Nested
  class UpdateTrip {

    @Test
    void shouldUpdateFields() {
      Trip trip = dataService.createTrip(team, admin, "Original", Instant.now());

      TripRequest request =
          new TripRequest(
              "Updated Title",
              MediaDto.builder().markdown("Updated description").build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.CANCELLED,
              Visibility.TEAM,
              null,
              null,
              List.of());

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertEquals("Updated Title", result.getName());
      assertEquals(Status.CANCELLED, result.getStatus());
      assertEquals(Visibility.TEAM, result.getVisibility());
    }

    @Test
    void shouldUpdateWithRoute() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      var route = dataService.createRoute(team, admin, "Test Route", Visibility.PUBLIC);

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              route.getSlug(),
              null,
              List.of());

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertEquals(route.getSlug(), result.getRouteSlug());
    }

    @Test
    void shouldClearRoute() {
      var route = dataService.createRoute(team, admin, "Test Route", Visibility.PUBLIC);
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      trip.setRoute(route);

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertNull(result.getRouteSlug());
    }

    @Test
    void shouldThrowForRouteFromDifferentTeam() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
      var foreignRoute =
          dataService.createRoute(otherTeam, admin, "Foreign Route", Visibility.PUBLIC);

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              foreignRoute.getSlug(),
              null,
              List.of());

      assertThrows(
          BusinessException.class,
          () -> tripService.updateTrip(team, trip.getSlug(), request, organizer));
    }

    @Test
    void shouldThrowForPrivateRouteOnPublicTrip() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      var privateRoute = dataService.createRoute(team, admin, "Private Route", Visibility.TEAM);

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              privateRoute.getSlug(),
              null,
              List.of());

      BusinessException exception =
          assertThrows(
              BusinessException.class,
              () -> tripService.updateTrip(team, trip.getSlug(), request, organizer));

      assertTrue(exception.getMessage().contains("private route"));
    }

    @Test
    void shouldAllowTeamRouteOnTeamTrip() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Team Trip", Instant.now(), Visibility.TEAM, Status.DRAFT, null);
      var teamRoute = dataService.createRoute(team, admin, "Team Route", Visibility.TEAM);

      TripRequest request =
          new TripRequest(
              "Team Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.TEAM,
              teamRoute.getSlug(),
              null,
              List.of());

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertNotNull(result);
      assertEquals(teamRoute.getSlug(), result.getRouteSlug());
    }

    @Test
    void shouldThrowForNonOrganizer() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      TripRequest request =
          new TripRequest(
              "Updated",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      assertThrows(
          BusinessException.class,
          () -> tripService.updateTrip(team, trip.getSlug(), request, member));
    }

    @Test
    void shouldThrowForPublicVisibilityInTeamVisibilityTeam() {
      Team privateTeam =
          dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
      dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
      Trip trip =
          dataService.createTrip(
              privateTeam,
              organizer,
              "Team Trip",
              Instant.now(),
              Visibility.TEAM,
              Status.DRAFT,
              null);

      TripRequest request =
          new TripRequest(
              "Title",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      BusinessException exception =
          assertThrows(
              BusinessException.class,
              () -> tripService.updateTrip(privateTeam, trip.getSlug(), request, organizer));

      assertTrue(exception.getMessage().contains("Private teams can only have team-only items"));
    }

    @Test
    void shouldAllowTeamTripUpdateInTeamTeam() {
      Team privateTeam =
          dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
      dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
      Trip trip =
          dataService.createTrip(
              privateTeam,
              organizer,
              "Team Trip",
              Instant.now(),
              Visibility.TEAM,
              Status.DRAFT,
              null);

      TripRequest request =
          new TripRequest(
              "Updated Team Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.PUBLISHED,
              Visibility.TEAM,
              null,
              null,
              List.of());

      TripDto result = tripService.updateTrip(privateTeam, trip.getSlug(), request, organizer);

      assertNotNull(result);
      assertEquals("Updated Team Trip", result.getName());
      assertEquals(Visibility.TEAM, result.getVisibility());
      assertEquals(Status.PUBLISHED, result.getStatus());
    }

    @Test
    void shouldAddNewStages() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      StageRequest newStage =
          StageRequest.builder()
              .name("New Stage")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(newStage));

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertEquals(1, result.getStages().size());
      assertEquals("New Stage", result.getStages().get(0).name());
    }

    @Test
    void shouldUpdateExistingStages() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      TripStage existingStage = dataService.createTripStage(admin, trip, "Original Stage", 0);
      String stageId = TsidUtils.toString(existingStage.getId());

      StageRequest updatedStage =
          StageRequest.builder()
              .id(stageId)
              .name("Updated Stage")
              .dateTime(Instant.now().plusSeconds(48 * 3600))
              .media(MediaDto.builder().build())
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(updatedStage));

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertEquals(1, result.getStages().size());
      assertEquals("Updated Stage", result.getStages().get(0).name());
    }

    @Test
    void shouldRemoveStagesNotInRequest() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      dataService.createTripStage(admin, trip, "Stage 1", 0);
      dataService.createTripStage(admin, trip, "Stage 2", 1);

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertEquals(0, result.getStages().size());
    }

    @Test
    void shouldUpdateStageWithStartAndEndPlaces() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      var startPlace = dataService.createPlace(team, admin, "Start Location", true, false);
      var endPlace = dataService.createPlace(team, admin, "End Location", false, true);

      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Places")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .startPlaceId(TsidUtils.toString(startPlace.getId()))
              .endPlaceId(TsidUtils.toString(endPlace.getId()))
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertEquals(1, result.getStages().size());
      assertNotNull(result.getStages().get(0).startPlace());
      assertNotNull(result.getStages().get(0).endPlace());
      assertEquals("Start Location", result.getStages().get(0).startPlace().name());
      assertEquals("End Location", result.getStages().get(0).endPlace().name());
    }

    @Test
    void shouldThrowForStageWithInvalidStartPlaceId() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Invalid Start")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .startPlaceId(TsidUtils.toString(9999L))
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      assertThrows(
          BusinessException.class,
          () -> tripService.updateTrip(team, trip.getSlug(), request, organizer));
    }

    @Test
    void shouldThrowForStageWithInvalidEndPlaceId() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Invalid End")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .endPlaceId(TsidUtils.toString(9999L))
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      assertThrows(
          BusinessException.class,
          () -> tripService.updateTrip(team, trip.getSlug(), request, organizer));
    }

    @Test
    void shouldThrowForStageWithPlaceFromDifferentTeam() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
      var foreignPlace = dataService.createPlace(otherTeam, admin, "Foreign Place");

      StageRequest stage =
          StageRequest.builder()
              .name("Stage with Foreign Place")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .startPlaceId(TsidUtils.toString(foreignPlace.getId()))
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      assertThrows(
          BusinessException.class,
          () -> tripService.updateTrip(team, trip.getSlug(), request, organizer));
    }

    @Test
    void shouldClearStagePlaces() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());
      var startPlace = dataService.createPlace(team, admin, "Start", true, false);
      var endPlace = dataService.createPlace(team, admin, "End", false, true);
      TripStage existingStage = dataService.createTripStage(admin, trip, "Stage", 0);
      existingStage.setStartPlace(startPlace);
      existingStage.setEndPlace(endPlace);
      String stageId = TsidUtils.toString(existingStage.getId());

      StageRequest stage =
          StageRequest.builder()
              .id(stageId)
              .name("Stage")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .startPlaceId(null)
              .endPlaceId(null)
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      TripDto result = tripService.updateTrip(team, trip.getSlug(), request, organizer);

      assertEquals(1, result.getStages().size());
      assertNull(result.getStages().get(0).startPlace());
      assertNull(result.getStages().get(0).endPlace());
    }

    @Test
    void shouldThrowForStageWithInvalidId() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      StageRequest stage =
          StageRequest.builder()
              .id(TsidUtils.toString(9999L))
              .name("Stage with Invalid ID")
              .dateTime(Instant.now().plusSeconds(24 * 3600))
              .media(MediaDto.builder().build())
              .build();

      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of(stage));

      BusinessException exception =
          assertThrows(
              BusinessException.class,
              () -> tripService.updateTrip(team, trip.getSlug(), request, organizer));

      assertTrue(exception.getMessage().contains("Stage"));
    }
  }

  // ==================== Delete Trip ====================

  @Nested
  class DeleteTrip {

    @Test
    void shouldSoftDelete() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      tripService.deleteTrip(team, trip.getSlug(), organizer);

      assertThrows(
          BusinessException.class,
          () -> tripService.getTripDetail(team, trip.getSlug(), organizer));
    }

    @Test
    void shouldThrowForNonOrganizer() {
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      assertThrows(
          BusinessException.class, () -> tripService.deleteTrip(team, trip.getSlug(), member));
    }
  }

  // ==================== Join Trip ====================

  @Nested
  class JoinTrip {

    @Test
    void shouldCreateParticipation() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Test Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);

      TripParticipationDto result = tripService.joinTrip(team, trip.getSlug(), member);

      assertNotNull(result);
      assertEquals(member.getId(), TsidUtils.toLong(result.userId()));
    }

    @Test
    void shouldThrowForDraftTripAsMember() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Draft Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);

      BusinessException exception =
          assertThrows(
              BusinessException.class, () -> tripService.joinTrip(team, trip.getSlug(), member));

      // Member can't see draft trips, so they get "not found"
      assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldThrowForDraftTripAsOrganizer() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Draft Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);

      BusinessException exception =
          assertThrows(
              BusinessException.class, () -> tripService.joinTrip(team, trip.getSlug(), organizer));

      // Organizer can see draft trips, so they get explicit validation message
      assertTrue(exception.getMessage().contains("Can only join published trips"));
    }

    @Test
    void shouldThrowWhenAlreadyJoined() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Test Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);
      dataService.createTripParticipation(trip, member);

      BusinessException exception =
          assertThrows(
              BusinessException.class, () -> tripService.joinTrip(team, trip.getSlug(), member));

      assertTrue(exception.getMessage().contains("already"));
    }

    @Test
    void shouldRestoreSoftDeletedParticipation() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Test Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);
      tripService.joinTrip(team, trip.getSlug(), member);
      tripService.leaveTrip(team, trip.getSlug(), member);

      TripParticipationDto result = tripService.joinTrip(team, trip.getSlug(), member);

      assertNotNull(result);
      assertEquals(member.getId(), TsidUtils.toLong(result.userId()));
    }
  }

  // ==================== Leave Trip ====================

  @Nested
  class LeaveTrip {

    @Test
    void shouldRemoveParticipation() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Test Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);
      tripService.joinTrip(team, trip.getSlug(), member);

      tripService.leaveTrip(team, trip.getSlug(), member);

      // Member can rejoin after leaving
      TripParticipationDto newParticipation = tripService.joinTrip(team, trip.getSlug(), member);
      assertNotNull(newParticipation);
    }

    @Test
    void shouldThrowWhenNotJoined() {
      Trip trip =
          dataService.createTrip(
              team, admin, "Test Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);

      assertThrows(
          BusinessException.class, () -> tripService.leaveTrip(team, trip.getSlug(), member));
    }
  }

  // ==================== Trips Disabled ====================

  @Nested
  class TripsDisabled {

    @BeforeEach
    void disableTrips() {
      team.setEnableTrips(false);
      team = dataService.updateTeam(team);
    }

    @Test
    void shouldThrowOnCreateWhenTripsDisabled() {
      TripRequest request =
          new TripRequest(
              "Test Trip",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      BusinessException exception =
          assertThrows(
              BusinessException.class, () -> tripService.createTrip(team, request, organizer));

      assertTrue(exception.getMessage().contains("disabled"));
    }

    @Test
    void shouldThrowOnUpdateWhenTripsDisabled() {
      // Create trip while trips are enabled
      team.setEnableTrips(true);
      team = dataService.updateTeam(team);
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      // Disable trips
      team.setEnableTrips(false);
      team = dataService.updateTeam(team);

      TripRequest request =
          new TripRequest(
              "Updated Title",
              MediaDto.builder().build(),
              Instant.now().plusSeconds(24 * 3600),
              Status.DRAFT,
              Visibility.PUBLIC,
              null,
              null,
              List.of());

      BusinessException exception =
          assertThrows(
              BusinessException.class,
              () -> tripService.updateTrip(team, trip.getSlug(), request, organizer));

      assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldThrowOnDeleteWhenTripsDisabled() {
      // Create trip while trips are enabled
      team.setEnableTrips(true);
      team = dataService.updateTeam(team);
      Trip trip = dataService.createTrip(team, admin, "Test Trip", Instant.now());

      // Disable trips
      team.setEnableTrips(false);
      team = dataService.updateTeam(team);

      BusinessException exception =
          assertThrows(
              BusinessException.class,
              () -> tripService.deleteTrip(team, trip.getSlug(), organizer));

      assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldThrowOnJoinWhenTripsDisabled() {
      // Create trip while trips are enabled
      team.setEnableTrips(true);
      team = dataService.updateTeam(team);
      Trip trip =
          dataService.createTrip(
              team, admin, "Test Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);

      // Disable trips
      team.setEnableTrips(false);
      team = dataService.updateTeam(team);

      BusinessException exception =
          assertThrows(
              BusinessException.class, () -> tripService.joinTrip(team, trip.getSlug(), member));

      assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldThrowOnLeaveWhenTripsDisabled() {
      // Create trip and join while trips are enabled
      team.setEnableTrips(true);
      team = dataService.updateTeam(team);
      Trip trip =
          dataService.createTrip(
              team, admin, "Test Trip", Instant.now(), Visibility.PUBLIC, Status.PUBLISHED, null);
      tripService.joinTrip(team, trip.getSlug(), member);

      // Disable trips
      team.setEnableTrips(false);
      team = dataService.updateTeam(team);

      BusinessException exception =
          assertThrows(
              BusinessException.class, () -> tripService.leaveTrip(team, trip.getSlug(), member));

      assertTrue(exception.getMessage().contains("not found"));
    }
  }
}
