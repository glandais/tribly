package com.tribly.domain.trip;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.trip.repository.TripRepository;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

  private User user;
  private Team team;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
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

      Optional<Trip> result = tripRepository.findByTeamAndSlug(team.getId(), "test-trip");

      assertTrue(result.isPresent());
      assertEquals(trip.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-existent slug")
    void findByTeamAndSlug_nonExistentSlug_shouldReturnEmpty() {
      dataService.createTrip(team, user, "Test Trip", now);

      Optional<Trip> result = tripRepository.findByTeamAndSlug(team.getId(), "non-existent");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted trip")
    void findByTeamAndSlug_deleted_shouldReturnEmpty() {
      Trip trip = dataService.createTrip(team, user, "Test Trip", now);
      dataService.deleteTrip(trip);

      Optional<Trip> result = tripRepository.findByTeamAndSlug(team.getId(), "test-trip");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not find trip from different team")
    void findByTeamAndSlug_differentTeam_shouldReturnEmpty() {
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createTrip(otherTeam, user, "Test Trip", now);

      Optional<Trip> result = tripRepository.findByTeamAndSlug(team.getId(), "test-trip");

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
  @DisplayName("find")
  class Find {

    @Test
    @DisplayName("Should return trips")
    void find_shouldReturnTrips() {
      dataService.createTrip(team, user, "Test Trip", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Test Trip", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return deleted trips")
    void find_shouldNotReturnDeleted() {
      Trip trip = dataService.createTrip(team, user, "Test Trip", now);
      dataService.createTrip(team, user, "Active Trip", now.plus(1, ChronoUnit.HOURS));
      dataService.deleteTrip(trip);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Active Trip", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return draft trips for anonymous users")
    void find_shouldNotReturnDraftForAnonymous() {
      dataService.createTrip(
          team, user, "Published Trip", now, Visibility.PUBLIC, Status.PUBLISHED, null);
      dataService.createTrip(team, user, "Draft Trip", now, Visibility.PUBLIC, Status.DRAFT, null);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Published Trip", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should support pagination")
    void find_shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createTrip(team, user, "Trip " + i, now.plus(i, ChronoUnit.HOURS));
      }

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 2);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(5, result.total());
    }
  }

  @Nested
  @DisplayName("getTypeName")
  class GetTypeName {

    @Test
    @DisplayName("Should return Trip as type name")
    void getTypeName_shouldReturnTrip() {
      assertEquals("Trip", tripRepository.getTypeName());
    }
  }

  @Nested
  @DisplayName("Enable Trips Filtering")
  class EnableTripsFiltering {

    @Test
    @DisplayName("Should return Trip when enableTrips is true")
    void find_shouldReturnTripWhenEnableTripsTrue() {
      team.setEnableTrips(true);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Summer Trip", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Summer Trip", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should NOT return Trip when enableTrips is false")
    void find_shouldNotReturnTripWhenEnableTripsFalse() {
      team.setEnableTrips(false);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Hidden Trip", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should filter Trips by enableTrips across multiple teams")
    void find_shouldFilterTripsAcrossMultipleTeams() {
      Team enabledTeam =
          dataService.createTeam(user, "Enabled Team", "enabled-team", Visibility.PUBLIC);
      Team disabledTeam =
          dataService.createTeam(user, "Disabled Team", "disabled-team", Visibility.PUBLIC);
      disabledTeam.setEnableTrips(false);
      dataService.updateTeam(disabledTeam);

      dataService.createTrip(enabledTeam, user, "Enabled Trip", now);
      dataService.createTrip(disabledTeam, user, "Disabled Trip", now.plus(1, ChronoUnit.HOURS));

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Enabled Trip", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should return TEAM Trip for authenticated member when enableTrips is true")
    void find_authenticatedMember_shouldReturnTeamTripWhenEnableTripsTrue() {
      User member = dataService.createUser("member@example.com", "Member");
      dataService.addUserToTeam(member, team, TeamRole.MEMBER);
      team.setEnableTrips(true);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Team Trip", now, Visibility.TEAM);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(member.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Team Trip", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should NOT return TEAM Trip for authenticated member when enableTrips is false")
    void find_authenticatedMember_shouldNotReturnTeamTripWhenEnableTripsFalse() {
      User member = dataService.createUser("member@example.com", "Member");
      dataService.addUserToTeam(member, team, TeamRole.MEMBER);
      team.setEnableTrips(false);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Team Trip", now, Visibility.TEAM);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(member.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Trip> result = tripRepository.find(query);

      assertEquals(0, result.items().size());
    }
  }
}
