package fr.pedalons.service.ad;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdAccessCheckerTest extends AbstractBaseTest {

  @Inject AdAccessChecker adAccessChecker;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext userService;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
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
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Test
  void getType_shouldReturnAd() {
    assertEquals(EntityType.AD, adAccessChecker.getType());
  }

  @Nested
  class ListAction {

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(member);
      boolean result = adAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      userService.setUserForTest(admin);
      boolean result = adAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      userService.setUserForTest(null);
      boolean result = adAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonMember() {
      userService.setUserForTest(nonMember);
      boolean result = adAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyWhenAdsDisabled() {
      team.setEnableAds(false);
      dataService.updateTeam(team);

      userService.setUserForTest(member);
      boolean result = adAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class CreateAction {

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(member);
      boolean result = adAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForOrganizer() {
      userService.setUserForTest(organizer);
      boolean result = adAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      userService.setUserForTest(null);
      boolean result = adAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyWhenAdsDisabled() {
      team.setEnableAds(false);
      dataService.updateTeam(team);

      userService.setUserForTest(member);
      boolean result = adAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class ReadAction {

    @Test
    void shouldAllowForMember() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(member);
      boolean result =
          adAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), ad.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(null);
      boolean result =
          adAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), ad.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonMember() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(nonMember);
      boolean result =
          adAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), ad.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class UpdateAction {

    @Test
    void shouldAllowForCreator() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);

      userService.setUserForTest(member);
      boolean result =
          adAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), ad.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);

      userService.setUserForTest(admin);
      boolean result =
          adAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), ad.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForNonCreatorMember() {
      Ad ad = dataService.createAd(team, admin, "Admin Ad", AdType.SALE);

      userService.setUserForTest(member);
      boolean result =
          adAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), ad.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForOrganizer() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);

      userService.setUserForTest(organizer);
      boolean result =
          adAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), ad.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(null);
      boolean result =
          adAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), ad.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class DeleteAction {

    @Test
    void shouldAllowForCreator() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);

      userService.setUserForTest(member);
      boolean result =
          adAccessChecker.hasRights(ActionType.DELETE, List.of(team.getSlug(), ad.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);

      userService.setUserForTest(admin);
      boolean result =
          adAccessChecker.hasRights(ActionType.DELETE, List.of(team.getSlug(), ad.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForNonCreatorMember() {
      Ad ad = dataService.createAd(team, admin, "Admin Ad", AdType.SALE);

      userService.setUserForTest(member);
      boolean result =
          adAccessChecker.hasRights(ActionType.DELETE, List.of(team.getSlug(), ad.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class UnsupportedActions {

    @Test
    void shouldDenyListAllTeams() {
      userService.setUserForTest(admin);
      boolean result =
          adAccessChecker.hasRights(ActionType.LIST_ALL_TEAMS, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyJoin() {
      userService.setUserForTest(admin);
      boolean result = adAccessChecker.hasRights(ActionType.JOIN, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyLeave() {
      userService.setUserForTest(admin);
      boolean result = adAccessChecker.hasRights(ActionType.LEAVE, List.of(team.getSlug()));
      assertFalse(result);
    }
  }
}
