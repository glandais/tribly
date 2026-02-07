package com.tribly.repository.trip;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripParticipation;
import com.tribly.domain.trip.TripStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Trip} utility methods.
 * No database required.
 */
class TripTest {

  private Trip trip;

  @BeforeEach
  void setUp() {
    trip = new Trip();
  }

  @Nested
  @DisplayName("addStage")
  class AddStage {

    @Test
    @DisplayName("Should add stage to list")
    void addStage_shouldAddToList() {
      TripStage stage = new TripStage();

      trip.addStage(stage);

      assertEquals(1, trip.getStages().size());
      assertTrue(trip.getStages().contains(stage));
    }

    @Test
    @DisplayName("Should set back-reference to trip")
    void addStage_shouldSetBackReference() {
      TripStage stage = new TripStage();

      trip.addStage(stage);

      assertSame(trip, stage.getTrip());
    }

    @Test
    @DisplayName("Should handle multiple stages")
    void addStage_shouldHandleMultiple() {
      TripStage s1 = new TripStage();
      TripStage s2 = new TripStage();
      TripStage s3 = new TripStage();

      trip.addStage(s1);
      trip.addStage(s2);
      trip.addStage(s3);

      assertEquals(3, trip.getStages().size());
    }
  }

  @Nested
  @DisplayName("addParticipation")
  class AddParticipation {

    @Test
    @DisplayName("Should add participation to list")
    void addParticipation_shouldAddToList() {
      TripParticipation participation = new TripParticipation();

      trip.addParticipation(participation);

      assertEquals(1, trip.getParticipations().size());
      assertTrue(trip.getParticipations().contains(participation));
    }

    @Test
    @DisplayName("Should set back-reference to trip")
    void addParticipation_shouldSetBackReference() {
      TripParticipation participation = new TripParticipation();

      trip.addParticipation(participation);

      assertSame(trip, participation.getTrip());
    }

    @Test
    @DisplayName("Should handle multiple participations")
    void addParticipation_shouldHandleMultiple() {
      TripParticipation p1 = new TripParticipation();
      TripParticipation p2 = new TripParticipation();
      TripParticipation p3 = new TripParticipation();

      trip.addParticipation(p1);
      trip.addParticipation(p2);
      trip.addParticipation(p3);

      assertEquals(3, trip.getParticipations().size());
    }
  }

  @Nested
  @DisplayName("getStageCount")
  class GetStageCount {

    @Test
    @DisplayName("Should return 0 for trip with no stages")
    void getStageCount_noStages_shouldReturnZero() {
      assertEquals(0, trip.getStageCount());
    }

    @Test
    @DisplayName("Should count non-deleted stages")
    void getStageCount_shouldCountNonDeleted() {
      TripStage s1 = new TripStage();
      TripStage s2 = new TripStage();
      trip.addStage(s1);
      trip.addStage(s2);

      assertEquals(2, trip.getStageCount());
    }

    @Test
    @DisplayName("Should exclude deleted stages")
    void getStageCount_shouldExcludeDeleted() {
      TripStage active = new TripStage();
      TripStage deleted = new TripStage();
      deleted.setDeleted(true);
      trip.addStage(active);
      trip.addStage(deleted);

      assertEquals(1, trip.getStageCount());
    }

    @Test
    @DisplayName("Should return 0 when all stages are deleted")
    void getStageCount_allDeleted_shouldReturnZero() {
      TripStage s1 = new TripStage();
      TripStage s2 = new TripStage();
      s1.setDeleted(true);
      s2.setDeleted(true);
      trip.addStage(s1);
      trip.addStage(s2);

      assertEquals(0, trip.getStageCount());
    }
  }

  @Nested
  @DisplayName("getParticipantCount")
  class GetParticipantCount {

    @Test
    @DisplayName("Should return 0 for trip with no participations")
    void getParticipantCount_noParticipations_shouldReturnZero() {
      assertEquals(0, trip.getParticipantCount());
    }

    @Test
    @DisplayName("Should count non-deleted participations")
    void getParticipantCount_shouldCountNonDeleted() {
      TripParticipation p1 = new TripParticipation();
      TripParticipation p2 = new TripParticipation();
      trip.addParticipation(p1);
      trip.addParticipation(p2);

      assertEquals(2, trip.getParticipantCount());
    }
  }
}
