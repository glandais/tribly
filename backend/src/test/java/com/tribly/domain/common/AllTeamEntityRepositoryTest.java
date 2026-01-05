package com.tribly.domain.common;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.AllTeamEntityRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamPage;
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
 * Tests for {@link AllTeamEntityRepository}.
 * Verifies that find() returns all TeamEntity types: Rides, Posts, and Routes.
 */
@QuarkusTest
class AllTeamEntityRepositoryTest {

  @Inject AllTeamEntityRepository teamEntityRepository;
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
    @DisplayName("Should return rides (plus TeamPage from team creation)")
    void find_shouldReturnRides() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<TeamEntity> result = teamEntityRepository.find(query);

      // Team creation also creates a TeamPage (about page)
      assertEquals(2, result.total());
      long rideCount = result.items().stream().filter(e -> e instanceof Ride).count();
      assertEquals(1, rideCount);
    }

    @Test
    @DisplayName("Should return posts (plus TeamPage from team creation)")
    void find_shouldReturnPosts() {
      dataService.createPost(team, user, "Test Post", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<TeamEntity> result = teamEntityRepository.find(query);

      // Team creation also creates a TeamPage (about page)
      assertEquals(2, result.total());
      long postCount = result.items().stream().filter(e -> e instanceof Post).count();
      assertEquals(1, postCount);
    }

    @Test
    @DisplayName("Should return routes (plus TeamPage from team creation)")
    void find_shouldReturnRoutes() {
      dataService.createRoute(team, user, "Test Route", Visibility.PUBLIC);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<TeamEntity> result = teamEntityRepository.find(query);

      // Team creation also creates a TeamPage (about page)
      assertEquals(2, result.total());
      long routeCount = result.items().stream().filter(e -> e instanceof Route).count();
      assertEquals(1, routeCount);
    }

    @Test
    @DisplayName("Should return all entity types together (including TeamPage)")
    void find_shouldReturnAllEntityTypes() {
      dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.createPost(team, user, "Test Post", now.plus(1, ChronoUnit.HOURS));
      dataService.createRoute(team, user, "Test Route", Visibility.PUBLIC);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<TeamEntity> result = teamEntityRepository.find(query);

      // 1 TeamPage (about) + 1 Ride + 1 Post + 1 Route = 4
      assertEquals(4, result.total());
      long teamPageCount = result.items().stream().filter(e -> e instanceof TeamPage).count();
      long rideCount = result.items().stream().filter(e -> e instanceof Ride).count();
      long postCount = result.items().stream().filter(e -> e instanceof Post).count();
      long routeCount = result.items().stream().filter(e -> e instanceof Route).count();
      assertEquals(1, teamPageCount);
      assertEquals(1, rideCount);
      assertEquals(1, postCount);
      assertEquals(1, routeCount);
    }

    @Test
    @DisplayName("Should not return deleted entities")
    void find_shouldNotReturnDeleted() {
      Ride ride = dataService.createRide(team, user, "Test Ride", "test-ride", now);
      dataService.createPost(team, user, "Test Post", now);
      dataService.createRoute(team, user, "Test Route", Visibility.PUBLIC);
      dataService.deleteRide(ride);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<TeamEntity> result = teamEntityRepository.find(query);

      // 1 TeamPage (about) + 1 Post + 1 Route = 3 (Ride deleted)
      assertEquals(3, result.total());
    }

    @Test
    @DisplayName("Should not return draft entities for anonymous users")
    void find_shouldNotReturnDraftForAnonymous() {
      dataService.createRide(team, user, "Published Ride", "published-ride", now, Status.PUBLISHED);
      dataService.createRide(team, user, "Draft Ride", "draft-ride", now, Status.DRAFT);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<TeamEntity> result = teamEntityRepository.find(query);

      // 1 TeamPage (about) + 1 Published Ride = 2 (Draft not included)
      assertEquals(2, result.total());
      long rideCount = result.items().stream().filter(e -> e instanceof Ride).count();
      assertEquals(1, rideCount);
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
      TriblyPage<TeamEntity> result = teamEntityRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(6, result.total());
    }
  }

  @Nested
  @DisplayName("getTypeName")
  class GetTypeName {

    @Test
    @DisplayName("Should return TeamEntity as type name")
    void getTypeName_shouldReturnTeamEntity() {
      assertEquals("TeamEntity", teamEntityRepository.getEntityType().getTypeName());
    }
  }
}
