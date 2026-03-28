package com.tribly.service.page;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamPage;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamPageAccessCheckerTest extends AbstractBaseTest {

  @Inject TeamPageAccessChecker teamPageAccessChecker;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext queryContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private Team privateTeam;
  private User admin;
  private User organizer;
  private User member;
  private User nonMember;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    privateTeam = dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Test
  void getType_shouldReturnTeamPage() {
    assertEquals(EntityType.TEAM_PAGE, teamPageAccessChecker.getType());
  }

  @Nested
  class ListAction {

    @Test
    void shouldAllowForAdmin() {
      queryContext.setUserForTest(admin);
      boolean result = teamPageAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForOrganizer() {
      queryContext.setUserForTest(organizer);
      boolean result = teamPageAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForMember() {
      queryContext.setUserForTest(member);
      boolean result = teamPageAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      queryContext.setUserForTest(null);
      boolean result = teamPageAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonMember() {
      queryContext.setUserForTest(nonMember);
      boolean result = teamPageAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class CreateAction {

    @Test
    void shouldAllowForAdmin() {
      queryContext.setUserForTest(admin);
      boolean result = teamPageAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForOrganizer() {
      queryContext.setUserForTest(organizer);
      boolean result = teamPageAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForMember() {
      queryContext.setUserForTest(member);
      boolean result = teamPageAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      queryContext.setUserForTest(null);
      boolean result = teamPageAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class ReadAction {

    @Test
    void shouldDenyForMissingSlug() {
      queryContext.setUserForTest(admin);
      List<Object> params = new ArrayList<>();
      params.add(team.getSlug());
      params.add(null);
      boolean result = teamPageAccessChecker.hasRights(ActionType.READ, params);
      assertFalse(result);
    }

    @Test
    void shouldAllowForMemberWithPublicPage() {
      TeamPage page =
          dataService.createAdditionalPage(team, admin, "Public Page", 1, Visibility.PUBLIC);

      queryContext.setUserForTest(member);
      boolean result =
          teamPageAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), page.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForMemberWithTeamPage() {
      TeamPage page =
          dataService.createAdditionalPage(team, admin, "Team Page", 1, Visibility.TEAM);

      queryContext.setUserForTest(member);
      boolean result =
          teamPageAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), page.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(admin);
      boolean result =
          teamPageAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), page.getSlug()));
      assertTrue(result);
    }
  }

  @Nested
  class UpdateAction {

    @Test
    void shouldDenyForMissingSlug() {
      queryContext.setUserForTest(admin);
      List<Object> params = new ArrayList<>();
      params.add(team.getSlug());
      params.add(null);
      boolean result = teamPageAccessChecker.hasRights(ActionType.UPDATE, params);
      assertFalse(result);
    }

    @Test
    void shouldAllowForAdmin() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(admin);
      boolean result =
          teamPageAccessChecker.hasRights(
              ActionType.UPDATE, List.of(team.getSlug(), page.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForOrganizer() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(organizer);
      boolean result =
          teamPageAccessChecker.hasRights(
              ActionType.UPDATE, List.of(team.getSlug(), page.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForMember() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(member);
      boolean result =
          teamPageAccessChecker.hasRights(
              ActionType.UPDATE, List.of(team.getSlug(), page.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(null);
      boolean result =
          teamPageAccessChecker.hasRights(
              ActionType.UPDATE, List.of(team.getSlug(), page.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class DeleteAction {

    @Test
    void shouldDenyForMissingSlug() {
      queryContext.setUserForTest(admin);
      List<Object> params = new ArrayList<>();
      params.add(team.getSlug());
      params.add(null);
      boolean result = teamPageAccessChecker.hasRights(ActionType.DELETE, params);
      assertFalse(result);
    }

    @Test
    void shouldAllowForAdmin() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(admin);
      boolean result =
          teamPageAccessChecker.hasRights(
              ActionType.DELETE, List.of(team.getSlug(), page.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForOrganizer() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(organizer);
      boolean result =
          teamPageAccessChecker.hasRights(
              ActionType.DELETE, List.of(team.getSlug(), page.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForMember() {
      TeamPage page = dataService.createAdditionalPage(team, admin, "Test Page", 1);

      queryContext.setUserForTest(member);
      boolean result =
          teamPageAccessChecker.hasRights(
              ActionType.DELETE, List.of(team.getSlug(), page.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class UnsupportedActions {

    @Test
    void shouldDenyListAllTeams() {
      queryContext.setUserForTest(admin);
      boolean result =
          teamPageAccessChecker.hasRights(ActionType.LIST_ALL_TEAMS, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyJoin() {
      queryContext.setUserForTest(admin);
      boolean result = teamPageAccessChecker.hasRights(ActionType.JOIN, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyLeave() {
      queryContext.setUserForTest(admin);
      boolean result = teamPageAccessChecker.hasRights(ActionType.LEAVE, List.of(team.getSlug()));
      assertFalse(result);
    }
  }
}
