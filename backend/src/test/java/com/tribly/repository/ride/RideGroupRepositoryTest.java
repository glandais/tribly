package com.tribly.repository.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideGroupRepositoryTest extends AbstractBaseTest {

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
}
