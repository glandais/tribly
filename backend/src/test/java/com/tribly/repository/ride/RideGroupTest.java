package com.tribly.repository.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RideGroup} utility methods.
 * No database required.
 */
class RideGroupTest {

  private RideGroup group;

  @BeforeEach
  void setUp() {
    group = new RideGroup();
    group.setName("Test Group");
  }

  @Nested
  @DisplayName("addParticipation")
  class AddParticipation {

    @Test
    @DisplayName("Should add participation to list")
    void addParticipation_shouldAddToList() {
      RideParticipation participation = new RideParticipation();

      group.addParticipation(participation);

      assertEquals(1, group.getParticipations().size());
      assertTrue(group.getParticipations().contains(participation));
    }

    @Test
    @DisplayName("Should set back-reference to group")
    void addParticipation_shouldSetBackReference() {
      RideParticipation participation = new RideParticipation();

      group.addParticipation(participation);

      assertSame(group, participation.getRideGroup());
    }

    @Test
    @DisplayName("Should handle multiple participations")
    void addParticipation_shouldHandleMultiple() {
      RideParticipation p1 = new RideParticipation();
      RideParticipation p2 = new RideParticipation();
      RideParticipation p3 = new RideParticipation();

      group.addParticipation(p1);
      group.addParticipation(p2);
      group.addParticipation(p3);

      assertEquals(3, group.getParticipations().size());
    }
  }

  @Nested
  @DisplayName("getCurrentParticipants")
  class GetCurrentParticipants {

    @Test
    @DisplayName("Should return 0 for empty group")
    void getCurrentParticipants_emptyGroup_shouldReturnZero() {
      assertEquals(0, group.getCurrentParticipants());
    }

    @Test
    @DisplayName("Should count non-deleted participations")
    void getCurrentParticipants_shouldCountNonDeleted() {
      RideParticipation p1 = new RideParticipation();
      RideParticipation p2 = new RideParticipation();
      group.addParticipation(p1);
      group.addParticipation(p2);

      assertEquals(2, group.getCurrentParticipants());
    }
  }

  @Nested
  @DisplayName("hasCapacity")
  class HasCapacity {

    @Test
    @DisplayName("Should return true when maxParticipants is null")
    void hasCapacity_nullMax_shouldReturnTrue() {
      group.setMaxParticipants(null);

      assertTrue(group.hasCapacity());
    }

    @Test
    @DisplayName("Should return true when maxParticipants is null even with participants")
    void hasCapacity_nullMaxWithParticipants_shouldReturnTrue() {
      group.setMaxParticipants(null);
      group.addParticipation(new RideParticipation());
      group.addParticipation(new RideParticipation());

      assertTrue(group.hasCapacity());
    }

    @Test
    @DisplayName("Should return true when below capacity")
    void hasCapacity_belowCapacity_shouldReturnTrue() {
      group.setMaxParticipants(5);
      group.addParticipation(new RideParticipation());
      group.addParticipation(new RideParticipation());

      assertTrue(group.hasCapacity());
    }

    @Test
    @DisplayName("Should return false when at capacity")
    void hasCapacity_atCapacity_shouldReturnFalse() {
      group.setMaxParticipants(2);
      group.addParticipation(new RideParticipation());
      group.addParticipation(new RideParticipation());

      assertFalse(group.hasCapacity());
    }

    @Test
    @DisplayName("Should return true when empty with capacity set")
    void hasCapacity_emptyWithCapacity_shouldReturnTrue() {
      group.setMaxParticipants(10);

      assertTrue(group.hasCapacity());
    }

    @Test
    @DisplayName("Should return false when maxParticipants is 0")
    void hasCapacity_zeroMax_shouldReturnFalse() {
      group.setMaxParticipants(0);

      assertFalse(group.hasCapacity());
    }
  }
}
