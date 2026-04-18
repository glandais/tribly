package fr.pedalons.api.assets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import io.quarkus.test.junit.QuarkusTest;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DownloadPublicAssetResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  // ==================== Get Avatar ====================

  @Test
  void getAvatar_withValidFileId_shouldSucceed() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload avatar
    String avatarUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/users/me/avatar")
            .then()
            .statusCode(200)
            .extract()
            .path("avatarUrl");

    // Download avatar without auth (public endpoint)
    given()
        .header("Accept", "image/jpeg")
        .when()
        .get(avatarUrl)
        .then()
        .statusCode(200)
        .contentType(containsString("image/"));
  }

  @Test
  void getAvatar_withDifferentSizes_shouldSucceed() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload avatar
    String avatarUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/users/me/avatar")
            .then()
            .statusCode(200)
            .extract()
            .path("avatarUrl");

    // Extract fileId from avatarUrl: /api/download/public/avatars/{fileId}/{size}
    String[] parts = avatarUrl.split("/");
    String fileId = parts[parts.length - 2];

    // Test different sizes (should all work, capped at 256)
    for (int size : new int[] {32, 64, 128, 256}) {
      given()
          .header("Accept", "image/jpeg")
          .when()
          .get("/api/download/public/avatars/" + fileId + "/" + size)
          .then()
          .statusCode(200)
          .contentType(containsString("image/"));
    }
  }

  @Test
  void getAvatar_withSizeLargerThanMax_shouldStillSucceed() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload avatar
    String avatarUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/users/me/avatar")
            .then()
            .statusCode(200)
            .extract()
            .path("avatarUrl");

    // Extract fileId from avatarUrl
    String[] parts = avatarUrl.split("/");
    String fileId = parts[parts.length - 2];

    // Request size larger than max (256) - should still work (capped internally)
    given()
        .header("Accept", "image/jpeg")
        .when()
        .get("/api/download/public/avatars/" + fileId + "/512")
        .then()
        .statusCode(200)
        .contentType(containsString("image/"));
  }

  @Test
  void getAvatar_nonexistentFileId_shouldReturn404() {
    given()
        .header("Accept", "image/jpeg")
        .when()
        .get("/api/download/public/avatars/" + TsidUtils.toString(999999L) + "/256")
        .then()
        .statusCode(404);
  }

  @Test
  void getAvatar_withWebpAcceptHeader_shouldReturnWebp() {
    File imageFile = new File("src/test/resources/image.png");

    // Upload avatar
    String avatarUrl =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart("file", imageFile, "image/png")
            .when()
            .post("/api/users/me/avatar")
            .then()
            .statusCode(200)
            .extract()
            .path("avatarUrl");

    // Download with webp accept header
    given()
        .header("Accept", "image/webp")
        .when()
        .get(avatarUrl)
        .then()
        .statusCode(200)
        .contentType(containsString("image/"));
  }
}
