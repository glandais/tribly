package com.tribly.service.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.TriblyException;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.teams.response.MemberDto;
import com.tribly.dto.teams.response.MemberListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.ad.AdService;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
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
  @Inject TriblyQueryContext queryContext;
  @Inject AdService adService;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User admin;
  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
  }

  // ==================== Get Team Members ====================

  @Test
  void getTeamMembers_shouldReturnMembersForAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.ORGANIZER);

    queryContext.setUserForTest(admin);
    MemberListResponse result = membershipService.getTeamMembers(team.getSlug(), 0, 10);

    assertEquals(3, result.members().size()); // admin + user1 + user2
  }

  @Test
  void getTeamMembers_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    assertThrows(
        TriblyException.class, () -> membershipService.getTeamMembers(team.getSlug(), 0, 10));
  }

  @Test
  void getTeamMembers_shouldSupportPagination() {
    for (int i = 3; i <= 7; i++) {
      User user = dataService.createUser("user" + i + "@example.com", "User " + i);
      dataService.addUserToTeam(user, team, TeamRole.MEMBER);
    }

    queryContext.setUserForTest(admin);
    MemberListResponse result = membershipService.getTeamMembers(team.getSlug(), 0, 3);

    assertEquals(3, result.members().size());
    assertEquals(6, result.total()); // admin + 5 users
  }

  // ==================== Join Team ====================

  @Test
  void joinTeam_shouldJoinPublicTeam() {
    queryContext.setUserForTest(user1);
    MemberDto result = membershipService.joinTeam(team.getSlug());

    assertNotNull(result);
    assertEquals(user1.getId(), TsidUtils.toLong(result.user().id()));
    assertEquals(TeamRole.MEMBER, result.role());
  }

  @Test
  void joinTeam_shouldThrowWhenAlreadyMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    TriblyException exception =
        assertThrows(TriblyException.class, () -> membershipService.joinTeam(team.getSlug()));

    assertEquals("ALREADY_REGISTERED", exception.getMessage());
  }

  @Test
  void joinTeam_shouldRestoreSoftDeletedMembership() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(membership);

    queryContext.setUserForTest(user1);
    MemberDto result = membershipService.joinTeam(team.getSlug());

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.MEMBER, result.role());
  }

  // ==================== Add Member ====================

  @Test
  void addMember_shouldAddMemberAsAdmin() {
    queryContext.setUserForTest(admin);
    MemberDto result =
        membershipService.addMember(team.getSlug(), user1.getId(), TeamRole.ORGANIZER);

    assertNotNull(result);
    assertEquals(user1.getId(), TsidUtils.toLong(result.user().id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  @Test
  void addMember_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    assertThrows(
        TriblyException.class,
        () -> membershipService.addMember(team.getSlug(), user2.getId(), TeamRole.MEMBER));
  }

  @Test
  void addMember_shouldThrowWhenAlreadyMember() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(admin);
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.addMember(team.getSlug(), user1.getId(), TeamRole.ORGANIZER));

    assertEquals("ALREADY_REGISTERED", exception.getMessage());
  }

  @Test
  void addMember_shouldRestoreSoftDeletedMembership() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(membership);

    queryContext.setUserForTest(admin);
    MemberDto result =
        membershipService.addMember(team.getSlug(), user1.getId(), TeamRole.ORGANIZER);

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  // ==================== Update Member Role ====================

  @Test
  void updateMemberRole_shouldUpdateRole() {
    UserTeam membership = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(admin);
    MemberDto result =
        membershipService.updateMemberRole(team.getSlug(), user1.getId(), TeamRole.ORGANIZER);

    assertEquals(membership.getId(), TsidUtils.toLong(result.id()));
    assertEquals(TeamRole.ORGANIZER, result.role());
  }

  @Test
  void updateMemberRole_shouldThrowForNonAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    assertThrows(
        TriblyException.class,
        () ->
            membershipService.updateMemberRole(team.getSlug(), user2.getId(), TeamRole.ORGANIZER));
  }

  @Test
  void updateMemberRole_shouldPreventDemotingLastAdmin() {
    queryContext.setUserForTest(admin);
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () ->
                membershipService.updateMemberRole(team.getSlug(), admin.getId(), TeamRole.MEMBER));

    assertEquals("LAST_ADMIN", exception.getMessage());
  }

  @Test
  void updateMemberRole_shouldAllowDemotingWhenMultipleAdmins() {
    User admin2 = dataService.createUser("admin2@example.com", "Admin Two");
    dataService.addUserToTeam(admin2, team, TeamRole.ADMIN);

    queryContext.setUserForTest(admin2);
    MemberDto result =
        membershipService.updateMemberRole(team.getSlug(), admin.getId(), TeamRole.MEMBER);

    assertEquals(TeamRole.MEMBER, result.role());
  }

  // ==================== Remove Member ====================

  @Test
  void removeMember_shouldRemoveMemberAsAdmin() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(admin);
    membershipService.removeMember(team.getSlug(), user1.getId());

    queryContext.setUserForTest(user1);
    // Verify soft deletion by trying to get team - should not see it
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> adService.listAds(team.getSlug(), null, null, null, null, 0, 10));
    assertEquals("FORBIDDEN", exception.getMessage());
  }

  @Test
  void removeMember_shouldAllowSelfRemoval() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(admin);
    membershipService.removeMember(team.getSlug(), user1.getId());

    queryContext.setUserForTest(user1);
    // Verify soft deletion
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> adService.listAds(team.getSlug(), null, null, null, null, 0, 10));
    assertEquals("FORBIDDEN", exception.getMessage());
  }

  @Test
  void removeMember_shouldPreventRemovingLastAdmin() {
    queryContext.setUserForTest(admin);
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.removeMember(team.getSlug(), admin.getId()));

    assertEquals("LAST_ADMIN", exception.getMessage());
  }

  @Test
  void removeMember_shouldAllowRemovingWhenMultipleAdmins() {
    User admin2 = dataService.createUser("admin2@example.com", "Admin Two");
    dataService.addUserToTeam(admin2, team, TeamRole.ADMIN);

    queryContext.setUserForTest(admin2);
    membershipService.removeMember(team.getSlug(), admin.getId());

    queryContext.setUserForTest(admin);
    // Verify soft deletion
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> adService.listAds(team.getSlug(), null, null, null, null, 0, 10));
    assertEquals("FORBIDDEN", exception.getMessage());
  }

  @Test
  void removeMember_shouldThrowForNonAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    assertThrows(
        TriblyException.class, () -> membershipService.removeMember(team.getSlug(), user2.getId()));
  }

  // ==================== Leave Team ====================

  @Test
  void leaveTeam_shouldRemoveSelf() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    queryContext.setUserForTest(user1);
    membershipService.leaveTeam(team.getSlug());

    queryContext.setUserForTest(user1);
    // Verify soft deletion
    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> adService.listAds(team.getSlug(), null, null, null, null, 0, 10));
    assertEquals("FORBIDDEN", exception.getMessage());
  }

  @Test
  void leaveTeam_shouldPreventLastAdminLeaving() {
    queryContext.setUserForTest(admin);
    TriblyException exception =
        assertThrows(TriblyException.class, () -> membershipService.leaveTeam(team.getSlug()));

    assertEquals("LAST_ADMIN", exception.getMessage());
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
    queryContext.setUserForTest(user1);
    membershipService.removeMember(team.getSlug(), admin.getId());

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.requireNotLastAdmin(this.team, lastAdmin));

    assertEquals("LAST_ADMIN", exception.getMessage());
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
    queryContext.setUserForTest(user1);
    membershipService.removeMember(team.getSlug(), admin.getId());

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () ->
                membershipService.requireNotLastAdminDemotion(team, lastAdmin, TeamRole.ORGANIZER));

    assertEquals("LAST_ADMIN", exception.getMessage());
  }

  // ==================== Self-Action Checks ====================

  @Test
  void requireCanRemoveMember_shouldSucceedForSelfRemoval() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    assertDoesNotThrow(
        () -> membershipService.requireCanRemoveMember(user1, TeamRole.MEMBER, user1));
  }

  @Test
  void requireCanRemoveMember_shouldSucceedForAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    assertDoesNotThrow(
        () -> membershipService.requireCanRemoveMember(user1, TeamRole.ADMIN, user2));
  }

  @Test
  void requireCanRemoveMember_shouldThrowForNonAdminRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.requireCanRemoveMember(user1, TeamRole.MEMBER, user2));

    assertEquals("FORBIDDEN", exception.getMessage());
  }

  @Test
  void requireCanRemoveMember_shouldThrowForOrganizerRemovingOthers() {
    dataService.addUserToTeam(user1, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    TriblyException exception =
        assertThrows(
            TriblyException.class,
            () -> membershipService.requireCanRemoveMember(user1, TeamRole.ORGANIZER, user2));

    assertEquals("FORBIDDEN", exception.getMessage());
  }
}
