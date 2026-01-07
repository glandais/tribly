package com.tribly.service.security;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.TriblyException;
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

    boolean result = teamSecurityService.isMember(user1, team);

    assertTrue(result);
  }

  @Test
  void isMember_shouldReturnFalseForNonMember() {
    boolean result = teamSecurityService.isMember(user1, team);

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

    boolean result = teamSecurityService.canSeeDrafts(user1, team);

    assertTrue(result);
  }

  @Test
  void canSeeDrafts_shouldReturnTrueForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    boolean result = teamSecurityService.canSeeDrafts(user1, team);

    assertTrue(result);
  }

  @Test
  void canSeeDrafts_shouldReturnFalseForMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    boolean result = teamSecurityService.canSeeDrafts(user1, team);

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

    UserTeam result = teamSecurityService.requireMembership(user1, team);

    assertEquals(membership.getId(), result.getId());
    assertEquals(TeamRole.MEMBER, result.getRole());
  }

  @Test
  void requireMembership_shouldThrowForNonMember() {
    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> teamSecurityService.requireMembership(user1, team));

    assertEquals("You are not a member of this team", exception.getMessage());
  }

  @Test
  void requireMembership_shouldThrowForNullUserId() {
    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> teamSecurityService.requireMembership(null, team));

    assertEquals("You are not a member of this team", exception.getMessage());
  }

  // ==================== Role-Based Checks ====================

  @Test
  void requireAdmin_shouldSucceedForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    assertDoesNotThrow(() -> teamSecurityService.requireAdmin(user1, team));
  }

  @Test
  void requireAdmin_shouldThrowForOrganizer() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    TriblyException exception =
        assertThrows(TriblyException.class, () -> teamSecurityService.requireAdmin(user1, team));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }

  @Test
  void requireAdmin_shouldThrowForMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(TriblyException.class, () -> teamSecurityService.requireAdmin(user1, team));

    assertEquals("Only admins can perform this action", exception.getMessage());
  }

  @Test
  void requireOrganizer_shouldSucceedForOrganizer() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    assertDoesNotThrow(() -> teamSecurityService.requireOrganizer(user1, team));
  }

  @Test
  void requireOrganizer_shouldSucceedForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    assertDoesNotThrow(() -> teamSecurityService.requireOrganizer(user1, team));
  }

  @Test
  void requireOrganizer_shouldThrowForMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> teamSecurityService.requireOrganizer(user1, team));

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

    TriblyException exception =
        assertThrows(
            TriblyException.class, () -> teamSecurityService.requirePublicTeamForJoin(team));

    assertEquals("This team is private. You need an invitation to join.", exception.getMessage());
  }
}
