package fr.pedalons.api.users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.repository.user.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserResourceTest extends AbstractResourceTest {

  @Inject UserRepository userRepository;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void getCurrentUser_shouldReturnUserDetails() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me")
        .then()
        .statusCode(200)
        .body("email", equalTo(EMAIL1));
  }

  @Test
  void getCurrentUser_withoutAuth_shouldReturn401() {
    given().when().get("/api/users/me").then().statusCode(401);
  }

  @Test
  void updateCurrentUser_shouldUpdateDisplayName() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body("{\"displayName\": \"Updated Name\"}")
        .when()
        .put("/api/users/me")
        .then()
        .statusCode(200)
        .body("displayName", equalTo("Updated Name"));
  }

  @Test
  void updateCurrentUser_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .body("{\"displayName\": \"Hacker\"}")
        .when()
        .put("/api/users/me")
        .then()
        .statusCode(401);
  }

  @Test
  void deleteCurrentUser_shouldSoftDeleteAccount() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/users/me")
        .then()
        .statusCode(204);

    // Verify user is no longer accessible (will return 404 because user is deleted)
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .get("/api/users/" + TsidUtils.toString(user1.getId()))
        .then()
        .statusCode(404);
  }

  @Test
  void deleteCurrentUser_withoutAuth_shouldReturn401() {
    given().when().delete("/api/users/me").then().statusCode(401);
  }

  @Test
  void searchUsers_shouldReturnMatchingUsers() {
    // Create additional test users
    dataService.createUser("alice@example.com", "Alice Johnson");
    dataService.createUser("bob@example.com", "Bob Smith");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("q", "alice")
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("[0].displayName", equalTo("Alice Johnson"))
        .body("[0]", not(hasKey("email"))); // Verify public DTO excludes email
  }

  // ==================== Upload Avatar Tests ====================

  @Test
  void uploadAvatar_withValidImage_shouldSucceed() {
    File imageFile = new File("src/test/resources/image.png");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", imageFile, "image/png")
        .when()
        .post("/api/users/me/avatar")
        .then()
        .statusCode(200)
        .body("avatarUrl", notNullValue());
  }

  @Test
  void uploadAvatar_withoutAuth_shouldReturn401() {
    File imageFile = new File("src/test/resources/image.png");

    given()
        .multiPart("file", imageFile, "image/png")
        .when()
        .post("/api/users/me/avatar")
        .then()
        .statusCode(401);
  }

  @Test
  void uploadAvatar_withoutFile_shouldReturn400() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("multipart/form-data")
        .when()
        .post("/api/users/me/avatar")
        .then()
        .statusCode(400);
  }

  // ==================== Delete Avatar Tests ====================

  @Test
  void deleteAvatar_withAuth_shouldSucceed() {
    // First upload an avatar
    File imageFile = new File("src/test/resources/image.png");
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .multiPart("file", imageFile, "image/png")
        .when()
        .post("/api/users/me/avatar")
        .then()
        .statusCode(200);

    // Then delete it
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/users/me/avatar")
        .then()
        .statusCode(200);
  }

  @Test
  void deleteAvatar_withoutAuth_shouldReturn401() {
    given().when().delete("/api/users/me/avatar").then().statusCode(401);
  }

  @Test
  void deleteAvatar_whenNoAvatar_shouldSucceed() {
    // Deleting when there's no avatar should still return 200
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/users/me/avatar")
        .then()
        .statusCode(200);
  }
}
