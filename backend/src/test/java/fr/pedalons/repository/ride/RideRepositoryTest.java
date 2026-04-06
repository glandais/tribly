package fr.pedalons.repository.ride;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideRepositoryTest extends AbstractBaseTest {

  @Inject RideRepository rideRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private User user;
  private Team team;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    now = Instant.now();
  }

  @Nested
  @DisplayName("findByTeamAndSlug")
  class FindByTeamAndSlug {

    @Test
    @DisplayName("Should find ride by team and slug")
    void findByTeamAndSlug_shouldFindRide() {
      Ride ride = dataService.createRide(team, user, "Test Ride", "test-ride", now);

      Optional<Ride> result =
          rideRepository.findByTeamAndSlug(
              domain.getId(), team.getId(), user.getId(), "test-ride", false);

      assertTrue(result.isPresent());
      assertEquals(ride.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-existent slug")
    void findByTeamAndSlug_nonExistentSlug_shouldReturnEmpty() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);

      Optional<Ride> result =
          rideRepository.findByTeamAndSlug(
              domain.getId(), team.getId(), user.getId(), "non-existent", false);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted ride")
    void findByTeamAndSlug_deleted_shouldReturnEmpty() {
      Ride ride = dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.deleteRide(ride);

      Optional<Ride> result =
          rideRepository.findByTeamAndSlug(
              domain.getId(), team.getId(), user.getId(), "test-ride", false);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not find ride from different team")
    void findByTeamAndSlug_differentTeam_shouldReturnEmpty() {
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createRide(otherTeam, user, "Test Ride", "test-ride", now);

      Optional<Ride> result =
          rideRepository.findByTeamAndSlug(
              domain.getId(), team.getId(), user.getId(), "test-ride", false);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findByTeamAndId")
  class FindByTeamAndId {

    @Test
    @DisplayName("Should find ride by team and id")
    void findByTeamAndId_shouldFindRide() {
      Ride ride = dataService.createRide(team, user, "Test Ride", "test-ride", now);

      Optional<Ride> result =
          rideRepository.findByTeamAndId(
              domain.getId(), team.getId(), user.getId(), ride.getId(), false);

      assertTrue(result.isPresent());
      assertEquals(ride.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-existent id")
    void findByTeamAndId_nonExistentId_shouldReturnEmpty() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);

      Optional<Ride> result =
          rideRepository.findByTeamAndId(
              domain.getId(), team.getId(), user.getId(), 999999L, false);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted ride")
    void findByTeamAndId_deleted_shouldReturnEmpty() {
      Ride ride = dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.deleteRide(ride);

      Optional<Ride> result =
          rideRepository.findByTeamAndId(
              domain.getId(), team.getId(), user.getId(), ride.getId(), false);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not find ride from different team")
    void findByTeamAndId_differentTeam_shouldReturnEmpty() {
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      Ride ride = dataService.createRide(otherTeam, user, "Test Ride", "test-ride", now);

      Optional<Ride> result =
          rideRepository.findByTeamAndId(
              domain.getId(), team.getId(), user.getId(), ride.getId(), false);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("existsByTeamAndSlug")
  class ExistsByTeamAndSlug {

    @Test
    @DisplayName("Should return true for existing ride")
    void existsByTeamAndSlug_existing_shouldReturnTrue() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);

      boolean exists = rideRepository.existsByTeamAndSlug(team.getId(), "test-ride");

      assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false for non-existent slug")
    void existsByTeamAndSlug_nonExistent_shouldReturnFalse() {
      boolean exists = rideRepository.existsByTeamAndSlug(team.getId(), "non-existent");

      assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true for deleted ride (slug still reserved)")
    void existsByTeamAndSlug_deleted_shouldReturnTrue() {
      Ride ride = dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.deleteRide(ride);

      boolean exists = rideRepository.existsByTeamAndSlug(team.getId(), "test-ride");

      assertTrue(exists);
    }
  }

  @Nested
  @DisplayName("Query Builder Methods")
  class QueryBuilderMethods {

    @Test
    @DisplayName("getQuerySlug should build correct query")
    void getQuerySlug_shouldBuildCorrectQuery() {
      TeamEntityQueryBasic query =
          rideRepository.getQuerySlug(
              domain.getId(), team.getId(), user.getId(), "test-slug", false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    @DisplayName("getQuerySlug should work with null userId")
    void getQuerySlug_shouldWorkWithNullUserId() {
      TeamEntityQueryBasic query =
          rideRepository.getQuerySlug(domain.getId(), team.getId(), null, "test-slug", false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    @DisplayName("getQueryId should build correct query")
    void getQueryId_shouldBuildCorrectQuery() {
      TeamEntityQueryBasic query =
          rideRepository.getQueryId(domain.getId(), team.getId(), user.getId(), 12345L, false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals(12345L, query.id());
    }

    @Test
    @DisplayName("getQueryId should work with null userId")
    void getQueryId_shouldWorkWithNullUserId() {
      TeamEntityQueryBasic query =
          rideRepository.getQueryId(domain.getId(), team.getId(), null, 12345L, false);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals(12345L, query.id());
    }
  }

  @Nested
  @DisplayName("Entity Type Methods")
  class EntityTypeMethods {

    @Test
    @DisplayName("getEntityType should return RIDE")
    void getEntityType_shouldReturnRide() {
      assertEquals(TeamEntityType.RIDE, rideRepository.getEntityType());
    }

    @Test
    @DisplayName("getAllEntityType should return RIDE")
    void getAllEntityType_shouldReturnRide() {
      assertEquals(EntityType.RIDE, rideRepository.getAllEntityType());
    }
  }
}
