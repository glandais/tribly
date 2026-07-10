package fr.pedalons.api.gpx;

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
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxPreviewResourceTest extends AbstractResourceTest {

  private static final File EXAMPLE_GPX = new File("src/test/resources/example.gpx");

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  /** Uploads example.gpx as USER1 and returns the resulting public id. */
  private String createPreview(String user) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .multiPart("gpxFile", EXAMPLE_GPX, "application/gpx+xml")
        .when()
        .post("/api/gpx-previews")
        .then()
        .statusCode(201)
        .extract()
        .path("id");
  }

  // ==================== Create ====================

  @Test
  void createPreview_shouldAnalyseTheFile() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("gpxFile", EXAMPLE_GPX, "application/gpx+xml")
        .when()
        .post("/api/gpx-previews")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("name", notNullValue())
        .body("distance", greaterThan(0.0f))
        .body("owned", equalTo(true))
        .body("tracks", not(empty()));
  }

  @Test
  void createPreview_withoutAuth_shouldReturn401() {
    given()
        .multiPart("gpxFile", EXAMPLE_GPX, "application/gpx+xml")
        .when()
        .post("/api/gpx-previews")
        .then()
        .statusCode(401);
  }

  @Test
  void createPreview_withoutFile_shouldReturn400() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("unused", "x")
        .when()
        .post("/api/gpx-previews")
        .then()
        .statusCode(400);
  }

  @Test
  void createPreview_withInvalidGpx_shouldReturn400() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("gpxFile", new File("src/test/resources/invalid.gpx"), "application/gpx+xml")
        .when()
        .post("/api/gpx-previews")
        .then()
        .statusCode(400);
  }

  // ==================== Read ====================

  @Test
  void getPreview_shouldReturnItToItsCreator() {
    String previewId = createPreview(USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/gpx-previews/" + previewId)
        .then()
        .statusCode(200)
        .body("id", equalTo(previewId))
        .body("owned", equalTo(true))
        .body("tracks[0].line", notNullValue());
  }

  @Test
  void getPreview_shouldBeReadableByAnotherAuthenticatedUser() {
    String previewId = createPreview(USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/gpx-previews/" + previewId)
        .then()
        .statusCode(200)
        .body("owned", equalTo(false));
  }

  @Test
  void getPreview_withoutAuth_shouldSucceed() {
    String previewId = createPreview(USER1);

    given()
        .when()
        .get("/api/gpx-previews/" + previewId)
        .then()
        .statusCode(200)
        .body("id", equalTo(previewId))
        .body("owned", equalTo(false))
        .body("tracks", not(empty()));
  }

  @Test
  void getPreview_withUnknownId_shouldReturn404() {
    given().when().get("/api/gpx-previews/" + UUID.randomUUID()).then().statusCode(404);
  }

  // ==================== Delete ====================

  @Test
  void deletePreview_asCreator_shouldSucceed() {
    String previewId = createPreview(USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/gpx-previews/" + previewId)
        .then()
        .statusCode(204);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/gpx-previews/" + previewId)
        .then()
        .statusCode(404);
  }

  @Test
  void deletePreview_asNonCreator_shouldReturn403() {
    String previewId = createPreview(USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .delete("/api/gpx-previews/" + previewId)
        .then()
        .statusCode(403);
  }

  // ==================== Save as route ====================

  @Test
  void createRouteFromPreview_shouldCreateARouteOnTheTeam() {
    String previewId = createPreview(USER1);
    RouteRequest request =
        new RouteRequest(
            "From preview",
            MediaDto.builder().markdown("Saved from the GPX tools").build(),
            SurfaceType.ROAD,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .when()
        .post("/api/gpx-previews/" + previewId + "/routes/" + team1Slug)
        .then()
        .statusCode(201)
        .body("name", equalTo("From preview"))
        .body("distance", greaterThan(0.0f));
  }

  @Test
  void createRouteFromPreview_asNonMember_shouldReturn403() {
    String previewId = createPreview(USER1);
    RouteRequest request =
        new RouteRequest(
            "From preview", MediaDto.builder().build(), SurfaceType.ROAD, Visibility.PUBLIC, null);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .when()
        .post("/api/gpx-previews/" + previewId + "/routes/" + team1Slug)
        .then()
        .statusCode(403);
  }

  // ==================== Send to GPS service ====================

  @Test
  void uploadToGpsService_withoutConnection_shouldReturn400() {
    String previewId = createPreview(USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        // No body, but the class-level @Consumes rejects RestAssured's default text/plain.
        .contentType(MediaType.APPLICATION_JSON)
        .when()
        .post("/api/gpx-previews/" + previewId + "/gps/GARMIN")
        .then()
        .statusCode(400);
  }
}
