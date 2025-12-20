package com.tribly.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ride.repository.RideGroupRepository;
import com.tribly.domain.ride.repository.RideParticipationRepository;
import com.tribly.domain.ride.repository.RideRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.team.TeamService;
import com.tribly.service.team.request.CreateTeamRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamResourceTest {

  public static final String USERNAME_ADMIN = "user1";
  public static final String USERNAME_TEST = "user2";

  @Inject TeamService teamService;

  @Inject RideRepository rideRepository;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject RideParticipationRepository participationRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserTeamRepository userTeamRepository;

  @Inject UserRepository userRepository;

  private User adminUser;
  private User memberUser;
  private Team publicTeam;
  private Team privateTeam;

  final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  protected String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
  }

  private static final String ADMIN_EMAIL = "user1@example.com";
  private static final String MEMBER_EMAIL = "user2@example.com";

  @BeforeEach
  @Transactional
  void setUp() {
    // Clean up in correct order
    participationRepository.deleteAll();
    rideGroupRepository.deleteAll();
    rideRepository.deleteAll();
    userTeamRepository.deleteAll();
    teamRepository.deleteAll();
    userRepository.deleteAll();

    // Create admin user
    adminUser = new User(ADMIN_EMAIL, "Admin User");
    userRepository.persistAndFlush(adminUser);

    // Create member user
    memberUser = new User(MEMBER_EMAIL, "Member User");
    userRepository.persistAndFlush(memberUser);

    // Create public team with admin as owner
    publicTeam =
        teamService
            .createTeam(
                new CreateTeamRequest("Test Public Team", "A public team", Visibility.PUBLIC, null),
                adminUser.getId())
            .team();

    // Create private team with admin as owner
    privateTeam =
        teamService
            .createTeam(
                new CreateTeamRequest("Test Private Team", "A private team", Visibility.TEAM, null),
                adminUser.getId())
            .team();
  }

  @Test
  @Transactional
  void createTeam_shouldCreateTeamAndMakeUserAdmin() {
    Team team =
        teamService
            .createTeam(
                new CreateTeamRequest(
                    "Test Cyclists", "A great cycling team", Visibility.PUBLIC, null),
                adminUser.getId())
            .team();

    assertNotNull(team.getId());
    assertEquals("Test Cyclists", team.getName());
    assertTrue(team.getSlug().startsWith("test-cyclists"));
    assertSame(Visibility.PUBLIC, team.getVisibility());

    TeamRole role = teamService.getUserRole(adminUser.getId(), team.getSlug()).orElse(null);
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
        .post("/api/teams/" + publicTeam.getSlug() + "/members/join")
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
        .post("/api/teams/" + privateTeam.getSlug() + "/members/join")
        .then()
        .statusCode(404);
  }

  @Test
  void updateTeam_asAdmin_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body("{\"name\": \"Updated Name\", \"description\": \"New description\"}")
        .when()
        .put("/api/teams/" + publicTeam.getSlug())
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Name"))
        .body("description", equalTo("New description"));
  }

  @Test
  void updateTeam_asNonAdmin_shouldBeDenied() {
    // Add member to team first via HTTP (pure HTTP pattern)
    addMemberViaApi(publicTeam.getSlug(), memberUser.getId());

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .body("{\"name\": \"Hacked Name\"}")
        .when()
        .put("/api/teams/" + publicTeam.getSlug())
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
    addMemberViaApi(publicTeam.getSlug(), memberUser.getId());

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + publicTeam.getSlug() + "/members/leave")
        .then()
        .statusCode(204);
  }

  @Test
  void getTeamMembers_shouldReturnMemberList() {
    // Add member to team first via HTTP (pure HTTP pattern)
    addMemberViaApi(publicTeam.getSlug(), memberUser.getId());

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .get("/api/teams/" + publicTeam.getSlug() + "/members")
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
        .delete("/api/teams/" + publicTeam.getSlug())
        .then()
        .statusCode(204);

    // Verify team is no longer accessible
    given().when().get("/api/teams/" + publicTeam.getSlug()).then().statusCode(404);
  }

  @Test
  void deleteTeam_asNonAdmin_shouldBeDenied() {
    // Add member to team first via HTTP (pure HTTP pattern)
    addMemberViaApi(publicTeam.getSlug(), memberUser.getId());

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .when()
        .delete("/api/teams/" + publicTeam.getSlug())
        .then()
        .statusCode(403);
  }

  @Test
  void deleteTeam_asNonMember_shouldBeDenied() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .when()
        .delete("/api/teams/" + privateTeam.getSlug())
        .then()
        .statusCode(403);
  }
}
