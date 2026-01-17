package com.tribly.repository.trip;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.platform.Domain;
import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TripRepository}.
 */
@QuarkusTest
class TripRepositoryTest {

  @Inject TripRepository tripRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext triblyQueryContext;

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
    @DisplayName("Should find trip by team and slug")
    void findByTeamAndSlug_shouldFindTrip() {
      Trip trip = dataService.createTrip(team, user, "Test Trip", now);

      Optional<Trip> result =
          tripRepository.findByTeamAndSlug(domain.getId(), team.getId(), user.getId(), "test-trip");

      assertTrue(result.isPresent());
      assertEquals(trip.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-existent slug")
    void findByTeamAndSlug_nonExistentSlug_shouldReturnEmpty() {
      dataService.createTrip(team, user, "Test Trip", now);

      Optional<Trip> result =
          tripRepository.findByTeamAndSlug(
              domain.getId(), team.getId(), user.getId(), "non-existent");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted trip")
    void findByTeamAndSlug_deleted_shouldReturnEmpty() {
      Trip trip = dataService.createTrip(team, user, "Test Trip", now);
      dataService.deleteTrip(trip);

      Optional<Trip> result =
          tripRepository.findByTeamAndSlug(domain.getId(), team.getId(), user.getId(), "test-trip");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not find trip from different team")
    void findByTeamAndSlug_differentTeam_shouldReturnEmpty() {
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createTrip(otherTeam, user, "Test Trip", now);

      Optional<Trip> result =
          tripRepository.findByTeamAndSlug(domain.getId(), team.getId(), user.getId(), "test-trip");

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("existsByTeamAndSlug")
  class ExistsByTeamAndSlug {

    @Test
    @DisplayName("Should return true for existing trip")
    void existsByTeamAndSlug_existing_shouldReturnTrue() {
      dataService.createTrip(team, user, "Test Trip", now);

      boolean exists = tripRepository.existsByTeamAndSlug(team.getId(), "test-trip");

      assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false for non-existent slug")
    void existsByTeamAndSlug_nonExistent_shouldReturnFalse() {
      boolean exists = tripRepository.existsByTeamAndSlug(team.getId(), "non-existent");

      assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true for deleted trip")
    void existsByTeamAndSlug_deleted_shouldReturnFalse() {
      Trip trip = dataService.createTrip(team, user, "Test Trip", now);
      dataService.deleteTrip(trip);

      boolean exists = tripRepository.existsByTeamAndSlug(team.getId(), "test-trip");

      assertTrue(exists);
    }
  }

  @Nested
  @DisplayName("getTypeName")
  class GetTypeName {

    @Test
    @DisplayName("Should return Trip as type name")
    void getTypeName_shouldReturnTrip() {
      assertEquals("Trip", tripRepository.getEntityType().getTypeName());
    }
  }
}
