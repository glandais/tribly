package com.tribly.api.users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.infrastructure.id.TsidUtils;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserResourceTest extends AbstractResourceTest {

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
  void getUserById_shouldReturnPublicProfile() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/" + TsidUtils.toString(user1.getId()))
        .then()
        .statusCode(200)
        .body("id", equalTo(TsidUtils.toString(user1.getId())))
        .body("displayName", equalTo("User One"))
        // Public profile should not include email
        .body("$", not(hasKey("email")));
  }

  @Test
  void getUserById_withNonexistentId_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/" + TsidUtils.toString(999999L))
        .then()
        .statusCode(404);
  }

  @Test
  void getUserById_withoutAuth_shouldReturn401() {
    given().when().get("/api/users/" + user1.getId()).then().statusCode(401);
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
}
