package com.tribly.service.publication;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.repository.post.PostRepository;
import com.tribly.repository.ride.RideRepository;
import com.tribly.repository.trip.TripRepository;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PublicationPublishSchedulerTest extends AbstractBaseTest {

  @Inject PublicationPublishScheduler publishScheduler;
  @Inject RideRepository rideRepository;
  @Inject PostRepository postRepository;
  @Inject TripRepository tripRepository;
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

  // ========== Ride Tests ==========

  @Test
  void autoPublish_shouldPublishRideWithPastPublishAt() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Auto Publish Ride",
            "auto-publish-ride",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }

  @Test
  void autoPublish_shouldNotPublishRideWithFuturePublishAt() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Future Publish Ride",
            "future-publish-ride",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().plusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.DRAFT, updated.getStatus());
    assertNotNull(updated.getPublishAt());
  }

  @Test
  void autoPublish_shouldNotPublishAlreadyPublishedRide() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Already Published Ride",
            "already-published-ride",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.PUBLISHED,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
  }

  @Test
  void autoPublish_shouldNotPublishCancelledRide() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Cancelled Ride",
            "cancelled-ride",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.CANCELLED,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.CANCELLED, updated.getStatus());
  }

  // ========== Post Tests ==========

  @Test
  void autoPublish_shouldPublishPostWithPastPublishAt() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Auto Publish Post",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }

  @Test
  void autoPublish_shouldNotPublishPostWithFuturePublishAt() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Future Publish Post",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().plusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.DRAFT, updated.getStatus());
    assertNotNull(updated.getPublishAt());
  }

  @Test
  void autoPublish_shouldNotPublishAlreadyPublishedPost() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Already Published Post",
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
  }

  @Test
  void autoPublish_shouldNotPublishCancelledPost() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Cancelled Post",
            Instant.now(),
            Visibility.PUBLIC,
            Status.CANCELLED,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.CANCELLED, updated.getStatus());
  }

  @Test
  void autoPublish_shouldUpdatePostDateTimeToPublishAt() {
    Instant originalDateTime = Instant.now().minusSeconds(86400); // yesterday
    Instant publishAt = Instant.now().minusSeconds(3600).truncatedTo(ChronoUnit.MICROS);
    Post post =
        dataService.createPost(
            team,
            user,
            "DateTime Update Post",
            originalDateTime,
            Visibility.PUBLIC,
            Status.DRAFT,
            publishAt);

    publishScheduler.autoPublishPublications();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertEquals(publishAt, updated.getDateTime());
  }

  // ========== Trip Tests ==========

  @Test
  void autoPublish_shouldPublishTripWithPastPublishAt() {
    Trip trip =
        dataService.createTrip(
            team,
            user,
            "Auto Publish Trip",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Trip updated = tripRepository.findById(trip.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }

  @Test
  void autoPublish_shouldNotPublishTripWithFuturePublishAt() {
    Trip trip =
        dataService.createTrip(
            team,
            user,
            "Future Publish Trip",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().plusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Trip updated = tripRepository.findById(trip.getId());
    assertEquals(Status.DRAFT, updated.getStatus());
    assertNotNull(updated.getPublishAt());
  }

  @Test
  void autoPublish_shouldNotPublishAlreadyPublishedTrip() {
    Trip trip =
        dataService.createTrip(
            team,
            user,
            "Already Published Trip",
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Trip updated = tripRepository.findById(trip.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
  }

  @Test
  void autoPublish_shouldNotPublishCancelledTrip() {
    Trip trip =
        dataService.createTrip(
            team,
            user,
            "Cancelled Trip",
            Instant.now(),
            Visibility.PUBLIC,
            Status.CANCELLED,
            Instant.now().minusSeconds(3600));

    publishScheduler.autoPublishPublications();

    Trip updated = tripRepository.findById(trip.getId());
    assertEquals(Status.CANCELLED, updated.getStatus());
  }

  // ========== Mixed Publication Tests ==========

  @Test
  void autoPublish_shouldHandleMultiplePublicationsOfDifferentTypes() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Multi Ride",
            "multi-ride",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(7200));
    Post post =
        dataService.createPost(
            team,
            user,
            "Multi Post",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));
    Trip trip =
        dataService.createTrip(
            team,
            user,
            "Multi Trip",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(1800));

    publishScheduler.autoPublishPublications();

    Ride updatedRide = rideRepository.findById(ride.getId());
    Post updatedPost = postRepository.findById(post.getId());
    Trip updatedTrip = tripRepository.findById(trip.getId());

    assertEquals(Status.PUBLISHED, updatedRide.getStatus());
    assertEquals(Status.PUBLISHED, updatedPost.getStatus());
    assertEquals(Status.PUBLISHED, updatedTrip.getStatus());
    assertNull(updatedRide.getPublishAt());
    assertNull(updatedPost.getPublishAt());
    assertNull(updatedTrip.getPublishAt());
  }

  @Test
  void autoPublish_shouldDoNothingWhenNoPublications() {
    publishScheduler.autoPublishPublications();

    // Should not throw exception
    assertTrue(true);
  }

  @Test
  void autoPublish_shouldNotPublishPublicationsWithoutPublishAt() {
    Ride ride =
        dataService.createRide(
            team,
            user,
            "No PublishAt Ride",
            "no-publishat-ride",
            LocalDate.of(2025, 6, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            Status.DRAFT);
    Post post =
        dataService.createPost(
            team, user, "No PublishAt Post", Instant.now(), Visibility.PUBLIC, Status.DRAFT);
    Trip trip =
        dataService.createTrip(
            team, user, "No PublishAt Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);

    publishScheduler.autoPublishPublications();

    Ride updatedRide = rideRepository.findById(ride.getId());
    Post updatedPost = postRepository.findById(post.getId());
    Trip updatedTrip = tripRepository.findById(trip.getId());

    assertEquals(Status.DRAFT, updatedRide.getStatus());
    assertEquals(Status.DRAFT, updatedPost.getStatus());
    assertEquals(Status.DRAFT, updatedTrip.getStatus());
  }

  @Test
  void autoPublish_shouldPublishAtExactPublishTime() {
    Instant publishTime = Instant.now();
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Exact Time Ride",
            "exact-time-ride",
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

    publishScheduler.autoPublishPublications();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }

  @Test
  void autoPublish_shouldNotUpdateRideDateTimeToPublishAt() {
    Instant originalDateTime =
        LocalDate.of(2025, 6, 15)
            .atTime(10, 0)
            .toInstant(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.MICROS);
    Instant publishAt = Instant.now().minusSeconds(3600);
    Ride ride =
        dataService.createRide(
            team,
            user,
            "Ride DateTime Unchanged",
            "ride-datetime-unchanged",
            originalDateTime,
            Visibility.PUBLIC,
            Status.DRAFT,
            publishAt);

    publishScheduler.autoPublishPublications();

    Ride updated = rideRepository.findById(ride.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    // Ride dateTime should NOT be updated (unlike Post)
    assertEquals(originalDateTime, updated.getDateTime());
  }

  @Test
  void autoPublish_shouldNotUpdateTripDateTimeToPublishAt() {
    Instant originalDateTime = Instant.now().minusSeconds(86400).truncatedTo(ChronoUnit.MICROS);
    Instant publishAt = Instant.now().minusSeconds(3600);
    Trip trip =
        dataService.createTrip(
            team,
            user,
            "Trip DateTime Unchanged",
            originalDateTime,
            Visibility.PUBLIC,
            Status.DRAFT,
            publishAt);

    publishScheduler.autoPublishPublications();

    Trip updated = tripRepository.findById(trip.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    // Trip dateTime should NOT be updated (unlike Post)
    assertEquals(originalDateTime, updated.getDateTime());
  }
}
