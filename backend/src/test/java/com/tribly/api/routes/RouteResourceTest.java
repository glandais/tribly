package com.tribly.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.common.TsidUtils;
import com.tribly.domain.route.Route;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  // ==================== List Routes Tests ====================

  @Test
  void listRoutes_withoutAuth_shouldSucceed() {
    given()
        .when()
        .get("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(200)
        .body("routes", notNullValue())
        .body("total", greaterThanOrEqualTo(0));
  }

  @Test
  void listRoutes_asMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(200)
        .body("routes", notNullValue());
  }

  @Test
  void listRoutes_asNonMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(200)
        .body("routes", notNullValue());
  }

  @Test
  void listRoutes_toNonexistentTeam_shouldReturn404() {
    given()
        .when()
        .get("/api/teams/nonexistent-team/routes")
        .then()
        // empty list
        .statusCode(404);
  }

  @Test
  void listRoutes_shouldSupportPagination() {
    given()
        .queryParam("page", 1)
        .queryParam("size", 10)
        .when()
        .get("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(200)
        .body("page", equalTo(1))
        .body("size", equalTo(10));
  }

  // ==================== Create Route Tests ====================

  @Test
  void createRoute_asAdmin_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    RouteRequest route =
        new RouteRequest(
            "Admin Route",
            MediaDto.builder().markdown("A test route").build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(201)
        .body("name", equalTo("Admin Route"))
        .body("media.markdown", equalTo("A test route"))
        .body("distance", greaterThan(0.0f))
        .body("elevationGain", greaterThanOrEqualTo(0.0f));
  }

  @Test
  void createRoute_asOrganizer_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    RouteRequest route =
        new RouteRequest(
            "Organizer Route",
            MediaDto.builder().markdown("Route by organizer").build(),
            SurfaceType.ROAD,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(201)
        .body("name", equalTo("Organizer Route"));
  }

  @Test
  void createRoute_asMember_shouldBeDenied() {
    File gpxFile = new File("src/test/resources/example.gpx");

    RouteRequest route =
        new RouteRequest(
            "Member Route",
            MediaDto.builder().build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(403);
  }

  @Test
  void createRoute_withoutAuth_shouldReturn401() {
    File gpxFile = new File("src/test/resources/example.gpx");

    RouteRequest route =
        new RouteRequest(
            "Unauth Route",
            MediaDto.builder().build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(401);
  }

  @Test
  void createRoute_asNonMember_shouldReturn403() {
    File gpxFile = new File("src/test/resources/example.gpx");

    RouteRequest route =
        new RouteRequest(
            "NonMember Route",
            MediaDto.builder().build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(403);
  }

  @Test
  void createRoute_withoutGpxFile_shouldReturn400() {
    RouteRequest route =
        new RouteRequest(
            "No GPX Route",
            MediaDto.builder().build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .when()
        .post("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(400);
  }

  @Test
  void createRoute_toNonexistentTeam_shouldReturn404() {
    File gpxFile = new File("src/test/resources/example.gpx");

    RouteRequest route =
        new RouteRequest(
            "Test Route", MediaDto.builder().build(), SurfaceType.GRAVEL, Visibility.PUBLIC, null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/nonexistent-team/routes")
        .then()
        .statusCode(404);
  }

  // ==================== Get Route Tests ====================

  @Test
  void getRoute_withoutAuth_shouldSucceed() {
    Route testRoute = dataService.createRoute(team1, user1, "Public Route", Visibility.PUBLIC);

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("id", equalTo(TsidUtils.toString(testRoute.getId())))
        .body("name", equalTo("Public Route"));
  }

  @Test
  void getRoute_asMember_shouldSucceed() {
    Route testRoute = dataService.createRoute(team1, user1, "Member Get Route", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("name", equalTo("Member Get Route"));
  }

  @Test
  void getRoute_asNonMember_shouldSucceed() {
    Route testRoute =
        dataService.createRoute(team1, user1, "NonMember Get Route", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("name", equalTo("NonMember Get Route"));
  }

  @Test
  void getRoute_nonexistent_shouldReturn404() {
    given()
        .when()
        .get("/api/teams/" + team1Slug + "/routes/nonexistent-route")
        .then()
        .statusCode(404);
  }

  @Test
  void getRoute_toNonexistentTeam_shouldReturn404() {
    given().when().get("/api/teams/nonexistent-team/routes/some-route").then().statusCode(404);
  }

  // ==================== Update Route Tests ====================

  @Test
  void updateRoute_asAdmin_shouldSucceed() {
    Route testRoute = dataService.createRoute(team1, user1, "Original Name", Visibility.PUBLIC);

    RouteRequest route =
        new RouteRequest(
            "Updated Name",
            MediaDto.builder().markdown("Updated description").build(),
            SurfaceType.MTB,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .when()
        .put("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Name"))
        .body("media.markdown", equalTo("Updated description"));
  }

  @Test
  void updateRoute_asOrganizer_shouldSucceed() {
    Route testRoute =
        dataService.createRoute(team1, user1, "Organizer Update Route", Visibility.PUBLIC);

    RouteRequest route =
        new RouteRequest(
            "Updated by Organizer",
            MediaDto.builder().markdown("Organizer updated").build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .when()
        .put("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated by Organizer"));
  }

  @Test
  void updateRoute_asMember_shouldBeDenied() {
    Route testRoute =
        dataService.createRoute(team1, user1, "Member Update Route", Visibility.PUBLIC);

    RouteRequest route =
        new RouteRequest(
            "Hacked by Member",
            MediaDto.builder().build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .when()
        .put("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(403);
  }

  @Test
  void updateRoute_withoutAuth_shouldReturn401() {
    Route testRoute =
        dataService.createRoute(team1, user1, "Unauth Update Route", Visibility.PUBLIC);

    RouteRequest route =
        new RouteRequest(
            "Unauthorized Update",
            MediaDto.builder().build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .when()
        .put("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(401);
  }

  @Test
  void updateRoute_asNonMember_shouldReturn403() {
    Route testRoute =
        dataService.createRoute(team1, user1, "NonMember Update Route", Visibility.PUBLIC);

    RouteRequest route =
        new RouteRequest(
            "Hacked by NonMember",
            MediaDto.builder().build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .when()
        .put("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(403);
  }

  @Test
  void updateRoute_withNewGpxFile_shouldSucceed() {
    Route testRoute = dataService.createRoute(team1, user1, "Original Route", Visibility.PUBLIC);

    RouteRequest route =
        new RouteRequest(
            "Updated Route",
            MediaDto.builder().markdown("Updated with new GPX").build(),
            SurfaceType.GRAVEL,
            Visibility.PUBLIC,
            null);

    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .put("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Route"))
        .body("media.markdown", equalTo("Updated with new GPX"))
        .body("distance", greaterThan(0.0f))
        .body("elevationGain", greaterThanOrEqualTo(0.0f));
  }

  @Test
  void updateRoute_nonexistent_shouldReturn404() {
    RouteRequest route =
        new RouteRequest(
            "Nonexistent", MediaDto.builder().build(), SurfaceType.GRAVEL, Visibility.PUBLIC, null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("route", route, MediaType.APPLICATION_JSON)
        .when()
        .put("/api/teams/" + team1Slug + "/routes/nonexistent-route")
        .then()
        .statusCode(404);
  }

  // ==================== Delete Route Tests ====================

  @Test
  void deleteRoute_asAdmin_shouldSucceed() {
    Route testRoute = dataService.createRoute(team1, user1, "To Delete", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(204);

    // Verify route is no longer accessible
    given()
        .when()
        .get("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(404);
  }

  @Test
  void deleteRoute_asOrganizer_shouldSucceed() {
    Route testRoute = dataService.createRoute(team1, user1, "Organizer Delete", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(204);
  }

  @Test
  void deleteRoute_asMember_shouldBeDenied() {
    Route testRoute = dataService.createRoute(team1, user1, "Member Delete", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .delete("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(403);
  }

  @Test
  void deleteRoute_withoutAuth_shouldReturn401() {
    Route testRoute = dataService.createRoute(team1, user1, "Unauth Delete", Visibility.PUBLIC);

    given()
        .when()
        .delete("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(401);
  }

  @Test
  void deleteRoute_asNonMember_shouldReturn403() {
    Route testRoute = dataService.createRoute(team1, user1, "NonMember Delete", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .delete("/api/teams/" + team1Slug + "/routes/" + testRoute.getSlug())
        .then()
        .statusCode(403);
  }

  @Test
  void deleteRoute_nonexistent_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug + "/routes/nonexistent-route")
        .then()
        .statusCode(404);
  }
}
