package com.tribly.service.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.dto.teams.response.TeamListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamServiceTest {

  @Inject TeamService teamService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
  }

  // ==================== Create Team ====================

  @Test
  void createTeam_shouldCreateTeamWithSlug() {
    TeamRequest request = new TeamRequest("Test Team", "A test team", Visibility.PUBLIC);

    TeamDetailDto result = teamService.createTeam(request, user1.getId());

    assertNotNull(result);
    assertEquals("Test Team", result.name());
    assertEquals("test-team", result.slug());
    assertEquals("A test team", result.description());
    assertEquals(Visibility.PUBLIC, result.visibility());
    assertEquals(TeamRole.ADMIN, result.role());
    assertEquals(1L, result.memberCount());
  }

  @Test
  void createTeam_shouldCreateAdminMembership() {
    TeamRequest request = new TeamRequest("My Team", null, Visibility.PUBLIC);

    TeamDetailDto result = teamService.createTeam(request, user1.getId());

    Optional<TeamRole> role = teamService.getUserRole(user1.getId(), result.slug());
    assertTrue(role.isPresent());
    assertEquals(TeamRole.ADMIN, role.get());
  }

  @Test
  void createTeam_shouldHandleSlugCollisionWithTimestamp() {
    TeamRequest request1 = new TeamRequest("Test Team", null, Visibility.PUBLIC);
    TeamRequest request2 = new TeamRequest("Test Team", null, Visibility.PUBLIC);

    TeamDetailDto team1 = teamService.createTeam(request1, user1.getId());
    TeamDetailDto team2 = teamService.createTeam(request2, user2.getId());

    assertEquals("test-team", team1.slug());
    assertNotEquals("test-team", team2.slug());
    assertTrue(team2.slug().startsWith("test-team-"));
  }

  @Test
  void createTeam_shouldThrowWhenUserNotFound() {
    TeamRequest request = new TeamRequest("Test Team", null, Visibility.PUBLIC);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> teamService.createTeam(request, 999999L));

    assertTrue(exception.getMessage().contains("User"));
  }

  // ==================== List Teams ====================

  @Test
  void listTeams_shouldReturnAllPublicTeamsForAnonymous() {
    dataService.createTeam("Public Team 1", "public-1", Visibility.PUBLIC);
    dataService.createTeam("Public Team 2", "public-2", Visibility.PUBLIC);
    dataService.createTeam("Private Team", "private", Visibility.TEAM);

    TeamListResponse result = teamService.listTeams(null, null, null, 0, 10);

    assertEquals(2, result.teams().size());
  }

  @Test
  void listTeams_shouldFilterByMemberTrue() {
    Team team1 = dataService.createTeam("Team 1", "team-1", Visibility.PUBLIC);
    dataService.createTeam("Team 2", "team-2", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team1, TeamRole.MEMBER);

    TeamListResponse result = teamService.listTeams(user1.getId(), true, null, 0, 10);

    assertEquals(1, result.teams().size());
    assertEquals("team-1", result.teams().getFirst().slug());
  }

  @Test
  void listTeams_shouldFilterByMemberFalse() {
    Team team1 = dataService.createTeam("Team 1", "team-1", Visibility.PUBLIC);
    dataService.createTeam("Team 2", "team-2", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team1, TeamRole.MEMBER);

    TeamListResponse result = teamService.listTeams(user1.getId(), false, null, 0, 10);

    assertEquals(1, result.teams().size());
    assertEquals("team-2", result.teams().getFirst().slug());
  }

  @Test
  void listTeams_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createTeam("Team " + i, "team-" + i, Visibility.PUBLIC);
    }

    TeamListResponse result = teamService.listTeams(null, null, null, 0, 2);

    assertEquals(2, result.teams().size());
    assertEquals(5, result.total());
  }

  // ==================== Get Team ====================

  @Test
  void getTeam_shouldReturnTeam() {
    dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);

    TeamDetailDto result = teamService.getTeam("test-team", null);

    assertEquals("Test Team", result.name());
    assertEquals("test-team", result.slug());
  }

  @Test
  void getTeam_shouldThrowWhenNotFound() {
    BusinessException exception =
        assertThrows(BusinessException.class, () -> teamService.getTeam("nonexistent", null));

    assertTrue(exception.getMessage().contains("Team"));
  }

  // ==================== Update Team ====================

  @Test
  void updateTeam_shouldUpdateAllFields() {
    Team team = dataService.createTeam("Original", "original", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    TeamRequest request = new TeamRequest("Updated Name", "Updated description", Visibility.TEAM);

    TeamDetailDto result = teamService.updateTeam("original", request, user1.getId());

    assertEquals("Updated Name", result.name());
    assertEquals("Updated description", result.description());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateTeam_shouldUpdatePartialFields() {
    Team team = dataService.createTeam("Original", "original", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    TeamRequest request = new TeamRequest("New Name", "original", Visibility.PUBLIC);

    TeamDetailDto result = teamService.updateTeam("original", request, user1.getId());

    assertEquals("New Name", result.name());
    assertEquals(Visibility.PUBLIC, result.visibility());
  }

  @Test
  void updateTeam_shouldPreserveNameWhenNull() {
    Team team = dataService.createTeam("Original Name", "original", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    TeamRequest request = new TeamRequest("New name", "Updated description", Visibility.PUBLIC);

    TeamDetailDto result = teamService.updateTeam("original", request, user1.getId());

    assertEquals("New name", result.name());
    assertEquals("Updated description", result.description());
  }

  @Test
  void updateTeam_shouldThrowForNonAdmin() {
    Team team = dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    TeamRequest request = new TeamRequest("New Name", null, null);

    assertThrows(
        BusinessException.class, () -> teamService.updateTeam("test-team", request, user1.getId()));
  }

  // ==================== Delete Team ====================

  @Test
  void deleteTeam_shouldSoftDeleteTeam() {
    Team team = dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    teamService.deleteTeam("test-team", user1.getId());

    assertThrows(BusinessException.class, () -> teamService.getTeam("test-team", null));
  }

  @Test
  void deleteTeam_shouldThrowForNonAdmin() {
    Team team = dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertThrows(BusinessException.class, () -> teamService.deleteTeam("test-team", user1.getId()));
  }

  // ==================== Get User Role ====================

  @Test
  void getUserRole_shouldReturnRoleForMember() {
    Team team = dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    Optional<TeamRole> result = teamService.getUserRole(user1.getId(), "test-team");

    assertTrue(result.isPresent());
    assertEquals(TeamRole.ORGANIZER, result.get());
  }

  @Test
  void getUserRole_shouldReturnEmptyForNonMember() {
    dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);

    Optional<TeamRole> result = teamService.getUserRole(user1.getId(), "test-team");

    assertTrue(result.isEmpty());
  }
}
