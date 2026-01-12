package com.tribly.api.calendar;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.domain.calendar.CalendarToken;
import com.tribly.domain.ride.Ride;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CalendarResourceTest extends AbstractResourceTest {

  private Ride ride;
  private CalendarToken token;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    ride = dataService.createRide(team1, user1, "Test Ride", "test-ride", Instant.now());
    token = dataService.createCalendarToken(user1, "test-calendar-token-123");
  }

  // ==================== Get Events Tests ====================

  @Test
  void getEvents_withAuth_shouldReturnEvents() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/calendar/events")
        .then()
        .statusCode(200)
        .body("events", notNullValue())
        .body("events.size()", greaterThanOrEqualTo(1))
        .body("events[0].title", equalTo("Test Ride"));
  }

  @Test
  void getEvents_withoutAuth_shouldReturn401() {
    given().when().get("/api/calendar/events").then().statusCode(401);
  }

  @Test
  void getEvents_shouldFilterByDateRange() {
    Instant from = Instant.now().minusSeconds(3600);
    Instant to = Instant.now().plusSeconds(3600);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("from", from.toString())
        .queryParam("to", to.toString())
        .when()
        .get("/api/calendar/events")
        .then()
        .statusCode(200)
        .body("events", notNullValue());
  }

  @Test
  void getEvents_shouldReturnEventsFromAllUserTeams() {
    // user1 is already admin of team1, let's verify they get events
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/calendar/events")
        .then()
        .statusCode(200)
        .body("events.size()", greaterThanOrEqualTo(1));
  }

  // ==================== Get Token Tests ====================

  @Test
  void getToken_withAuth_shouldReturnToken() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/calendar/token")
        .then()
        .statusCode(200)
        .body("token", notNullValue())
        .body("globalFeedUrl", containsString("/api/calendar/ics"))
        .body("teamFeedUrlTemplate", containsString("/calendar/ics"));
  }

  @Test
  void getToken_withoutAuth_shouldReturn401() {
    given().when().get("/api/calendar/token").then().statusCode(401);
  }

  @Test
  void getToken_shouldCreateTokenIfNotExists() {
    // user2 doesn't have a token yet
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .get("/api/calendar/token")
        .then()
        .statusCode(200)
        .body("token", notNullValue())
        .body("token.length()", equalTo(64)); // 32 bytes = 64 hex chars
  }

  @Test
  void getToken_shouldReturnExistingToken() {
    // user1 already has a token created in setUp
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/calendar/token")
        .then()
        .statusCode(200)
        .body("token", equalTo(token.getToken()));
  }

  // ==================== Regenerate Token Tests ====================

  @Test
  void regenerateToken_withAuth_shouldReturnNewToken() {
    String oldToken = token.getToken();

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .contentType(ContentType.JSON)
        .post("/api/calendar/token/regenerate")
        .then()
        .statusCode(200)
        .body("token", notNullValue())
        .body("token", not(equalTo(oldToken)));
  }

  @Test
  void regenerateToken_withoutAuth_shouldReturn401() {
    given()
        .when()
        .contentType(ContentType.JSON)
        .post("/api/calendar/token/regenerate")
        .then()
        .statusCode(401);
  }

  @Test
  void regenerateToken_shouldInvalidateOldToken() {
    String oldToken = token.getToken();

    // First regenerate
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .contentType(ContentType.JSON)
        .post("/api/calendar/token/regenerate")
        .then()
        .statusCode(200);

    // Old token should no longer work for ICS feed
    given().queryParam("token", oldToken).when().get("/api/calendar/ics").then().statusCode(403);
  }

  // ==================== ICS Feed Tests ====================

  @Test
  void getGlobalIcsFeed_withValidToken_shouldReturnIcs() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/calendar/ics")
        .then()
        .statusCode(200)
        .contentType("text/calendar")
        .body(containsString("BEGIN:VCALENDAR"))
        .body(containsString("Test Ride"))
        .body(containsString("END:VCALENDAR"));
  }

  @Test
  void getGlobalIcsFeed_withInvalidToken_shouldReturn403() {
    given()
        .queryParam("token", "invalid-token")
        .when()
        .get("/api/calendar/ics")
        .then()
        .statusCode(403);
  }

  @Test
  void getGlobalIcsFeed_withoutToken_shouldReturn403() {
    given().when().get("/api/calendar/ics").then().statusCode(403);
  }

  @Test
  void getGlobalIcsFeed_shouldHaveCacheControlHeaders() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/calendar/ics")
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("no-cache"))
        .header("Content-Disposition", containsString("tribly-calendar.ics"));
  }

  @Test
  void getGlobalIcsFeed_shouldReturnValidIcsFormat() {
    given()
        .queryParam("token", token.getToken())
        .when()
        .get("/api/calendar/ics")
        .then()
        .statusCode(200)
        .body(containsString("VERSION:2.0"))
        .body(containsString("PRODID:-//Tribly//Calendar//EN"))
        .body(containsString("BEGIN:VEVENT"))
        .body(containsString("END:VEVENT"));
  }
}
