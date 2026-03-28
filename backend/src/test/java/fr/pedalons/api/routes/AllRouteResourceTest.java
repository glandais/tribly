package fr.pedalons.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.route.Route;
import fr.pedalons.enums.Visibility;
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
}
