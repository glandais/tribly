package com.tribly.service.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.repository.RideRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RidePublishSchedulerTest {

  @Inject RidePublishScheduler ridePublishScheduler;
  @Inject RideRepository rideRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Test
  void autoPublishRides_shouldPublishRidesWithPastPublishAt() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Auto Publish",
            "auto-publish",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));

    ridePublishScheduler.autoPublishRides();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }

  @Test
  void autoPublishRides_shouldNotPublishRidesWithFuturePublishAt() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Future Publish",
            "future-publish",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().plusSeconds(3600));

    ridePublishScheduler.autoPublishRides();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.DRAFT, updated.getStatus());
    assertNotNull(updated.getPublishAt());
  }

  @Test
  void autoPublishRides_shouldNotPublishAlreadyPublishedRides() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Already Published",
            "already-published",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.PUBLISHED,
            Instant.now().minusSeconds(3600));

    ridePublishScheduler.autoPublishRides();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
  }

  @Test
  void autoPublishRides_shouldNotPublishCancelledRides() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Cancelled",
            "cancelled",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.CANCELLED,
            Instant.now().minusSeconds(3600));

    ridePublishScheduler.autoPublishRides();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.CANCELLED, updated.getStatus());
  }

  @Test
  void autoPublishRides_shouldHandleMultipleRides() {
    Ride ride1 =
        dataService.createRide(
            team,
            user,
            "Ride 1",
            "ride-1",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(7200));
    Ride ride2 =
        dataService.createRide(
            team,
            user,
            "Ride 2",
            "ride-2",
            LocalDate.of(2025, 6, 16).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));
    Ride ride3 =
        dataService.createRide(
            team,
            user,
            "Ride 3",
            "ride-3",
            LocalDate.of(2025, 6, 17).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(1800));

    ridePublishScheduler.autoPublishRides();

    Ride updated1 = rideRepository.findById(ride1.getId());
    Ride updated2 = rideRepository.findById(ride2.getId());
    Ride updated3 = rideRepository.findById(ride3.getId());

    assertEquals(Status.PUBLISHED, updated1.getStatus());
    assertEquals(Status.PUBLISHED, updated2.getStatus());
    assertEquals(Status.PUBLISHED, updated3.getStatus());
    assertNull(updated1.getPublishAt());
    assertNull(updated2.getPublishAt());
    assertNull(updated3.getPublishAt());
  }

  @Test
  void autoPublishRides_shouldDoNothingWhenNoRides() {
    ridePublishScheduler.autoPublishRides();

    // Should not throw exception
    assertTrue(true);
  }

  @Test
  void autoPublishRides_shouldNotPublishRidesWithoutPublishAt() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "No PublishAt",
            "no-publishat",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Status.DRAFT);

    ridePublishScheduler.autoPublishRides();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.DRAFT, updated.getStatus());
  }

  @Test
  void autoPublishRides_shouldPublishAtExactPublishTime() {
    Instant publishTime = Instant.now();
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Exact Time",
            "exact-time",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            publishTime);

    // Wait a moment to ensure we're past publishTime
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    ridePublishScheduler.autoPublishRides();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }
}
