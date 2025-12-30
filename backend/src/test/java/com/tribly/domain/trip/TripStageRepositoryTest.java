package com.tribly.domain.trip;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.trip.repository.TripStageRepository;
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
 * Tests for {@link TripStageRepository}.
 */
@QuarkusTest
class TripStageRepositoryTest {

  @Inject TripStageRepository stageRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;
  private Team team;
  private Trip trip;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    now = Instant.now();
    trip = dataService.createTrip(team, user, "Test Trip", now);
  }

  @Nested
  @DisplayName("findByIdAndTrip")
  class FindByIdAndTrip {

    @Test
    @DisplayName("Should find stage by id and trip")
    void findByIdAndTrip_shouldFindStage() {
      TripStage stage = dataService.createTripStage(user, trip, "Stage 1");

      Optional<TripStage> result = stageRepository.findByIdAndTrip(stage.getId(), trip.getId());

      assertTrue(result.isPresent());
      assertEquals(stage.getId(), result.get().getId());
      assertEquals("Stage 1", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty for non-existent stage id")
    void findByIdAndTrip_nonExistentId_shouldReturnEmpty() {
      dataService.createTripStage(user, trip, "Stage 1");

      Optional<TripStage> result = stageRepository.findByIdAndTrip(999999L, trip.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for stage in different trip")
    void findByIdAndTrip_differentTrip_shouldReturnEmpty() {
      TripStage stage = dataService.createTripStage(user, trip, "Stage 1");
      Trip otherTrip = dataService.createTrip(team, user, "Other Trip", now);

      Optional<TripStage> result =
          stageRepository.findByIdAndTrip(stage.getId(), otherTrip.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted stage")
    void findByIdAndTrip_deleted_shouldReturnEmpty() {
      TripStage stage = dataService.createTripStage(user, trip, "Stage 1");
      dataService.deleteTripStage(stage);

      Optional<TripStage> result = stageRepository.findByIdAndTrip(stage.getId(), trip.getId());

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findBySlugAndTeam")
  class FindBySlugAndTeam {

    @Test
    @DisplayName("Should find stage by slug and team")
    void findBySlugAndTeam_shouldFindStage() {
      TripStage stage = dataService.createTripStage(user, trip, "Stage 1");

      Optional<TripStage> result = stageRepository.findBySlugAndTeam("stage-1", team.getId());

      assertTrue(result.isPresent());
      assertEquals(stage.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-existent slug")
    void findBySlugAndTeam_nonExistentSlug_shouldReturnEmpty() {
      dataService.createTripStage(user, trip, "Stage 1");

      Optional<TripStage> result = stageRepository.findBySlugAndTeam("non-existent", team.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for stage in different team")
    void findBySlugAndTeam_differentTeam_shouldReturnEmpty() {
      dataService.createTripStage(user, trip, "Stage 1");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      Optional<TripStage> result = stageRepository.findBySlugAndTeam("stage-1", otherTeam.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted stage")
    void findBySlugAndTeam_deleted_shouldReturnEmpty() {
      TripStage stage = dataService.createTripStage(user, trip, "Stage 1");
      dataService.deleteTripStage(stage);

      Optional<TripStage> result = stageRepository.findBySlugAndTeam("stage-1", team.getId());

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Basic CRUD")
  class BasicCrud {

    @Test
    @DisplayName("Should persist and find stage by id")
    void persist_shouldPersistAndFindById() {
      TripStage stage = dataService.createTripStage(user, trip, "Stage 1");

      TripStage found = stageRepository.findById(stage.getId());

      assertNotNull(found);
      assertEquals(stage.getId(), found.getId());
      assertEquals("Stage 1", found.getName());
    }

    @Test
    @DisplayName("Should preserve trip association")
    void createStage_shouldPreserveTrip() {
      TripStage stage = dataService.createTripStage(user, trip, "Stage 1");

      TripStage found = stageRepository.findById(stage.getId());

      assertEquals(trip.getId(), found.getTrip().getId());
    }

    @Test
    @DisplayName("Should preserve sort order")
    void createStage_shouldPreserveSortOrder() {
      TripStage stage1 = dataService.createTripStage(user, trip, "Stage 1", 1);
      TripStage stage2 = dataService.createTripStage(user, trip, "Stage 2", 2);

      assertEquals(1, stageRepository.findById(stage1.getId()).getSortOrder());
      assertEquals(2, stageRepository.findById(stage2.getId()).getSortOrder());
    }
  }

  @Nested
  @DisplayName("Multiple Stages")
  class MultipleStages {

    @Test
    @DisplayName("Stages from different trips should be independent")
    void stages_fromDifferentTrips_shouldBeIndependent() {
      Trip trip2 = dataService.createTrip(team, user, "Trip 2", now);

      TripStage stage1 = dataService.createTripStage(user, trip, "Stage 1");
      TripStage stage2 = dataService.createTripStage(user, trip2, "Stage A");

      assertNotEquals(stage1.getId(), stage2.getId());
      assertEquals(trip.getId(), stageRepository.findById(stage1.getId()).getTrip().getId());
      assertEquals(trip2.getId(), stageRepository.findById(stage2.getId()).getTrip().getId());
    }
  }
}
