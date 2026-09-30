package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.util.QueryStats;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Database-cost budget for the publication list endpoints. See {@link AbstractQueryCountTest}. */
@QuarkusTest
class PublicationQueryCountTest extends AbstractQueryCountTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  /**
   * Seeds rides that exercise the full {@code RideDto} mapping: every ride has groups, every group
   * has participations, every participation has a distinct user. A ride with no participants would
   * never touch the association walk the DTO actually performs in production, so the test would
   * pass while the endpoint stayed quadratic.
   */
  private void seedRides(int count) {
    Instant base = Instant.now().plus(7, ChronoUnit.DAYS);
    List<User> participants = List.of(user1, user2, user3, user4, user5);
    for (int i = 0; i < count; i++) {
      Ride ride =
          dataService.createRide(
              team1,
              user1,
              "Budget Ride " + i,
              "budget-ride-" + i,
              base.plusSeconds(i),
              Visibility.PUBLIC,
              Status.PUBLISHED);
      for (int g = 0; g < 2; g++) {
        RideGroup group = dataService.createRideGroup(user1, ride, "Group " + g, g);
        for (User participant : participants) {
          dataService.createParticipation(group, participant);
        }
      }
    }
  }

  /** Trips carry stages and participations, the {@code TripDto} equivalent of groups/participants. */
  private void seedTrips(int count) {
    Instant base = Instant.now().plus(7, ChronoUnit.DAYS);
    List<User> participants = List.of(user1, user2, user3, user4, user5);
    for (int i = 0; i < count; i++) {
      Trip trip =
          dataService.createTrip(
              team1, user1, "Budget Trip " + i, base.plusSeconds(i), Visibility.PUBLIC);
      for (int s = 0; s < 3; s++) {
        dataService.createTripStage(user1, trip, "Trip " + i + " Stage " + s, s);
      }
      for (User participant : participants) {
        dataService.createTripParticipation(trip, participant);
      }
    }
  }

  /**
   * docs/LEDGER_*.md API-6: posts by as many distinct authors as rows, half signed by the team — so
   * a per-row walk of {@code createdBy}, or a per-row membership check, would show.
   */
  private void seedPostsByDistinctAuthors(int count) {
    Instant base = Instant.now().minus(1, ChronoUnit.DAYS);
    for (int i = 0; i < count; i++) {
      User author = dataService.createUser("author" + i + "@example.com", "Author " + i);
      Post post = dataService.createPost(team1, author, "Budget Post " + i, base.plusSeconds(i));
      dataService.setPostSignedAsTeam(post, i % 2 == 0);
    }
  }

  @Test
  void listTeamPosts_authorsCostAPageNotARow() {
    seedPostsByDistinctAuthors(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/publications?type=POST",
        asUser1(),
        "/api/teams/" + team1Slug + "/publications?type=POST");
  }

  @Test
  void listTeamPostsAnonymous_authorsCostAPageNotARow() {
    seedPostsByDistinctAuthors(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/publications?type=POST anonymous",
        anonymous(),
        "/api/teams/" + team1Slug + "/publications?type=POST");
  }

  @Test
  void listTeamRides_costDoesNotScaleWithRowCount() {
    seedRides(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/publications?type=RIDE",
        asUser1(),
        "/api/teams/" + team1Slug + "/publications?type=RIDE");
  }

  @Test
  void listAllPublications_costDoesNotScaleWithRowCount() {
    seedRides(LARGE_PAGE);
    assertFlatQueryCount("GET /api/publications", asUser1(), "/api/publications");
  }

  /** The same list seen by an anonymous visitor — a different query shape (no UserTeam join). */
  @Test
  void listAllPublicationsAnonymous_costDoesNotScaleWithRowCount() {
    seedRides(LARGE_PAGE);
    assertFlatQueryCount("GET /api/publications anonymous", anonymous(), "/api/publications");
  }

  @Test
  void listTeamTrips_costDoesNotScaleWithRowCount() {
    seedTrips(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/publications?type=TRIP",
        asUser1(),
        "/api/teams/" + team1Slug + "/publications?type=TRIP");
  }

  /**
   * The ride detail endpoint walks groups -> participations -> user for the full participant list,
   * so its cost must be flat in the number of participants, not one SELECT per participant.
   */
  @Test
  void rideDetail_queryCountDoesNotScaleWithParticipantCount() {
    Instant base = Instant.now().plus(7, ChronoUnit.DAYS);
    List<User> participants = List.of(user1, user2, user3, user4, user5);

    Ride small = dataService.createRide(team1, user1, "Small", "small-ride", base);
    dataService.createParticipation(
        dataService.createRideGroup(user1, small, "Only group", 0), user1);

    Ride large = dataService.createRide(team1, user1, "Large", "large-ride", base.plusSeconds(1));
    for (int g = 0; g < 6; g++) {
      RideGroup group = dataService.createRideGroup(user1, large, "Group " + g, g);
      for (User participant : participants) {
        dataService.createParticipation(group, participant);
      }
    }

    QueryStats.Counters smallCounters =
        measureDetail("GET /api/teams/{teamSlug}/rides/{rideSlug} [1 participant]", "small-ride");
    QueryStats.Counters largeCounters =
        measureDetail("GET /api/teams/{teamSlug}/rides/{rideSlug} [30 participants]", "large-ride");

    long growth = largeCounters.statements() - smallCounters.statements();
    assertTrue(
        growth <= MAX_STATEMENT_GROWTH,
        () ->
            "N+1 on the ride detail endpoint: 1 participant cost "
                + smallCounters.statements()
                + " SQL statements, 30 participants cost "
                + largeCounters.statements()
                + " (+"
                + growth
                + ", budget +"
                + MAX_STATEMENT_GROWTH
                + ")");
  }

  /**
   * Every group of the detail carries its route's thumbnail (ledger API-3). They are resolved for
   * the whole ride in one query, so six groups on six routes must cost what one group on one route
   * does — not one asset walk per group.
   */
  @Test
  void rideDetail_groupRouteThumbnails_costDoesNotScaleWithGroupCount() {
    Instant base = Instant.now().plus(7, ChronoUnit.DAYS);
    Ride small = dataService.createRide(team1, user1, "Small", "small-ride", base);
    seedGroupOnThumbnailedRoute(small, 0);
    Ride large = dataService.createRide(team1, user1, "Large", "large-ride", base.plusSeconds(1));
    for (int g = 0; g < 6; g++) {
      seedGroupOnThumbnailedRoute(large, g);
    }

    QueryStats.Counters smallCounters =
        measureDetail("GET /api/teams/{teamSlug}/rides/{rideSlug} [1 routed group]", "small-ride");
    QueryStats.Counters largeCounters =
        measureDetail("GET /api/teams/{teamSlug}/rides/{rideSlug} [6 routed groups]", "large-ride");

    long growth = largeCounters.statements() - smallCounters.statements();
    assertTrue(
        growth <= MAX_STATEMENT_GROWTH,
        () ->
            "N+1 on the group route thumbnails: 1 group cost "
                + smallCounters.statements()
                + " SQL statements, 6 groups cost "
                + largeCounters.statements()
                + " (+"
                + growth
                + ", budget +"
                + MAX_STATEMENT_GROWTH
                + ")");
  }

  private void seedGroupOnThumbnailedRoute(Ride ride, int index) {
    RideGroup group = dataService.createRideGroup(user1, ride, "Group " + index, index);
    Route route = dataService.createRoute(team1, user1, ride.getName() + " route " + index);
    dataService.attachAsset(route, user1, AssetType.ROUTE_THUMBNAIL_LIGHT, "light.png");
    dataService.attachAsset(route, user1, AssetType.ROUTE_THUMBNAIL_DARK, "dark.png");
    dataService.setRideGroupRoute(group, route);
  }

  private QueryStats.Counters measureDetail(String label, String rideSlug) {
    return queryStats.measureAll(
        label,
        () ->
            given()
                .auth()
                .oauth2(getAccessToken(USER1))
                .when()
                .get("/api/teams/" + team1Slug + "/rides/" + rideSlug)
                .then()
                .statusCode(200));
  }
}
