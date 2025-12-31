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

  // ==================== Image Upload Tests ====================

  @Test
  void uploadImage_shouldReturnImageUrl() {
    File imageFile = new File("src/test/resources/image.png");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", imageFile, "image/png")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("fileName", notNullValue())
        .body("contentType", containsString("image/"))
        .body("url", containsString("/api/download/public/assets/"))
        .body("imageUrl", containsString("/api/download/public/images/"))
        .body("imageUrl", containsString("/{size}"));
  }

  @Test
  void uploadImage_shouldReturnDimensions() {
    File imageFile = new File("src/test/resources/image.png");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", imageFile, "image/png")
        .when()
        .post("/api/teams/" + team1Slug + "/assets")
        .then()
        .statusCode(201)
        .body("imageDimensions", notNullValue())
        .body("imageDimensions.width", notNullValue())
        .body("imageDimensions.height", notNullValue());
  }

  @Test
  void uploadNonImage_shouldNotReturnImageUrl() {
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
        .body("imageUrl", org.hamcrest.Matchers.nullValue())
        .body("imageDimensions", org.hamcrest.Matchers.nullValue());
  }

  @Test
  void uploadImage_toPrivateTeam_shouldReturnTeamImageUrl() {
    File imageFile = new File("src/test/resources/image.png");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", imageFile, "image/png")
        .when()
        .post("/api/teams/" + team2Slug + "/assets")
        .then()
        .statusCode(201)
        .body("imageUrl", containsString("/api/download/team/images/"));
  }

  // ==================== Download Image Tests (resized via imgproxy) ====================

  @Test
  void downloadPublicImage_withoutAuth_shouldSucceed() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload image to public team
    String imageUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/teams/" + team1Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("imageUrl");

    // Replace {size} placeholder with actual size
    String resizedUrl = imageUrl.replace("{size}", "200");

    // Download without auth should work for public images
    given()
        .header("Accept", "image/jpeg")
        .when()
        .get(resizedUrl)
        .then()
        .statusCode(200)
        .contentType(containsString("image/"));
  }

  @Test
  void downloadPublicImage_differentSizes_shouldSucceed() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload image to public team
    String imageUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/teams/" + team1Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("imageUrl");

    // Test different sizes
    for (int size : new int[] {100, 200, 400, 800}) {
      String resizedUrl = imageUrl.replace("{size}", String.valueOf(size));
      given()
          .header("Accept", "image/jpeg")
          .when()
          .get(resizedUrl)
          .then()
          .statusCode(200)
          .contentType(containsString("image/"));
    }
  }

  @Test
  void downloadTeamImage_withoutAuth_shouldRedirectToKeycloak() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload image to private team
    String imageUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/teams/" + team2Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("imageUrl");

    String resizedUrl = imageUrl.replace("{size}", "200");

    // Download without auth should redirect to Keycloak
    given()
        .redirects()
        .follow(false)
        .header("Accept", "image/jpeg")
        .when()
        .get(resizedUrl)
        .then()
        .statusCode(302)
        .header("Location", containsString("/realms/quarkus/protocol/openid-connect/auth"));
  }

  @Test
  void downloadTeamImage_asOrganizer_shouldSucceed() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload image to private team
    String imageUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/teams/" + team2Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("imageUrl");

    String resizedUrl = imageUrl.replace("{size}", "200");

    // Download as organizer should work
    given()
        .redirects()
        .follow(false)
        .auth()
        .oauth2(getAccessToken(USER2))
        .header("Accept", "image/jpeg")
        .when()
        .get(resizedUrl)
        .then()
        .statusCode(200)
        .contentType(containsString("image/"));
  }

  @Test
  void downloadTeamImage_asNonMember_shouldReturn403() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload image to private team
    String imageUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/teams/" + team2Slug + "/assets")
            .then()
            .statusCode(201)
            .extract()
            .path("imageUrl");

    String resizedUrl = imageUrl.replace("{size}", "200");

    // Download as non-member should be denied
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .header("Accept", "image/jpeg")
        .when()
        .get(resizedUrl)
        .then()
        .statusCode(403);
  }

  @Test
  void downloadImage_nonexistent_shouldReturn404() {
    given()
        .header("Accept", "image/jpeg")
        .when()
        .get("/api/download/public/images/" + TsidUtils.toString(1L) + "/200")
        .then()
        .statusCode(404);
  }
}
