package com.tribly.repository.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Ride} utility methods.
 * No database required.
 */
class RideTest {

  private Ride ride;

  @BeforeEach
  void setUp() {
    ride = new Ride();
  }

  @Nested
  @DisplayName("addGroup")
  class AddGroup {

    @Test
    @DisplayName("Should add group to list")
    void addGroup_shouldAddToList() {
      RideGroup group = new RideGroup();

      ride.addGroup(group);

      assertEquals(1, ride.getGroups().size());
      assertTrue(ride.getGroups().contains(group));
    }

    @Test
    @DisplayName("Should set back-reference to ride")
    void addGroup_shouldSetBackReference() {
      RideGroup group = new RideGroup();

      ride.addGroup(group);

      assertSame(ride, group.getRide());
    }

    @Test
    @DisplayName("Should handle multiple groups")
    void addGroup_shouldHandleMultiple() {
      RideGroup g1 = new RideGroup();
      RideGroup g2 = new RideGroup();
      RideGroup g3 = new RideGroup();

      ride.addGroup(g1);
      ride.addGroup(g2);
      ride.addGroup(g3);

      assertEquals(3, ride.getGroups().size());
    }
  }

  @Nested
  @DisplayName("getGroupCount")
  class GetGroupCount {

    @Test
    @DisplayName("Should return 0 for ride with no groups")
    void getGroupCount_noGroups_shouldReturnZero() {
      assertEquals(0, ride.getGroupCount());
    }

    @Test
    @DisplayName("Should count non-deleted groups")
    void getGroupCount_shouldCountNonDeleted() {
      RideGroup g1 = new RideGroup();
      RideGroup g2 = new RideGroup();
      ride.addGroup(g1);
      ride.addGroup(g2);

      assertEquals(2, ride.getGroupCount());
    }
  }

  @Nested
  @DisplayName("getParticipantCount")
  class GetParticipantCount {

    @Test
    @DisplayName("Should return 0 for ride with no groups")
    void getParticipantCount_noGroups_shouldReturnZero() {
      assertEquals(0, ride.getParticipantCount());
    }

    @Test
    @DisplayName("Should return 0 for ride with empty groups")
    void getParticipantCount_emptyGroups_shouldReturnZero() {
      ride.addGroup(new RideGroup());
      ride.addGroup(new RideGroup());

      assertEquals(0, ride.getParticipantCount());
    }

    @Test
    @DisplayName("Should sum participants across groups")
    void getParticipantCount_shouldSumAcrossGroups() {
      RideGroup g1 = new RideGroup();
      g1.addParticipation(new RideParticipation());
      g1.addParticipation(new RideParticipation());

      RideGroup g2 = new RideGroup();
      g2.addParticipation(new RideParticipation());

      ride.addGroup(g1);
      ride.addGroup(g2);

      assertEquals(3, ride.getParticipantCount());
    }
  }
}
