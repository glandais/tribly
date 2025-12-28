package com.tribly.domain.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.repository.RideGroupRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideGroupRepositoryTest {

  @Inject RideGroupRepository rideGroupRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;
  private Ride ride;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    ride =
        dataService.createRide(
            team,
            user,
            "Test Ride",
            "test-ride",
            LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC));
  }

  @Test
  void findByIdAndRide_shouldReturnGroupWhenExists() {
    RideGroup group = dataService.createRideGroup(user, ride, "Fast Group");

    Optional<RideGroup> result = rideGroupRepository.findByIdAndRide(group.getId(), ride.getId());

    assertTrue(result.isPresent());
    assertEquals("Fast Group", result.get().getName());
  }

  @Test
  void findByIdAndRide_shouldReturnEmptyForWrongRide() {
    RideGroup group = dataService.createRideGroup(user, ride, "Fast Group");
    Ride otherRide =
        dataService.createRide(
            team,
            user,
            "Other Ride",
            "other-ride",
            LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));

    Optional<RideGroup> result =
        rideGroupRepository.findByIdAndRide(group.getId(), otherRide.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByIdAndRide_shouldIgnoreDeletedGroups() {
    RideGroup group = dataService.createRideGroup(user, ride, "Deleted Group");
    dataService.deleteRideGroup(group);

    Optional<RideGroup> result = rideGroupRepository.findByIdAndRide(group.getId(), ride.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByRide_shouldReturnAllGroupsForRide() {
    dataService.createRideGroup(user, ride, "Group 1");
    dataService.createRideGroup(user, ride, "Group 2");
    dataService.createRideGroup(user, ride, "Group 3");

    List<RideGroup> result = rideGroupRepository.findByRide(ride.getId());

    assertEquals(3, result.size());
  }

  @Test
  void findByRide_shouldOrderBySortOrder() {
    dataService.createRideGroup(user, ride, "Third", 3);
    dataService.createRideGroup(user, ride, "First", 1);
    dataService.createRideGroup(user, ride, "Second", 2);

    List<RideGroup> result = rideGroupRepository.findByRide(ride.getId());

    assertEquals(3, result.size());
    assertEquals("First", result.get(0).getName());
    assertEquals("Second", result.get(1).getName());
    assertEquals("Third", result.get(2).getName());
  }

  @Test
  void findByRide_shouldIgnoreDeletedGroups() {
    dataService.createRideGroup(user, ride, "Visible Group");
    RideGroup deletedGroup = dataService.createRideGroup(user, ride, "Deleted Group");
    dataService.deleteRideGroup(deletedGroup);

    List<RideGroup> result = rideGroupRepository.findByRide(ride.getId());

    assertEquals(1, result.size());
    assertEquals("Visible Group", result.getFirst().getName());
  }

  @Test
  void findByRide_shouldReturnEmptyForRideWithoutGroups() {
    Ride emptyRide =
        dataService.createRide(
            team,
            user,
            "Empty Ride",
            "empty-ride",
            LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));

    List<RideGroup> result = rideGroupRepository.findByRide(emptyRide.getId());

    assertEquals(0, result.size());
  }
}
