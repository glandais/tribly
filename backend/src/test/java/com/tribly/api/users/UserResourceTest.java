package com.tribly.api.users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.common.TsidUtils;
import com.tribly.domain.user.User;
import com.tribly.repository.user.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
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

  @Test
  void getCurrentUser_shouldCreateUserWhenNotInDatabase() {
    // Given: user5 exists in Keycloak but NOT in the database
    Optional<User> existingUser = userRepository.findByEmail(EMAIL5);
    assertTrue(existingUser.isEmpty());

    // When: user5 calls getCurrentUser for the first time
    given()
        .auth()
        .oauth2(getAccessToken(USER5))
        .when()
        .get("/api/users/me")
        .then()
        .statusCode(200)
        .body("email", equalTo(EMAIL5))
        // Display name from JWT (firstName + lastName in Keycloak: "User Five")
        .body("displayName", equalTo("User Five"));

    // Then: user should be created in the database
    Optional<User> createdUser = userRepository.findByEmail(EMAIL5);
    assertTrue(createdUser.isPresent());
    assertThat(createdUser.get().getDisplayName(), equalTo("User Five"));
  }

  @Test
  void getCurrentUser_shouldNotUpdateDisplayNameFromJwt() {
    // Given: user1 exists in database with display name "Test User 1"
    // (different from JWT which would have "User One" from Keycloak)
    assertThat(user1.getDisplayName(), equalTo("Test User 1"));

    // When: user1 calls getCurrentUser
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me")
        .then()
        .statusCode(200)
        .body("email", equalTo(EMAIL1))
        // Display name should remain "Test User 1", NOT "User One" from JWT
        .body("displayName", equalTo("Test User 1"));

    // Then: display name in database should NOT be updated
    Optional<User> refreshedUser = userRepository.findByEmail(EMAIL1);
    assertTrue(refreshedUser.isPresent());
    assertThat(refreshedUser.get().getDisplayName(), equalTo("Test User 1"));
  }
}
