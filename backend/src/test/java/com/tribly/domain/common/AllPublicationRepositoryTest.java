package com.tribly.domain.common;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.AllPublicationRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AllPublicationRepository}.
 * Verifies that find() returns both Rides and Posts (but not Routes).
 */
@QuarkusTest
class AllPublicationRepositoryTest {

  @Inject AllPublicationRepository publicationRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;
  private Team team;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    now = Instant.now();
  }

  @Nested
  @DisplayName("find")
  class Find {

    @Test
    @DisplayName("Should return rides")
    void find_shouldReturnRides() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertTrue(result.items().getFirst() instanceof Ride);
    }

    @Test
    @DisplayName("Should return posts")
    void find_shouldReturnPosts() {
      dataService.createPost(team, user, "Test Post", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertTrue(result.items().getFirst() instanceof Post);
    }

    @Test
    @DisplayName("Should return both rides and posts")
    void find_shouldReturnBothRidesAndPosts() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.createPost(team, user, "Test Post", now.plus(1, ChronoUnit.HOURS));

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.total());
      long rideCount = result.items().stream().filter(p -> p instanceof Ride).count();
      long postCount = result.items().stream().filter(p -> p instanceof Post).count();
      assertEquals(1, rideCount);
      assertEquals(1, postCount);
    }

    @Test
    @DisplayName("Should not return routes")
    void find_shouldNotReturnRoutes() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.createPost(team, user, "Test Post", now);
      dataService.createRoute(team, user, "Test Route", Visibility.PUBLIC);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.total()); // Only Ride and Post
    }

    @Test
    @DisplayName("Should not return deleted publications")
    void find_shouldNotReturnDeleted() {
      Ride ride = dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.createPost(team, user, "Test Post", now);
      dataService.deleteRide(ride);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertTrue(result.items().getFirst() instanceof Post);
    }

    @Test
    @DisplayName("Should not return draft publications for anonymous users")
    void find_shouldNotReturnDraftForAnonymous() {
      dataService.createRide(team, user, "Published Ride", "published-ride", now, Status.PUBLISHED);
      dataService.createRide(team, user, "Draft Ride", "draft-ride", now, Status.DRAFT);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Published Ride", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Pagination")
  class Pagination {

    @Test
    @DisplayName("Should support pagination")
    void find_shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createPost(team, user, "Post " + i, now.plus(i, ChronoUnit.HOURS));
      }

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 2);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(5, result.total());
    }
  }
}
