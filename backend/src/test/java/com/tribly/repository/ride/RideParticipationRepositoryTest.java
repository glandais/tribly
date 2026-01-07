package com.tribly.repository.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
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
class RideParticipationRepositoryTest {

  @Inject RideParticipationRepository participationRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user1;
  private User user2;
  private Ride ride;
  private RideGroup group;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user1 = dataService.createUser("user1@example.com", "User One");
    team = dataService.createTeam(user1, "Test Team", "test-team", Visibility.PUBLIC);
    user2 = dataService.createUser("user2@example.com", "User Two");
    ride =
        dataService.createRide(
            team,
            user1,
            "Test Ride",
            "test-ride",
            LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC));
    group = dataService.createRideGroup(user1, ride, "Fast Group");
  }

  @Test
  void findByUserAndGroup_shouldReturnParticipationWhenExists() {
    dataService.createParticipation(group, user1);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndGroup(user1.getId(), group.getId());

    assertTrue(result.isPresent());
    assertEquals(user1.getId(), result.get().getUser().getId());
    assertEquals(group.getId(), result.get().getRideGroup().getId());
  }

  @Test
  void findByUserAndGroup_shouldReturnEmptyWhenNotExists() {
    Optional<RideParticipation> result =
        participationRepository.findByUserAndGroup(user1.getId(), group.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndGroup_shouldReturnEmptyForWrongUser() {
    dataService.createParticipation(group, user1);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndGroup(user2.getId(), group.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndGroup_shouldIgnoreDeletedParticipations() {
    RideParticipation participation = dataService.createParticipation(group, user1);
    dataService.deleteParticipation(participation);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndGroup(user1.getId(), group.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndRide_shouldReturnParticipationWhenExists() {
    dataService.createParticipation(group, user1);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRide(user1.getId(), ride.getId());

    assertTrue(result.isPresent());
    assertEquals(user1.getId(), result.get().getUser().getId());
  }

  @Test
  void findByUserAndRide_shouldReturnEmptyWhenNotExists() {
    Optional<RideParticipation> result =
        participationRepository.findByUserAndRide(user1.getId(), ride.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndRide_shouldFindParticipationInAnyGroup() {
    RideGroup group2 = dataService.createRideGroup(user1, ride, "Slow Group");
    dataService.createParticipation(group2, user1);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRide(user1.getId(), ride.getId());

    assertTrue(result.isPresent());
    assertEquals("Slow Group", result.get().getRideGroup().getName());
  }

  @Test
  void findByUserAndRide_shouldIgnoreDeletedParticipations() {
    RideParticipation participation = dataService.createParticipation(group, user1);
    dataService.deleteParticipation(participation);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRide(user1.getId(), ride.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndRide_shouldReturnEmptyForDifferentRide() {
    dataService.createParticipation(group, user1);
    Ride otherRide =
        dataService.createRide(
            team,
            user1,
            "Other Ride",
            "other-ride",
            LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRide(user1.getId(), otherRide.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndRideIncludingDeleted_shouldReturnParticipationWhenExists() {
    dataService.createParticipation(group, user1);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRideIncludingDeleted(user1.getId(), ride.getId());

    assertTrue(result.isPresent());
    assertEquals(user1.getId(), result.get().getUser().getId());
  }

  @Test
  void findByUserAndRideIncludingDeleted_shouldReturnEmptyWhenNotExists() {
    Optional<RideParticipation> result =
        participationRepository.findByUserAndRideIncludingDeleted(user1.getId(), ride.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndRideIncludingDeleted_shouldReturnDeletedParticipation() {
    RideParticipation participation = dataService.createParticipation(group, user1);
    dataService.deleteParticipation(participation);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRideIncludingDeleted(user1.getId(), ride.getId());

    assertTrue(result.isPresent());
    assertTrue(result.get().isDeleted());
    assertEquals(user1.getId(), result.get().getUser().getId());
  }

  @Test
  void findByUserAndRideIncludingDeleted_shouldReturnEmptyForWrongUser() {
    dataService.createParticipation(group, user1);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRideIncludingDeleted(user2.getId(), ride.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndRideIncludingDeleted_shouldReturnEmptyForDifferentRide() {
    dataService.createParticipation(group, user1);
    Ride otherRide =
        dataService.createRide(
            team,
            user1,
            "Other Ride",
            "other-ride",
            LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRideIncludingDeleted(user1.getId(), otherRide.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserAndRideIncludingDeleted_shouldFindParticipationInAnyGroup() {
    RideGroup group2 = dataService.createRideGroup(user1, ride, "Slow Group");
    dataService.createParticipation(group2, user1);

    Optional<RideParticipation> result =
        participationRepository.findByUserAndRideIncludingDeleted(user1.getId(), ride.getId());

    assertTrue(result.isPresent());
    assertEquals("Slow Group", result.get().getRideGroup().getName());
  }
}
