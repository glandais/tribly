package com.tribly.service.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.*;
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
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideServiceTest {

  @Inject RideService rideService;
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

  // ==================== List Rides ====================

  @Test
  void listRides_shouldReturnPublishedRidesForNonMembers() {
    dataService.createRide(
        team,
        admin,
        "Public Ride",
        "public-ride",
        Instant.now(),
        Visibility.PUBLIC,
        Status.PUBLISHED);
    dataService.createRide(
        team, admin, "Team Ride", "team-ride", Instant.now(), Visibility.TEAM, Status.PUBLISHED);

    RideListResponse result = rideService.listRides(team, null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Public Ride", result.rides().getFirst().getName());
  }

  @Test
  void listRides_shouldReturnTeamRidesForMembers() {
    dataService.createRide(
        team,
        admin,
        "Public Ride",
        "public-ride",
        Instant.now(),
        Visibility.PUBLIC,
        Status.PUBLISHED);
    dataService.createRide(
        team, admin, "Team Ride", "team-ride", Instant.now(), Visibility.TEAM, Status.PUBLISHED);

    RideListResponse result = rideService.listRides(team, member, null, null, null, 0, 10);

    assertEquals(2, result.rides().size());
  }

  @Test
  void listRides_shouldFilterByDateRange() {
    Instant today = Instant.now();
    Instant tomorrow = today.plusSeconds(24 * 3600);
    Instant nextWeek = today.plusSeconds(24 * 3600 * 7);
    dataService.createRide(team, admin, "Today Ride", "today", today);
    dataService.createRide(team, admin, "Tomorrow Ride", "tomorrow", tomorrow);
    dataService.createRide(team, admin, "Next Week", "next-week", nextWeek);

    RideListResponse result = rideService.listRides(team, null, null, today, tomorrow, 0, 10);

    assertEquals(2, result.rides().size());
  }

  @Test
  void listRides_shouldShowDraftsToOrganizers() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    RideListResponse result = rideService.listRides(team, organizer, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals(Status.DRAFT, result.rides().getFirst().getStatus());
  }

  @Test
  void listRides_shouldHideDraftsFromMembers() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    RideListResponse result = rideService.listRides(team, member, null, null, null, 0, 10);

    assertEquals(0, result.rides().size());
  }

  @Test
  void listRides_shouldHideDraftsFromNonMembers() {
    dataService.createRide(
        team, admin, "Draft Ride", "draft-ride", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    dataService.createRide(
        team,
        admin,
        "Published Ride",
        "published-ride",
        Instant.now(),
        Visibility.PUBLIC,
        Status.PUBLISHED);

    RideListResponse result = rideService.listRides(team, null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().getName());
  }

  @Test
  void listRides_shouldReturnEmptyWhenNonMemberRequestsDrafts() {
    dataService.createRide(
        team, admin, "Draft Ride", "draft-ride", Instant.now(), Visibility.PUBLIC, Status.DRAFT);

    RideListResponse result = rideService.listRides(team, null, null, null, null, 0, 10);

    assertEquals(0, result.rides().size());
  }

  @Test
  void listRides_shouldReturnDraftsWhenOrganizerRequestsThem() {
    dataService.createRide(
        team, admin, "Draft Ride", "draft-ride", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    dataService.createRide(
        team,
        admin,
        "Published Ride",
        "published-ride",
        Instant.now(),
        Visibility.PUBLIC,
        Status.PUBLISHED);

    RideListResponse result = rideService.listRides(team, organizer, null, null, null, 0, 10);

    assertEquals(2, result.rides().size());
  }

  @Test
  void listRides_shouldReturnPublishedWhenNonMemberRequestsThem() {
    dataService.createRide(
        team, admin, "Draft Ride", "draft-ride", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    dataService.createRide(
        team,
        admin,
        "Published Ride",
        "published-ride",
        Instant.now(),
        Visibility.PUBLIC,
        Status.PUBLISHED);

    RideListResponse result = rideService.listRides(team, null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().getName());
    assertEquals(Status.PUBLISHED, result.rides().getFirst().getStatus());
  }

  @Test
  void listRides_shouldThrowForNonMemberOfPrivateTeam() {
    User privateTeamAdmin = dataService.createUser("private-admin@example.com", "Private Admin");
    Team privateTeam =
        dataService.createTeam(privateTeamAdmin, "Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(privateTeamAdmin, privateTeam, TeamRole.ADMIN);
    dataService.createRide(
        privateTeam,
        privateTeamAdmin,
        "Private Ride",
        "private-ride",
        Instant.now(),
        Visibility.TEAM,
        Status.PUBLISHED);

    RideListResponse result = rideService.listRides(privateTeam, null, null, null, null, 0, 10);
    assertEquals(0, result.rides().size());
  }

  // ==================== Get Ride ====================

  @Test
  void getRideBySlug_shouldReturnRide() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());

    RideDto result = rideService.getRideDetail(team, "test-ride", null);

    assertEquals("Test Ride", result.getName());
    assertEquals("test-ride", result.getSlug());
  }

  @Test
  void getRideBySlug_shouldThrowForNonexistent() {
    assertThrows(
        BusinessException.class, () -> rideService.getRideDetail(team, "nonexistent", null));
  }

  @Test
  void getRideBySlug_shouldShowDraftToOrganizer() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    RideDto result = rideService.getRideDetail(team, "draft", organizer);

    assertEquals(Status.DRAFT, result.getStatus());
  }

  @Test
  void getRideBySlug_shouldHideDraftFromMember() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    assertThrows(BusinessException.class, () -> rideService.getRideDetail(team, "draft", member));
  }

  // ==================== Create Ride ====================

  @Test
  void createRide_shouldCreateWithSlug() {
    RideRequest request =
        new RideRequest(
            "Sunday Ride",
            MediaDto.builder().markdown("A nice ride").build(),
            Instant.now().plusSeconds(24 * 3600 * 7),
            Status.DRAFT,
            Visibility.TEAM,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide(team, request, organizer);

    assertNotNull(result);
    assertEquals("Sunday Ride", result.getName());
    assertEquals("sunday-ride", result.getSlug());
    assertEquals(Status.DRAFT, result.getStatus());
  }

  @Test
  void createRide_shouldHandleSlugCollision() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide(team, request, organizer);

    assertNotEquals("test-ride", result.getSlug());
    assertTrue(result.getSlug().startsWith("test-ride-"));
  }

  @Test
  void createRide_shouldCreateWithGroups() {
    GroupRequest group1 = new GroupRequest(null, "Fast", null, 30, 10, null);
    GroupRequest group2 = new GroupRequest(null, "Slow", null, 20, 15, null);
    RideRequest request =
        new RideRequest(
            "Group Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of(group1, group2));

    RideDto result = rideService.createRide(team, request, organizer);

    assertEquals("Group Ride", result.getName());
    List<RideGroupDto> groups = result.getGroups();
    assertEquals(2, groups.size());
  }

  @Test
  void createRide_shouldThrowForNonOrganizer() {
    RideRequest request =
        new RideRequest(
            "Test",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    assertThrows(BusinessException.class, () -> rideService.createRide(team, request, member));
  }

  @Test
  void createRide_shouldThrowForPublicRideInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    RideRequest request =
        new RideRequest(
            "Public Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> rideService.createRide(privateTeam, request, organizer));

    assertTrue(exception.getMessage().contains("Private teams can only have team-only items"));
  }

  @Test
  void createRide_shouldSucceedForTeamRideInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    RideRequest request =
        new RideRequest(
            "Team Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.TEAM,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide(privateTeam, request, organizer);

    assertNotNull(result);
    assertEquals("Team Ride", result.getName());
    assertEquals(Visibility.TEAM, result.getVisibility());
  }

  @Test
  void createRide_shouldCreateWithRoute() {
    var route = dataService.createRoute(team, admin, "Test Route", Visibility.PUBLIC);

    RideRequest request =
        new RideRequest(
            "Ride with Route",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            route.getSlug(),
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide(team, request, organizer);

    assertNotNull(result);
    assertNotNull(result.getRouteSlug());
    assertEquals(route.getSlug(), result.getRouteSlug());
  }

  @Test
  void createRide_shouldThrowForInvalidRouteSlug() {
    RideRequest request =
        new RideRequest(
            "Ride with Invalid Route",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            "nonexistent-route",
            null,
            null,
            null,
            List.of());

    assertThrows(BusinessException.class, () -> rideService.createRide(team, request, organizer));
  }

  @Test
  void createRide_shouldThrowForRouteFromDifferentTeam() {
    Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
    var foreignRoute =
        dataService.createRoute(otherTeam, admin, "Foreign Route", Visibility.PUBLIC);

    RideRequest request =
        new RideRequest(
            "Ride with Foreign Route",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            foreignRoute.getSlug(),
            null,
            null,
            null,
            List.of());

    assertThrows(BusinessException.class, () -> rideService.createRide(team, request, organizer));
  }

  @Test
  void createRide_shouldThrowForPrivateRouteOnPublicTeam() {
    var privateRoute = dataService.createRoute(team, admin, "Private Route", Visibility.TEAM);

    RideRequest request =
        new RideRequest(
            "Public Ride with Private Route",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            privateRoute.getSlug(),
            null,
            null,
            null,
            List.of());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> rideService.createRide(team, request, organizer));

    assertTrue(exception.getMessage().contains("private route"));
  }

  @Test
  void createRide_shouldAllowTeamRouteOnTeamRide() {
    var teamRoute = dataService.createRoute(team, admin, "Team Route", Visibility.TEAM);

    RideRequest request =
        new RideRequest(
            "Team Ride with Team Route",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.TEAM,
            teamRoute.getSlug(),
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide(team, request, organizer);

    assertNotNull(result);
    assertEquals(teamRoute.getSlug(), result.getRouteSlug());
  }

  @Test
  void createRide_shouldCreateWithStartAndEndPlaces() {
    var startPlace = dataService.createPlace(team, admin, "Start Location", true, false);
    var endPlace = dataService.createPlace(team, admin, "End Location", false, true);
    String startPlaceId = TsidUtils.toString(startPlace.getId());
    String endPlaceId = TsidUtils.toString(endPlace.getId());

    RideRequest request =
        new RideRequest(
            "Ride with Places",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            startPlaceId,
            endPlaceId,
            null,
            List.of());

    RideDto result = rideService.createRide(team, request, organizer);

    assertNotNull(result);
    assertNotNull(result.getStartPlace());
    assertNotNull(result.getEndPlace());
    assertEquals("Start Location", result.getStartPlace().name());
    assertEquals("End Location", result.getEndPlace().name());
  }

  @Test
  void createRide_shouldThrowForInvalidStartPlaceId() {
    RideRequest request =
        new RideRequest(
            "Ride with Invalid Start",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            TsidUtils.toString(9999L),
            null,
            null,
            List.of());

    assertThrows(BusinessException.class, () -> rideService.createRide(team, request, organizer));
  }

  @Test
  void createRide_shouldThrowForInvalidEndPlaceId() {
    RideRequest request =
        new RideRequest(
            "Ride with Invalid End",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            TsidUtils.toString(9999L),
            null,
            List.of());

    assertThrows(BusinessException.class, () -> rideService.createRide(team, request, organizer));
  }

  @Test
  void createRide_shouldThrowForPlaceFromDifferentTeam() {
    Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
    var foreignPlace = dataService.createPlace(otherTeam, admin, "Foreign Place");
    String foreignPlaceId = TsidUtils.toString(foreignPlace.getId());

    RideRequest request =
        new RideRequest(
            "Ride with Foreign Place",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            foreignPlaceId,
            null,
            null,
            List.of());

    assertThrows(BusinessException.class, () -> rideService.createRide(team, request, organizer));
  }

  // ==================== Update Ride ====================

  @Test
  void updateRide_shouldUpdateFields() {
    dataService.createRide(team, admin, "Original", "original", Instant.now());
    RideRequest request =
        new RideRequest(
            "Updated Title",
            MediaDto.builder().markdown("Updated description").build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.CANCELLED,
            Visibility.TEAM,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "original", request, organizer);

    assertEquals("Updated Title", result.getName());
    assertEquals("Updated description", result.getMedia().markdown());
    assertEquals(Status.CANCELLED, result.getStatus());
    assertEquals(Visibility.TEAM, result.getVisibility());
  }

  @Test
  void updateRide_shouldUpdatePartialFields() {
    dataService.createRide(team, admin, "Original", "original", Instant.now());
    RideRequest request =
        new RideRequest(
            "New Title",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.PUBLISHED,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "original", request, organizer);

    assertEquals("New Title", result.getName());
    assertEquals(Status.PUBLISHED, result.getStatus());
  }

  @Test
  void updateRide_shouldPreserveStatusWhenNull() {
    dataService.createRide(
        team,
        admin,
        "Published Ride",
        "published-ride",
        Instant.now(),
        Visibility.PUBLIC,
        Status.PUBLISHED);
    RideRequest request =
        new RideRequest(
            "Updated Title",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.PUBLISHED,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "published-ride", request, organizer);

    assertEquals("Updated Title", result.getName());
    assertEquals(Status.PUBLISHED, result.getStatus());
  }

  @Test
  void updateRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideRequest request =
        new RideRequest(
            "New",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class, () -> rideService.updateRide(team, "test", request, member));
  }

  @Test
  void updateRide_shouldThrowForPublicVisibilityInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
    dataService.createRide(
        privateTeam,
        organizer,
        "Team Ride",
        "team-ride",
        Instant.now(),
        Visibility.TEAM,
        Status.DRAFT);

    RideRequest request =
        new RideRequest(
            "Title",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.updateRide(privateTeam, "team-ride", request, organizer));

    assertTrue(exception.getMessage().contains("Private teams can only have team-only items"));
  }

  @Test
  void updateRide_shouldSucceedForTeamVisibilityInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeam(organizer, "Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
    dataService.createRide(
        privateTeam,
        organizer,
        "Team Ride",
        "team-ride",
        Instant.now(),
        Visibility.TEAM,
        Status.DRAFT);

    RideRequest request =
        new RideRequest(
            "Updated Title",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.TEAM,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(privateTeam, "team-ride", request, organizer);

    assertEquals("Updated Title", result.getName());
    assertEquals(Visibility.TEAM, result.getVisibility());
  }

  @Test
  void updateRide_shouldAddNewGroups() {
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());

    GroupRequest newGroup = new GroupRequest(null, "New Group", null, 25, 10, null);
    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of(newGroup));

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertEquals(1, result.getGroups().size());
    assertEquals("New Group", result.getGroups().getFirst().name());
    assertEquals(25, result.getGroups().getFirst().averageSpeed());
  }

  @Test
  void updateRide_shouldUpdateExistingGroups() {
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    RideGroup existingGroup = dataService.createRideGroup(admin, ride, "Original Group", 0);
    String groupId = TsidUtils.toString(existingGroup.getId());

    GroupRequest updatedGroup = new GroupRequest(groupId, "Updated Group", null, 30, 15, null);
    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of(updatedGroup));

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertEquals(1, result.getGroups().size());
    assertEquals("Updated Group", result.getGroups().getFirst().name());
    assertEquals(30, result.getGroups().getFirst().averageSpeed());
  }

  @Test
  void updateRide_shouldRemoveGroupsNotInRequest() {
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    dataService.createRideGroup(admin, ride, "Group 1", 0);
    dataService.createRideGroup(admin, ride, "Group 2", 1);

    // Update with empty groups list - should remove all groups
    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertEquals(0, result.getGroups().size());
  }

  @Test
  void updateRide_shouldReorderGroups() {
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    RideGroup group1 = dataService.createRideGroup(admin, ride, "Group A", 0);
    RideGroup group2 = dataService.createRideGroup(admin, ride, "Group B", 1);
    String group1Id = TsidUtils.toString(group1.getId());
    String group2Id = TsidUtils.toString(group2.getId());

    // Swap order: B first, then A
    GroupRequest reorderedGroup1 = new GroupRequest(group2Id, "Group B", null, 25, 10, null);
    GroupRequest reorderedGroup2 = new GroupRequest(group1Id, "Group A", null, 20, 5, null);
    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of(reorderedGroup1, reorderedGroup2));

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertEquals(2, result.getGroups().size());
    assertEquals("Group B", result.getGroups().get(0).name());
    assertEquals("Group A", result.getGroups().get(1).name());
  }

  @Test
  void updateRide_shouldAddAndKeepExistingGroups() {
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    RideGroup existingGroup = dataService.createRideGroup(admin, ride, "Existing Group", 0);
    String existingGroupId = TsidUtils.toString(existingGroup.getId());

    GroupRequest keepExisting =
        new GroupRequest(existingGroupId, "Existing Group", null, 25, 10, null);
    GroupRequest addNew = new GroupRequest(null, "New Group", null, 30, 15, null);
    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of(keepExisting, addNew));

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertEquals(2, result.getGroups().size());
    assertEquals("Existing Group", result.getGroups().get(0).name());
    assertEquals("New Group", result.getGroups().get(1).name());
  }

  @Test
  void updateRide_shouldThrowForGroupWithInvalidId() {
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());

    GroupRequest invalidGroup =
        new GroupRequest(TsidUtils.toString(9999L), "Invalid Group", null, 25, 10, null);
    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of(invalidGroup));

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.updateRide(team, "test-ride", request, organizer));

    assertTrue(exception.getMessage().contains("Group"));
  }

  @Test
  void updateRide_shouldUpdateWithStartAndEndPlaces() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    var startPlace = dataService.createPlace(team, admin, "Start Location", true, false);
    var endPlace = dataService.createPlace(team, admin, "End Location", false, true);
    String startPlaceId = TsidUtils.toString(startPlace.getId());
    String endPlaceId = TsidUtils.toString(endPlace.getId());

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            startPlaceId,
            endPlaceId,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertNotNull(result.getStartPlace());
    assertNotNull(result.getEndPlace());
    assertEquals("Start Location", result.getStartPlace().name());
    assertEquals("End Location", result.getEndPlace().name());
  }

  @Test
  void updateRide_shouldThrowForInvalidStartPlaceId() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            TsidUtils.toString(9999L),
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.updateRide(team, "test-ride", request, organizer));
  }

  @Test
  void updateRide_shouldThrowForInvalidEndPlaceId() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            TsidUtils.toString(9999L),
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.updateRide(team, "test-ride", request, organizer));
  }

  @Test
  void updateRide_shouldThrowForPlaceFromDifferentTeam() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
    var foreignPlace = dataService.createPlace(otherTeam, admin, "Foreign Place");
    String foreignPlaceId = TsidUtils.toString(foreignPlace.getId());

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            foreignPlaceId,
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.updateRide(team, "test-ride", request, organizer));
  }

  @Test
  void updateRide_shouldClearStartAndEndPlaces() {
    var startPlace = dataService.createPlace(team, admin, "Start", true, false);
    var endPlace = dataService.createPlace(team, admin, "End", false, true);
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    ride.setStart(startPlace);
    ride.setEnd(endPlace);

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertNull(result.getStartPlace());
    assertNull(result.getEndPlace());
  }

  @Test
  void updateRide_shouldUpdateWithRoute() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    var route = dataService.createRoute(team, admin, "Test Route", Visibility.PUBLIC);

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            route.getSlug(),
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertNotNull(result.getRouteSlug());
    assertEquals(route.getSlug(), result.getRouteSlug());
  }

  @Test
  void updateRide_shouldThrowForInvalidRouteSlug() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            "nonexistent-route",
            null,
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.updateRide(team, "test-ride", request, organizer));
  }

  @Test
  void updateRide_shouldThrowForRouteFromDifferentTeam() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    Team otherTeam = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
    var foreignRoute =
        dataService.createRoute(otherTeam, admin, "Foreign Route", Visibility.PUBLIC);

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            foreignRoute.getSlug(),
            null,
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.updateRide(team, "test-ride", request, organizer));
  }

  @Test
  void updateRide_shouldThrowForPrivateRouteOnPublicRide() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    var privateRoute = dataService.createRoute(team, admin, "Private Route", Visibility.TEAM);

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            privateRoute.getSlug(),
            null,
            null,
            null,
            List.of());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.updateRide(team, "test-ride", request, organizer));

    assertTrue(exception.getMessage().contains("private route"));
  }

  @Test
  void updateRide_shouldAllowTeamRouteOnTeamRide() {
    Ride ride =
        dataService.createRide(
            team, admin, "Team Ride", "team-ride", Instant.now(), Visibility.TEAM, Status.DRAFT);
    var teamRoute = dataService.createRoute(team, admin, "Team Route", Visibility.TEAM);

    RideRequest request =
        new RideRequest(
            "Team Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.TEAM,
            teamRoute.getSlug(),
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "team-ride", request, organizer);

    assertNotNull(result);
    assertEquals(teamRoute.getSlug(), result.getRouteSlug());
  }

  @Test
  void updateRide_shouldClearRoute() {
    var route = dataService.createRoute(team, admin, "Test Route", Visibility.PUBLIC);
    Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
    ride.setRoute(route);

    RideRequest request =
        new RideRequest(
            "Test Ride",
            MediaDto.builder().build(),
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide(team, "test-ride", request, organizer);

    assertNull(result.getRouteSlug());
  }

  // ==================== Delete Ride ====================

  @Test
  void deleteRide_shouldSoftDelete() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());

    rideService.deleteRide(team, "test", organizer);

    assertThrows(BusinessException.class, () -> rideService.getRideDetail(team, "test", organizer));
  }

  @Test
  void deleteRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());

    assertThrows(BusinessException.class, () -> rideService.deleteRide(team, "test", member));
  }

  // ==================== List Groups ====================

  @Test
  void listGroups_shouldReturnGroupsOrderedBySortOrder() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    dataService.createRideGroup(admin, ride, "Group 1", 2);
    dataService.createRideGroup(admin, ride, "Group 2", 1);

    RideDto rideDto = rideService.getRideDetail(team, ride.getSlug(), admin);
    List<RideGroupDto> groups = rideDto.getGroups();

    assertEquals(2, groups.size());
    assertEquals("Group 2", groups.get(0).name());
    assertEquals("Group 1", groups.get(1).name());
  }

  @Test
  void listGroups_shouldReturnEmptyForNoGroups() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());

    RideDto rideDto = rideService.getRideDetail(team, ride.getSlug(), admin);

    assertEquals(0, rideDto.getGroups().size());
    assertEquals(0, rideDto.getGroupCount());
  }

  // ==================== Join Group ====================

  @Test
  void joinGroup_shouldCreateParticipation() {
    Ride ride =
        dataService.createRide(team, admin, "Test", "test", Instant.now(), Status.PUBLISHED);
    RideGroup group = dataService.createRideGroup(admin, ride, "Group");

    RideParticipationDto result = rideService.joinGroup(team, "test", group.getId(), member);

    assertNotNull(result);
    assertEquals(member.getId(), TsidUtils.toLong(result.userId()));
  }

  @Test
  void joinGroup_shouldThrowForDraftRide() {
    Ride ride =
        dataService.createRide(
            team, admin, "Draft", "draft", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    RideGroup group = dataService.createRideGroup(admin, ride, "Group");

    assertThrows(
        BusinessException.class, () -> rideService.joinGroup(team, "draft", group.getId(), member));
  }

  @Test
  void joinGroup_shouldThrowForDraftRideEvenForOrganizer() {
    Ride ride =
        dataService.createRide(
            team, admin, "Draft", "draft", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    RideGroup group = dataService.createRideGroup(admin, ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup(team, "draft", group.getId(), organizer));

    assertTrue(exception.getMessage().contains("published"));
  }

  @Test
  void joinGroup_shouldThrowWhenAlreadyInGroup() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroup(admin, ride, "Group");
    dataService.createParticipation(group, member);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup(team, "test", group.getId(), member));

    assertTrue(exception.getMessage().contains("already"));
  }

  @Test
  void joinGroup_shouldThrowWhenGroupFull() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroupWithMaxParticipants(admin, ride, "Group", 1);
    dataService.createParticipation(group, organizer);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup(team, "test", group.getId(), member));

    assertTrue(
        exception.getMessage().contains("full") || exception.getMessage().contains("capacity"));
  }

  // ==================== Leave Group ====================

  @Test
  void leaveGroup_shouldRemoveParticipation() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroup(admin, ride, "Group");
    dataService.createParticipation(group, member);

    rideService.leaveGroup(team, "test", group.getId(), member);

    // Member can now rejoin (participation was soft-deleted)
    RideParticipationDto newParticipation =
        rideService.joinGroup(team, "test", group.getId(), member);
    assertNotNull(newParticipation);
  }

  @Test
  void leaveGroup_shouldThrowWhenNotInGroup() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroup(admin, ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.leaveGroup(team, "test", group.getId(), member));

    assertTrue(
        exception.getMessage().contains("not") || exception.getMessage().contains("participation"));
  }
}
