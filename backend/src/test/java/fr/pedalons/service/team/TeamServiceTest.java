package fr.pedalons.service.team;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.teams.request.TeamRequest;
import fr.pedalons.dto.teams.response.TeamDetailDto;
import fr.pedalons.dto.teams.response.TeamListResponse;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.common.SlugService;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.team.request.MinRole;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamServiceTest extends AbstractBaseTest {

  @Inject TeamService teamService;
  @Inject SlugService slugService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext queryContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
  }

  // ==================== Create Team ====================

  @Test
  void createTeam_shouldCreateTeamWithSlug() {
    TeamRequest request =
        new TeamRequest(
            "Test Team",
            MediaDto.builder().markdown("A test team").build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.createTeam(request);

    assertNotNull(result);
    assertEquals("Test Team", result.name());
    assertEquals("test-team", result.slug());
    assertEquals("A test team", result.about().markdown());
    assertEquals(Visibility.TEAM, result.visibility());
    assertEquals(TeamRole.ADMIN, result.role());
    assertEquals(1L, result.memberCount());
  }

  @Test
  void createTeam_shouldCreateAdminMembership() {
    TeamRequest request =
        new TeamRequest(
            "My Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.createTeam(request);

    assertEquals(TeamRole.ADMIN, result.role());
  }

  @Test
  void createTeam_shouldHandleSlugCollisionWithTimestamp() {
    TeamRequest request1 =
        new TeamRequest(
            "Test Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);
    TeamRequest request2 =
        new TeamRequest(
            "Test Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto team1 = teamService.createTeam(request1);
    queryContext.setUserForTest(user2);
    TeamDetailDto team2 = teamService.createTeam(request2);

    assertEquals("test-team", team1.slug());
    assertNotEquals("test-team", team2.slug());
    assertTrue(team2.slug().startsWith("test-team-"));
  }

  @Test
  void createTeam_shouldRejectNonTeamVisibility() {
    TeamRequest request =
        new TeamRequest(
            "My Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    BusinessException ex =
        assertThrows(BusinessException.class, () -> teamService.createTeam(request));
    assertEquals("INVALID_VISIBILITY", ex.getMessage());
  }

  @Test
  void createTeam_shouldEnforceOneTeamLimit() {
    TeamRequest request =
        new TeamRequest(
            "My Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    teamService.createTeam(request);

    TeamRequest request2 =
        new TeamRequest(
            "Second Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);
    BusinessException ex =
        assertThrows(BusinessException.class, () -> teamService.createTeam(request2));
    assertEquals("USER_TEAM_LIMIT_REACHED", ex.getMessage());
  }

  // ==================== List Teams ====================

  @Test
  void listTeams_shouldReturnAllPublicTeamsForAnonymous() {
    dataService.createTeam(user1, "Public Team 1", "public-1", Visibility.PUBLIC);
    dataService.createTeam(user1, "Public Team 2", "public-2", Visibility.PUBLIC);
    dataService.createTeam(user1, "Private Team", "private", Visibility.TEAM);

    queryContext.setUserForTest(null);
    TeamListResponse result = teamService.listTeams(MinRole.NOT_MEMBER, null, 0, 10);

    assertEquals(2, result.teams().size());
  }

  @Test
  void listTeams_shouldFilterByMinRoleMember() {
    Team team1 = dataService.createTeam(user2, "Team 1", "team-1", Visibility.PUBLIC);
    dataService.createTeam(user2, "Team 2", "team-2", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team1, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    TeamListResponse result = teamService.listTeams(MinRole.MEMBER, null, 0, 10);

    assertEquals(1, result.teams().size());
    assertEquals("team-1", result.teams().getFirst().slug());
  }

  @Test
  void listTeams_shouldFilterByMinRoleAdmin() {
    Team team1 = dataService.createTeam(user2, "Team 1", "team-1", Visibility.PUBLIC);
    Team team2 = dataService.createTeam(user1, "Team 2", "team-2", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team1, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    TeamListResponse result = teamService.listTeams(MinRole.ADMIN, null, 0, 10);

    assertEquals(1, result.teams().size());
    assertEquals("team-2", result.teams().getFirst().slug());
  }

  @Test
  void listTeams_shouldSupportPagination() {
    for (int i = 1; i <= 5; i++) {
      dataService.createTeam(user1, "Team " + i, "team-" + i, Visibility.PUBLIC);
    }

    queryContext.setUserForTest(null);
    TeamListResponse result = teamService.listTeams(MinRole.NOT_MEMBER, null, 0, 2);

    assertEquals(2, result.teams().size());
    assertEquals(5, result.total());
  }

  // ==================== Get Team ====================

  @Test
  void getTeam_shouldReturnTeam() {
    Team team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);

    queryContext.setUserForTest(null);
    TeamDetailDto result = teamService.getTeamDetailDto(team.getSlug());

    assertEquals("Test Team", result.name());
    assertEquals("test-team", result.slug());
  }

  // ==================== Update Team ====================

  @Test
  void updateTeam_shouldUpdateAllFields() {
    Team team = dataService.createTeam(user1, "Original", "original", Visibility.PUBLIC);
    dataService.setTeamVisibilityEditable(team, true);
    TeamRequest request =
        new TeamRequest(
            "Updated Name",
            MediaDto.builder().markdown("Updated description").build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.updateTeam(team.getSlug(), request);

    assertEquals("Updated Name", result.name());
    assertEquals("Updated description", result.about().markdown());
    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateTeam_withLockedVisibility_shouldThrowWhenChangingVisibility() {
    Team team = dataService.createTeam(user1, "Locked Team", "locked", Visibility.PUBLIC);
    // visibilityEditable is false by default
    TeamRequest request =
        new TeamRequest(
            "Locked Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    BusinessException ex =
        assertThrows(
            BusinessException.class, () -> teamService.updateTeam(team.getSlug(), request));
    assertEquals("INVALID_VISIBILITY", ex.getMessage());
  }

  @Test
  void updateTeam_withLockedVisibility_shouldSucceedWhenVisibilityUnchanged() {
    Team team = dataService.createTeam(user1, "Locked Team", "locked", Visibility.PUBLIC);
    TeamRequest request =
        new TeamRequest(
            "New Name",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.updateTeam(team.getSlug(), request);

    assertEquals("New Name", result.name());
    assertEquals(Visibility.PUBLIC, result.visibility());
  }

  @Test
  void updateTeam_withEditableVisibility_shouldAllowChange() {
    Team team = dataService.createTeam(user1, "Editable Team", "editable", Visibility.PUBLIC);
    dataService.setTeamVisibilityEditable(team, true);
    TeamRequest request =
        new TeamRequest(
            "Editable Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.updateTeam(team.getSlug(), request);

    assertEquals(Visibility.TEAM, result.visibility());
  }

  @Test
  void updateTeam_shouldUpdatePartialFields() {
    Team team = dataService.createTeam(user1, "Original", "original", Visibility.PUBLIC);
    TeamRequest request =
        new TeamRequest(
            "New Name",
            MediaDto.builder().markdown("original").build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.updateTeam(team.getSlug(), request);

    assertEquals("New Name", result.name());
    assertEquals(Visibility.PUBLIC, result.visibility());
  }

  @Test
  void updateTeam_shouldPreserveNameWhenNull() {
    Team team = dataService.createTeam(user1, "Original Name", "original", Visibility.PUBLIC);
    TeamRequest request =
        new TeamRequest(
            "New name",
            MediaDto.builder().markdown("Updated description").build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.updateTeam(team.getSlug(), request);

    assertEquals("New name", result.name());
    assertEquals("Updated description", result.about().markdown());
  }

  @Test
  void updateTeam_shouldThrowForNonAdmin() {
    Team team = dataService.createTeam(user2, "Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    TeamRequest request =
        new TeamRequest(
            "New Name",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);

    queryContext.setUserForTest(user1);
    assertThrows(PedalonsException.class, () -> teamService.updateTeam(team.getSlug(), request));
  }

  // ==================== Delete Team ====================

  @Test
  void deleteTeam_shouldSoftDeleteTeam() {
    Team team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    teamService.deleteTeam(team.getSlug());

    queryContext.setUserForTest(null);
    assertThrows(PedalonsException.class, () -> teamService.getTeamDetailDto(team.getSlug()));
  }

  @Test
  void deleteTeam_shouldThrowForNonAdmin() {
    Team team = dataService.createTeam(user2, "Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    assertThrows(PedalonsException.class, () -> teamService.deleteTeam(team.getSlug()));
  }

  // ==================== Update Slug ====================

  @Test
  void updateSlug_shouldUpdateSlugForAdmin() {
    Team team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.updateSlug(team.getSlug(), "new-slug");

    assertEquals("new-slug", result.slug());
  }

  @Test
  void updateSlug_shouldReturnSameDtoWhenSlugUnchanged() {
    Team team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    TeamDetailDto result = teamService.updateSlug(team.getSlug(), "test-team");

    assertEquals("test-team", result.slug());
  }

  @Test
  void updateSlug_shouldThrowForInvalidSlugFormat() {
    Team team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    assertThrows(
        BusinessException.class, () -> teamService.updateSlug(team.getSlug(), "Invalid Slug!"));
  }

  @Test
  void updateSlug_shouldThrowWhenNewSlugAlreadyTaken() {
    Team team1 = dataService.createTeam(user1, "Team 1", "team-1", Visibility.PUBLIC);
    dataService.createTeam(user2, "Team 2", "team-2", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    assertThrows(ConflictException.class, () -> teamService.updateSlug(team1.getSlug(), "team-2"));
  }

  @Test
  void updateSlug_shouldCreateRedirectFromOldSlug() {
    Team team = dataService.createTeam(user1, "Test Team", "original-slug", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    teamService.updateSlug(team.getSlug(), "new-slug");

    // Old slug should redirect to team
    assertTrue(slugService.resolveTeamRedirect(domain.getId(), "original-slug").isPresent());
  }

  @Test
  void updateSlug_shouldAllowReusingOldSlugAfterChange() {
    Team team = dataService.createTeam(user1, "Test Team", "original-slug", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    teamService.updateSlug("original-slug", "intermediate-slug");
    TeamDetailDto result = teamService.updateSlug("intermediate-slug", "original-slug");

    assertEquals("original-slug", result.slug());
  }

  @Test
  void updateSlug_shouldThrowForNonAdmin() {
    Team team = dataService.createTeam(user2, "Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    assertThrows(
        ForbiddenException.class, () -> teamService.updateSlug(team.getSlug(), "new-slug"));
  }

  @Test
  void updateSlug_shouldThrowForAnonymous() {
    Team team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);

    queryContext.setUserForTest(null);
    assertThrows(
        ForbiddenException.class, () -> teamService.updateSlug(team.getSlug(), "new-slug"));
  }

  @Test
  void updateSlug_shouldThrowForNonMember() {
    Team team = dataService.createTeam(user2, "Test Team", "test-team", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    assertThrows(
        ForbiddenException.class, () -> teamService.updateSlug(team.getSlug(), "new-slug"));
  }

  @Test
  void updateSlug_shouldResolveOldSlugToTeam() {
    Team team = dataService.createTeam(user1, "Test Team", "old-slug", Visibility.PUBLIC);

    queryContext.setUserForTest(user1);
    teamService.updateSlug("old-slug", "new-slug");

    // Should still be able to get team by old slug (redirect)
    TeamDetailDto result = teamService.getTeamDetailDto("old-slug");
    assertEquals("new-slug", result.slug());
  }
}
