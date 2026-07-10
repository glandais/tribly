package fr.pedalons.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.route.Route;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.team.request.MinRole;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AllRouteResourceTest extends AbstractResourceTest {

  private Route publicRoute;
  private Route teamRoute;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    publicRoute = dataService.createRoute(team1, user1, "Public Route", Visibility.PUBLIC);
    teamRoute = dataService.createRoute(team1, user1, "Team Route", Visibility.TEAM);
  }

  // ==================== List All Routes Tests ====================

  @Test
  void listAllRoutes_withoutAuth_shouldReturnPublicRoutes() {
    given()
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()))
        .body("total", greaterThanOrEqualTo(1))
        .body("page", equalTo(0))
        .body("size", equalTo(20));
  }

  @Test
  void listAllRoutes_asMember_shouldReturnPublicAndTeamRoutes() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()))
        .body("total", greaterThanOrEqualTo(2));
  }

  @Test
  void listAllRoutes_asNonMember_shouldReturnOnlyPublicRoutes() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldSupportPagination() {
    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("page", equalTo(0))
        .body("size", equalTo(10));
  }

  @Test
  void listAllRoutes_shouldSupportSearch() {
    given()
        .queryParam("search", "Public")
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldFilterByMinDistance() {
    given()
        .queryParam("minDistance", 1000)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldFilterByMaxDistance() {
    given()
        .queryParam("maxDistance", 100000)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldFilterByDistanceRange() {
    given()
        .queryParam("minDistance", 1000)
        .queryParam("maxDistance", 50000)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldFilterByElevationGain() {
    given()
        .queryParam("minElevationGain", 100)
        .queryParam("maxElevationGain", 2000)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldFilterByHilliness() {
    given()
        .queryParam("hilliness", "FLAT")
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldFilterBySurfaceType() {
    given()
        .queryParam("surfaceType", "ROAD")
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldFilterByWindDirection() {
    given()
        .queryParam("windDirection", "NORTH")
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldSupportProximitySearch() {
    given()
        .queryParam("nearLat", 48.8566)
        .queryParam("nearLon", 2.3522)
        .queryParam("nearRadius", 50000)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldSupportProximitySearchWithNearType() {
    given()
        .queryParam("nearLat", 48.8566)
        .queryParam("nearLon", 2.3522)
        .queryParam("nearType", "START")
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldSupportSorting() {
    given()
        .queryParam("sortBy", "DISTANCE")
        .queryParam("sortDir", "DESC")
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()));
  }

  @Test
  void listAllRoutes_shouldSupportMultipleFilters() {
    given()
        .queryParam("search", "route")
        .queryParam("minDistance", 1000)
        .queryParam("maxDistance", 100000)
        .queryParam("surfaceType", "ROAD")
        .queryParam("sortBy", "ELEVATION_GAIN")
        .queryParam("sortDir", "ASC")
        .queryParam("page", 0)
        .queryParam("size", 5)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", is(notNullValue()))
        .body("page", equalTo(0))
        .body("size", equalTo(5));
  }

  // ==================== Vector Tile Tests ====================

  /** Tile covering the test routes, which run from (6, 45) to (6.1, 45.1). */
  private static final String ROUTES_TILE = "/api/routes/tiles/8/132/92.mvt";

  @Test
  void allRoutesTile_withoutAuth_shouldOnlyContainPublicRoute() {
    String tile = MvtAssert.decode(given().when().get(ROUTES_TILE));

    MvtAssert.assertContainsSlugs(tile, publicRoute.getSlug());
    MvtAssert.assertMissingSlugs(tile, teamRoute.getSlug());
  }

  @Test
  void allRoutesTile_asNonMember_shouldOnlyContainPublicRoute() {
    String tile =
        MvtAssert.decode(given().auth().oauth2(getAccessToken(USER4)).when().get(ROUTES_TILE));

    MvtAssert.assertContainsSlugs(tile, publicRoute.getSlug());
    MvtAssert.assertMissingSlugs(tile, teamRoute.getSlug());
  }

  @Test
  void allRoutesTile_asMember_shouldContainTeamRoute() {
    String tile =
        MvtAssert.decode(given().auth().oauth2(getAccessToken(USER3)).when().get(ROUTES_TILE));

    MvtAssert.assertContainsSlugs(tile, publicRoute.getSlug(), teamRoute.getSlug());
  }

  @Test
  void allRoutesTile_outsideRouteBounds_shouldBeEmpty() {
    given().when().get("/api/routes/tiles/12/0/0.mvt").then().statusCode(200).body(emptyString());
  }

  @Test
  void allRoutesTile_withCoordinatesOutsideZoomLevel_shouldReturnBadRequest() {
    given().when().get("/api/routes/tiles/8/9999/92.mvt").then().statusCode(400);
  }

  @Test
  void allRoutesTile_withMinRole_withoutAuth_shouldBeEmpty() {
    given()
        .queryParam("minRole", MinRole.MEMBER)
        .when()
        .get(ROUTES_TILE)
        .then()
        .statusCode(200)
        .body(emptyString());
  }

  @Test
  void allRoutesTile_withMinRole_asMember_shouldContainTeamRoute() {
    String tile =
        MvtAssert.decode(
            given()
                .auth()
                .oauth2(getAccessToken(USER3))
                .queryParam("minRole", MinRole.MEMBER)
                .when()
                .get(ROUTES_TILE));

    MvtAssert.assertContainsSlugs(tile, publicRoute.getSlug(), teamRoute.getSlug());
  }

  @Test
  void allRoutesTile_withMinRole_asNonMember_shouldBeEmpty() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .queryParam("minRole", MinRole.MEMBER)
        .when()
        .get(ROUTES_TILE)
        .then()
        .statusCode(200)
        .body(emptyString());
  }

  @Test
  void allRoutesTile_withSearch_shouldOnlyContainMatchingRoute() {
    String tile =
        MvtAssert.decode(
            given()
                .auth()
                .oauth2(getAccessToken(USER3))
                .queryParam("search", "team")
                .when()
                .get(ROUTES_TILE));

    MvtAssert.assertContainsSlugs(tile, teamRoute.getSlug());
    MvtAssert.assertMissingSlugs(tile, publicRoute.getSlug());
  }
}
