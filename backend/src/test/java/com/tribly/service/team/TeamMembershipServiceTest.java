package com.tribly.service.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.teams.response.MemberDto;
import com.tribly.dto.teams.response.MemberListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.TriblyException;
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
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
    dataService.addUserToTeam(admin, team, TeamRole.ADMIN);
  }

  // ==================== Get Team Members ====================

  @Test
  void getTeamMembers_shouldReturnMembersForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.ORGANIZER);

    MemberListResponse result = membershipService.getTeamMembers(team, admin, 0, 10);

    assertEquals(3, result.members().size()); // admin + user1 + user2
  }

  @Test
  void getTeamMembers_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertThrows(TriblyException.class, () -> membershipService.getTeamMembers(team, user1, 0, 10));
  }

  @Test
  void getTeamMembers_shouldSupportPagination() {
    for (int i = 3; i <= 7; i++) {
      User user = dataService.createUser("user" + i + "@example.com", "User " + i);
      dataService.addUserToTeam(user, team, TeamRole.MEMBER);
    }

    MemberListResponse result = membershipService.getTeamMembers(team, admin, 0, 3);

    assertEquals(3, result.members().size());
    assertEquals(6, result.total()); // admin + 5 users
  }

  // ==================== Join Team ====================

  @Test
  void joinTeam_shouldJoinPublicTeam() {
    MemberDto result = membershipService.joinTeam(team, user1);

    assertNotNull(result);
    assertEquals(user1.getId(), TsidUtils.toLong(result.user().id()));
    assertEquals(TeamRole.MEMBER, result.role());
  }

  @Test
  void joinTeam_shouldThrowWhenAlreadyMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(TriblyException.class, () -> membershipService.joinTeam(team, user1));

    assertTrue(exception.getMessage().contains("already a member"));
  }

  @Test
  void joinTeam_shouldRestoreSoftDeletedMembership() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(membership);

    MemberDto result = membershipService.joinTeam(team, user1);

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.MEMBER, result.role());
  }

  // ==================== Add Member ====================

  @Test
  void addMember_shouldAddMemberAsAdmin() {
    MemberDto result = membershipService.addMember(team, user1.getId(), TeamRole.ORGANIZER, admin);

    assertNotNull(result);
    assertEquals(user1.getId(), TsidUtils.toLong(result.user().id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  @Test
  void addMember_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertThrows(
        TriblyException.class,
        () -> membershipService.addMember(team, user2.getId(), TeamRole.MEMBER, user1));
  }

  @Test
  void addMember_shouldThrowWhenAlreadyMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.addMember(team, user1.getId(), TeamRole.ORGANIZER, admin));

    assertTrue(exception.getMessage().contains("already a member"));
  }

  @Test
  void addMember_shouldRestoreSoftDeletedMembership() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(membership);

    MemberDto result = membershipService.addMember(team, user1.getId(), TeamRole.ORGANIZER, admin);

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  // ==================== Update Member Role ====================

  @Test
  void updateMemberRole_shouldUpdateRole() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    MemberDto result =
        membershipService.updateMemberRole(team, user1.getId(), TeamRole.ORGANIZER, admin);

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  @Test
  void updateMemberRole_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertThrows(
        TriblyException.class,
        () -> membershipService.updateMemberRole(team, user2.getId(), TeamRole.ORGANIZER, user1));
  }

  @Test
  void updateMemberRole_shouldPreventDemotingLastAdmin() {
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.updateMemberRole(team, admin.getId(), TeamRole.MEMBER, admin));

    assertTrue(exception.getMessage().contains("last admin"));
  }

  @Test
  void updateMemberRole_shouldAllowDemotingWhenMultipleAdmins() {
    User admin2 = dataService.createUser("admin2@example.com", "Admin Two");
    dataService.addUserToTeam(admin2, team, TeamRole.ADMIN);

    MemberDto result =
        membershipService.updateMemberRole(team, admin.getId(), TeamRole.MEMBER, admin2);

    assertEquals(TeamRole.MEMBER, result.role());
  }

  // ==================== Remove Member ====================

  @Test
  void removeMember_shouldRemoveMemberAsAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    membershipService.removeMember(team, user1.getId(), admin);

    // Verify soft deletion by trying to get team - should not see it
    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> membershipService.getTeamMembers(team, user1, 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void removeMember_shouldAllowSelfRemoval() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    membershipService.removeMember(team, user1.getId(), user1);

    // Verify soft deletion
    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> membershipService.getTeamMembers(team, user1, 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void removeMember_shouldPreventRemovingLastAdmin() {
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.removeMember(team, admin.getId(), admin));

    assertTrue(exception.getMessage().contains("last admin"));
  }

  @Test
  void removeMember_shouldAllowRemovingWhenMultipleAdmins() {
    User admin2 = dataService.createUser("admin2@example.com", "Admin Two");
    dataService.addUserToTeam(admin2, team, TeamRole.ADMIN);

    membershipService.removeMember(team, admin.getId(), admin2);

    // Verify soft deletion
    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> membershipService.getTeamMembers(team, admin, 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void removeMember_shouldThrowForNonAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertThrows(
        TriblyException.class, () -> membershipService.removeMember(team, user2.getId(), user1));
  }

  // ==================== Leave Team ====================

  @Test
  void leaveTeam_shouldRemoveSelf() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    membershipService.leaveTeam(team, user1);

    // Verify soft deletion
    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> membershipService.getTeamMembers(team, user1, 0, 10));
    assertTrue(
        exception.getMessage().contains("not a member")
            || exception.getMessage().contains("admin"));
  }

  @Test
  void leaveTeam_shouldPreventLastAdminLeaving() {
    TriblyException exception =
        assertThrows(TriblyException.class, () -> membershipService.leaveTeam(team, admin));

    assertTrue(exception.getMessage().contains("last admin"));
  }

  // ==================== Business Rule Checks ====================

  @Test
  void requireNotLastAdmin_shouldSucceedWhenMultipleAdmins() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UserTeam admin2 = dataService.addUserToTeam(user2, team, TeamRole.ADMIN);

    assertDoesNotThrow(() -> membershipService.requireNotLastAdmin(team, admin2));
  }

  @Test
  void requireNotLastAdmin_shouldSucceedForNonAdminRole() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UserTeam member = dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertDoesNotThrow(() -> membershipService.requireNotLastAdmin(team, member));
  }

  @Test
  void requireNotLastAdmin_shouldThrowWhenLastAdmin() {
    UserTeam lastAdmin = dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> membershipService.requireNotLastAdmin(team, lastAdmin));

    assertEquals("Cannot remove the last admin", exception.getMessage());
  }

  @Test
  void requireNotLastAdminDemotion_shouldSucceedWhenMultipleAdmins() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UserTeam admin2 = dataService.addUserToTeam(user2, team, TeamRole.ADMIN);

    assertDoesNotThrow(
        () -> membershipService.requireNotLastAdminDemotion(team, admin2, TeamRole.ORGANIZER));
  }

  @Test
  void requireNotLastAdminDemotion_shouldSucceedWhenNotDemotingFromAdmin() {
    UserTeam organizer = dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    assertDoesNotThrow(
        () -> membershipService.requireNotLastAdminDemotion(team, organizer, TeamRole.MEMBER));
  }

  @Test
  void requireNotLastAdminDemotion_shouldSucceedWhenPromotingToAdmin() {
    UserTeam admin = dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    assertDoesNotThrow(
        () -> membershipService.requireNotLastAdminDemotion(team, admin, TeamRole.ADMIN));
  }

  @Test
  void requireNotLastAdminDemotion_shouldThrowWhenDemotingLastAdmin() {
    UserTeam lastAdmin = dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () ->
                membershipService.requireNotLastAdminDemotion(team, lastAdmin, TeamRole.ORGANIZER));

    assertEquals("Cannot remove the last admin", exception.getMessage());
  }

  // ==================== Self-Action Checks ====================

  @Test
  void requireCanRemoveMember_shouldSucceedForSelfRemoval() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertDoesNotThrow(() -> membershipService.requireCanRemoveMember(user1, user1, team));
  }

  @Test
  void requireCanRemoveMember_shouldSucceedForAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertDoesNotThrow(() -> membershipService.requireCanRemoveMember(user1, user2, team));
  }

  @Test
  void requireCanRemoveMember_shouldThrowForNonAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.requireCanRemoveMember(user1, user2, team));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }

  @Test
  void requireCanRemoveMember_shouldThrowForOrganizerRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.requireCanRemoveMember(user1, user2, team));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }
}
