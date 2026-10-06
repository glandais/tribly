package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.teams.request.TeamRequest;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The team's zone (docs/LEDGER_*.md API-60): served on the team, set on creation and update, and
 * the {@code GET …/timezone} the editors label their fields with — organisers and above only.
 */
@QuarkusTest
class TeamTimezoneResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  /** A new team is always created members-only. */
  private static TeamRequest teamRequest(String name, @Nullable String timezone) {
    return teamRequest(name, Visibility.TEAM, timezone);
  }

  /** An update must keep the team's visibility, PUBLIC for team1. */
  private static TeamRequest updateRequest(@Nullable String timezone) {
    return teamRequest("Team 1", Visibility.PUBLIC, timezone);
  }

  private static TeamRequest teamRequest(
      String name, Visibility visibility, @Nullable String timezone) {
    return new TeamRequest(
        name,
        MediaDto.builder().build(),
        visibility,
        true,
        true,
        true,
        true,
        true,
        false,
        null,
        null,
        timezone);
  }

  private RequestSpecification as(String user) {
    return given().auth().oauth2(getAccessToken(user));
  }

  private String timezoneUrl() {
    return "/api/teams/" + team1Slug + "/timezone";
  }

  // ─── The team's own zone ──────────────────────────────────────────────────

  @Test
  void anExistingTeam_isInParis() {
    as(USER3)
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("timezone", equalTo("Europe/Paris"));
  }

  @Test
  void create_withAZone_storesIt_onTheTeamAndItsAboutPage() {
    String slug =
        as(USER5)
            .contentType("application/json")
            .body(teamRequest("Tokyo Riders", "Asia/Tokyo"))
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .body("timezone", equalTo("Asia/Tokyo"))
            .extract()
            .path("slug");

    Team team = dataService.findTeamBySlug(domain, slug);
    assertEquals("Asia/Tokyo", team.getTimezone());
    assertEquals("Asia/Tokyo", dataService.getAboutPageTimezone(team.getId()));
  }

  @Test
  void create_withoutAZone_isInParis() {
    as(USER5)
        .contentType("application/json")
        .body(teamRequest("Sans fuseau", null))
        .when()
        .post("/api/teams")
        .then()
        .statusCode(201)
        .body("timezone", equalTo("Europe/Paris"));
  }

  @Test
  void create_withAnUnknownZone_is400() {
    as(USER5)
        .contentType("application/json")
        .body(teamRequest("Martiens", "Mars/Olympus_Mons"))
        .when()
        .post("/api/teams")
        .then()
        .statusCode(400)
        .body("code", equalTo("INVALID_TIMEZONE"));
  }

  @Test
  void update_changesTheZone() {
    as(USER1)
        .contentType("application/json")
        .body(updateRequest("America/Montreal"))
        .when()
        .put("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("timezone", equalTo("America/Montreal"));
  }

  @Test
  void update_withoutAZone_leavesItAlone() {
    dataService.setTeamTimezone(team1, "Asia/Tokyo");
    as(USER1)
        .contentType("application/json")
        .body(updateRequest(null))
        .when()
        .put("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("timezone", equalTo("Asia/Tokyo"));
  }

  @Test
  void update_withAnUnknownZone_is400_andChangesNothing() {
    as(USER1)
        .contentType("application/json")
        .body(updateRequest("Nowhere/Land"))
        .when()
        .put("/api/teams/" + team1Slug)
        .then()
        .statusCode(400)
        .body("code", equalTo("INVALID_TIMEZONE"));
    assertEquals("Europe/Paris", dataService.findTeamBySlug(domain, team1Slug).getTimezone());
  }

  // ─── GET /api/teams/{teamSlug}/timezone ───────────────────────────────────

  @Test
  void organizer_getsTheZoneOfThePoint() {
    as(USER2)
        .queryParam("lat", 35.68)
        .queryParam("lon", 139.76)
        .when()
        .get(timezoneUrl())
        .then()
        .statusCode(200)
        .body("timezone", equalTo("Asia/Tokyo"));
  }

  @Test
  void admin_getsTheZoneOfThePoint() {
    as(USER1)
        .queryParam("lat", 40.71)
        .queryParam("lon", -74.01)
        .when()
        .get(timezoneUrl())
        .then()
        .statusCode(200)
        .body("timezone", equalTo("America/New_York"));
  }

  @Test
  void withoutAPoint_theTeamsZone() {
    dataService.setTeamTimezone(team1, "America/Montreal");
    as(USER2)
        .when()
        .get(timezoneUrl())
        .then()
        .statusCode(200)
        .body("timezone", equalTo("America/Montreal"));
  }

  @Test
  void withHalfAPoint_theTeamsZone() {
    as(USER2)
        .queryParam("lat", 35.68)
        .when()
        .get(timezoneUrl())
        .then()
        .statusCode(200)
        .body("timezone", equalTo("Europe/Paris"));
  }

  @Test
  void aLatitudeOutOfRange_is400() {
    as(USER2)
        .queryParam("lat", 95)
        .queryParam("lon", 0)
        .when()
        .get(timezoneUrl())
        .then()
        .statusCode(400);
  }

  @Test
  void aLongitudeOutOfRange_is400() {
    as(USER2)
        .queryParam("lat", 0)
        .queryParam("lon", 200)
        .when()
        .get(timezoneUrl())
        .then()
        .statusCode(400);
  }

  @Test
  void member_isRefused() {
    as(USER3)
        .queryParam("lat", 35.68)
        .queryParam("lon", 139.76)
        .when()
        .get(timezoneUrl())
        .then()
        .statusCode(403);
  }

  @Test
  void nonMember_isRefused() {
    as(USER4).when().get(timezoneUrl()).then().statusCode(403);
  }

  @Test
  void anonymous_isRefused() {
    given().when().get(timezoneUrl()).then().statusCode(401);
  }

  @Test
  void aTeamOfAnotherDomain_is404() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL1, "Other User 1");
    dataService.createTeam(other, otherUser, "Foreign Team", "foreign-team", Visibility.PUBLIC);

    as(USER1).when().get("/api/teams/foreign-team/timezone").then().statusCode(404);
  }

  @Test
  void anUnknownTeam_is404() {
    as(USER1).when().get("/api/teams/no-such-team/timezone").then().statusCode(404);
  }
}
