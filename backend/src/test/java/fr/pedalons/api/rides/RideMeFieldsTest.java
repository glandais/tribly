package fr.pedalons.api.rides;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.enums.WindDirection;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The "me" fields of the ride detail payload: {@code registered}, {@code registeredGroupId}, {@code
 * full}, and the per-group metrics maquettes 11/12 need.
 *
 * <p>The point of these fields is that a client knows, from the detail response alone, whether the
 * current user is signed up and to which group — so the tests check the anonymous shape as
 * carefully as the registered one. Anonymous must read false/null, never 401.
 */
@QuarkusTest
class RideMeFieldsTest extends AbstractResourceTest {

  private Ride ride;
  private RideGroup fast;
  private RideGroup social;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Instant dateTime = Instant.now().plus(7, ChronoUnit.DAYS);
    ride = dataService.createRide(team1, user1, "Sunday Ride", "sunday-ride", dateTime);
    // "Fast" is capped at one seat, "Social" is uncapped: the ride is only full when both are.
    fast = dataService.createRideGroupWithMaxParticipants(user1, ride, "Fast", 1);
    social = dataService.createRideGroup(user1, ride, "Social", 1);

    Route route =
        dataService.createRouteWithProperties(
            team1,
            user1,
            "Fast Loop",
            Visibility.PUBLIC,
            42000,
            730,
            SurfaceType.ROAD,
            WindDirection.NORTH,
            45.0,
            6.0,
            45.1,
            6.1);
    dataService.setRideGroupRoute(fast, route);
  }

  private io.restassured.response.ValidatableResponse getRide(String userName) {
    var request = given();
    if (userName != null) {
      request = request.auth().oauth2(getAccessToken(userName));
    }
    return request
        .when()
        .get("/api/teams/" + team1Slug + "/rides/sunday-ride")
        .then()
        .statusCode(200);
  }

  @Test
  void getRide_anonymous_shouldReportNotRegisteredRatherThanFail() {
    getRide(null)
        .body("registered", equalTo(false))
        .body("registeredGroupId", nullValue())
        .body("full", equalTo(false))
        .body("groups.find { it.name == 'Fast' }.registered", equalTo(false))
        .body("groups.find { it.name == 'Social' }.registered", equalTo(false));
  }

  @Test
  void getRide_whenNotRegistered_shouldReportFalse() {
    getRide(USER3).body("registered", equalTo(false)).body("registeredGroupId", nullValue());
  }

  @Test
  void getRide_whenRegistered_shouldNameTheGroupJoined() {
    dataService.createParticipation(social, user3);

    getRide(USER3)
        .body("registered", equalTo(true))
        .body("registeredGroupId", equalTo(TsidUtils.toString(social.getId())))
        .body("groups.find { it.name == 'Social' }.registered", equalTo(true))
        .body("groups.find { it.name == 'Fast' }.registered", equalTo(false));
  }

  /** Another member's registration must never leak into my payload. */
  @Test
  void getRide_registrationOfAnotherUser_shouldNotMakeMeRegistered() {
    dataService.createParticipation(social, user2);

    getRide(USER3).body("registered", equalTo(false)).body("registeredGroupId", nullValue());
  }

  @Test
  void getRide_uncappedGroup_shouldNeverBeFull() {
    dataService.createParticipation(social, user2);
    dataService.createParticipation(social, user3);

    getRide(USER1).body("groups.find { it.name == 'Social' }.full", equalTo(false));
  }

  @Test
  void getRide_cappedGroupAtCapacity_shouldBeFullButRideStaysOpen() {
    dataService.createParticipation(fast, user2);

    getRide(USER1)
        .body("groups.find { it.name == 'Fast' }.full", equalTo(true))
        .body("groups.find { it.name == 'Social' }.full", equalTo(false))
        // One uncapped group is enough to keep the whole ride open.
        .body("full", equalTo(false));
  }

  @Test
  void getRide_everyGroupCapped_shouldMakeTheRideFull() {
    Ride capped = dataService.createRide(team1, user1, "Capped", "capped-ride", Instant.now());
    RideGroup only = dataService.createRideGroupWithMaxParticipants(user1, capped, "Only", 1);
    dataService.createParticipation(only, user2);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/capped-ride")
        .then()
        .statusCode(200)
        .body("full", equalTo(true));
  }

  @Test
  void getRide_groupWithARoute_shouldCarryDistanceAndElevationGain() {
    getRide(null)
        .body("groups.find { it.name == 'Fast' }.distance", equalTo(42000.0f))
        .body("groups.find { it.name == 'Fast' }.elevationGain", equalTo(730.0f))
        // A group without a route carries no metrics at all rather than zeros.
        .body("groups.find { it.name == 'Social' }.distance", nullValue())
        .body("groups.find { it.name == 'Social' }.elevationGain", nullValue());
  }

  /** Per-user fields must not be reusable by a shared cache. */
  @Test
  void getRide_shouldNotBeCacheableAcrossUsers() {
    getRide(USER1).header("Cache-Control", containsString("private"));
  }

  @Test
  void getRide_afterJoiningThroughTheApi_shouldFlipRegistered() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .when()
        .post(
            "/api/teams/"
                + team1Slug
                + "/rides/sunday-ride/groups/"
                + TsidUtils.toString(fast.getId())
                + "/join")
        .then()
        .statusCode(anyOf(is(200), is(201)));

    getRide(USER3)
        .body("registered", equalTo(true))
        .body("registeredGroupId", equalTo(TsidUtils.toString(fast.getId())));
  }
}
