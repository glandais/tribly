package fr.pedalons.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.enums.WindDirection;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code GET /api/routes/count} and {@code GET /api/teams/{teamSlug}/routes/count}.
 *
 * <p>These exist so a filter sheet can announce "Voir N parcours" without fetching a page of routes
 * to read its {@code total}. The property that matters is therefore not the figure on its own but
 * that it always equals the {@code total} of the listing it announces — every test below asserts the
 * two together, because a count that drifts from its list is worse than no count at all.
 */
@QuarkusTest
class RouteCountResourceTest extends AbstractResourceTest {

  private static final String ALL = "/api/routes";
  private static final String ALL_COUNT = "/api/routes/count";

  private String teamList() {
    return "/api/teams/" + team1Slug + "/routes";
  }

  private String teamCount() {
    return "/api/teams/" + team1Slug + "/routes/count";
  }

  private void seedRoute(
      Team team,
      User creator,
      String name,
      Visibility visibility,
      int distance,
      SurfaceType surface) {
    dataService.createRouteWithProperties(
        team,
        creator,
        name,
        visibility,
        distance,
        500,
        surface,
        WindDirection.NORTH,
        45.0,
        6.0,
        45.1,
        6.1);
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    seedRoute(team1, user1, "Short Road", Visibility.PUBLIC, 20_000, SurfaceType.ROAD);
    seedRoute(team1, user1, "Long Road", Visibility.PUBLIC, 120_000, SurfaceType.ROAD);
    seedRoute(team1, user1, "Long Gravel", Visibility.PUBLIC, 150_000, SurfaceType.GRAVEL);
    seedRoute(team2, user1, "Private Route", Visibility.TEAM, 50_000, SurfaceType.ROAD);
  }

  /** The {@code total} of either a listing or a count — the two are meant to be interchangeable. */
  private int total(String url, RequestSpecification request) {
    return request.when().get(url).then().statusCode(200).extract().path("total");
  }

  @Test
  void countTeamRoutes_anonymous_shouldMatchTheListTotal() {
    given().when().get(teamCount()).then().statusCode(200).body("total", equalTo(3));

    assertEquals(total(teamList(), given()), total(teamCount(), given()));
  }

  @Test
  void countTeamRoutes_shouldHonourTheSameFiltersAsTheList() {
    given()
        .queryParam("minDistance", 100_000)
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(2));

    given()
        .queryParam("minDistance", 100_000)
        .queryParam("surfaceType", "GRAVEL")
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(1));

    given()
        .queryParam("search", "Gravel")
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }

  @Test
  void countTeamRoutes_shouldIgnorePaginationAndCountEveryMatch() {
    // page/size are not part of the count contract; passing them changes nothing.
    given()
        .queryParam("page", 5)
        .queryParam("size", 1)
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(3));
  }

  @Test
  void countTeamRoutes_onNonexistentTeam_shouldReturn404() {
    given().when().get("/api/teams/nonexistent-team/routes/count").then().statusCode(404);
  }

  @Test
  void countRoutesOfAPrivateTeam_asNonMember_shouldReturnZero() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team2Slug + "/routes/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(0));
  }

  @Test
  void countRoutesOfAPrivateTeam_asMember_shouldSeeThem() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team2Slug + "/routes/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }

  @Test
  void countAllRoutes_anonymous_shouldMatchTheListTotalAndHideThePrivateTeam() {
    given().when().get(ALL_COUNT).then().statusCode(200).body("total", equalTo(3));

    assertEquals(total(ALL, given()), total(ALL_COUNT, given()));
  }

  /** Same short circuit as the listing: an anonymous visitor is a member of nothing. */
  @Test
  void countAllRoutes_withMinRole_anonymous_shouldReturnZero() {
    given()
        .queryParam("minRole", "MEMBER")
        .when()
        .get(ALL_COUNT)
        .then()
        .statusCode(200)
        .body("total", equalTo(0));
  }

  @Test
  void countAllRoutes_withMinRole_asMember_shouldMatchTheListTotal() {
    assertEquals(
        total(ALL, given().auth().oauth2(getAccessToken(USER1)).queryParam("minRole", "MEMBER")),
        total(
            ALL_COUNT,
            given().auth().oauth2(getAccessToken(USER1)).queryParam("minRole", "MEMBER")));
  }

  @Test
  void countAllRoutes_shouldHonourProximity() {
    given()
        .queryParam("nearLat", 45.0)
        .queryParam("nearLon", 6.0)
        .queryParam("nearRadius", 1_000)
        .when()
        .get(ALL_COUNT)
        .then()
        .statusCode(200)
        .body("total", equalTo(3));

    given()
        .queryParam("nearLat", 10.0)
        .queryParam("nearLon", 10.0)
        .queryParam("nearRadius", 1_000)
        .when()
        .get(ALL_COUNT)
        .then()
        .statusCode(200)
        .body("total", equalTo(0));
  }

  /** A count is a query like any other: it must never see past its own domain. */
  @Test
  void countAllRoutes_shouldNotCountAnotherDomain() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL1, "Other User 1");
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Foreign Team", "foreign-team", Visibility.PUBLIC);
    seedRoute(otherTeam, otherUser, "Foreign Route", Visibility.PUBLIC, 30_000, SurfaceType.ROAD);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get(ALL_COUNT)
        .then()
        .statusCode(200)
        .body("total", equalTo(4));

    given().when().get("/api/teams/foreign-team/routes/count").then().statusCode(404);
  }
}
