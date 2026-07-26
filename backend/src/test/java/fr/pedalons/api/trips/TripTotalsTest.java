package fr.pedalons.api.trips;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The trip totals a card shows: how far, how much climbing, and until when.
 *
 * <p>They fold over the stages' routes, which a list row does not load — the list path gets them
 * from one aggregate query for the whole page, the detail path folds the stages it already holds.
 * Both are checked here, against the same fixture, because the two must not disagree.
 */
@QuarkusTest
class TripTotalsTest extends AbstractResourceTest {

  private static final String ALL = "/api/publications";

  private Instant day1;
  private Instant day3;
  private Trip trip;

  private String tripDetail() {
    return "/api/teams/" + team1Slug + "/trips/" + trip.getSlug();
  }

  private static String tripRow() {
    return "publications.find { it.name == 'Tour des Alpes' }";
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    day1 = Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
    day3 = day1.plus(2, ChronoUnit.DAYS);

    trip = dataService.createTrip(team1, user1, "Tour des Alpes", day1);

    Route route1 = dataService.createRoute(team1, user1, "Trace 1");
    dataService.setRouteMetrics(route1, 10_000f, 300f);
    Route route2 = dataService.createRoute(team1, user1, "Trace 2");
    dataService.setRouteMetrics(route2, 20_000f, 500f);

    TripStage stage1 = dataService.createTripStage(user1, trip, "Etape 1", 0, day1);
    dataService.setTripStageRoute(stage1, route1);
    TripStage stage2 = dataService.createTripStage(user1, trip, "Etape 2", 1, day3);
    dataService.setTripStageRoute(stage2, route2);

    Place start = dataService.createPlaceWithAddress(team1, user1, "Depart", "1 rue du Depart");
    Place end = dataService.createPlaceWithAddress(team1, user1, "Arrivee", "2 rue de l'Arrivee");
    dataService.setTripStagePlaces(stage1, start, end);
  }

  // ---------------------------------------------------------------- list path (bulk)

  @Test
  void listRow_shouldCarryTheTotalsAndTheEndDate() {
    given()
        .queryParam("type", "TRIP")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(tripRow() + ".stageCount", equalTo(2))
        .body(tripRow() + ".totalDistance", equalTo(30_000f))
        .body(tripRow() + ".totalElevationGain", equalTo(800f))
        .body(tripRow() + ".endDate", notNullValue());
  }

  @Test
  void listRow_endDate_shouldBeTheLastStagesDate() {
    String endDate =
        given()
            .queryParam("type", "TRIP")
            .when()
            .get(ALL)
            .then()
            .statusCode(200)
            .extract()
            .path(tripRow() + ".endDate");

    assertEquals(day3, Instant.parse(endDate));
  }

  @Test
  void listRow_shouldIgnoreADeletedStage() {
    TripStage extra =
        dataService.createTripStage(user1, trip, "Etape fantome", 2, day3.plus(5, ChronoUnit.DAYS));
    Route ghostRoute = dataService.createRoute(team1, user1, "Route fantome");
    dataService.setRouteMetrics(ghostRoute, 99_000f, 9_000f);
    dataService.setTripStageRoute(extra, ghostRoute);
    dataService.deleteTripStage(extra);

    given()
        .queryParam("type", "TRIP")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(tripRow() + ".stageCount", equalTo(2))
        .body(tripRow() + ".totalDistance", equalTo(30_000f))
        .body(tripRow() + ".totalElevationGain", equalTo(800f));
  }

  @Test
  void listRow_tripWithoutStages_shouldHaveNoTotalsRatherThanZero() {
    dataService.createTrip(team1, user1, "Voyage sans etape", day1);

    given()
        .queryParam("type", "TRIP")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Voyage sans etape' }.stageCount", equalTo(0))
        .body("publications.find { it.name == 'Voyage sans etape' }.totalDistance", nullValue())
        .body(
            "publications.find { it.name == 'Voyage sans etape' }.totalElevationGain", nullValue())
        .body("publications.find { it.name == 'Voyage sans etape' }.endDate", nullValue());
  }

  @Test
  void listRow_stagesWithoutARoute_shouldHaveAnEndDateButNoDistance() {
    Trip unrouted = dataService.createTrip(team1, user1, "Voyage sans trace", day1);
    dataService.createTripStage(user1, unrouted, "Jour 1", 0, day1);
    dataService.createTripStage(user1, unrouted, "Jour 2", 1, day3);

    given()
        .queryParam("type", "TRIP")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Voyage sans trace' }.stageCount", equalTo(2))
        .body("publications.find { it.name == 'Voyage sans trace' }.totalDistance", nullValue())
        .body("publications.find { it.name == 'Voyage sans trace' }.endDate", notNullValue());
  }

  // ---------------------------------------------------------------- detail path (fold)

  @Test
  void detail_shouldAgreeWithTheListRow() {
    given()
        .when()
        .get(tripDetail())
        .then()
        .statusCode(200)
        .body("stageCount", equalTo(2))
        .body("totalDistance", equalTo(30_000f))
        .body("totalElevationGain", equalTo(800f))
        .body("endDate", notNullValue());
  }

  @Test
  void detail_stages_shouldBeNumbered() {
    given()
        .when()
        .get(tripDetail())
        .then()
        .statusCode(200)
        .body("stages", hasSize(2))
        .body("stages[0].stageIndex", equalTo(1))
        .body("stages[0].stageCount", equalTo(2))
        .body("stages[1].stageIndex", equalTo(2))
        .body("stages[1].stageCount", equalTo(2));
  }

  @Test
  void detail_stages_shouldCarryTheirPlaceAddresses() {
    given()
        .when()
        .get(tripDetail())
        .then()
        .statusCode(200)
        .body("stages[0].startPlace.address", equalTo("1 rue du Depart"))
        .body("stages[0].endPlace.address", equalTo("2 rue de l'Arrivee"));
  }

  // ---------------------------------------------------------------- visibility and tenancy

  @Test
  void anonymous_shouldNotSeeAPrivateTeamsTripTotals() {
    Trip secret = dataService.createTrip(team2, user1, "Voyage secret", day1);
    Route secretRoute = dataService.createRoute(team2, user1, "Trace secrete", Visibility.TEAM);
    dataService.setRouteMetrics(secretRoute, 50_000f, 1_000f);
    TripStage secretStage = dataService.createTripStage(user1, secret, "Jour secret", 0, day1);
    dataService.setTripStageRoute(secretStage, secretRoute);

    given()
        .queryParam("type", "TRIP")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.name", not(hasItem("Voyage secret")));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("type", "TRIP")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Voyage secret' }.totalDistance", equalTo(50_000f));
  }

  @Test
  void totals_shouldNotBeComputedAcrossDomains() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL1, "Other User 1");
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Foreign Team", "foreign-team", Visibility.PUBLIC);
    Trip foreign = dataService.createTrip(otherTeam, otherUser, "Voyage etranger", day1);
    Route foreignRoute = dataService.createRoute(otherTeam, otherUser, "Trace etrangere");
    dataService.setRouteMetrics(foreignRoute, 77_000f, 7_000f);
    TripStage foreignStage = dataService.createTripStage(otherUser, foreign, "Jour 1", 0, day1);
    dataService.setTripStageRoute(foreignStage, foreignRoute);

    given()
        .queryParam("type", "TRIP")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.name", not(hasItem("Voyage etranger")))
        // The local trip's totals are untouched by the foreign stage.
        .body(tripRow() + ".totalDistance", equalTo(30_000f));
  }
}
