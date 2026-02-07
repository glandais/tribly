package com.tribly.repository.trip;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripParticipation;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
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
 * Tests for {@link TripParticipationRepository}.
 */
@QuarkusTest
class TripParticipationRepositoryTest {

  @Inject TripParticipationRepository participationRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user1;
  private User user2;
  private Team team;
  private Trip trip;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user1 = dataService.createUser("user1@example.com", "User 1");
    user2 = dataService.createUser("user2@example.com", "User 2");
    team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);
    now = Instant.now();
    trip = dataService.createTrip(team, user1, "Test Trip", now);
  }

  @Nested
  @DisplayName("findByUserAndTrip")
  class FindByUserAndTrip {

    @Test
    @DisplayName("Should find participation by user and trip")
    void findByUserAndTrip_shouldFindParticipation() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);

      Optional<TripParticipation> result =
          participationRepository.findByUserAndTrip(user1.getId(), trip.getId());

      assertTrue(result.isPresent());
      assertEquals(participation.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-participating user")
    void findByUserAndTrip_nonParticipating_shouldReturnEmpty() {
      dataService.createTripParticipation(trip, user1);

      Optional<TripParticipation> result =
          participationRepository.findByUserAndTrip(user2.getId(), trip.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for user in different trip")
    void findByUserAndTrip_differentTrip_shouldReturnEmpty() {
      Trip otherTrip = dataService.createTrip(team, user1, "Other Trip", now);
      dataService.createTripParticipation(otherTrip, user1);

      Optional<TripParticipation> result =
          participationRepository.findByUserAndTrip(user1.getId(), trip.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted participation")
    void findByUserAndTrip_deleted_shouldReturnEmpty() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);
      dataService.deleteTripParticipation(participation);

      Optional<TripParticipation> result =
          participationRepository.findByUserAndTrip(user1.getId(), trip.getId());

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findByUserAndTripIncludingDeleted")
  class FindByUserAndTripIncludingDeleted {

    @Test
    @DisplayName("Should find active participation")
    void findByUserAndTripIncludingDeleted_active_shouldFind() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);

      Optional<TripParticipation> result =
          participationRepository.findByUserAndTrip(user1.getId(), trip.getId());

      assertTrue(result.isPresent());
      assertEquals(participation.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-participating user")
    void findByUserAndTripIncludingDeleted_nonParticipating_shouldReturnEmpty() {
      dataService.createTripParticipation(trip, user1);

      Optional<TripParticipation> result =
          participationRepository.findByUserAndTrip(user2.getId(), trip.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for user in different trip")
    void findByUserAndTripIncludingDeleted_differentTrip_shouldReturnEmpty() {
      Trip otherTrip = dataService.createTrip(team, user1, "Other Trip", now);
      dataService.createTripParticipation(otherTrip, user1);

      Optional<TripParticipation> result =
          participationRepository.findByUserAndTrip(user1.getId(), trip.getId());

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Basic CRUD")
  class BasicCrud {

    @Test
    @DisplayName("Should persist and find participation by id")
    void persist_shouldPersistAndFindById() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);

      TripParticipation found = participationRepository.findById(participation.getId());

      assertNotNull(found);
      assertEquals(participation.getId(), found.getId());
    }

    @Test
    @DisplayName("Should preserve trip association")
    void createParticipation_shouldPreserveTrip() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);

      TripParticipation found = participationRepository.findById(participation.getId());

      assertEquals(trip.getId(), found.getTrip().getId());
    }

    @Test
    @DisplayName("Should preserve user association")
    void createParticipation_shouldPreserveUser() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);

      TripParticipation found = participationRepository.findById(participation.getId());

      assertEquals(user1.getId(), found.getUser().getId());
    }

    @Test
    @DisplayName("Should have registeredAt timestamp")
    void createParticipation_shouldHaveRegisteredAt() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);

      TripParticipation found = participationRepository.findById(participation.getId());

      assertNotNull(found.getRegisteredAt());
    }
  }

  @Nested
  @DisplayName("Multiple Participations")
  class MultipleParticipations {

    @Test
    @DisplayName("Multiple users can participate in same trip")
    void multipleUsers_sameTrip_shouldBeIndependent() {
      TripParticipation p1 = dataService.createTripParticipation(trip, user1);
      TripParticipation p2 = dataService.createTripParticipation(trip, user2);

      assertNotEquals(p1.getId(), p2.getId());
      assertEquals(user1.getId(), participationRepository.findById(p1.getId()).getUser().getId());
      assertEquals(user2.getId(), participationRepository.findById(p2.getId()).getUser().getId());
    }

    @Test
    @DisplayName("Same user can participate in multiple trips")
    void sameUser_multipleTrips_shouldBeIndependent() {
      Trip trip2 = dataService.createTrip(team, user1, "Trip 2", now);
      TripParticipation p1 = dataService.createTripParticipation(trip, user1);
      TripParticipation p2 = dataService.createTripParticipation(trip2, user1);

      assertNotEquals(p1.getId(), p2.getId());
      assertEquals(trip.getId(), participationRepository.findById(p1.getId()).getTrip().getId());
      assertEquals(trip2.getId(), participationRepository.findById(p2.getId()).getTrip().getId());
    }
  }

  @Nested
  @DisplayName("Soft Delete")
  class SoftDelete {

    @Test
    @DisplayName("Should soft delete participation")
    void deleteParticipation_shouldSetDeletedTrue() {
      TripParticipation participation = dataService.createTripParticipation(trip, user1);

      dataService.deleteTripParticipation(participation);

      Optional<TripParticipation> found =
          participationRepository.findByIdOptional(participation.getId());
      assertTrue(found.isEmpty());
    }

  }
}
