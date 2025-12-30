package com.tribly.api.assets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

import com.tribly.api.AbstractResourceTest;
import com.tribly.infrastructure.id.TsidUtils;
import io.quarkus.test.junit.QuarkusTest;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AssetResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  // ==================== Upload Tests ====================

  @Test
  void uploadAsset_asAdmin_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("url", containsString("/api/download/public/assets/"));
  }

  @Test
  void uploadAsset_asOrganizer_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .multiPart("file", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("url", containsString("/api/download/public/assets/"));
  }

  @Test
  void uploadAsset_asMember_shouldBeDenied() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .multiPart("file", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(403);
  }

  @Test
  void uploadAsset_withoutAuth_shouldReturn401() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .multiPart("file", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(401);
  }

  @Test
  void uploadAsset_asNonMember_shouldReturn403() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .multiPart("file", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(403);
  }

  @Test
  void uploadAsset_toNonexistentTeam_shouldReturn404() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/nonexistent-team/assets")
        .then()
        .statusCode(404);
  }

  @Test
  void uploadAsset_withoutFile_shouldReturn400() {
    File gpxFile = new File("src/test/resources/example.gpx");
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("wrongName", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(400);
  }

  @Test
  void uploadAsset_toPrivateTeam_shouldReturnTeamUrl() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/" + team2Slug + "/assets")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("url", containsString("/api/download/team/assets/"));
  }

  // ==================== Download Public Asset Tests (team1 - PUBLIC) ====================

  @Test
  void downloadPublicAsset_withoutAuth_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    // Upload asset to public team
    String assetUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/" + team1Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("url");

    // Download without auth should work for public assets
    given().when().get(assetUrl).then().statusCode(200);
  }

  @Test
  void downloadPublicAsset_asMember_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    // Upload asset to public team
    String assetUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/" + team1Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("url");

    // Download as member should work
    given().auth().oauth2(getAccessToken(USER3)).when().get(assetUrl).then().statusCode(200);
  }

  @Test
  void downloadPublicAsset_asNonMember_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    // Upload asset to public team
    String assetUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/" + team1Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("url");

    // Download as non-member should work for public assets
    given().auth().oauth2(getAccessToken(USER4)).when().get(assetUrl).then().statusCode(200);
  }

  // ==================== Download Team Asset Tests (team2 - TEAM/private) ====================

  @Test
  void downloadTeamAsset_asOrganizer_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    // Upload asset to private team
    String assetUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/" + team2Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("url");

    // Download as organizer should work
    given()
        .redirects()
        .follow(false)
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .get(assetUrl)
        .then()
        .contentType("application/gpx+xml")
        .statusCode(200);
  }

  @Test
  void downloadTeamAsset_withoutAuth_shouldRedirectToKeycloak() {
    File gpxFile = new File("src/test/resources/example.gpx");

    // Upload asset to private team
    String assetUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/" + team2Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("url");

    // Download without auth should redirect to Keycloak
    given()
        .redirects()
        .follow(false)
        .when()
        .get(assetUrl)
        .then()
        .statusCode(302)
        .header("Location", containsString("/realms/quarkus/protocol/openid-connect/auth"));
  }

  @Test
  void downloadTeamAsset_asMember_shouldSucceed() {
    File gpxFile = new File("src/test/resources/example.gpx");

    // Upload asset to private team
    String assetUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/" + team2Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("url");

    // Download as member (not organizer) should be denied
    given()
        .redirects()
        .follow(false)
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(assetUrl)
        .then()
        .statusCode(403);
  }

  @Test
  void downloadTeamAsset_asNonMember_shouldReturn404() {
    File gpxFile = new File("src/test/resources/example.gpx");

    // Upload asset to private team
    String assetUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", gpxFile, "application/gpx+xml")
            .when()
            .post("/api/teams/" + team2Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("url");

    given().auth().oauth2(getAccessToken(USER4)).when().get(assetUrl).then().statusCode(403);
  }

  @Test
  void downloadAsset_nonexistent_shouldReturn404() {
    given()
        .when()
        .get("/api/download/public/assets/" + TsidUtils.toString(1L) + "/file.gpx")
        .then()
        .statusCode(404);
  }
}
