package com.tribly.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DownloadPublicResourceTest {

  public static final String USERNAME_TEST = "user1";
  private static final String TEST_EMAIL = "user1@example.com";

  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team testTeam;
  private User testUser;
  private String routeSlug;

  final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  protected String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
  }

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();

    // Create test user
    testUser = dataService.createUser(TEST_EMAIL, "Test User");

    // Create test team with organizer
    testTeam = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(testUser, testTeam, TeamRole.ORGANIZER);

    // Create route with GPX file
    File gpxFile = new File("src/test/resources/example.gpx");
    routeSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_TEST))
            .multiPart("name", "Download Test Route")
            .multiPart("description", "Route for download testing")
            .multiPart("visibility", "PUBLIC")
            .multiPart("gpxFile", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/test-team/routes")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");
  }

  @Test
  void downloadGpx_shouldReturnGpxFile() {
    given()
        .when()
        .get("/api/download/public/teams/test-team/routes/" + routeSlug + "/gpx")
        .then()
        .statusCode(200)
        .contentType("application/gpx+xml")
        .header("Content-Disposition", containsString("attachment"))
        .header("Content-Disposition", containsString("filename"))
        .body(notNullValue());
  }

  @Test
  void downloadFit_shouldReturnFitFile() {
    given()
        .when()
        .get("/api/download/public/teams/test-team/routes/" + routeSlug + "/fit")
        .then()
        .statusCode(200)
        .contentType("application/octet-stream")
        .header("Content-Disposition", containsString("attachment"))
        .header("Content-Disposition", containsString("filename"))
        .body(notNullValue());
  }

  @Test
  void getThumbnail_shouldReturnImage() {
    given()
        .when()
        .get("/api/download/public/teams/test-team/routes/" + routeSlug + "/thumbnail")
        .then()
        .statusCode(200)
        .contentType("image/png")
        .body(notNullValue());
  }

  @Test
  void downloadGpx_withNonexistentRoute_shouldReturn404() {
    given()
        .when()
        .get("/api/download/public/teams/test-team/routes/missing/gpx")
        .then()
        .statusCode(404);
  }

  @Test
  void downloadFit_withNonexistentRoute_shouldReturn404() {
    given()
        .when()
        .get("/api/download/public/teams/test-team/routes/missing/fit")
        .then()
        .statusCode(404);
  }

  @Test
  void getThumbnail_withNonexistentRoute_shouldReturn404() {
    given()
        .when()
        .get("/api/download/public/teams/test-team/routes/missing/thumbnail")
        .then()
        .statusCode(404);
  }

  @Test
  void getTrack_shouldReturnGpxTrack() {
    given()
        .when()
        .get("/api/teams/test-team/routes/" + routeSlug + "/track")
        .then()
        .statusCode(200)
        .contentType("application/json")
        .body("trackPoints", notNullValue());
  }
}
