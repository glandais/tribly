package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.teams.request.TeamRequest;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamMembershipResourceTest extends AbstractResourceTest {

  public static final String USER1 = "user1";
  public static final String USER2 = "user2";
  public static final String USER3 = "user3";

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void joinTeam_alreadyMember_shouldReturn409() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Member tries to join again - should fail
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(409)
        .body("code", equalTo("ALREADY_REGISTERED"));
  }

  @Test
  void updateMemberRole_asAdmin_shouldSucceed() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Admin promotes member to admin
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body("{\"role\": \"ADMIN\"}")
        .when()
        .put("/api/teams/" + slug + "/members/" + TsidUtils.toString(user2.getId()))
        .then()
        .statusCode(200)
        .body("role", equalTo("ADMIN"));
  }

  @Test
  void updateMemberRole_asMember_shouldBeDenied() {
    // Create team

    TeamRequest teamRequest =
        new TeamRequest(
            "Role Denied Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Member tries to promote another member - should be denied
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body("{\"role\": \"ADMIN\"}")
        .when()
        .put("/api/teams/" + slug + "/members/" + TsidUtils.toString(user3.getId()))
        .then()
        .statusCode(403);
  }

  @Test
  void removeMember_asAdmin_shouldSucceed() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Admin removes member
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + slug + "/members/" + TsidUtils.toString(user2.getId()))
        .then()
        .statusCode(204);

    // Verify member count decreased
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
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
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Member tries to remove another member - should be denied
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/teams/" + slug + "/members/" + TsidUtils.toString(user3.getId()))
        .then()
        .statusCode(403);
  }

  @Test
  void removeLastAdmin_shouldBeDenied() {
    // Create team (admin is the only admin)
    TeamRequest teamRequest =
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER1))
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
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body("{\"role\": \"MEMBER\"}")
        .when()
        .put("/api/teams/" + slug + "/members/" + TsidUtils.toString(user1.getId()))
        .then()
        .statusCode(400)
        .body("code", equalTo("LAST_ADMIN"));
  }

  @Test
  void getTeamMembers_withPagination_shouldWork() {
    // Create team
    TeamRequest teamRequest =
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + slug + "/members/join")
        .then()
        .statusCode(201);

    // Get members with pagination
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER1))
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
    TeamRequest teamRequest =
        new TeamRequest(
            "Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);
    String slug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body("{\"userId\": \"" + TsidUtils.toString(user2.getId()) + "\", \"role\": \"MEMBER\"}")
        .when()
        .post("/api/teams/" + slug + "/members")
        .then()
        .statusCode(201)
        .body("role", equalTo("MEMBER"));

    // Verify member was added
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + slug + "/members")
        .then()
        .statusCode(200)
        .body("total", equalTo(2));
  }
}
