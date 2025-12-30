package com.tribly.service.security;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamSecurityServiceTest {

  @Inject TeamSecurityService teamSecurityService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user1 = dataService.createUser("user1@example.com", "User One");
    team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);
    user2 = dataService.createUser("user2@example.com", "User Two");
    user3 = dataService.createUser("user3@example.com", "User Three");
  }

  // ==================== Membership Checks ====================

  @Test
  void isMember_shouldReturnTrueForMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    boolean result = teamSecurityService.isMember(user1.getId(), team);

    assertTrue(result);
  }

  @Test
  void isMember_shouldReturnFalseForNonMember() {
    boolean result = teamSecurityService.isMember(user1.getId(), team);

    assertFalse(result);
  }

  @Test
  void isMember_shouldReturnFalseForNullUserId() {
    boolean result = teamSecurityService.isMember(null, team);

    assertFalse(result);
  }

  @Test
  void canSeeDrafts_shouldReturnTrueForOrganizer() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    boolean result = teamSecurityService.canSeeDrafts(user1.getId(), team);

    assertTrue(result);
  }

  @Test
  void canSeeDrafts_shouldReturnTrueForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    boolean result = teamSecurityService.canSeeDrafts(user1.getId(), team);

    assertTrue(result);
  }

  @Test
  void canSeeDrafts_shouldReturnFalseForMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    boolean result = teamSecurityService.canSeeDrafts(user1.getId(), team);

    assertFalse(result);
  }

  @Test
  void canSeeDrafts_shouldReturnFalseForNullUserId() {
    boolean result = teamSecurityService.canSeeDrafts(null, team);

    assertFalse(result);
  }

  @Test
  void requireMembership_shouldReturnMembershipForMember() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    UserTeam result = teamSecurityService.requireMembership(user1.getId(), "test-team");

    assertEquals(membership.getId(), result.getId());
    assertEquals(TeamRole.MEMBER, result.getRole());
  }

  @Test
  void requireMembership_shouldThrowForNonMember() {
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> teamSecurityService.requireMembership(user1.getId(), "test-team"));

    assertEquals("You are not a member of this team", exception.getMessage());
  }

  @Test
  void requireMembership_shouldThrowForNullUserId() {
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> teamSecurityService.requireMembership(null, "test-team"));

    assertEquals("You are not a member of this team", exception.getMessage());
  }

  // ==================== Role-Based Checks ====================

  @Test
  void requireAdmin_shouldSucceedForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    assertDoesNotThrow(() -> teamSecurityService.requireAdmin(user1.getId(), "test-team"));
  }

  @Test
  void requireAdmin_shouldThrowForOrganizer() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> teamSecurityService.requireAdmin(user1.getId(), "test-team"));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }

  @Test
  void requireAdmin_shouldThrowForMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> teamSecurityService.requireAdmin(user1.getId(), "test-team"));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }

  @Test
  void requireOrganizer_shouldSucceedForOrganizer() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    assertDoesNotThrow(() -> teamSecurityService.requireOrganizer(user1.getId(), "test-team"));
  }

  @Test
  void requireOrganizer_shouldSucceedForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    assertDoesNotThrow(() -> teamSecurityService.requireOrganizer(user1.getId(), "test-team"));
  }

  @Test
  void requireOrganizer_shouldThrowForMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> teamSecurityService.requireOrganizer(user1.getId(), "test-team"));

    assertEquals("Not organizer", exception.getMessage());
  }

  // ==================== Team Visibility Checks ====================

  @Test
  void requirePublicTeamForJoin_shouldSucceedForPublicTeam() {
    team.setVisibility(Visibility.PUBLIC);
    dataService.updateTeam(team);

    assertDoesNotThrow(() -> teamSecurityService.requirePublicTeamForJoin(team));
  }

  @Test
  void requirePublicTeamForJoin_shouldThrowForTeamVisibility() {
    team.setVisibility(Visibility.TEAM);
    dataService.updateTeam(team);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> teamSecurityService.requirePublicTeamForJoin(team));

    assertEquals("This team is private. You need an invitation to join.", exception.getMessage());
  }

  // ==================== Business Rule Checks ====================

  @Test
  void requireNotLastAdmin_shouldSucceedWhenMultipleAdmins() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UserTeam admin2 = dataService.addUserToTeam(user2, team, TeamRole.ADMIN);

    assertDoesNotThrow(() -> teamSecurityService.requireNotLastAdmin("test-team", admin2));
  }

  @Test
  void requireNotLastAdmin_shouldSucceedForNonAdminRole() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UserTeam member = dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertDoesNotThrow(() -> teamSecurityService.requireNotLastAdmin("test-team", member));
  }

  @Test
  void requireNotLastAdmin_shouldThrowWhenLastAdmin() {
    UserTeam lastAdmin = dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> teamSecurityService.requireNotLastAdmin("test-team", lastAdmin));

    assertEquals("Cannot remove the last admin", exception.getMessage());
  }

  @Test
  void requireNotLastAdminDemotion_shouldSucceedWhenMultipleAdmins() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UserTeam admin2 = dataService.addUserToTeam(user2, team, TeamRole.ADMIN);

    assertDoesNotThrow(
        () ->
            teamSecurityService.requireNotLastAdminDemotion(
                "test-team", admin2, TeamRole.ORGANIZER));
  }

  @Test
  void requireNotLastAdminDemotion_shouldSucceedWhenNotDemotingFromAdmin() {
    UserTeam organizer = dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    assertDoesNotThrow(
        () ->
            teamSecurityService.requireNotLastAdminDemotion(
                "test-team", organizer, TeamRole.MEMBER));
  }

  @Test
  void requireNotLastAdminDemotion_shouldSucceedWhenPromotingToAdmin() {
    UserTeam admin = dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    assertDoesNotThrow(
        () -> teamSecurityService.requireNotLastAdminDemotion("test-team", admin, TeamRole.ADMIN));
  }

  @Test
  void requireNotLastAdminDemotion_shouldThrowWhenDemotingLastAdmin() {
    UserTeam lastAdmin = dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                teamSecurityService.requireNotLastAdminDemotion(
                    "test-team", lastAdmin, TeamRole.ORGANIZER));

    assertEquals("Cannot remove the last admin", exception.getMessage());
  }

  // ==================== Self-Action Checks ====================

  @Test
  void requireCanRemoveMember_shouldSucceedForSelfRemoval() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertDoesNotThrow(
        () ->
            teamSecurityService.requireCanRemoveMember(user1.getId(), user1.getId(), "test-team"));
  }

  @Test
  void requireCanRemoveMember_shouldSucceedForAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertDoesNotThrow(
        () ->
            teamSecurityService.requireCanRemoveMember(user1.getId(), user2.getId(), "test-team"));
  }

  @Test
  void requireCanRemoveMember_shouldThrowForNonAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                teamSecurityService.requireCanRemoveMember(
                    user1.getId(), user2.getId(), "test-team"));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }

  @Test
  void requireCanRemoveMember_shouldThrowForOrganizerRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                teamSecurityService.requireCanRemoveMember(
                    user1.getId(), user2.getId(), "test-team"));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }
}
