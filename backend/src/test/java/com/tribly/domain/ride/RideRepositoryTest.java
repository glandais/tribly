package com.tribly.domain.ride;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideRepositoryTest {

  @Inject RideRepository rideRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    team = dataService.createTeam("Test Team", "test-team", Visibility.PUBLIC);
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Test
  void find_shouldReturnRidesByTeam() {
    dataService.createRide(
        team,
        user,
        "Ride 1",
        "ride-1",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC));
    dataService.createRide(
        team,
        user,
        "Ride 2",
        "ride-2",
        LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));

    TeamEntityQueryBasic query =
        new TeamEntityQueryBasic(null, null, null, null, null, null, 0, 10);
    TriblyPage<Ride> result = rideRepository.find(query);

    assertEquals(2, result.items().size());
    assertEquals(2, result.total());
  }

  @Test
  void find_shouldFilterBySlug() {
    dataService.createRide(
        team,
        user,
        "Ride 1",
        "ride-1",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC));
    dataService.createRide(
        team,
        user,
        "Ride 2",
        "ride-2",
        LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));

    TeamEntityQueryBasic query =
        new TeamEntityQueryBasic(null, null, "ride-1", null, null, null, 0, 10);
    TriblyPage<Ride> result = rideRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("ride-1", result.items().getFirst().getSlug());
  }

  @Test
  void find_shouldFilterByDateRange() {
    dataService.createRide(
        team,
        user,
        "Ride 1",
        "ride-1",
        LocalDate.of(2025, 1, 10).atTime(0, 0).toInstant(ZoneOffset.UTC));
    dataService.createRide(
        team,
        user,
        "Ride 2",
        "ride-2",
        LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));
    dataService.createRide(
        team,
        user,
        "Ride 3",
        "ride-3",
        LocalDate.of(2025, 1, 30).atTime(0, 0).toInstant(ZoneOffset.UTC));

    TeamEntityQueryBasic query =
        new TeamEntityQueryBasic(
            null,
            null,
            null,
            null,
            LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
            LocalDate.of(2025, 1, 25).atTime(0, 0).toInstant(ZoneOffset.UTC),
            0,
            10);
    TriblyPage<Ride> result = rideRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("ride-2", result.items().getFirst().getSlug());
  }

  @Test
  void find_shouldFilterByVisibility() {
    dataService.createRide(
        team,
        user,
        "Public Ride",
        "public-ride",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
        Visibility.PUBLIC);
    dataService.createRide(
        team,
        user,
        "Team Ride",
        "team-ride",
        LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC),
        Visibility.TEAM);

    TeamEntityQueryBasic query =
        new TeamEntityQueryBasic(null, null, null, null, null, null, 0, 10);
    TriblyPage<Ride> result = rideRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("public-ride", result.items().getFirst().getSlug());
  }

  @Test
  void find_shouldFilterByStatus() {
    dataService.createRide(
        team,
        user,
        "Draft Ride",
        "draft-ride",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
        Visibility.PUBLIC,
        Status.DRAFT);
    dataService.createRide(
        team,
        user,
        "Published Ride",
        "published-ride",
        LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC),
        Visibility.PUBLIC,
        Status.PUBLISHED);

    TeamEntityQueryBasic query =
        new TeamEntityQueryBasic(null, null, null, null, null, null, 0, 10);
    TriblyPage<Ride> result = rideRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("published-ride", result.items().getFirst().getSlug());
  }

  @Test
  void find_shouldIgnoreDeletedRides() {
    dataService.createRide(
        team,
        user,
        "Visible Ride",
        "visible",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC));
    Ride deletedRide =
        dataService.createRide(
            team,
            user,
            "Deleted Ride",
            "deleted",
            LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC));
    dataService.deleteRide(deletedRide);

    TeamEntityQueryBasic query =
        new TeamEntityQueryBasic(null, null, null, null, null, null, 0, 10);
    TriblyPage<Ride> result = rideRepository.find(query);

    assertEquals(1, result.items().size());
    assertEquals("visible", result.items().getFirst().getSlug());
  }

  @Test
  void existsByTeamAndSlug_shouldReturnTrueWhenExists() {
    dataService.createRide(
        team,
        user,
        "Test Ride",
        "test-ride",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC));

    boolean exists = rideRepository.existsByTeamAndSlug(team.getId(), "test-ride");

    assertTrue(exists);
  }

  @Test
  void existsByTeamAndSlug_shouldReturnFalseWhenNotExists() {
    boolean exists = rideRepository.existsByTeamAndSlug(team.getId(), "nonexistent");

    assertFalse(exists);
  }

  @Test
  void findRidesToAutoPublish_shouldReturnRidesWithPublishAtInPast() {
    dataService.createRide(
        team,
        user,
        "Auto Publish 1",
        "auto-1",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
        Visibility.PUBLIC,
        Status.DRAFT,
        Instant.now().minusSeconds(3600));

    dataService.createRide(
        team,
        user,
        "Auto Publish 2",
        "auto-2",
        LocalDate.of(2025, 1, 20).atTime(0, 0).toInstant(ZoneOffset.UTC),
        Visibility.PUBLIC,
        Status.DRAFT,
        Instant.now().plusSeconds(3600));

    List<Ride> result = rideRepository.findPublicationsToAutoPublish(Status.DRAFT, Instant.now());

    assertEquals(1, result.size());
    assertEquals("auto-1", result.getFirst().getSlug());
  }

  @Test
  void findRidesToAutoPublish_shouldIgnoreRidesWithoutPublishAt() {
    dataService.createRide(
        team,
        user,
        "No Publish At",
        "no-publish",
        LocalDate.of(2025, 1, 15).atTime(0, 0).toInstant(ZoneOffset.UTC),
        Status.DRAFT);

    List<Ride> result = rideRepository.findPublicationsToAutoPublish(Status.DRAFT, Instant.now());

    assertEquals(0, result.size());
  }
}
