package com.tribly.api.calendar;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.domain.calendar.CalendarToken;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripStage;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamCalendarResourceTest extends AbstractResourceTest {

  private Ride ride;
  private Trip trip;
  private TripStage stage;
  private CalendarToken token;
  private Instant now;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    now = Instant.now();
    ride = dataService.createRide(team1, user1, "Team Ride", "team-ride", now);
    trip = dataService.createTrip(team1, user1, "Team Trip", now);
    stage = dataService.createTripStage(user1, trip, "Stage 1", now);
    token = dataService.createCalendarToken(user1, "team-calendar-token-123");
  }

  // ==================== Get Team Events Tests ====================

  @Test
  void getTeamEvents_withoutAuth_shouldReturn403() {
    // Public team events should be accessible without auth
    given().when().get("/api/teams/" + team1Slug + "/calendar/events").then().statusCode(403);
  }

  @Test
  void getTeamEvents_withAuth_shouldReturnEvents() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/events")
        .then()
        .statusCode(200)
        .body("events", notNullValue())
        .body("events.size()", greaterThanOrEqualTo(1));
  }

  @Test
  void getTeamEvents_shouldReturnRides() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/events")
        .then()
        .statusCode(200)
        .body("events.find { it.type == 'RIDE' }.title", equalTo("Team Ride"));
  }

  @Test
  void getTeamEvents_shouldReturnTripStages() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/events")
        .then()
        .statusCode(200)
        .body("events.find { it.type == 'TRIP_STAGE' }.title", equalTo("Stage 1"));
  }

  @Test
  void getTeamEvents_shouldFilterByDateRange() {
    Instant from = now.minusSeconds(3600);
    Instant to = now.plusSeconds(3600);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("from", from.toString())
        .queryParam("to", to.toString())
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/events")
        .then()
        .statusCode(200)
        .body("events", notNullValue());
  }

  @Test
  void getTeamEvents_forNonexistentTeam_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/nonexistent-team/calendar/events")
        .then()
        .statusCode(404);
  }

  @Test
  void getTeamEvents_shouldIncludeEventMetadata() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/events")
        .then()
        .statusCode(200)
        .body("events[0].id", notNullValue())
        .body("events[0].title", notNullValue())
        .body("events[0].start", notNullValue())
        .body("events[0].type", notNullValue())
        .body("events[0].teamSlug", equalTo(team1Slug))
        .body("events[0].teamName", equalTo("Team 1"));
  }

  // ==================== Get Team ICS Feed Tests ====================

  @Test
  void getTeamIcsFeed_withValidToken_shouldReturnIcs() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/ics")
        .then()
        .statusCode(200)
        .contentType("text/calendar")
        .body(containsString("BEGIN:VCALENDAR"))
        .body(containsString("Team Ride"))
        .body(containsString("END:VCALENDAR"));
  }

  @Test
  void getTeamIcsFeed_shouldIncludeTeamNameInCalendar() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/ics")
        .then()
        .statusCode(200)
        .body(containsString("X-WR-CALNAME:Tribly - Team 1"));
  }

  @Test
  void getTeamIcsFeed_withInvalidToken_shouldReturn403() {
    given()
        .queryParam("token", "invalid-token")
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/ics")
        .then()
        .statusCode(403);
  }

  @Test
  void getTeamIcsFeed_withoutToken_shouldReturn403() {
    given().when().get("/api/teams/" + team1Slug + "/calendar/ics").then().statusCode(403);
  }

  @Test
  void getTeamIcsFeed_forNonexistentTeam_shouldReturn404() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/teams/nonexistent-team/calendar/ics")
        .then()
        .statusCode(404);
  }

  @Test
  void getTeamIcsFeed_shouldHaveCacheControlHeaders() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/ics")
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("no-cache"))
        .header("Content-Disposition", containsString(team1Slug + "-calendar.ics"));
  }

  @Test
  void getTeamIcsFeed_shouldReturnValidIcsFormat() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/ics")
        .then()
        .statusCode(200)
        .body(containsString("VERSION:2.0"))
        .body(containsString("PRODID:-//Tribly//Calendar//EN"))
        .body(containsString("BEGIN:VEVENT"))
        .body(containsString("END:VEVENT"));
  }

  @Test
  void getTeamIcsFeed_shouldIncludeRideUrls() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/ics")
        .then()
        .statusCode(200)
        .body(containsString("URL:"))
        .body(containsString("/teams/" + team1Slug + "/rides/team-ride"));
  }

  @Test
  void getTeamIcsFeed_shouldIncludeTripStageUrls() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/teams/" + team1Slug + "/calendar/ics")
        .then()
        .statusCode(200)
        .body(containsString("/teams/" + team1Slug + "/trips/"));
  }

  // ==================== Private Team Access Tests ====================

  @Test
  void getTeamEvents_forPrivateTeam_asMember_shouldSucceed() {
    // Create a ride in the private team
    dataService.createRide(team2, user1, "Private Ride", "private-ride", now);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team2Slug + "/calendar/events")
        .then()
        .statusCode(200)
        .body("events", notNullValue());
  }

  @Test
  void getTeamEvents_forPrivateTeam_asNonMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team2Slug + "/calendar/events")
        .then()
        .statusCode(403);
  }
}
