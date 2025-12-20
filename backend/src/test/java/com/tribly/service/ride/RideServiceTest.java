package com.tribly.service.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.rides.request.CreateGroupRequest;
import com.tribly.dto.rides.request.CreateRideRequest;
import com.tribly.dto.rides.request.UpdateGroupRequest;
import com.tribly.dto.rides.request.UpdateRideRequest;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.ParticipationStatus;
import com.tribly.enums.RideStatus;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
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
    team = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
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
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Public Ride",
        "public-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Team Ride",
        "team-ride",
        LocalDate.now(),
        Visibility.TEAM,
        RideStatus.PUBLISHED);

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Public Ride", result.rides().getFirst().title());
  }

  @Test
  void listRides_shouldReturnTeamRidesForMembers() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Public Ride",
        "public-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Team Ride",
        "team-ride",
        LocalDate.now(),
        Visibility.TEAM,
        RideStatus.PUBLISHED);

    RideListResponse result =
        rideService.listRides("test-team", member.getId(), null, null, null, 0, 10);

    assertEquals(2, result.rides().size());
  }

  @Test
  void listRides_shouldFilterByStatus() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published",
        "published",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    dataService.createRideWithVisibilityAndStatus(
        team, admin, "Draft", "draft", LocalDate.now(), Visibility.PUBLIC, RideStatus.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", null, null, null, RideStatus.PUBLISHED, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals(RideStatus.PUBLISHED, result.rides().getFirst().status());
  }

  @Test
  void listRides_shouldFilterByDateRange() {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    LocalDate nextWeek = today.plusDays(7);
    dataService.createRide(team, admin, "Today Ride", "today", today);
    dataService.createRide(team, admin, "Tomorrow Ride", "tomorrow", tomorrow);
    dataService.createRide(team, admin, "Next Week", "next-week", nextWeek);

    RideListResponse result =
        rideService.listRides("test-team", null, today, tomorrow, null, 0, 10);

    assertEquals(2, result.rides().size());
  }

  @Test
  void listRides_shouldShowDraftsToOrganizers() {
    dataService.createRideWithStatus(
        team, admin, "Draft", "draft", LocalDate.now(), RideStatus.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", organizer.getId(), null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals(RideStatus.DRAFT, result.rides().getFirst().status());
  }

  @Test
  void listRides_shouldHideDraftsFromMembers() {
    dataService.createRideWithStatus(
        team, admin, "Draft", "draft", LocalDate.now(), RideStatus.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", member.getId(), null, null, null, 0, 10);

    assertEquals(0, result.rides().size());
  }

  @Test
  void listRides_shouldHideDraftsFromNonMembers() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Draft Ride",
        "draft-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.DRAFT);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().title());
  }

  @Test
  void listRides_shouldReturnEmptyWhenNonMemberRequestsDrafts() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Draft Ride",
        "draft-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", null, null, null, RideStatus.DRAFT, 0, 10);

    assertEquals(0, result.rides().size());
  }

  @Test
  void listRides_shouldReturnDraftsWhenOrganizerRequestsThem() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Draft Ride",
        "draft-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.DRAFT);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);

    RideListResponse result =
        rideService.listRides("test-team", organizer.getId(), null, null, RideStatus.DRAFT, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Draft Ride", result.rides().getFirst().title());
    assertEquals(RideStatus.DRAFT, result.rides().getFirst().status());
  }

  @Test
  void listRides_shouldReturnPublishedWhenNonMemberRequestsThem() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Draft Ride",
        "draft-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.DRAFT);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);

    RideListResponse result =
        rideService.listRides("test-team", null, null, null, RideStatus.PUBLISHED, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().title());
    assertEquals(RideStatus.PUBLISHED, result.rides().getFirst().status());
  }

  @Test
  void listRides_shouldThrowForNonMemberOfPrivateTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    User privateTeamAdmin = dataService.createUser("private-admin@example.com", "Private Admin");
    dataService.addUserToTeam(privateTeamAdmin, privateTeam, TeamRole.ADMIN);
    dataService.createRideWithVisibilityAndStatus(
        privateTeam,
        privateTeamAdmin,
        "Private Ride",
        "private-ride",
        LocalDate.now(),
        Visibility.TEAM,
        RideStatus.PUBLISHED);

    assertThrows(
        BusinessException.class,
        () -> rideService.listRides("private-team", null, null, null, null, 0, 10));
  }

  // ==================== Get Ride ====================

  @Test
  void getRideBySlug_shouldReturnRide() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", LocalDate.now());

    RideDetailDto result = rideService.getRideDetail("test-team", "test-ride", null);

    assertEquals("Test Ride", result.title());
    assertEquals("test-ride", result.slug());
  }

  @Test
  void getRideBySlug_shouldThrowForNonexistent() {
    assertThrows(
        BusinessException.class, () -> rideService.getRideDetail("test-team", "nonexistent", null));
  }

  @Test
  void getRideBySlug_shouldShowDraftToOrganizer() {
    dataService.createRideWithStatus(
        team, admin, "Draft", "draft", LocalDate.now(), RideStatus.DRAFT);

    RideDetailDto result = rideService.getRideDetail("test-team", "draft", organizer.getId());

    assertEquals(RideStatus.DRAFT, result.status());
  }

  @Test
  void getRideBySlug_shouldHideDraftFromMember() {
    dataService.createRideWithStatus(
        team, admin, "Draft", "draft", LocalDate.now(), RideStatus.DRAFT);

    assertThrows(
        BusinessException.class,
        () -> rideService.getRideDetail("test-team", "draft", member.getId()));
  }

  // ==================== Create Ride ====================

  @Test
  void createRide_shouldCreateWithSlug() {
    CreateRideRequest request =
        new CreateRideRequest(
            "Sunday Ride",
            "A nice ride",
            LocalDate.now().plusDays(7),
            LocalTime.of(9, 0),
            Visibility.TEAM,
            null,
            null,
            null,
            null);

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertNotNull(result);
    assertEquals("Sunday Ride", result.title());
    assertEquals("sunday-ride", result.slug());
    assertEquals(RideStatus.DRAFT, result.status());
  }

  @Test
  void createRide_shouldHandleSlugCollision() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", LocalDate.now());
    CreateRideRequest request =
        new CreateRideRequest(
            "Test Ride", null, LocalDate.now().plusDays(1), null, null, null, null, null, null);

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertNotEquals("test-ride", result.slug());
    assertTrue(result.slug().startsWith("test-ride-"));
  }

  @Test
  void createRide_shouldCreateWithGroups() {
    CreateGroupRequest group1 = new CreateGroupRequest("Fast", "Fast group", 30, 10, null);
    CreateGroupRequest group2 = new CreateGroupRequest("Slow", "Slow group", 20, 15, null);
    CreateRideRequest request =
        new CreateRideRequest(
            "Group Ride",
            null,
            LocalDate.now().plusDays(1),
            null,
            null,
            null,
            null,
            null,
            List.of(group1, group2));

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertEquals("Group Ride", result.title());
    RideGroupListResponse groups =
        rideService.listGroups("test-team", result.slug(), organizer.getId());
    assertEquals(2, groups.data().size());
  }

  @Test
  void createRide_shouldThrowForNonOrganizer() {
    CreateRideRequest request =
        new CreateRideRequest(
            "Test", null, LocalDate.now().plusDays(1), null, null, null, null, null, null);

    assertThrows(
        BusinessException.class,
        () -> rideService.createRide("test-team", request, member.getId()));
  }

  @Test
  void createRide_shouldThrowForPublicRideInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    CreateRideRequest request =
        new CreateRideRequest(
            "Public Ride",
            null,
            LocalDate.now().plusDays(1),
            null,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.createRide("private-team", request, organizer.getId()));

    assertTrue(exception.getMessage().contains("Private teams can only have team-only rides"));
  }

  @Test
  void createRide_shouldSucceedForTeamRideInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    CreateRideRequest request =
        new CreateRideRequest(
            "Team Ride",
            null,
            LocalDate.now().plusDays(1),
            null,
            Visibility.TEAM,
            null,
            null,
            null,
            null);

    RideDto result = rideService.createRide("private-team", request, organizer.getId());

    assertNotNull(result);
    assertEquals("Team Ride", result.title());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  // ==================== Update Ride ====================

  @Test
  void updateRide_shouldUpdateFields() {
    dataService.createRide(team, admin, "Original", "original", LocalDate.now());
    UpdateRideRequest request =
        new UpdateRideRequest(
            "Updated Title",
            "Updated description",
            LocalDate.now().plusDays(1),
            LocalTime.of(10, 0),
            RideStatus.CANCELLED,
            Visibility.TEAM,
            null,
            null,
            null);

    RideDto result = rideService.updateRide("test-team", "original", request, organizer.getId());

    assertEquals("Updated Title", result.title());
    assertEquals("Updated description", result.description());
    assertEquals(RideStatus.CANCELLED, result.status());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateRide_shouldUpdatePartialFields() {
    dataService.createRide(team, admin, "Original", "original", LocalDate.now());
    UpdateRideRequest request =
        new UpdateRideRequest(
            "New Title", null, null, null, RideStatus.PUBLISHED, null, null, null, null);

    RideDto result = rideService.updateRide("test-team", "original", request, organizer.getId());

    assertEquals("New Title", result.title());
    assertEquals(RideStatus.PUBLISHED, result.status());
  }

  @Test
  void updateRide_shouldPreserveStatusWhenNull() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDate.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    UpdateRideRequest request =
        new UpdateRideRequest("Updated Title", null, null, null, null, null, null, null, null);

    RideDto result =
        rideService.updateRide("test-team", "published-ride", request, organizer.getId());

    assertEquals("Updated Title", result.title());
    assertEquals(RideStatus.PUBLISHED, result.status());
  }

  @Test
  void updateRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    UpdateRideRequest request =
        new UpdateRideRequest("New", null, null, null, null, null, null, null, null);

    assertThrows(
        BusinessException.class,
        () -> rideService.updateRide("test-team", "test", request, member.getId()));
  }

  @Test
  void updateRide_shouldThrowForPublicVisibilityInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
    dataService.createRideWithVisibilityAndStatus(
        privateTeam,
        organizer,
        "Team Ride",
        "team-ride",
        LocalDate.now(),
        Visibility.TEAM,
        RideStatus.DRAFT);

    UpdateRideRequest request =
        new UpdateRideRequest(null, null, null, null, null, Visibility.PUBLIC, null, null, null);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.updateRide("private-team", "team-ride", request, organizer.getId()));

    assertTrue(exception.getMessage().contains("Private teams can only have team-only rides"));
  }

  @Test
  void updateRide_shouldSucceedForTeamVisibilityInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
    dataService.createRideWithVisibilityAndStatus(
        privateTeam,
        organizer,
        "Team Ride",
        "team-ride",
        LocalDate.now(),
        Visibility.TEAM,
        RideStatus.DRAFT);

    UpdateRideRequest request =
        new UpdateRideRequest(
            "Updated Title", null, null, null, null, Visibility.TEAM, null, null, null);

    RideDto result =
        rideService.updateRide("private-team", "team-ride", request, organizer.getId());

    assertEquals("Updated Title", result.title());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  // ==================== Delete Ride ====================

  @Test
  void deleteRide_shouldSoftDelete() {
    dataService.createRide(team, admin, "Test", "test", LocalDate.now());

    rideService.deleteRide("test-team", "test", organizer.getId());

    assertThrows(
        BusinessException.class,
        () -> rideService.getRideDetail("test-team", "test", organizer.getId()));
  }

  @Test
  void deleteRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", LocalDate.now());

    assertThrows(
        BusinessException.class, () -> rideService.deleteRide("test-team", "test", member.getId()));
  }

  // ==================== Create Group ====================

  @Test
  void createGroup_shouldCreateGroup() {
    dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    CreateGroupRequest request = new CreateGroupRequest("Fast Group", "Description", 30, 10, null);

    RideGroupDto result = rideService.createGroup("test-team", "test", request, organizer.getId());

    assertNotNull(result);
    assertEquals("Fast Group", result.name());
    assertEquals(30, result.averageSpeed());
    assertEquals(10, result.maxParticipants());
  }

  @Test
  void createGroup_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    CreateGroupRequest request = new CreateGroupRequest("Group", null, null, null, null);

    assertThrows(
        BusinessException.class,
        () -> rideService.createGroup("test-team", "test", request, member.getId()));
  }

  // ==================== List Groups ====================

  @Test
  void listGroups_shouldReturnGroupsOrderedBySortOrder() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    dataService.createRideGroupWithOrder(ride, "Group 1", 2);
    dataService.createRideGroupWithOrder(ride, "Group 2", 1);

    RideGroupListResponse result = rideService.listGroups("test-team", "test", null);

    assertEquals(2, result.data().size());
    assertEquals("Group 2", result.data().get(0).name());
    assertEquals("Group 1", result.data().get(1).name());
  }

  @Test
  void listGroups_shouldReturnEmptyForNoGroups() {
    dataService.createRide(team, admin, "Test", "test", LocalDate.now());

    RideGroupListResponse result = rideService.listGroups("test-team", "test", null);

    assertEquals(0, result.data().size());
  }

  // ==================== Update Group ====================

  @Test
  void updateGroup_shouldUpdateFields() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Original");
    UpdateGroupRequest request =
        new UpdateGroupRequest("Updated Name", "Updated desc", 25, 20, null);

    RideGroupDto result =
        rideService.updateGroup("test-team", "test", group.getId(), request, organizer.getId());

    assertEquals("Updated Name", result.name());
    assertEquals("Updated desc", result.description());
    assertEquals(25, result.averageSpeed());
    assertEquals(20, result.maxParticipants());
  }

  @Test
  void updateGroup_shouldUpdatePartialFields() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Original");
    UpdateGroupRequest request = new UpdateGroupRequest("New Name", null, null, null, null);

    RideGroupDto result =
        rideService.updateGroup("test-team", "test", group.getId(), request, organizer.getId());

    assertEquals("New Name", result.name());
  }

  @Test
  void updateGroup_shouldPreserveNameWhenNull() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Original Name");
    UpdateGroupRequest request =
        new UpdateGroupRequest(null, "Updated description", null, null, null);

    RideGroupDto result =
        rideService.updateGroup("test-team", "test", group.getId(), request, organizer.getId());

    assertEquals("Original Name", result.name());
    assertEquals("Updated description", result.description());
  }

  @Test
  void updateGroup_shouldThrowForNonOrganizer() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");
    UpdateGroupRequest request = new UpdateGroupRequest("New", null, null, null, null);

    assertThrows(
        BusinessException.class,
        () -> rideService.updateGroup("test-team", "test", group.getId(), request, member.getId()));
  }

  // ==================== Delete Group ====================

  @Test
  void deleteGroup_shouldSoftDelete() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");

    rideService.deleteGroup("test-team", "test", group.getId(), organizer.getId());

    RideGroupListResponse result = rideService.listGroups("test-team", "test", null);
    assertEquals(0, result.data().size());
  }

  @Test
  void deleteGroup_shouldThrowForNonOrganizer() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");

    assertThrows(
        BusinessException.class,
        () -> rideService.deleteGroup("test-team", "test", group.getId(), member.getId()));
  }

  // ==================== Join Group ====================

  @Test
  void joinGroup_shouldCreateParticipation() {
    Ride ride =
        dataService.createRideWithStatus(
            team, admin, "Test", "test", LocalDate.now(), RideStatus.PUBLISHED);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    RideParticipationDto result =
        rideService.joinGroup("test-team", "test", group.getId(), member.getId(), null);

    assertNotNull(result);
    assertEquals(member.getId(), TsidUtils.toLong(result.userId()));
    assertEquals(ParticipationStatus.REGISTERED, result.status());
  }

  @Test
  void joinGroup_shouldThrowForDraftRide() {
    Ride ride =
        dataService.createRideWithVisibilityAndStatus(
            team, admin, "Draft", "draft", LocalDate.now(), Visibility.PUBLIC, RideStatus.DRAFT);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "draft", group.getId(), member.getId(), null));
  }

  @Test
  void joinGroup_shouldThrowForDraftRideEvenForOrganizer() {
    Ride ride =
        dataService.createRideWithVisibilityAndStatus(
            team, admin, "Draft", "draft", LocalDate.now(), Visibility.PUBLIC, RideStatus.DRAFT);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                rideService.joinGroup(
                    "test-team", "draft", group.getId(), organizer.getId(), null));

    assertTrue(exception.getMessage().contains("published"));
  }

  @Test
  void joinGroup_shouldThrowWhenAlreadyInGroup() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");
    dataService.createParticipationWithStatus(group, member, ParticipationStatus.CONFIRMED);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "test", group.getId(), member.getId(), null));

    assertTrue(exception.getMessage().contains("already"));
  }

  @Test
  void joinGroup_shouldThrowWhenGroupFull() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroupWithMaxParticipants(ride, "Group", 1);
    dataService.createParticipationWithStatus(group, organizer, ParticipationStatus.CONFIRMED);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "test", group.getId(), member.getId(), null));

    assertTrue(
        exception.getMessage().contains("full") || exception.getMessage().contains("capacity"));
  }

  // ==================== Leave Group ====================

  @Test
  void leaveGroup_shouldRemoveParticipation() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");
    dataService.createParticipationWithStatus(group, member, ParticipationStatus.CONFIRMED);

    rideService.leaveGroup("test-team", "test", group.getId(), member.getId());

    // Member can now rejoin (participation was soft-deleted)
    RideParticipationDto newParticipation =
        rideService.joinGroup("test-team", "test", group.getId(), member.getId(), null);
    assertNotNull(newParticipation);
  }

  @Test
  void leaveGroup_shouldThrowWhenNotInGroup() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDate.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.leaveGroup("test-team", "test", group.getId(), member.getId()));

    assertTrue(
        exception.getMessage().contains("not") || exception.getMessage().contains("participation"));
  }
}
