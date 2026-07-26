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

  // ==================== Team-scoped search (?teamSlug=) ====================
  //
  // Fixture recap: team1 holds user1 (ADMIN), user2 (ORGANIZER) and user3 (MEMBER); user4 and
  // user5 exist in the same domain but belong to no team. Every display name starts with
  // "Test User", so one query separates "everyone in the domain" from "everyone on the team".

  @Test
  void searchUsers_withTeamSlug_shouldReturnOnlyTeamMembers() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("q", "Test User")
        .queryParam("teamSlug", team1Slug)
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(200)
        .body("size()", equalTo(3))
        .body("displayName", containsInAnyOrder("Test User 1", "Test User 2", "Test User 3"))
        .body("displayName", not(hasItem("Test User 4")))
        .body("displayName", not(hasItem("Test User 5")))
        .body("[0]", not(hasKey("email")));
  }

  /**
   * The parameter may only narrow. If this ever returns more than the unscoped search, the filter
   * has become a way to reach users the plain search hides — which is exactly what it must not be.
   */
  @Test
  void searchUsers_withTeamSlug_shouldNeverReturnMoreThanUnscoped() {
    int unscoped =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .queryParam("q", "Test User")
            .when()
            .get("/api/users/search")
            .then()
            .statusCode(200)
            .extract()
            .path("size()");

    int scoped =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .queryParam("q", "Test User")
            .queryParam("teamSlug", team1Slug)
            .when()
            .get("/api/users/search")
            .then()
            .statusCode(200)
            .extract()
            .path("size()");

    assertEquals(5, unscoped, "the domain fixture has five users matching the query");
    assertEquals(3, scoped, "only three of them are on team1");
    assertTrue(scoped < unscoped, "the team filter must remove results, never add any");
  }

  /**
   * The case the parameter exists for: an organizer creates rides but may not list team members
   * ({@code USER_TEAM}/{@code LIST} is admin-only), so this is their only source of candidates
   * when designating a group leader.
   */
  @Test
  void searchUsers_withTeamSlug_asOrganizer_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .queryParam("q", "Test User")
        .queryParam("teamSlug", team1Slug)
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(200)
        .body("size()", equalTo(3));
  }

  @Test
  void searchUsers_withTeamSlug_asPlainMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("q", "Test User")
        .queryParam("teamSlug", team1Slug)
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(200)
        .body("size()", equalTo(3));
  }

  /** team1 is PUBLIC, so the team resolves: it is membership alone that rejects the caller. */
  @Test
  void searchUsers_withTeamSlug_asNonMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .queryParam("q", "Test User")
        .queryParam("teamSlug", team1Slug)
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(403);
  }

  /**
   * A private team resolves by slug — {@code getTeam} checks no visibility — so today membership is
   * what rejects, with a 403. The assertion tolerates a 404 on purpose: answering "not found" for a
   * team the caller may not see would be an improvement, not a regression, and this test should not
   * stand in its way. What it pins is that the call never succeeds.
   */
  @Test
  void searchUsers_withPrivateTeamSlug_asNonMember_shouldNotSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .queryParam("q", "Test User")
        .queryParam("teamSlug", team2Slug)
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(anyOf(equalTo(403), equalTo(404)));
  }

  @Test
  void searchUsers_withUnknownTeamSlug_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("q", "Test User")
        .queryParam("teamSlug", "no-such-team")
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(404);
  }

  @Test
  void searchUsers_withTeamSlug_withoutAuth_shouldReturn401() {
    given()
        .queryParam("q", "Test User")
        .queryParam("teamSlug", team1Slug)
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(401);
  }

  /** An empty query short-circuits before the team is ever resolved, membership included. */
  @Test
  void searchUsers_withTeamSlug_andBlankQuery_shouldReturnEmptyList() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("q", "   ")
        .queryParam("teamSlug", team1Slug)
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(200)
        .body("size()", equalTo(0));
  }

  /** The unscoped path must keep behaving exactly as before the parameter existed. */
  @Test
  void searchUsers_withoutTeamSlug_shouldStillSpanTheWholeDomain() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .queryParam("q", "Test User")
        .when()
        .get("/api/users/search")
        .then()
        .statusCode(200)
        .body("size()", equalTo(5));
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
