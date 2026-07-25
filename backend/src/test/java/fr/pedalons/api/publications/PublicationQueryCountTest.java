package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.trip.Trip;
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
