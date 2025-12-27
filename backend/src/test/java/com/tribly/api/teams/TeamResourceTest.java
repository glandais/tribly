package com.tribly.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.user.User;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.team.TeamService;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamResourceTest {

  public static final String USERNAME_ADMIN = "user1";
  public static final String USERNAME_TEST = "user2";

  @Inject TeamService teamService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User adminUser;
  private User memberUser;
  private TeamDetailDto publicTeam;
  private TeamDetailDto privateTeam;

  final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  protected String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
  }

  private static final String ADMIN_EMAIL = "user1@example.com";
  private static final String MEMBER_EMAIL = "user2@example.com";

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();

    // Create admin user
    adminUser = dataService.createUser(ADMIN_EMAIL, "Admin User");

    // Create member user
    memberUser = dataService.createUser(MEMBER_EMAIL, "Member User");

    // Create public team with admin as owner
    publicTeam =
        teamService.createTeam(
            new TeamRequest("Test Public Team", "A public team", Visibility.PUBLIC),
            adminUser.getId());

    // Create private team with admin as owner
    privateTeam =
        teamService.createTeam(
            new TeamRequest("Test Private Team", "A private team", Visibility.TEAM),
            adminUser.getId());
  }

  @Test
  void createTeam_shouldCreateTeamAndMakeUserAdmin() {
    TeamDetailDto team =
        teamService.createTeam(
            new TeamRequest("Test Cyclists", "A great cycling team", Visibility.PUBLIC),
            adminUser.getId());

    assertNotNull(team.id());
    assertEquals("Test Cyclists", team.name());
    assertTrue(team.slug().startsWith("test-cyclists"));
    assertSame(Visibility.PUBLIC, team.visibility());

    TeamRole role = teamService.getUserRole(adminUser.getId(), team.slug()).orElse(null);
    assertEquals(TeamRole.ADMIN, role);
  }

  @Test
  void createTeamViaApi_shouldCreateTeamSuccessfully() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(
            "{\"name\": \"API Test Team\", \"description\": \"Created via API\", \"visibility\":"
                + " \"PUBLIC\"}")
        .when()
        .post("/api/teams")
        .then()
        .statusCode(201)
        .body("name", equalTo("API Test Team"))
        .body("slug", startsWith("api-test-team"))
        .body("visibility", equalTo("PUBLIC"));
  }

  @Test
  void getMyTeams_shouldReturnUserTeams() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .get("/api/teams?member=true")
        .then()
        .statusCode(200)
        .body("teams", hasSize(greaterThanOrEqualTo(2)))
        .body("teams[0].role", equalTo("ADMIN"));
  }

  @Test
  void joinPublicTeam_shouldAddUserAsMember() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + publicTeam.slug() + "/members/join")
        .then()
        .statusCode(201)
        .body("role", equalTo("MEMBER"));
  }

  @Test
  void joinPrivateTeam_shouldBeDenied() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + privateTeam.slug() + "/members/join")
        .then()
        .statusCode(404);
  }

  @Test
  void updateTeam_asAdmin_shouldSucceed() {
    TeamRequest teamRequest = new TeamRequest("Updated Name", "New description", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(teamRequest)
        .when()
        .put("/api/teams/" + publicTeam.slug())
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Name"))
        .body("description", equalTo("New description"));
  }

  @Test
  void updateTeam_asNonAdmin_shouldBeDenied() {
    // Add member to team first via HTTP (pure HTTP pattern)
    addMemberViaApi(publicTeam.slug(), memberUser.getId());

    TeamRequest teamRequest = new TeamRequest("Hacked Name", null, Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .body(teamRequest)
        .when()
        .put("/api/teams/" + publicTeam.slug())
        .then()
        .statusCode(403);
  }

  private void addMemberViaApi(String teamSlug, Long userId) {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body("{\"userId\": \"" + TsidUtils.toString(userId) + "\", \"role\": \"MEMBER\"}")
        .when()
        .post("/api/teams/" + teamSlug + "/members")
        .then()
        .statusCode(201);
  }

  @Test
  void leaveTeam_shouldRemoveMembership() {
    // Add member to team first via HTTP (pure HTTP pattern)
    addMemberViaApi(publicTeam.slug(), memberUser.getId());

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + publicTeam.slug() + "/members/leave")
        .then()
        .statusCode(204);
  }

  @Test
  void getTeamMembers_shouldReturnMemberList() {
    // Add member to team first via HTTP (pure HTTP pattern)
    addMemberViaApi(publicTeam.slug(), memberUser.getId());

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .get("/api/teams/" + publicTeam.slug() + "/members")
        .then()
        .statusCode(200)
        .body("members", hasSize(2))
        .body("total", equalTo(2));
  }

  @Test
  void deleteTeam_asAdmin_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .delete("/api/teams/" + publicTeam.slug())
        .then()
        .statusCode(204);

    // Verify team is no longer accessible
    given().when().get("/api/teams/" + publicTeam.slug()).then().statusCode(404);
  }

  @Test
  void deleteTeam_asNonAdmin_shouldBeDenied() {
    // Add member to team first via HTTP (pure HTTP pattern)
    addMemberViaApi(publicTeam.slug(), memberUser.getId());

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .when()
        .delete("/api/teams/" + publicTeam.slug())
        .then()
        .statusCode(403);
  }

  @Test
  void deleteTeam_asNonMember_shouldBeDenied() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .when()
        .delete("/api/teams/" + privateTeam.slug())
        .then()
        .statusCode(403);
  }
}
