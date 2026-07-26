package fr.pedalons.api.users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** {@code GET /api/users/me/participations} — the feed behind "my next outing". */
@QuarkusTest
class UserParticipationsResourceTest extends AbstractResourceTest {

  private static final String PATH = "/api/users/me/participations";

  private Instant base;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    base = Instant.now().plus(7, ChronoUnit.DAYS);
  }

  private Ride joinedRide(String name, String slug, Instant dateTime, User user) {
    Ride ride = dataService.createRide(team1, user1, name, slug, dateTime);
    RideGroup group = dataService.createRideGroup(user1, ride, "Group");
    dataService.createParticipation(group, user);
    return ride;
  }

  @Test
  void listMyParticipations_anonymous_shouldReturn401() {
    given().when().get(PATH).then().statusCode(401);
  }

  @Test
  void listMyParticipations_shouldReturnOnlyWhatIJoined() {
    joinedRide("Joined", "joined-ride", base, user3);
    joinedRide("Someone Else", "someone-else-ride", base, user2);
    dataService.createRide(team1, user1, "Nobody", "nobody-ride", base);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body("total", equalTo(1))
        .body("publications", hasSize(1))
        .body("publications[0].name", equalTo("Joined"))
        .body("publications[0].registered", equalTo(true));
  }

  @Test
  void listMyParticipations_shouldIncludeTrips() {
    joinedRide("Joined Ride", "joined-ride", base, user3);
    Trip trip = dataService.createTrip(team1, user1, "Joined Trip", base.plusSeconds(60));
    dataService.createTripParticipation(trip, user3);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body("total", equalTo(2))
        .body("publications.find { it.type == 'TRIP' }.name", equalTo("Joined Trip"))
        .body("publications.find { it.type == 'TRIP' }.registered", equalTo(true));
  }

  /** Soonest first — the opposite of the newest-first ordering every other listing uses. */
  @Test
  void listMyParticipations_shouldBeOrderedByStartDateAscending() {
    joinedRide("Later", "later-ride", base.plus(3, ChronoUnit.DAYS), user3);
    joinedRide("Sooner", "sooner-ride", base, user3);
    joinedRide("Middle", "middle-ride", base.plus(1, ChronoUnit.DAYS), user3);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body("publications.name", contains("Sooner", "Middle", "Later"));
  }

  @Test
  void listMyParticipations_shouldHonourFromAndTo() {
    joinedRide("Past", "past-ride", Instant.now().minus(30, ChronoUnit.DAYS), user3);
    joinedRide("Future", "future-ride", base, user3);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("from", Instant.now().toString())
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body("total", equalTo(1))
        .body("publications[0].name", equalTo("Future"));
  }

  @Test
  void listMyParticipations_shouldPaginate() {
    for (int i = 0; i < 3; i++) {
      joinedRide("Ride " + i, "ride-" + i, base.plusSeconds(i), user3);
    }

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("page", 1)
        .queryParam("size", 2)
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body("total", equalTo(3))
        .body("page", equalTo(1))
        .body("size", equalTo(2))
        .body("publications", hasSize(1))
        .body("publications[0].name", equalTo("Ride 2"));
  }

  @Test
  void listMyParticipations_shouldNotBeCacheable() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("private"));
  }

  /**
   * The same email exists on two domains, and each one is a different user. A registration made on
   * {@code other.localhost} must be invisible from {@code localhost}, whatever the token says.
   */
  @Test
  void listMyParticipations_shouldNotLeakAcrossDomains() {
    joinedRide("Mine", "mine-ride", base, user3);

    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser3 = dataService.createUser(other, EMAIL3, "Other User 3");
    Team otherTeam =
        dataService.createTeam(other, otherUser3, "Other Team", "other-team", Visibility.PUBLIC);
    Ride otherRide = dataService.createRide(otherTeam, otherUser3, "Foreign", "foreign-ride", base);
    dataService.createParticipation(
        dataService.createRideGroup(otherUser3, otherRide, "Group"), otherUser3);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(PATH)
        .then()
        .statusCode(200)
        .body("total", equalTo(1))
        .body("publications.find { it.name == 'Foreign' }", nullValue());
  }
}
