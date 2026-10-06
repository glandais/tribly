package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.trip.Trip;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code when=UPCOMING|PAST} on the team and the cross-team publication lists (docs/LEDGER_*.md
 * API-85): judged by the end — stored, or the departure plus 3 h when an older backend left none —
 * with the order set by the server and {@code sortDir} still winning when given.
 */
@QuarkusTest
class PublicationWhenFilterTest extends AbstractResourceTest {

  private Instant now;
  private Ride underWay;
  private Ride longUnderWay;
  private Ride over;
  private Ride overEarly;
  private Ride future;
  private Trip trip;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.setTeamModules(team1, true, true);
    now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    // No stored end: read as the departure plus 3 h — under way for two more hours.
    underWay = dataService.createRide(team1, user1, "En cours", "en-cours", now.minus(hours(1)));
    // Left 5 h ago, but its stored end is still ahead: the stored end wins.
    longUnderWay = dataService.createRide(team1, user1, "Longue", "longue", now.minus(hours(5)));
    dataService.setEndDateTime(longUnderWay.getId(), now.plus(hours(1)));
    // No stored end, left 5 h ago: over by the default duration.
    over = dataService.createRide(team1, user1, "Finie", "finie", now.minus(hours(5)));
    // Left an hour ago, but its stored end has passed.
    overEarly = dataService.createRide(team1, user1, "Courte", "courte", now.minus(hours(1)));
    dataService.setEndDateTime(overEarly.getId(), now.minus(Duration.ofMinutes(10)));
    future = dataService.createRide(team1, user1, "Dimanche", "dimanche", now.plus(hours(48)));
    trip = dataService.createTrip(team1, user1, "Voyage commencé", now.minus(hours(30)));
    dataService.setEndDateTime(trip.getId(), now.plus(hours(40)));
    // A post has no end: in neither list, whatever its date.
    dataService.createPost(team1, user1, "Billet", now);
  }

  private static Duration hours(int h) {
    return Duration.ofHours(h);
  }

  private static String id(Object entity) {
    return switch (entity) {
      case Ride r -> TsidUtils.toString(r.getId());
      case Trip t -> TsidUtils.toString(t.getId());
      default -> throw new IllegalArgumentException();
    };
  }

  @Test
  void upcoming_keepsWhatIsNotOver_soonestDepartureFirst() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("when", "UPCOMING")
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.id", contains(id(trip), id(longUnderWay), id(underWay), id(future)))
        .body("publications[0].endDateTime", notNullValue());
  }

  @Test
  void past_keepsWhatIsOver_latestDepartureFirst() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("when", "PAST")
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.id", contains(id(overEarly), id(over)));
  }

  @Test
  void sortDir_overridesTheOrderOfWhen() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("when", "UPCOMING")
        .queryParam("sortDir", "DESC")
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.id", contains(id(future), id(underWay), id(longUnderWay), id(trip)));
  }

  @Test
  void upcoming_withParticipating_isJeParticipe() {
    RideGroup group = dataService.createRideGroup(user1, underWay, "G");
    dataService.createParticipation(group, user1);
    RideGroup pastGroup = dataService.createRideGroup(user1, over, "G");
    dataService.createParticipation(pastGroup, user1);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("when", "UPCOMING")
        .queryParam("participating", true)
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.id", contains(id(underWay)));
  }

  @Test
  void theCrossTeamList_andTheCounts_takeWhenToo() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("when", "PAST")
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.id", contains(id(overEarly), id(over)));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("when", "UPCOMING")
        .when()
        .get("/api/publications/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(4));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("when", "PAST")
        .when()
        .get("/api/teams/" + team1Slug + "/publications/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(2));
  }

  @Test
  void endDateTime_isOnEveryRideAndTrip_theDefaultWhenNoneIsStored() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + underWay.getSlug())
        .then()
        .statusCode(200)
        .body("endDateTime", equalTo(now.plus(hours(2)).toString()));
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/trips/" + trip.getSlug())
        .then()
        .statusCode(200)
        .body("endDateTime", equalTo(now.plus(hours(40)).toString()));
  }
}
