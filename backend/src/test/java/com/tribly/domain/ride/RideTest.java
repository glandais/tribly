package com.tribly.domain.ride;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("Should exclude deleted groups")
    void getGroupCount_shouldExcludeDeleted() {
      RideGroup active = new RideGroup();
      RideGroup deleted = new RideGroup();
      deleted.setDeleted(true);
      ride.addGroup(active);
      ride.addGroup(deleted);

      assertEquals(1, ride.getGroupCount());
    }

    @Test
    @DisplayName("Should return 0 when all groups are deleted")
    void getGroupCount_allDeleted_shouldReturnZero() {
      RideGroup g1 = new RideGroup();
      RideGroup g2 = new RideGroup();
      g1.setDeleted(true);
      g2.setDeleted(true);
      ride.addGroup(g1);
      ride.addGroup(g2);

      assertEquals(0, ride.getGroupCount());
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

    @Test
    @DisplayName("Should exclude participants from deleted groups")
    void getParticipantCount_shouldExcludeDeletedGroups() {
      RideGroup active = new RideGroup();
      active.addParticipation(new RideParticipation());
      active.addParticipation(new RideParticipation());

      RideGroup deleted = new RideGroup();
      deleted.setDeleted(true);
      deleted.addParticipation(new RideParticipation());
      deleted.addParticipation(new RideParticipation());

      ride.addGroup(active);
      ride.addGroup(deleted);

      assertEquals(2, ride.getParticipantCount());
    }

    @Test
    @DisplayName("Should exclude deleted participations within groups")
    void getParticipantCount_shouldExcludeDeletedParticipations() {
      RideGroup group = new RideGroup();
      RideParticipation active = new RideParticipation();
      RideParticipation deleted = new RideParticipation();
      deleted.setDeleted(true);
      group.addParticipation(active);
      group.addParticipation(deleted);

      ride.addGroup(group);

      assertEquals(1, ride.getParticipantCount());
    }

    @Test
    @DisplayName("Should handle mixed deleted states")
    void getParticipantCount_mixedDeletedStates() {
      // Active group with 2 active, 1 deleted participation
      RideGroup g1 = new RideGroup();
      g1.addParticipation(new RideParticipation());
      g1.addParticipation(new RideParticipation());
      RideParticipation deletedParticipation = new RideParticipation();
      deletedParticipation.setDeleted(true);
      g1.addParticipation(deletedParticipation);

      // Deleted group (should not count any)
      RideGroup g2 = new RideGroup();
      g2.setDeleted(true);
      g2.addParticipation(new RideParticipation());

      // Active group with 1 active participation
      RideGroup g3 = new RideGroup();
      g3.addParticipation(new RideParticipation());

      ride.addGroup(g1);
      ride.addGroup(g2);
      ride.addGroup(g3);

      assertEquals(3, ride.getParticipantCount()); // 2 from g1 + 0 from g2 + 1 from g3
    }

    @Test
    @DisplayName("Should return 0 when all groups are deleted")
    void getParticipantCount_allGroupsDeleted_shouldReturnZero() {
      RideGroup g1 = new RideGroup();
      g1.setDeleted(true);
      g1.addParticipation(new RideParticipation());

      RideGroup g2 = new RideGroup();
      g2.setDeleted(true);
      g2.addParticipation(new RideParticipation());

      ride.addGroup(g1);
      ride.addGroup(g2);

      assertEquals(0, ride.getParticipantCount());
    }
  }
}
