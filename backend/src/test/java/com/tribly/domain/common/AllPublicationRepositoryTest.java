package com.tribly.domain.common;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.AllPublicationRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
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

    @Test
    @DisplayName("Should return CANCELLED publications for anonymous users")
    void find_shouldReturnCancelledForAnonymous() {
      dataService.createRide(team, user, "Cancelled Ride", "cancelled-ride", now, Status.CANCELLED);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Cancelled Ride", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should return TEAM visibility publications for authenticated member")
    void find_authenticatedMember_shouldReturnTeamVisibility() {
      User member = dataService.createUser("member@example.com", "Member");
      dataService.addUserToTeam(member, team, TeamRole.MEMBER);
      dataService.createRide(
          team, user, "Team Ride", "team-ride", now, Visibility.TEAM, Status.PUBLISHED);
      dataService.createPost(
          team, user, "Team Post", now.plus(1, ChronoUnit.HOURS), Visibility.TEAM);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(member.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.total());
    }

    @Test
    @DisplayName("Should NOT return TEAM visibility publications for non-member")
    void find_authenticatedNonMember_shouldNotReturnTeamVisibility() {
      User nonMember = dataService.createUser("nonmember@example.com", "Non Member");
      dataService.createRide(
          team, user, "Team Ride", "team-ride", now, Visibility.TEAM, Status.PUBLISHED);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(nonMember.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(0, result.total());
    }

    @Test
    @DisplayName("Should return CANCELLED TEAM publications for authenticated member")
    void find_authenticatedMember_shouldReturnCancelledTeamVisibility() {
      User member = dataService.createUser("member@example.com", "Member");
      dataService.addUserToTeam(member, team, TeamRole.MEMBER);
      dataService.createRide(
          team,
          user,
          "Cancelled Team Ride",
          "cancelled-ride",
          now,
          Visibility.TEAM,
          Status.CANCELLED);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(member.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Cancelled Team Ride", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should NOT return DRAFT publications for regular member")
    void find_authenticatedMember_shouldNotReturnDraft() {
      User member = dataService.createUser("member@example.com", "Member");
      dataService.addUserToTeam(member, team, TeamRole.MEMBER);
      dataService.createRide(
          team, user, "Draft Ride", "draft-ride", now, Visibility.PUBLIC, Status.DRAFT);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(member.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(0, result.total());
    }

    @Test
    @DisplayName("Should return DRAFT publications for organizer")
    void find_organizer_shouldReturnDraft() {
      User organizer = dataService.createUser("organizer@example.com", "Organizer");
      dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
      dataService.createRide(
          team, user, "Draft Ride", "draft-ride", now, Visibility.PUBLIC, Status.DRAFT);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(organizer.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Draft Ride", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should return DRAFT publications for admin")
    void find_admin_shouldReturnDraft() {
      User admin = dataService.createUser("admin@example.com", "Admin");
      dataService.addUserToTeam(admin, team, TeamRole.ADMIN);
      dataService.createRide(
          team, user, "Draft Ride", "draft-ride", now, Visibility.PUBLIC, Status.DRAFT);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(admin.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Draft Ride", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Organizer should NOT see DRAFT from other teams")
    void find_organizer_shouldNotSeeDraftFromOtherTeams() {
      User organizer = dataService.createUser("organizer@example.com", "Organizer");
      dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);

      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createRide(
          otherTeam, user, "Other Draft", "other-draft", now, Visibility.PUBLIC, Status.DRAFT);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(organizer.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(0, result.total());
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

  @Nested
  @DisplayName("Enable Trips Filtering")
  class EnableTripsFiltering {

    @Test
    @DisplayName("Should return Trip in publications when enableTrips is true")
    void find_shouldReturnTripWhenEnableTripsTrue() {
      team.setEnableTrips(true);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Summer Trip", now);
      dataService.createPost(team, user, "Test Post", now.plus(1, ChronoUnit.HOURS));

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.total());
      long tripCount = result.items().stream().filter(p -> p instanceof Trip).count();
      assertEquals(1, tripCount);
    }

    @Test
    @DisplayName("Should NOT return Trip in publications when enableTrips is false")
    void find_shouldNotReturnTripWhenEnableTripsFalse() {
      team.setEnableTrips(false);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Hidden Trip", now);
      dataService.createPost(team, user, "Test Post", now.plus(1, ChronoUnit.HOURS));

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertTrue(result.items().getFirst() instanceof Post);
    }

    @Test
    @DisplayName("Should still return Rides and Posts when enableTrips is false")
    void find_shouldStillReturnRidesAndPostsWhenEnableTripsFalse() {
      team.setEnableTrips(false);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Hidden Trip", now);
      dataService.createRide(team, user, "Test Ride", "test-ride", now.plus(1, ChronoUnit.HOURS));
      dataService.createPost(team, user, "Test Post", now.plus(2, ChronoUnit.HOURS));

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.total());
      long rideCount = result.items().stream().filter(p -> p instanceof Ride).count();
      long postCount = result.items().stream().filter(p -> p instanceof Post).count();
      long tripCount = result.items().stream().filter(p -> p instanceof Trip).count();
      assertEquals(1, rideCount);
      assertEquals(1, postCount);
      assertEquals(0, tripCount);
    }

    @Test
    @DisplayName("Should filter Trips by enableTrips across multiple teams in publications")
    void find_shouldFilterTripsAcrossMultipleTeamsInPublications() {
      Team enabledTeam =
          dataService.createTeam(user, "Enabled Team", "enabled-team", Visibility.PUBLIC);
      Team disabledTeam =
          dataService.createTeam(user, "Disabled Team", "disabled-team", Visibility.PUBLIC);
      disabledTeam.setEnableTrips(false);
      dataService.updateTeam(disabledTeam);

      dataService.createTrip(enabledTeam, user, "Enabled Trip", now);
      dataService.createTrip(disabledTeam, user, "Disabled Trip", now.plus(1, ChronoUnit.HOURS));
      dataService.createPost(
          disabledTeam, user, "Post from Disabled", now.plus(2, ChronoUnit.HOURS));

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.total());
      long tripCount = result.items().stream().filter(p -> p instanceof Trip).count();
      long postCount = result.items().stream().filter(p -> p instanceof Post).count();
      assertEquals(1, tripCount); // Only from enabledTeam
      assertEquals(1, postCount); // From disabledTeam (posts are not affected)
    }

    @Test
    @DisplayName("Should return TEAM Trip for authenticated member when enableTrips is true")
    void find_authenticatedMember_shouldReturnTeamTripWhenEnableTripsTrue() {
      User member = dataService.createUser("member@example.com", "Member");
      dataService.addUserToTeam(member, team, TeamRole.MEMBER);
      team.setEnableTrips(true);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Team Trip", now, Visibility.TEAM);
      dataService.createPost(
          team, user, "Team Post", now.plus(1, ChronoUnit.HOURS), Visibility.TEAM);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(member.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(2, result.total());
      long tripCount = result.items().stream().filter(p -> p instanceof Trip).count();
      assertEquals(1, tripCount);
    }

    @Test
    @DisplayName("Should NOT return TEAM Trip for authenticated member when enableTrips is false")
    void find_authenticatedMember_shouldNotReturnTeamTripWhenEnableTripsFalse() {
      User member = dataService.createUser("member@example.com", "Member");
      dataService.addUserToTeam(member, team, TeamRole.MEMBER);
      team.setEnableTrips(false);
      dataService.updateTeam(team);
      dataService.createTrip(team, user, "Team Trip", now, Visibility.TEAM);
      dataService.createPost(
          team, user, "Team Post", now.plus(1, ChronoUnit.HOURS), Visibility.TEAM);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(member.getId(), null, null, null, null, null, null, 0, 10);
      TriblyPage<Publication> result = publicationRepository.find(query);

      assertEquals(1, result.total());
      assertTrue(result.items().getFirst() instanceof Post);
    }
  }
}
