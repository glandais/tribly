package com.tribly.domain.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.TriblyPage;
import com.tribly.domain.user.User;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserTeamRepositoryTest {

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
    team = dataService.createTeam("Test Team", "test-team");
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
    user3 = dataService.createUser("user3@example.com", "User Three");
  }

  @Test
  void findByUserAndTeamIncludingDeleted_shouldReturnMembershipWhenExists() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    Optional<UserTeam> result =
        userTeamRepository.findByUserAndTeamIncludingDeleted(user1.getId(), "test-team");

    assertTrue(result.isPresent());
    assertEquals(user1.getId(), result.get().getUser().getId());
    assertEquals(team.getId(), result.get().getTeam().getId());
    assertEquals(TeamRole.MEMBER, result.get().getRole());
  }

  @Test
  void findByUserAndTeamIncludingDeleted_shouldReturnEmptyWhenNotExists() {
    Optional<UserTeam> result =
        userTeamRepository.findByUserAndTeamIncludingDeleted(user1.getId(), "test-team");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndTeamIncludingDeleted_shouldReturnDeletedMembership() {
    UserTeam userTeam = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(userTeam);

    Optional<UserTeam> result =
        userTeamRepository.findByUserAndTeamIncludingDeleted(user1.getId(), "test-team");

    assertTrue(result.isPresent());
    assertTrue(result.get().isDeleted());
  }

  @Test
  void findByTeam_shouldReturnAllMembersOfTeam() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    TriblyPage<UserTeam> result = userTeamRepository.findByTeam("test-team", 0, 10);

    assertEquals(3, result.items().size());
    assertEquals(3, result.total());
  }

  @Test
  void findByTeam_shouldSupportPagination() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    TriblyPage<UserTeam> result = userTeamRepository.findByTeam("test-team", 0, 2);

    assertEquals(2, result.items().size());
    assertEquals(3, result.total());
  }

  @Test
  void findByTeam_shouldIgnoreDeletedMemberships() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    UserTeam deletedMembership = dataService.addUserToTeam(user2, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(deletedMembership);

    TriblyPage<UserTeam> result = userTeamRepository.findByTeam("test-team", 0, 10);

    assertEquals(1, result.items().size());
    assertEquals(user1.getId(), result.items().getFirst().getUser().getId());
  }

  @Test
  void findByTeam_shouldReturnEmptyForNonexistentTeam() {
    TriblyPage<UserTeam> result = userTeamRepository.findByTeam("nonexistent-team", 0, 10);

    assertEquals(0, result.items().size());
  }

  @Test
  void countAdminsByTeam_shouldReturnCorrectCount() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user2, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    long count = userTeamRepository.countAdminsByTeam("test-team");

    assertEquals(2, count);
  }

  @Test
  void countAdminsByTeam_shouldIgnoreDeletedMemberships() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    UserTeam deletedAdmin = dataService.addUserToTeam(user2, team, TeamRole.ADMIN);
    dataService.deleteUserTeam(deletedAdmin);

    long count = userTeamRepository.countAdminsByTeam("test-team");

    assertEquals(1, count);
  }

  @Test
  void countAdminsByTeam_shouldReturnZeroWhenNoAdmins() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.addUserToTeam(user2, team, TeamRole.ORGANIZER);

    long count = userTeamRepository.countAdminsByTeam("test-team");

    assertEquals(0, count);
  }

  @Test
  void findByUserAndTeam_shouldReturnMembershipWhenExists() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);

    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user1.getId(), "test-team");

    assertTrue(result.isPresent());
    assertEquals(user1.getId(), result.get().getUser().getId());
    assertEquals(TeamRole.MEMBER, result.get().getRole());
  }

  @Test
  void findByUserAndTeam_shouldReturnEmptyWhenNotExists() {
    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user1.getId(), "test-team");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndTeam_shouldIgnoreDeletedMembership() {
    UserTeam userTeam = dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteUserTeam(userTeam);

    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user1.getId(), "test-team");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndTeam_shouldIgnoreDeletedTeam() {
    dataService.addUserToTeam(user1, team, TeamRole.MEMBER);
    dataService.deleteTeam(team);

    Optional<UserTeam> result = userTeamRepository.findByUserAndTeam(user1.getId(), "test-team");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndTeam_shouldReturnCorrectRole() {
    dataService.addUserToTeam(user1, team, TeamRole.ADMIN);
    dataService.addUserToTeam(user2, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(user3, team, TeamRole.MEMBER);

    Optional<UserTeam> admin = userTeamRepository.findByUserAndTeam(user1.getId(), "test-team");
    Optional<UserTeam> organizer = userTeamRepository.findByUserAndTeam(user2.getId(), "test-team");
    Optional<UserTeam> member = userTeamRepository.findByUserAndTeam(user3.getId(), "test-team");

    assertTrue(admin.isPresent());
    assertEquals(TeamRole.ADMIN, admin.get().getRole());

    assertTrue(organizer.isPresent());
    assertEquals(TeamRole.ORGANIZER, organizer.get().getRole());

    assertTrue(member.isPresent());
    assertEquals(TeamRole.MEMBER, member.get().getRole());
  }
}
