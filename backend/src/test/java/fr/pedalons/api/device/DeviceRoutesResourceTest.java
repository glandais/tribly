package fr.pedalons.api.device;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DeviceRoutesResourceTest extends AbstractResourceTest {

  private String routeSlug;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    // Create route via REST API to generate FIT file
    routeSlug = createRouteWithGpx(team1Slug, USER1, "Test Route", Visibility.PUBLIC);
  }

  /**
   * Helper method to create a route via REST API with GPX file upload. This triggers the full GPX
   * processing pipeline that generates the FIT file.
   */
  private String createRouteWithGpx(
      String teamSlug, String user, String name, Visibility visibility) {
    File gpxFile = new File("src/test/resources/example.gpx");
    RouteRequest routeRequest =
        new RouteRequest(
            name,
            MediaDto.builder().markdown("Test route").build(),
            SurfaceType.ROAD,
            visibility,
            null);

    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .multiPart("route", routeRequest, MediaType.APPLICATION_JSON)
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + teamSlug + "/routes")
        .then()
        .statusCode(201)
        .extract()
        .path("slug");
  }

  // ==================== Get Routes ====================

  @Test
  void getRoutes_withAuth_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/device/routes")
        .then()
        .statusCode(200)
        .body("routes", notNullValue());
  }

  @Test
  void getRoutes_withLatLon_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("lat", 48.8566)
        .queryParam("lon", 2.3522)
        .when()
        .get("/api/device/routes")
        .then()
        .statusCode(200)
        .body("routes", notNullValue());
  }

  @Test
  void getRoutes_withoutAuth_shouldReturn401() {
    given().when().get("/api/device/routes").then().statusCode(401);
  }

  // ==================== Download FIT ====================

  @Test
  void downloadFit_asMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/device/routes/" + team1Slug + "/" + routeSlug + "/fit")
        .then()
        .statusCode(200)
        .contentType("application/vnd.ant.fit")
        .header("Content-Disposition", containsString(routeSlug + ".fit"));
  }

  @Test
  void downloadFit_asAdmin_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/device/routes/" + team1Slug + "/" + routeSlug + "/fit")
        .then()
        .statusCode(200)
        .contentType("application/vnd.ant.fit");
  }

  @Test
  void downloadFit_withoutAuth_shouldReturn401() {
    given()
        .when()
        .get("/api/device/routes/" + team1Slug + "/" + routeSlug + "/fit")
        .then()
        .statusCode(401);
  }

  @Test
  void downloadFit_asNonMember_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/device/routes/" + team1Slug + "/" + routeSlug + "/fit")
        .then()
        .statusCode(404);
  }

  @Test
  void downloadFit_nonexistentRoute_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/device/routes/" + team1Slug + "/nonexistent-route/fit")
        .then()
        .statusCode(404);
  }

  @Test
  void downloadFit_nonexistentTeam_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/device/routes/nonexistent-team/" + routeSlug + "/fit")
        .then()
        .statusCode(404);
  }

  @Test
  void downloadFit_onPrivateTeam_asMember_shouldSucceed() {
    String privateRouteSlug =
        createRouteWithGpx(team2Slug, USER1, "Private Route", Visibility.TEAM);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/device/routes/" + team2Slug + "/" + privateRouteSlug + "/fit")
        .then()
        .statusCode(200)
        .contentType("application/vnd.ant.fit");
  }

  @Test
  void downloadFit_onPrivateTeam_asNonMember_shouldReturn404() {
    String privateRouteSlug =
        createRouteWithGpx(team2Slug, USER1, "Private Route", Visibility.TEAM);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/device/routes/" + team2Slug + "/" + privateRouteSlug + "/fit")
        .then()
        .statusCode(404);
  }
}
