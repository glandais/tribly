package com.tribly.domain.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.repository.TeamQuery;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.team.response.TeamAndRole;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamRepositoryTest {

  @Inject TeamRepository teamRepository;
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

  @Test
  void findBySlug_shouldReturnTeamWhenExists() {
    dataService.createTeam("Test Team", "test-team");

    Optional<Team> result = teamRepository.findBySlug("test-team");

    assertTrue(result.isPresent());
    assertEquals("Test Team", result.get().getName());
    assertEquals("test-team", result.get().getSlug());
  }

  @Test
  void findBySlug_shouldReturnEmptyWhenNotExists() {
    Optional<Team> result = teamRepository.findBySlug("nonexistent");

    assertTrue(result.isEmpty());
  }

  @Test
  void findBySlug_shouldIgnoreDeletedTeams() {
    Team team = dataService.createTeam("Deleted Team", "deleted-team");
    dataService.deleteTeam(team);

    Optional<Team> result = teamRepository.findBySlug("deleted-team");

    assertTrue(result.isEmpty());
  }

  @Test
  void existsBySlug_shouldReturnTrueWhenExists() {
    dataService.createTeam("Test Team", "test-team");

    boolean exists = teamRepository.existsBySlug("test-team");

    assertTrue(exists);
  }

  @Test
  void existsBySlug_shouldReturnFalseWhenNotExists() {
    boolean exists = teamRepository.existsBySlug("nonexistent");

    assertFalse(exists);
  }

  @Test
  void existsBySlug_shouldIgnoreDeletedTeams() {
    Team team = dataService.createTeam("Deleted Team", "deleted-team");
    dataService.deleteTeam(team);

    boolean exists = teamRepository.existsBySlug("deleted-team");

    assertFalse(exists);
  }

  @Test
  void find_shouldReturnPublicTeamsForAnonymous() {
    dataService.createTeamWithVisibility("Public Team", "public-team", Visibility.PUBLIC);
    dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);

    TeamQuery query = new TeamQuery(0, 10, null, null, null, null);
    TriblyPage<TeamAndRole> result = teamRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("public-team", result.items().getFirst().team().getSlug());
  }

  @Test
  void find_shouldFilterBySlug() {
    dataService.createTeamWithVisibility("Team 1", "team-1", Visibility.PUBLIC);
    dataService.createTeamWithVisibility("Team 2", "team-2", Visibility.PUBLIC);

    TeamQuery query = new TeamQuery(0, 10, "team-1", null, null, null);
    TriblyPage<TeamAndRole> result = teamRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("team-1", result.items().getFirst().team().getSlug());
  }

  @Test
  void find_shouldFilterBySearch() {
    Team team1 =
        dataService.createTeamWithVisibility("Cycling Club", "cycling-club", Visibility.PUBLIC);
    team1.setDescription("A great cycling team");
    dataService.updateTeam(team1);

    dataService.createTeamWithVisibility("Running Club", "running-club", Visibility.PUBLIC);

    TeamQuery query = new TeamQuery(0, 10, null, null, null, "%cycling%");
    TriblyPage<TeamAndRole> result = teamRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("cycling-club", result.items().getFirst().team().getSlug());
  }

  @Test
  void find_shouldShowUserTeamsWhenMemberFilterTrue() {
    Team team1 = dataService.createTeamWithVisibility("My Team", "my-team", Visibility.TEAM);
    Team team2 =
        dataService.createTeamWithVisibility("Other Team", "other-team", Visibility.PUBLIC);

    dataService.addUserToTeam(user1, team1, TeamRole.MEMBER);

    TeamQuery query = new TeamQuery(0, 10, null, user1.getId(), true, null);
    TriblyPage<TeamAndRole> result = teamRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("my-team", result.items().getFirst().team().getSlug());
    assertEquals(TeamRole.MEMBER, result.items().getFirst().teamRole());
  }

  @Test
  void find_shouldShowPublicNonMemberTeamsWhenMemberFilterFalse() {
    Team publicTeam =
        dataService.createTeamWithVisibility("Public Team", "public-team", Visibility.PUBLIC);
    Team memberTeam =
        dataService.createTeamWithVisibility("Member Team", "member-team", Visibility.PUBLIC);

    dataService.addUserToTeam(user1, memberTeam, TeamRole.MEMBER);

    TeamQuery query = new TeamQuery(0, 10, null, user1.getId(), false, null);
    TriblyPage<TeamAndRole> result = teamRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("public-team", result.items().getFirst().team().getSlug());
  }

  @Test
  void find_shouldShowPublicAndMemberTeamsWhenMemberFilterNull() {
    Team publicTeam =
        dataService.createTeamWithVisibility("Public Team", "public-team", Visibility.PUBLIC);
    Team privateTeam =
        dataService.createTeamWithVisibility("Private Team", "private-team", Visibility.TEAM);

    dataService.addUserToTeam(user1, privateTeam, TeamRole.MEMBER);

    TeamQuery query = new TeamQuery(0, 10, null, user1.getId(), null, null);
    TriblyPage<TeamAndRole> result = teamRepository.find(query);

    assertEquals(2, result.items().size());
  }

  @Test
  void find_shouldIgnoreDeletedTeams() {
    dataService.createTeamWithVisibility("Visible Team", "visible", Visibility.PUBLIC);
    Team deletedTeam =
        dataService.createTeamWithVisibility("Deleted Team", "deleted", Visibility.PUBLIC);
    dataService.deleteTeam(deletedTeam);

    TeamQuery query = new TeamQuery(0, 10, null, null, null, null);
    TriblyPage<TeamAndRole> result = teamRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("visible", result.items().getFirst().team().getSlug());
  }

  @Test
  void findOne_shouldReturnTeamAndRoleWhenExists() {
    Team team = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);

    Optional<TeamAndRole> result = teamRepository.findOne("test-team", user1.getId());

    assertTrue(result.isPresent());
    assertEquals("test-team", result.get().team().getSlug());
    assertEquals(TeamRole.ADMIN, result.get().teamRole());
  }

  @Test
  void findOne_shouldReturnEmptyWhenNotExists() {
    Optional<TeamAndRole> result = teamRepository.findOne("nonexistent", user1.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findOne_shouldReturnTeamWithNullRoleWhenNotMember() {
    dataService.createTeamWithVisibility("Public Team", "public-team", Visibility.PUBLIC);

    Optional<TeamAndRole> result = teamRepository.findOne("public-team", user1.getId());

    assertTrue(result.isPresent());
    assertEquals("public-team", result.get().team().getSlug());
    assertNull(result.get().teamRole());
  }
}
