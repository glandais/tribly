package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.common.request.SlugChangeRequest;
import fr.pedalons.dto.teams.request.TeamRequest;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.team.TeamService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamResourceTest extends AbstractResourceTest {

  @Inject TeamService teamService;
  @Inject PedalonsQueryContext queryContext;

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

  // ==================== Visibility: PUBLIC_UNLISTED ====================

  @Test
  void listTeams_anonymous_shouldExcludePublicUnlisted() {
    dataService.createTeam(user4, "Unlisted Club", "unlisted-club", Visibility.PUBLIC_UNLISTED);

    given()
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        // team1 is PUBLIC and must be listed, the unlisted team must never be
        .body("teams.slug", hasItem(team1Slug))
        .body("teams.slug", not(hasItem("unlisted-club")))
        .body("teams.slug", not(hasItem(team2Slug)));
  }

  @Test
  void listTeams_member_shouldIncludePublicUnlisted() {
    dataService.createTeam(user4, "Unlisted Club", "unlisted-club", Visibility.PUBLIC_UNLISTED);

    // user4 is ADMIN/member of the unlisted team, so it must appear in its own listing
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.slug", hasItem("unlisted-club"));
  }

  @Test
  void listTeams_loggedNonMember_shouldExcludePublicUnlisted() {
    dataService.createTeam(user4, "Unlisted Club", "unlisted-club", Visibility.PUBLIC_UNLISTED);

    // user5 is a member of neither the unlisted team nor team1/team2
    given()
        .auth()
        .oauth2(getAccessToken(USER5))
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.slug", not(hasItem("unlisted-club")));
  }

  @Test
  void listTeams_anonymousSearch_shouldNotFindPublicUnlisted() {
    dataService.createTeam(user4, "Hidden Squad", "hidden-squad", Visibility.PUBLIC_UNLISTED);

    given()
        .queryParam("search", "Hidden")
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.slug", not(hasItem("hidden-squad")));
  }

  @Test
  void getTeam_publicUnlisted_anonymous_shouldReturnTeamDetails() {
    dataService.createTeam(user4, "Unlisted Club", "unlisted-club", Visibility.PUBLIC_UNLISTED);

    // direct URL access is the whole point of an unlisted team
    given()
        .when()
        .get("/api/teams/unlisted-club")
        .then()
        .statusCode(200)
        .body("slug", equalTo("unlisted-club"))
        .body("visibility", equalTo("PUBLIC_UNLISTED"));
  }

  @Test
  void getTeam_privateTeam_anonymous_shouldReturn403() {
    // team2 has TEAM visibility: not reachable by URL for a non-member
    given().when().get("/api/teams/" + team2Slug).then().statusCode(403);
  }

  @Test
  void createTeamViaApi_shouldCreateTeamSuccessfully() {
    TeamRequest teamRequest =
        new TeamRequest(
            "API Test Team",
            MediaDto.builder().build(),
            Visibility.TEAM,
            true,
            true,
            true,
            true,
            true,
            null);
    given()
        .auth()
        .oauth2(getAccessToken(USER5))
        .contentType("application/json")
        .body(teamRequest)
        .when()
        .post("/api/teams")
        .then()
        .statusCode(201)
        .body("name", equalTo("API Test Team"))
        .body("slug", startsWith("api-test-team"))
        .body("visibility", equalTo("TEAM"));
  }

  @Test
  void createTeam_withoutAuth_shouldReturn401() {
    TeamRequest teamRequest =
        new TeamRequest(
            "API Test Team",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);
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
        .get("/api/teams?minRole=MEMBER")
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
        .statusCode(403);
  }

  @Test
  void updateTeam_asAdmin_shouldSucceed() {
    TeamRequest teamRequest =
        new TeamRequest(
            "Updated Name",
            MediaDto.builder().markdown("New description").build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);

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
        .body("about.markdown", equalTo("New description"));
  }

  @Test
  void updateTeam_asNonAdmin_shouldBeDenied() {
    TeamRequest teamRequest =
        new TeamRequest(
            "Hacked Name",
            MediaDto.builder().build(),
            Visibility.PUBLIC,
            true,
            true,
            true,
            true,
            true,
            null);

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

  // ==================== Change Slug Tests ====================

  @Test
  void changeSlug_asAdmin_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(new SlugChangeRequest("new-team-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/slug")
        .then()
        .statusCode(200)
        .body("slug", equalTo("new-team-slug"));
  }

  @Test
  void changeSlug_asNonAdmin_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(new SlugChangeRequest("hacked-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/slug")
        .then()
        .statusCode(403);
  }

  @Test
  void changeSlug_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .body(new SlugChangeRequest("unauth-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/slug")
        .then()
        .statusCode(401);
  }

  @Test
  void changeSlug_asNonMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .contentType("application/json")
        .body(new SlugChangeRequest("nonmember-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/slug")
        .then()
        .statusCode(403);
  }
}
