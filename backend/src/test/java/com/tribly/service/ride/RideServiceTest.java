package com.tribly.service.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.RideStatus;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
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
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Team Ride",
        "team-ride",
        LocalDateTime.now(),
        Visibility.TEAM,
        RideStatus.PUBLISHED);

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Public Ride", result.rides().getFirst().name());
  }

  @Test
  void listRides_shouldReturnTeamRidesForMembers() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Public Ride",
        "public-ride",
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Team Ride",
        "team-ride",
        LocalDateTime.now(),
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
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    dataService.createRideWithVisibilityAndStatus(
        team, admin, "Draft", "draft", LocalDateTime.now(), Visibility.PUBLIC, RideStatus.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", null, null, null, RideStatus.PUBLISHED, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals(RideStatus.PUBLISHED, result.rides().getFirst().status());
  }

  @Test
  void listRides_shouldFilterByDateRange() {
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime tomorrow = today.plusDays(1);
    LocalDateTime nextWeek = today.plusDays(7);
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
        team, admin, "Draft", "draft", LocalDateTime.now(), RideStatus.DRAFT);

    RideListResponse result =
        rideService.listRides("test-team", organizer.getId(), null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals(RideStatus.DRAFT, result.rides().getFirst().status());
  }

  @Test
  void listRides_shouldHideDraftsFromMembers() {
    dataService.createRideWithStatus(
        team, admin, "Draft", "draft", LocalDateTime.now(), RideStatus.DRAFT);

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
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.DRAFT);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);

    RideListResponse result = rideService.listRides("test-team", null, null, null, null, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().name());
  }

  @Test
  void listRides_shouldReturnEmptyWhenNonMemberRequestsDrafts() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Draft Ride",
        "draft-ride",
        LocalDateTime.now(),
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
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.DRAFT);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);

    RideListResponse result =
        rideService.listRides("test-team", organizer.getId(), null, null, RideStatus.DRAFT, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Draft Ride", result.rides().getFirst().name());
    assertEquals(RideStatus.DRAFT, result.rides().getFirst().status());
  }

  @Test
  void listRides_shouldReturnPublishedWhenNonMemberRequestsThem() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Draft Ride",
        "draft-ride",
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.DRAFT);
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);

    RideListResponse result =
        rideService.listRides("test-team", null, null, null, RideStatus.PUBLISHED, 0, 10);

    assertEquals(1, result.rides().size());
    assertEquals("Published Ride", result.rides().getFirst().name());
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
        LocalDateTime.now(),
        Visibility.TEAM,
        RideStatus.PUBLISHED);

    assertThrows(
        BusinessException.class,
        () -> rideService.listRides("private-team", null, null, null, null, 0, 10));
  }

  // ==================== Get Ride ====================

  @Test
  void getRideBySlug_shouldReturnRide() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", LocalDateTime.now());

    RideDto result = rideService.getRideDetail("test-team", "test-ride", null);

    assertEquals("Test Ride", result.name());
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
        team, admin, "Draft", "draft", LocalDateTime.now(), RideStatus.DRAFT);

    RideDto result = rideService.getRideDetail("test-team", "draft", organizer.getId());

    assertEquals(RideStatus.DRAFT, result.status());
  }

  @Test
  void getRideBySlug_shouldHideDraftFromMember() {
    dataService.createRideWithStatus(
        team, admin, "Draft", "draft", LocalDateTime.now(), RideStatus.DRAFT);

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
            LocalDateTime.now().plusDays(7),
            RideStatus.DRAFT,
            Visibility.TEAM,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertNotNull(result);
    assertEquals("Sunday Ride", result.name());
    assertEquals("sunday-ride", result.slug());
    assertEquals(RideStatus.DRAFT, result.status());
  }

  @Test
  void createRide_shouldHandleSlugCollision() {
    dataService.createRide(team, admin, "Test Ride", "test-ride", LocalDateTime.now());
    RideRequest request =
        new RideRequest(
            "Test Ride",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertNotEquals("test-ride", result.slug());
    assertTrue(result.slug().startsWith("test-ride-"));
  }

  @Test
  void createRide_shouldCreateWithGroups() {
    GroupRequest group1 = new GroupRequest(null, "Fast", "Fast group", 30, 10, null);
    GroupRequest group2 = new GroupRequest(null, "Slow", "Slow group", 20, 15, null);
    RideRequest request =
        new RideRequest(
            "Group Ride",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            List.of(group1, group2));

    RideDto result = rideService.createRide("test-team", request, organizer.getId());

    assertEquals("Group Ride", result.name());
    RideGroupListResponse groups =
        rideService.listGroups("test-team", result.slug(), organizer.getId());
    assertEquals(2, groups.data().size());
  }

  @Test
  void createRide_shouldThrowForNonOrganizer() {
    RideRequest request =
        new RideRequest(
            "Test",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            List.of());

    assertThrows(
        BusinessException.class,
        () -> rideService.createRide("test-team", request, member.getId()));
  }

  @Test
  void createRide_shouldThrowForPublicRideInTeamVisibilityTeam() {
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    RideRequest request =
        new RideRequest(
            "Public Ride",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.PUBLIC,
            null,
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
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);

    RideRequest request =
        new RideRequest(
            "Team Ride",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.TEAM,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.createRide("private-team", request, organizer.getId());

    assertNotNull(result);
    assertEquals("Team Ride", result.name());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  // ==================== Update Ride ====================

  @Test
  void updateRide_shouldUpdateFields() {
    dataService.createRide(team, admin, "Original", "original", LocalDateTime.now());
    RideRequest request =
        new RideRequest(
            "Updated Title",
            "Updated description",
            LocalDateTime.now().plusDays(1),
            RideStatus.CANCELLED,
            Visibility.TEAM,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide("test-team", "original", request, organizer.getId());

    assertEquals("Updated Title", result.name());
    assertEquals("Updated description", result.description());
    assertEquals(RideStatus.CANCELLED, result.status());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateRide_shouldUpdatePartialFields() {
    dataService.createRide(team, admin, "Original", "original", LocalDateTime.now());
    RideRequest request =
        new RideRequest(
            "New Title",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.PUBLISHED,
            Visibility.PUBLIC,
            null,
            null,
            null,
            List.of());

    RideDto result = rideService.updateRide("test-team", "original", request, organizer.getId());

    assertEquals("New Title", result.name());
    assertEquals(RideStatus.PUBLISHED, result.status());
  }

  @Test
  void updateRide_shouldPreserveStatusWhenNull() {
    dataService.createRideWithVisibilityAndStatus(
        team,
        admin,
        "Published Ride",
        "published-ride",
        LocalDateTime.now(),
        Visibility.PUBLIC,
        RideStatus.PUBLISHED);
    RideRequest request =
        new RideRequest(
            "Updated Title",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.PUBLISHED,
            Visibility.PUBLIC,
            null,
            null,
            null,
            List.of());

    RideDto result =
        rideService.updateRide("test-team", "published-ride", request, organizer.getId());

    assertEquals("Updated Title", result.name());
    assertEquals(RideStatus.PUBLISHED, result.status());
  }

  @Test
  void updateRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());
    RideRequest request =
        new RideRequest(
            "New",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            List.of());

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
        LocalDateTime.now(),
        Visibility.TEAM,
        RideStatus.DRAFT);

    RideRequest request =
        new RideRequest(
            "Title",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.PUBLIC,
            null,
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
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
    dataService.createRideWithVisibilityAndStatus(
        privateTeam,
        organizer,
        "Team Ride",
        "team-ride",
        LocalDateTime.now(),
        Visibility.TEAM,
        RideStatus.DRAFT);

    RideRequest request =
        new RideRequest(
            "Updated Title",
            null,
            LocalDateTime.now().plusDays(1),
            RideStatus.DRAFT,
            Visibility.TEAM,
            null,
            null,
            null,
            List.of());

    RideDto result =
        rideService.updateRide("private-team", "team-ride", request, organizer.getId());

    assertEquals("Updated Title", result.name());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  // ==================== Delete Ride ====================

  @Test
  void deleteRide_shouldSoftDelete() {
    dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());

    rideService.deleteRide("test-team", "test", organizer.getId());

    assertThrows(
        BusinessException.class,
        () -> rideService.getRideDetail("test-team", "test", organizer.getId()));
  }

  @Test
  void deleteRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());

    assertThrows(
        BusinessException.class, () -> rideService.deleteRide("test-team", "test", member.getId()));
  }

  // ==================== List Groups ====================

  @Test
  void listGroups_shouldReturnGroupsOrderedBySortOrder() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());
    dataService.createRideGroupWithOrder(ride, "Group 1", 2);
    dataService.createRideGroupWithOrder(ride, "Group 2", 1);

    RideGroupListResponse result = rideService.listGroups("test-team", "test", null);

    assertEquals(2, result.data().size());
    assertEquals("Group 2", result.data().get(0).name());
    assertEquals("Group 1", result.data().get(1).name());
  }

  @Test
  void listGroups_shouldReturnEmptyForNoGroups() {
    dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());

    RideGroupListResponse result = rideService.listGroups("test-team", "test", null);

    assertEquals(0, result.data().size());
  }

  // ==================== Join Group ====================

  @Test
  void joinGroup_shouldCreateParticipation() {
    Ride ride =
        dataService.createRideWithStatus(
            team, admin, "Test", "test", LocalDateTime.now(), RideStatus.PUBLISHED);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    RideParticipationDto result =
        rideService.joinGroup("test-team", "test", group.getId(), member.getId());

    assertNotNull(result);
    assertEquals(member.getId(), TsidUtils.toLong(result.userId()));
  }

  @Test
  void joinGroup_shouldThrowForDraftRide() {
    Ride ride =
        dataService.createRideWithVisibilityAndStatus(
            team,
            admin,
            "Draft",
            "draft",
            LocalDateTime.now(),
            Visibility.PUBLIC,
            RideStatus.DRAFT);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "draft", group.getId(), member.getId()));
  }

  @Test
  void joinGroup_shouldThrowForDraftRideEvenForOrganizer() {
    Ride ride =
        dataService.createRideWithVisibilityAndStatus(
            team,
            admin,
            "Draft",
            "draft",
            LocalDateTime.now(),
            Visibility.PUBLIC,
            RideStatus.DRAFT);
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.joinGroup("test-team", "draft", group.getId(), organizer.getId()));

    assertTrue(exception.getMessage().contains("published"));
  }

  @Test
  void joinGroup_shouldThrowWhenAlreadyInGroup() {
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());
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
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());
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
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());
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
    Ride ride = dataService.createRide(team, admin, "Test", "test", LocalDateTime.now());
    RideGroup group = dataService.createRideGroup(ride, "Group");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> rideService.leaveGroup("test-team", "test", group.getId(), member.getId()));

    assertTrue(
        exception.getMessage().contains("not") || exception.getMessage().contains("participation"));
  }
}
