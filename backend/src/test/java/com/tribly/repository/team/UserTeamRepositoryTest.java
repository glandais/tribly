package com.tribly.repository.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.common.TriblyPage;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserTeamRepositoryTest extends AbstractBaseTest {

  @Inject UserTeamRepository userTeamRepository;
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

  @Test
  void findByUserAndTeamIncludingDeleted_shouldReturnMembershipWhenExists() {
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user2.getId(), team.getId());

    assertTrue(result.isPresent());
    assertEquals(user2.getId(), result.get().getUser().getId());
    assertEquals(team.getId(), result.get().getTeam().getId());
    assertEquals(TeamRole.MEMBER, result.get().getRole());
  }

  @Test
  void findByTeam_shouldReturnAllMembersOfTeam() {
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    TriblyPage<UserTeam> result = userTeamRepository.findByTeam(team.getId(), 0, 10);

    assertEquals(3, result.items().size());
    assertEquals(3, result.total());
  }

  @Test
  void findByTeam_shouldSupportPagination() {
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    TriblyPage<UserTeam> result = userTeamRepository.findByTeam(team.getId(), 0, 2);

    assertEquals(2, result.items().size());
    assertEquals(3, result.total());
  }

  @Test
  void findByTeam_shouldIgnoreDeletedMemberships() {
    UserTeam deletedMembership = dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(deletedMembership);

    TriblyPage<UserTeam> result = userTeamRepository.findByTeam(team.getId(), 0, 10);

    assertEquals(1, result.items().size());
    assertEquals(user1.getId(), result.items().getFirst().getUser().getId());
  }

  @Test
  void findByTeam_shouldReturnEmptyForNonexistentTeam() {
    TriblyPage<UserTeam> result = userTeamRepository.findByTeam(-1L, 0, 10);

    assertEquals(0, result.items().size());
  }

  @Test
  void countAdminsByTeam_shouldReturnCorrectCount() {
    dataService.addUserToTeam(user2, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    long count = userTeamRepository.countAdminsByTeam(team.getId());

    assertEquals(2, count);
  }

  @Test
  void countAdminsByTeam_shouldIgnoreDeletedMemberships() {
    UserTeam deletedAdmin = dataService.addUserToTeam(user2, team, TeamRole.ADMIN);
    dataService.deleteUserTeam(deletedAdmin);

    long count = userTeamRepository.countAdminsByTeam(team.getId());

    assertEquals(1, count);
  }

  @Test
  void findByUserAndTeam_shouldReturnMembershipWhenExists() {
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);

    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user2.getId(), team.getId());

    assertTrue(result.isPresent());
    assertEquals(user2.getId(), result.get().getUser().getId());
    assertEquals(TeamRole.MEMBER, result.get().getRole());
  }

  @Test
  void findByUserAndTeam_shouldReturnEmptyWhenNotExists() {
    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user2.getId(), team.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndTeam_shouldIgnoreDeletedMembership() {
    UserTeam userTeam = dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(userTeam);

    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user2.getId(), team.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndTeam_shouldIgnoreDeletedTeam() {
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.deleteTeam(team);

    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user2.getId(), team.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndTeam_shouldReturnCorrectRole() {
    dataService.addUserToTeam(user2, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    Optional<UserTeam> admin = userTeamRepository.findByUserAndTeam(user1.getId(), team.getId());
    Optional<UserTeam> organizer =
        userTeamRepository.findByUserAndTeam(user2.getId(), team.getId());
    Optional<UserTeam> member = userTeamRepository.findByUserAndTeam(user3.getId(), team.getId());

    assertTrue(admin.isPresent());
    assertEquals(TeamRole.ADMIN, admin.get().getRole());

    assertTrue(organizer.isPresent());
    assertEquals(TeamRole.ORGANIZER, organizer.get().getRole());

    assertTrue(member.isPresent());
    assertEquals(TeamRole.MEMBER, member.get().getRole());
  }

  // ==================== findByUserId ====================

  @Test
  void findByUserId_shouldReturnAllTeamsForUser() {
    Team team2 = dataService.createTeam(user2, "Second Team", "second-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user1, team2, TeamRole.MEMBER);

    List<UserTeam> result = userTeamRepository.findByUserId(user1.getId());

    assertEquals(2, result.size());
  }

  @Test
  void findByUserId_shouldReturnEmptyListWhenUserHasNoTeams() {
    List<UserTeam> result = userTeamRepository.findByUserId(user2.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldIgnoreDeletedMemberships() {
    UserTeam membership = dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(membership);

    List<UserTeam> result = userTeamRepository.findByUserId(user2.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldIgnoreDeletedTeams() {
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.deleteTeam(team);

    List<UserTeam> result = userTeamRepository.findByUserId(user2.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldIgnoreDeletedUsers() {
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.deleteUser(user2);

    List<UserTeam> result = userTeamRepository.findByUserId(user2.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldReturnCorrectTeamsForDifferentUsers() {
    Team team2 = dataService.createTeam(user2, "Second Team", "second-team", Visibility.PUBLIC);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    List<UserTeam> user1Teams = userTeamRepository.findByUserId(user1.getId());
    List<UserTeam> user2Teams = userTeamRepository.findByUserId(user2.getId());
    List<UserTeam> user3Teams = userTeamRepository.findByUserId(user3.getId());

    assertEquals(1, user1Teams.size());
    assertEquals(team.getId(), user1Teams.getFirst().getTeam().getId());

    assertEquals(1, user2Teams.size());
    assertEquals(team2.getId(), user2Teams.getFirst().getTeam().getId());

    assertEquals(1, user3Teams.size());
    assertEquals(team.getId(), user3Teams.getFirst().getTeam().getId());
  }
}
