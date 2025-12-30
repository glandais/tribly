package com.tribly.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.team.TeamService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamResourceTest extends AbstractResourceTest {

  @Inject TeamService teamService;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void listPublicTeams_shouldReturnEmptyList() {
    given()
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams", is(notNullValue()))
        .body("total", greaterThanOrEqualTo(0))
        .body("page", equalTo(0))
        .body("size", equalTo(20));
  }

  @Test
  void listPublicTeams_shouldSupportPagination() {
    given()
        .queryParam("page", 1)
        .queryParam("size", 10)
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("page", equalTo(1))
        .body("size", equalTo(10));
  }

  @Test
  void listPublicTeams_shouldSupportSearch() {
    given()
        .queryParam("search", "cycling")
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams", is(notNullValue()));
  }

  @Test
  void getTeam_withNonexistentSlug_shouldReturn404() {
    given().when().get("/api/teams/nonexistent-team-slug").then().statusCode(404);
  }

  @Test
  void getTeam_publicTeam_shouldReturnTeamDetails() {
    given()
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("slug", equalTo(team1Slug))
        .body("name", equalTo("Team 1"))
        .body("visibility", equalTo("PUBLIC"));
  }

  @Test
  void createTeam_shouldCreateTeamAndMakeUserAdmin() {
    TeamDetailDto team =
        teamService.createTeam(
            new TeamRequest(
                "Test Cyclists",
                MediaDto.builder().markdown("A great cycling team").build(),
                Visibility.PUBLIC),
            user1.getId());

    assertNotNull(team.id());
    assertEquals("Test Cyclists", team.name());
    assertTrue(team.slug().startsWith("test-cyclists"));
    assertSame(Visibility.PUBLIC, team.visibility());

    TeamRole role = teamService.getUserRole(user1.getId(), team.slug()).orElse(null);
    assertEquals(TeamRole.ADMIN, role);
  }

  @Test
  void createTeamViaApi_shouldCreateTeamSuccessfully() {
    TeamRequest teamRequest =
        new TeamRequest("API Test Team", MediaDto.builder().build(), Visibility.PUBLIC);
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(teamRequest)
        .when()
        .post("/api/teams")
        .then()
        .statusCode(201)
        .body("name", equalTo("API Test Team"))
        .body("slug", startsWith("api-test-team"))
        .body("visibility", equalTo("PUBLIC"));
  }

  @Test
  void createTeam_withoutAuth_shouldReturn401() {
    TeamRequest teamRequest =
        new TeamRequest("API Test Team", MediaDto.builder().build(), Visibility.PUBLIC);
    given()
        .contentType("application/json")
        .body(teamRequest)
        .when()
        .post("/api/teams")
        .then()
        .statusCode(401);
  }

  @Test
  void getMyTeams_shouldReturnUserTeams() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
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
        .oauth2(getAccessToken(USER4))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/members/join")
        .then()
        .statusCode(201)
        .body("role", equalTo("MEMBER"));
  }

  @Test
  void joinPrivateTeam_shouldBeDenied() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team2Slug + "/members/join")
        .then()
        .statusCode(404);
  }

  @Test
  void updateTeam_asAdmin_shouldSucceed() {
    TeamRequest teamRequest =
        new TeamRequest(
            "Updated Name",
            MediaDto.builder().markdown("New description").build(),
            Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(teamRequest)
        .when()
        .put("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Name"))
        .body("media.markdown", equalTo("New description"));
  }

  @Test
  void updateTeam_asNonAdmin_shouldBeDenied() {
    TeamRequest teamRequest =
        new TeamRequest("Hacked Name", MediaDto.builder().build(), Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(teamRequest)
        .when()
        .put("/api/teams/" + team1Slug)
        .then()
        .statusCode(403);
  }

  @Test
  void leaveTeam_shouldRemoveMembership() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/members/leave")
        .then()
        .statusCode(204);
  }

  @Test
  void getTeamMembers_shouldReturnMemberList() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/members")
        .then()
        .statusCode(200)
        .body("members", hasSize(3))
        .body("total", equalTo(3));
  }

  @Test
  void getMembers_withoutAuth_shouldReturn401() {
    given().when().get("/api/teams/some-team/members").then().statusCode(401);
  }

  @Test
  void deleteTeam_asAdmin_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug)
        .then()
        .statusCode(204);

    // Verify team is no longer accessible
    given().when().get("/api/teams/" + team1Slug).then().statusCode(404);
  }

  @Test
  void deleteTeam_asNonAdmin_shouldBeDenied() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/teams/" + team1Slug)
        .then()
        .statusCode(403);
  }

  @Test
  void deleteTeam_asNonMember_shouldBeDenied() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/teams/" + team2Slug)
        .then()
        .statusCode(403);
  }
}
