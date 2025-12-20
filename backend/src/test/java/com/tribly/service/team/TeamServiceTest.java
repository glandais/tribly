package com.tribly.service.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.team.request.CreateTeamRequest;
import com.tribly.service.team.request.UpdateTeamRequest;
import com.tribly.service.team.response.TeamAndRole;
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
    CreateTeamRequest request =
        new CreateTeamRequest("Test Team", "A test team", Visibility.PUBLIC, null);

    TeamAndRole result = teamService.createTeam(request, user1.getId());

    assertNotNull(result);
    assertEquals("Test Team", result.team().getName());
    assertEquals("test-team", result.team().getSlug());
    assertEquals("A test team", result.team().getDescription());
    assertEquals(Visibility.PUBLIC, result.team().getVisibility());
    assertEquals(TeamRole.ADMIN, result.teamRole());
    assertEquals(1L, result.memberCount());
  }

  @Test
  void createTeam_shouldCreateAdminMembership() {
    CreateTeamRequest request = new CreateTeamRequest("My Team", null, Visibility.PUBLIC, null);

    TeamAndRole result = teamService.createTeam(request, user1.getId());

    Optional<TeamRole> role = teamService.getUserRole(user1.getId(), result.team().getSlug());
    assertTrue(role.isPresent());
    assertEquals(TeamRole.ADMIN, role.get());
  }

  @Test
  void createTeam_shouldHandleSlugCollisionWithTimestamp() {
    CreateTeamRequest request1 = new CreateTeamRequest("Test Team", null, Visibility.PUBLIC, null);
    CreateTeamRequest request2 = new CreateTeamRequest("Test Team", null, Visibility.PUBLIC, null);

    TeamAndRole team1 = teamService.createTeam(request1, user1.getId());
    TeamAndRole team2 = teamService.createTeam(request2, user2.getId());

    assertEquals("test-team", team1.team().getSlug());
    assertNotEquals("test-team", team2.team().getSlug());
    assertTrue(team2.team().getSlug().startsWith("test-team-"));
  }

  @Test
  void createTeam_shouldSetMaxMembers() {
    CreateTeamRequest request = new CreateTeamRequest("Limited Team", null, Visibility.PUBLIC, 10);

    TeamAndRole result = teamService.createTeam(request, user1.getId());

    assertEquals(10, result.team().getMaxMembers());
  }

  @Test
  void createTeam_shouldThrowWhenUserNotFound() {
    CreateTeamRequest request = new CreateTeamRequest("Test Team", null, Visibility.PUBLIC, null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> teamService.createTeam(request, 999999L));

    assertTrue(exception.getMessage().contains("User"));
  }

  // ==================== List Teams ====================

  @Test
  void listTeams_shouldReturnAllPublicTeamsForAnonymous() {
    dataService.createTeamWithVisibility("Public Team 1", "public-1", Visibility.PUBLIC);
    dataService.createTeamWithVisibility("Public Team 2", "public-2", Visibility.PUBLIC);
    dataService.createTeamWithVisibility("Private Team", "private", Visibility.TEAM);

    TriblyPage<TeamAndRole> result = teamService.listTeams(null, null, null, 0, 10);

    assertEquals(2, result.items().size());
  }

  @Test
  void listTeams_shouldFilterByMemberTrue() {
    Team team1 = dataService.createTeamWithVisibility("Team 1", "team-1", Visibility.PUBLIC);
    dataService.createTeamWithVisibility("Team 2", "team-2", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team1, TeamRole.MEMBER);

    TriblyPage<TeamAndRole> result = teamService.listTeams(user1.getId(), true, null, 0, 10);

    assertEquals(1, result.items().size());
    assertEquals("team-1", result.items().get(0).team().getSlug());
  }

  @Test
  void listTeams_shouldFilterByMemberFalse() {
    Team team1 = dataService.createTeamWithVisibility("Team 1", "team-1", Visibility.PUBLIC);
    dataService.createTeamWithVisibility("Team 2", "team-2", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team1, TeamRole.MEMBER);

    TriblyPage<TeamAndRole> result = teamService.listTeams(user1.getId(), false, null, 0, 10);

    assertEquals(1, result.items().size());
    assertEquals("team-2", result.items().get(0).team().getSlug());
  }

  @Test
  void listTeams_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createTeamWithVisibility("Team " + i, "team-" + i, Visibility.PUBLIC);
    }

    TriblyPage<TeamAndRole> result = teamService.listTeams(null, null, null, 0, 2);

    assertEquals(2, result.items().size());
    assertEquals(5, result.total());
  }

  // ==================== Get Team ====================

  @Test
  void getTeam_shouldReturnTeam() {
    dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);

    TeamAndRole result = teamService.getTeam("test-team", null);

    assertEquals("Test Team", result.team().getName());
    assertEquals("test-team", result.team().getSlug());
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
    Team team = dataService.createTeamWithVisibility("Original", "original", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UpdateTeamRequest request =
        new UpdateTeamRequest(
            "Updated Name",
            "Updated description",
            Visibility.TEAM,
            "https://example.com/logo.png",
            "https://example.com/cover.png",
            50);

    TeamAndRole result = teamService.updateTeam("original", request, user1.getId());

    assertEquals("Updated Name", result.team().getName());
    assertEquals("Updated description", result.team().getDescription());
    assertEquals(Visibility.TEAM, result.team().getVisibility());
    assertEquals("https://example.com/logo.png", result.team().getLogoUrl());
    assertEquals("https://example.com/cover.png", result.team().getCoverImageUrl());
    assertEquals(50, result.team().getMaxMembers());
  }

  @Test
  void updateTeam_shouldUpdatePartialFields() {
    Team team = dataService.createTeamWithVisibility("Original", "original", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UpdateTeamRequest request = new UpdateTeamRequest("New Name", null, null, null, null, null);

    TeamAndRole result = teamService.updateTeam("original", request, user1.getId());

    assertEquals("New Name", result.team().getName());
    assertEquals(Visibility.PUBLIC, result.team().getVisibility());
  }

  @Test
  void updateTeam_shouldThrowForNonAdmin() {
    Team team = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    UpdateTeamRequest request = new UpdateTeamRequest("New Name", null, null, null, null, null);

    assertThrows(
        BusinessException.class, () -> teamService.updateTeam("test-team", request, user1.getId()));
  }

  // ==================== Delete Team ====================

  @Test
  void deleteTeam_shouldSoftDeleteTeam() {
    Team team = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    teamService.deleteTeam("test-team", user1.getId());

    assertThrows(BusinessException.class, () -> teamService.getTeam("test-team", null));
  }

  @Test
  void deleteTeam_shouldThrowForNonAdmin() {
    Team team = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertThrows(BusinessException.class, () -> teamService.deleteTeam("test-team", user1.getId()));
  }

  // ==================== Get User Role ====================

  @Test
  void getUserRole_shouldReturnRoleForMember() {
    Team team = dataService.createTeam("Test Team", "test-team");
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);

    Optional<TeamRole> result = teamService.getUserRole(user1.getId(), "test-team");

    assertTrue(result.isPresent());
    assertEquals(TeamRole.ORGANIZER, result.get());
  }

  @Test
  void getUserRole_shouldReturnEmptyForNonMember() {
    dataService.createTeam("Test Team", "test-team");

    Optional<TeamRole> result = teamService.getUserRole(user1.getId(), "test-team");

    assertTrue(result.isEmpty());
  }
}
