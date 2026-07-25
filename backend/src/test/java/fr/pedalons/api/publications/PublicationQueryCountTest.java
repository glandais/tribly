package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.util.QueryStats;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Read-query budget for the publication list endpoints.
 *
 * <p>The assertion that matters here is a <b>shape</b> assertion, not a magic number: the same
 * endpoint is measured over a small page and a large page, and the SELECT count must stay flat. A
 * fixed "must be under N queries" bound rots — it passes for the wrong reason once the fixture
 * changes, and it says nothing about whether the count grows per row. Comparing two page sizes says
 * exactly the thing we care about: <i>does serving 10x the rows cost 10x the queries?</i>
 *
 * <p>Absolute worst-case numbers for every endpoint in the suite are collected automatically by
 * {@link fr.pedalons.util.QueryCountFilter} and printed by {@link
 * fr.pedalons.util.QueryCountReport} at the end of the run (also written to {@code
 * target/query-count-report.txt}).
 *
 * <p>Run locally with: {@code mvn test -Dtest=PublicationQueryCountTest}.
 */
@QuarkusTest
class PublicationQueryCountTest extends AbstractResourceTest {

  private static final int SMALL_PAGE = 3;
  private static final int LARGE_PAGE = 30;

  /**
   * Headroom between the small-page and large-page measurements. Lazy associations are batch-loaded
   * ({@code quarkus.hibernate-orm.fetch.batch-size}), so going from 3 to 30 rows may add at most one
   * extra batch round-trip per batched association — a handful of queries, never 27 of them.
   */
  private static final long MAX_GROWTH = 8;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  /**
   * Seeds rides that exercise the full {@code RideDto} mapping: every ride has groups, every group
   * has participations, every participation has a distinct user. A ride with no participants would
   * never touch the association walk that the DTO actually performs in production.
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

  private QueryStats.Counters measurePage(String label, String path, int size) {
    return queryStats.measureAll(
        label,
        () ->
            given()
                .auth()
                .oauth2(getAccessToken(USER1))
                .when()
                .get(path + (path.contains("?") ? "&" : "?") + "size=" + size)
                .then()
                .statusCode(200));
  }

  /**
   * Measures {@code path} at {@link #SMALL_PAGE} and {@link #LARGE_PAGE} rows and fails if the
   * statement count grew more than {@link #MAX_GROWTH}.
   */
  private void assertFlatQueryCount(String name, String path) {
    seedRides(LARGE_PAGE);

    QueryStats.Counters small = measurePage(name + " [" + SMALL_PAGE + " rows]", path, SMALL_PAGE);
    QueryStats.Counters large = measurePage(name + " [" + LARGE_PAGE + " rows]", path, LARGE_PAGE);

    long growth = large.statements() - small.statements();
    assertTrue(
        growth <= MAX_GROWTH,
        () ->
            "N+1 on "
                + name
                + ": serving "
                + LARGE_PAGE
                + " rows cost "
                + large.statements()
                + " SQL statements vs "
                + small.statements()
                + " for "
                + SMALL_PAGE
                + " rows (+"
                + growth
                + ", budget +"
                + MAX_GROWTH
                + "). The query count must not grow with the number of rows —"
                + " batch-fetch or join-fetch the association being walked per row."
                + " Entities hydrated: "
                + small.entityLoads()
                + " -> "
                + large.entityLoads());
  }

  @Test
  void listTeamRides_queryCountDoesNotScaleWithRowCount() {
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/publications?type=RIDE",
        "/api/teams/" + team1Slug + "/publications?type=RIDE");
  }

  @Test
  void listAllPublications_queryCountDoesNotScaleWithRowCount() {
    assertFlatQueryCount("GET /api/publications", "/api/publications");
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
        growth <= MAX_GROWTH,
        () ->
            "N+1 on the ride detail endpoint: 1 participant cost "
                + smallCounters.statements()
                + " SQL statements, 30 participants cost "
                + largeCounters.statements()
                + " (+"
                + growth
                + ", budget +"
                + MAX_GROWTH
                + ")");
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

  /** The same list seen by an anonymous visitor — a different query shape (no UserTeam join). */
  @Test
  void listAllPublicationsAnonymous_queryCountDoesNotScaleWithRowCount() {
    seedRides(LARGE_PAGE);

    QueryStats.Counters small =
        queryStats.measureAll(
            "GET /api/publications anonymous [" + SMALL_PAGE + " rows]",
            () ->
                given().when().get("/api/publications?size=" + SMALL_PAGE).then().statusCode(200));
    QueryStats.Counters large =
        queryStats.measureAll(
            "GET /api/publications anonymous [" + LARGE_PAGE + " rows]",
            () ->
                given().when().get("/api/publications?size=" + LARGE_PAGE).then().statusCode(200));

    long growth = large.statements() - small.statements();
    assertTrue(
        growth <= MAX_GROWTH,
        () ->
            "N+1 on the anonymous publication feed: "
                + small.statements()
                + " -> "
                + large.statements()
                + " statements (+"
                + growth
                + ", budget +"
                + MAX_GROWTH
                + ")");
  }
}
