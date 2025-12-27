package com.tribly.service.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
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
    team = dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);
    admin = dataService.createUser("admin@example.com", "Admin");
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

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

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

    RideListResponse result =
        rideService.listRides("test-team", member.getId(), null, null, null, 0, 10);

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

    RideListResponse result =
        rideService.listRides("test-team", null, null, today, tomorrow, 0, 10);

    assertEquals(2, result.rides().size());
  }

  @Test
  void listRides_shouldShowDraftsToOrganizers() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", organizer.getId(), null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals(Status.DRAFT, result.rides().getFirst().getStatus());
  }

  @Test
  void listRides_shouldHideDraftsFromMembers() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", member.getId(), null, null, null, 0, 10);

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

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().getName());
  }

  @Test
  void listRides_shouldReturnEmptyWhenNonMemberRequestsDrafts() {
    dataService.createRide(
        team, admin, "Draft Ride", "draft-ride", Instant.now(), Visibility.PUBLIC, Status.DRAFT);

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

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

    RideListResponse result =
        rideService.listRides("test-team", organizer.getId(), null, null, null, 0, 10);

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

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().getName());
    assertEquals(Status.PUBLISHED, result.rides().getFirst().getStatus());
  }

  @Test
  void listRides_shouldThrowForNonMemberOfPrivateTeam() {
    Team privateTeam = dataService.createTeam("Private Team", "private-team", Visibility.TEAM);
    User privateTeamAdmin = dataService.createUser("private-admin@example.com", "Private Admin");
    dataService.addUserToTeam(privateTeamAdmin, privateTeam, TeamRole.ADMIN);
    dataService.createRide(
        privateTeam,
        privateTeamAdmin,
        "Private Ride",
        "private-ride",
        Instant.now(),
        Visibility.TEAM,
        Status.PUBLISHED);

    RideListResponse result = rideService.listRides("private-team", null, null, null, null, 0, 10);
    assertEquals(0, result.rides().size());
  }

  // ==================== Get Ride ====================

  @Test
  void getRideBySlug_shouldReturnRide() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());

    RideDto result = rideService.getRideDetail("test-team", "test-ride", null);

    assertEquals("Test Ride", result.getName());
    assertEquals("test-ride", result.getSlug());
  }

  @Test
  void getRideBySlug_shouldThrowForNonexistent() {
    assertThrows(
        BusinessException.class, () -> rideService.getRideDetail("test-team", "nonexistent", null));
  }

  @Test
  void getRideBySlug_shouldShowDraftToOrganizer() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    RideDto result = rideService.getRideDetail("test-team", "draft", organizer.getId());

    assertEquals(Status.DRAFT, result.getStatus());
  }

  @Test
  void getRideBySlug_shouldHideDraftFromMember() {
    dataService.createRide(team, admin, "Draft", "draft", Instant.now(), Status.DRAFT);

    assertThrows(
        BusinessException.class,
        () -> rideService.getRideDetail("test-team", "draft", member.getId()));
  }

  // ==================== Create Ride ====================

  @Test
  void createRide_shouldCreateWithSlug() {
    RideRequest request =
        new RideRequest(
            "Sunday Ride",
            "A nice ride",
            Instant.now().plusSeconds(24 * 3600 * 7),
            Status.DRAFT,
            Visibility.TEAM,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

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
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertNotEquals("test-ride", result.getSlug());
    assertTrue(result.getSlug().startsWith("test-ride-"));
  }

  @Test
  void createRide_shouldCreateWithGroups() {
    GroupRequest group1 = new GroupRequest(null, "Fast", "Fast group", 30, 10, null);
    GroupRequest group2 = new GroupRequest(null, "Slow", "Slow group", 20, 15, null);
    RideRequest request =
        new RideRequest(
            "Group Ride",
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            List.of(group1, group2));

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertEquals("Group Ride", result.getName());
    RideGroupListResponse groups =
        rideService.listGroups("test-team", result.getSlug(), organizer.getId());
    assertEquals(2, groups.data().size());
  }

  @Test
  void createRide_shouldThrowForNonOrganizer() {
    RideRequest request =
        new RideRequest(
            "Test",
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.createRide("test-team", request, member.getId()));
  }

  @Test
  void createRide_shouldThrowForPublicRideInTeamVisibilityTeam() {
    Team privateTeam = dataService.createTeam("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    RideRequest request =
        new RideRequest(
            "Public Ride",
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            List.of());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.createRide("private-team", request, organizer.getId()));

    assertTrue(exception.getMessage().contains("Private teams can only have team-only rides"));
  }

  @Test
  void createRide_shouldSucceedForTeamRideInTeamVisibilityTeam() {
    Team privateTeam = dataService.createTeam("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    RideRequest request =
        new RideRequest(
            "Team Ride",
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.TEAM,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide("private-team", request, organizer.getId());

    assertNotNull(result);
    assertEquals("Team Ride", result.getName());
    assertEquals(Visibility.TEAM, result.getVisibility());
  }

  // ==================== Update Ride ====================

  @Test
  void updateRide_shouldUpdateFields() {
    dataService.createRide(team, admin, "Original", "original", Instant.now());
    RideRequest request =
        new RideRequest(
            "Updated Title",
            "Updated description",
            Instant.now().plusSeconds(24 * 3600),
            Status.CANCELLED,
            Visibility.TEAM,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide("test-team", "original", request, organizer.getId());

    assertEquals("Updated Title", result.getName());
    assertEquals("Updated description", result.getDescription());
    assertEquals(Status.CANCELLED, result.getStatus());
    assertEquals(Visibility.TEAM, result.getVisibility());
  }

  @Test
  void updateRide_shouldUpdatePartialFields() {
    dataService.createRide(team, admin, "Original", "original", Instant.now());
    RideRequest request =
        new RideRequest(
            "New Title",
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.PUBLISHED,
            Visibility.PUBLIC,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide("test-team", "original", request, organizer.getId());

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
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.PUBLISHED,
            Visibility.PUBLIC,
            null,
            null,
            List.of());

    RideDto result =
        rideService.updateRide("test-team", "published-ride", request, organizer.getId());

    assertEquals("Updated Title", result.getName());
    assertEquals(Status.PUBLISHED, result.getStatus());
  }

  @Test
  void updateRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideRequest request =
        new RideRequest(
            "New",
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.updateRide("test-team", "test", request, member.getId()));
  }

  @Test
  void updateRide_shouldThrowForPublicVisibilityInTeamVisibilityTeam() {
    Team privateTeam = dataService.createTeam("Private Team", "private-team", Visibility.TEAM);
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
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            List.of());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.updateRide("private-team", "team-ride", request, organizer.getId()));

    assertTrue(exception.getMessage().contains("Private teams can only have team-only rides"));
  }

  @Test
  void updateRide_shouldSucceedForTeamVisibilityInTeamVisibilityTeam() {
    Team privateTeam = dataService.createTeam("Private Team", "private-team", Visibility.TEAM);
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
            null,
            Instant.now().plusSeconds(24 * 3600),
            Status.DRAFT,
            Visibility.TEAM,
            null,
            null,
            List.of());

    RideDto result =
        rideService.updateRide("private-team", "team-ride", request, organizer.getId());

    assertEquals("Updated Title", result.getName());
    assertEquals(Visibility.TEAM, result.getVisibility());
  }

  // ==================== Delete Ride ====================

  @Test
  void deleteRide_shouldSoftDelete() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());

    rideService.deleteRide("test-team", "test", organizer.getId());

    assertThrows(
        BusinessException.class,
        () -> rideService.getRideDetail("test-team", "test", organizer.getId()));
  }

  @Test
  void deleteRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());

    assertThrows(
        BusinessException.class, () -> rideService.deleteRide("test-team", "test", member.getId()));
  }

  // ==================== List Groups ====================

  @Test
  void listGroups_shouldReturnGroupsOrderedBySortOrder() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    dataService.createRideGroup(ride, "Group 1", 2);
    dataService.createRideGroup(ride, "Group 2", 1);

    RideGroupListResponse result = rideService.listGroups("test-team", "test", null);

    assertEquals(2, result.data().size());
    assertEquals("Group 2", result.data().get(0).name());
    assertEquals("Group 1", result.data().get(1).name());
  }

  @Test
  void listGroups_shouldReturnEmptyForNoGroups() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());

    RideGroupListResponse result = rideService.listGroups("test-team", "test", null);

    assertEquals(0, result.data().size());
  }

  // ==================== Join Group ====================

  @Test
  void joinGroup_shouldCreateParticipation() {
    Ride ride =
        dataService.createRide(team, admin, "Test", "test", Instant.now(), Status.PUBLISHED);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    RideParticipationDto result =
        rideService.joinGroup("test-team", "test", group.getId(), member.getId());

    assertNotNull(result);
    assertEquals(member.getId(), TsidUtils.toLong(result.userId()));
  }

  @Test
  void joinGroup_shouldThrowForDraftRide() {
    Ride ride =
        dataService.createRide(
            team, admin, "Draft", "draft", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "draft", group.getId(), member.getId()));
  }

  @Test
  void joinGroup_shouldThrowForDraftRideEvenForOrganizer() {
    Ride ride =
        dataService.createRide(
            team, admin, "Draft", "draft", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "draft", group.getId(), organizer.getId()));

    assertTrue(exception.getMessage().contains("published"));
  }

  @Test
  void joinGroup_shouldThrowWhenAlreadyInGroup() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");
    dataService.createParticipation(group, member);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "test", group.getId(), member.getId()));

    assertTrue(exception.getMessage().contains("already"));
  }

  @Test
  void joinGroup_shouldThrowWhenGroupFull() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroupWithMaxParticipants(ride, "Group", 1);
    dataService.createParticipation(group, organizer);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "test", group.getId(), member.getId()));

    assertTrue(
        exception.getMessage().contains("full") || exception.getMessage().contains("capacity"));
  }

  // ==================== Leave Group ====================

  @Test
  void leaveGroup_shouldRemoveParticipation() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");
    dataService.createParticipation(group, member);

    rideService.leaveGroup("test-team", "test", group.getId(), member.getId());

    // Member can now rejoin (participation was soft-deleted)
    RideParticipationDto newParticipation =
        rideService.joinGroup("test-team", "test", group.getId(), member.getId());
    assertNotNull(newParticipation);
  }

  @Test
  void leaveGroup_shouldThrowWhenNotInGroup() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", Instant.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.leaveGroup("test-team", "test", group.getId(), member.getId()));

    assertTrue(
        exception.getMessage().contains("not") || exception.getMessage().contains("participation"));
  }
}
