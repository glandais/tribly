package com.tribly.service.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.teams.response.MemberDto;
import com.tribly.dto.teams.response.MemberListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamMembershipServiceTest {

  @Inject TeamMembershipService membershipService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User admin;
  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    team = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    admin = dataService.createUser("admin@example.com", "Admin");
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
    dataService.addUserToTeam(admin, team, TeamRole.ADMIN);
  }

  // ==================== Get Team Members ====================

  @Test
  void getTeamMembers_shouldReturnMembersForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.ORGANIZER);

    MemberListResponse result = membershipService.getTeamMembers("test-team", admin.getId(), 0, 10);

    assertEquals(3, result.members().size()); // admin + user1 + user2
  }

  @Test
  void getTeamMembers_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertThrows(
        BusinessException.class,
        () -> membershipService.getTeamMembers("test-team", user1.getId(), 0, 10));
  }

  @Test
  void getTeamMembers_shouldSupportPagination() {
    for (int i = 3; i <= 7; i++) {
      User user = dataService.createUser("user" + i + "@example.com", "User " + i);
      dataService.addUserToTeam(user, team, TeamRole.MEMBER);
    }

    MemberListResponse result = membershipService.getTeamMembers("test-team", admin.getId(), 0, 3);

    assertEquals(3, result.members().size());
    assertEquals(6, result.total()); // admin + 5 users
  }

  // ==================== Join Team ====================

  @Test
  void joinTeam_shouldJoinPublicTeam() {
    MemberDto result = membershipService.joinTeam("test-team", user1.getId());

    assertNotNull(result);
    assertEquals(user1.getId(), TsidUtils.toLong(result.userId()));
    assertEquals(TeamRole.MEMBER, result.role());
  }

  @Test
  void joinTeam_shouldThrowWhenAlreadyMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> membershipService.joinTeam("test-team", user1.getId()));

    assertTrue(exception.getMessage().contains("already a member"));
  }

  @Test
  void joinTeam_shouldRestoreSoftDeletedMembership() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(membership);

    MemberDto result = membershipService.joinTeam("test-team", user1.getId());

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.MEMBER, result.role());
  }

  @Test
  void joinTeam_shouldThrowWhenTeamFull() {
    team.setMaxMembers(1);
    dataService.updateTeam(team);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> membershipService.joinTeam("test-team", user1.getId()));

    assertTrue(exception.getMessage().contains("maximum member capacity"));
  }

  // ==================== Add Member ====================

  @Test
  void addMember_shouldAddMemberAsAdmin() {
    MemberDto result =
        membershipService.addMember("test-team", user1.getId(), TeamRole.ORGANIZER, admin.getId());

    assertNotNull(result);
    assertEquals(user1.getId(), TsidUtils.toLong(result.userId()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  @Test
  void addMember_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertThrows(
        BusinessException.class,
        () ->
            membershipService.addMember(
                "test-team", user2.getId(), TeamRole.MEMBER, user1.getId()));
  }

  @Test
  void addMember_shouldThrowWhenAlreadyMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                membershipService.addMember(
                    "test-team", user1.getId(), TeamRole.ORGANIZER, admin.getId()));

    assertTrue(exception.getMessage().contains("already a member"));
  }

  @Test
  void addMember_shouldRestoreSoftDeletedMembership() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(membership);

    MemberDto result =
        membershipService.addMember("test-team", user1.getId(), TeamRole.ORGANIZER, admin.getId());

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  // ==================== Update Member Role ====================

  @Test
  void updateMemberRole_shouldUpdateRole() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    MemberDto result =
        membershipService.updateMemberRole(
            "test-team", user1.getId(), TeamRole.ORGANIZER, admin.getId());

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  @Test
  void updateMemberRole_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertThrows(
        BusinessException.class,
        () ->
            membershipService.updateMemberRole(
                "test-team", user2.getId(), TeamRole.ORGANIZER, user1.getId()));
  }

  @Test
  void updateMemberRole_shouldPreventDemotingLastAdmin() {
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                membershipService.updateMemberRole(
                    "test-team", admin.getId(), TeamRole.MEMBER, admin.getId()));

    assertTrue(exception.getMessage().contains("last admin"));
  }

  @Test
  void updateMemberRole_shouldAllowDemotingWhenMultipleAdmins() {
    User admin2 = dataService.createUser("admin2@example.com", "Admin Two");
    dataService.addUserToTeam(admin2, team, TeamRole.ADMIN);

    MemberDto result =
        membershipService.updateMemberRole(
            "test-team", admin.getId(), TeamRole.MEMBER, admin2.getId());

    assertEquals(TeamRole.MEMBER, result.role());
  }

  // ==================== Remove Member ====================

  @Test
  void removeMember_shouldRemoveMemberAsAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    membershipService.removeMember("test-team", user1.getId(), admin.getId());

    // Verify soft deletion by trying to get team - should not see it
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> membershipService.getTeamMembers("test-team", user1.getId(), 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void removeMember_shouldAllowSelfRemoval() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    membershipService.removeMember("test-team", user1.getId(), user1.getId());

    // Verify soft deletion
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> membershipService.getTeamMembers("test-team", user1.getId(), 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void removeMember_shouldPreventRemovingLastAdmin() {
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> membershipService.removeMember("test-team", admin.getId(), admin.getId()));

    assertTrue(exception.getMessage().contains("last admin"));
  }

  @Test
  void removeMember_shouldAllowRemovingWhenMultipleAdmins() {
    User admin2 = dataService.createUser("admin2@example.com", "Admin Two");
    dataService.addUserToTeam(admin2, team, TeamRole.ADMIN);

    membershipService.removeMember("test-team", admin.getId(), admin2.getId());

    // Verify soft deletion
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> membershipService.getTeamMembers("test-team", admin.getId(), 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void removeMember_shouldThrowForNonAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertThrows(
        BusinessException.class,
        () -> membershipService.removeMember("test-team", user2.getId(), user1.getId()));
  }

  // ==================== Leave Team ====================

  @Test
  void leaveTeam_shouldRemoveSelf() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    membershipService.leaveTeam("test-team", user1.getId());

    // Verify soft deletion
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> membershipService.getTeamMembers("test-team", user1.getId(), 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void leaveTeam_shouldPreventLastAdminLeaving() {
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> membershipService.leaveTeam("test-team", admin.getId()));

    assertTrue(exception.getMessage().contains("last admin"));
  }
}
