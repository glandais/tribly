package com.tribly.service.common;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.team.Team;
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
class AllPublicationAccessCheckerTest extends AbstractBaseTest {

  @Inject AllPublicationAccessChecker accessChecker;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext userService;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team publicTeam;
  private Team privateTeam;
  private User admin;
  private User member;
  private User nonMember;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    publicTeam = dataService.createTeam(admin, "Public Team", "public-team", Visibility.PUBLIC);
    privateTeam = dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(member, privateTeam, TeamRole.MEMBER);
  }

  @Test
  void getType_shouldReturnPublication() {
    assertEquals(EntityType.PUBLICATION, accessChecker.getType());
  }

  @Nested
  class ListAllTeamsAction {

    @Test
    void shouldAllowForAnonymous() {
      userService.setUserForTest(null);
      boolean result = accessChecker.hasRights(ActionType.LIST_ALL_TEAMS, List.of());
      assertTrue(result);
    }

    @Test
    void shouldAllowForUser() {
      userService.setUserForTest(member);
      boolean result = accessChecker.hasRights(ActionType.LIST_ALL_TEAMS, List.of());
      assertTrue(result);
    }
  }

  @Nested
  class ListActionWithoutTeam {

    @Test
    void shouldAllowForAnonymous() {
      userService.setUserForTest(null);
      // No team slug = all publications across teams
      List<Object> params = new ArrayList<>();
      params.add(null);
      boolean result = accessChecker.hasRights(ActionType.LIST, params);
      assertTrue(result);
    }

    @Test
    void shouldAllowForUser() {
      userService.setUserForTest(member);
      List<Object> params = new ArrayList<>();
      params.add(null);
      boolean result = accessChecker.hasRights(ActionType.LIST, params);
      assertTrue(result);
    }
  }

  @Nested
  class ListActionWithPublicTeam {

    @Test
    void shouldAllowForAnonymous() {
      userService.setUserForTest(null);
      boolean result = accessChecker.hasRights(ActionType.LIST, List.of(publicTeam.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForNonMember() {
      userService.setUserForTest(nonMember);
      boolean result = accessChecker.hasRights(ActionType.LIST, List.of(publicTeam.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.LIST, List.of(publicTeam.getSlug()));
      assertTrue(result);
    }
  }

  @Nested
  class ListActionWithPrivateTeam {

    @Test
    void shouldDenyForAnonymous() {
      userService.setUserForTest(null);
      boolean result = accessChecker.hasRights(ActionType.LIST, List.of(privateTeam.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonMember() {
      userService.setUserForTest(nonMember);
      boolean result = accessChecker.hasRights(ActionType.LIST, List.of(privateTeam.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(member);
      boolean result = accessChecker.hasRights(ActionType.LIST, List.of(privateTeam.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.LIST, List.of(privateTeam.getSlug()));
      assertTrue(result);
    }
  }

  @Nested
  class UnsupportedActions {

    @Test
    void shouldDenyRead() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.READ, List.of(publicTeam.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyCreate() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.CREATE, List.of(publicTeam.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyUpdate() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.UPDATE, List.of(publicTeam.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyDelete() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.DELETE, List.of(publicTeam.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyJoin() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.JOIN, List.of(publicTeam.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyLeave() {
      userService.setUserForTest(admin);
      boolean result = accessChecker.hasRights(ActionType.LEAVE, List.of(publicTeam.getSlug()));
      assertFalse(result);
    }
  }
}
