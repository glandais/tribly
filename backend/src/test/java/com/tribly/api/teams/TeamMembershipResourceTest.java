package com.tribly.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.tribly.domain.user.User;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamMembershipResourceTest {

  public static final String USERNAME_ADMIN = "user1";
  public static final String USERNAME_TEST = "user2";
  public static final String USERNAME2_TEST = "user3";

  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User adminUser;
  private User memberUser;
  private User thirdUser;

  final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  protected String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
  }

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();

    adminUser = dataService.createUser("user1@example.com", "Admin User");
    memberUser = dataService.createUser("user2@example.com", "Member User");
    thirdUser = dataService.createUser("user3@example.com", "Third User");
  }

  @Test
  void joinTeam_alreadyMember_shouldReturn409() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest("Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Member joins
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Member tries to join again - should fail
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(409)
        .body("code", equalTo("CONFLICT"));
  }

  @Test
  void updateMemberRole_asAdmin_shouldSucceed() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest("Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Member joins
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Admin promotes member to admin
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body("{\"role\": \"ADMIN\"}")
        .when()
        .put("/api/teams/" + slug + "/members/" + TsidUtils.toString(memberUser.getId()))
        .then()
        .statusCode(200)
        .body("role", equalTo("ADMIN"));
  }

  @Test
  void updateMemberRole_asMember_shouldBeDenied() {
    // Create team

    TeamRequest teamRequest =
        new TeamRequest("Role Denied Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Two members join
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME2_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Member tries to promote another member - should be denied
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .body("{\"role\": \"ADMIN\"}")
        .when()
        .put("/api/teams/" + slug + "/members/" + TsidUtils.toString(thirdUser.getId()))
        .then()
        .statusCode(403);
  }

  @Test
  void removeMember_asAdmin_shouldSucceed() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest("Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Member joins
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Admin removes member
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .delete("/api/teams/" + slug + "/members/" + TsidUtils.toString(memberUser.getId()))
        .then()
        .statusCode(204);

    // Verify member count decreased
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .get("/api/teams/" + slug + "/members")
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }

  @Test
  void removeMember_asMember_shouldBeDenied() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest("Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Two members join
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME2_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Member tries to remove another member - should be denied
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .when()
        .delete("/api/teams/" + slug + "/members/" + TsidUtils.toString(thirdUser.getId()))
        .then()
        .statusCode(403);
  }

  @Test
  void removeLastAdmin_shouldBeDenied() {
    // Create team (admin is the only admin)
    TeamRequest teamRequest =
        new TeamRequest("Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Admin tries to leave (self-removal as last admin)
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/leave")
        .then()
        .statusCode(400)
        .body("code", equalTo("LAST_ADMIN"));
  }

  @Test
  void demoteLastAdmin_shouldBeDenied() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest("Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Admin tries to demote themselves (last admin)
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body("{\"role\": \"MEMBER\"}")
        .when()
        .put("/api/teams/" + slug + "/members/" + TsidUtils.toString(adminUser.getId()))
        .then()
        .statusCode(400)
        .body("code", equalTo("LAST_ADMIN"));
  }

  @Test
  void getTeamMembers_withPagination_shouldWork() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest("Team", MediaDto.builder().build(), Visibility.PUBLIC);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Two members join
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME2_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Get members with pagination
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .queryParam("page", 0)
        .queryParam("size", 2)
        .when()
        .get("/api/teams/" + slug + "/members")
        .then()
        .statusCode(200)
        .body("members", hasSize(2))
        .body("total", equalTo(3))
        .body("page", equalTo(0))
        .body("size", equalTo(2));

    // Get second page
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .queryParam("page", 1)
        .queryParam("size", 2)
        .when()
        .get("/api/teams/" + slug + "/members")
        .then()
        .statusCode(200)
        .body("members", hasSize(1))
        .body("total", equalTo(3));
  }

  @Test
  void addMember_byAdmin_shouldSucceed() {
    // Create private team
    TeamRequest teamRequest = new TeamRequest("Team", MediaDto.builder().build(), Visibility.TEAM);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Admin adds member to private team
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(
            "{\"userId\": \""
                + TsidUtils.toString(memberUser.getId())
                + "\", \"role\": \"MEMBER\"}")
        .when()
        .post("/api/teams/" + slug + "/members")
        .then()
        .statusCode(201)
        .body("role", equalTo("MEMBER"));

    // Verify member was added
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .get("/api/teams/" + slug + "/members")
        .then()
        .statusCode(200)
        .body("total", equalTo(2));
  }
}
