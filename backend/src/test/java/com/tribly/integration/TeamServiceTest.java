package com.tribly.integration;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.TeamRole;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.service.auth.JwtService;
import com.tribly.service.team.TeamService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TeamServiceTest {

    @Inject
    TeamService teamService;

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    @Inject
    JwtService jwtService;

    private User testUser;
    private User anotherUser;
    private String validToken;
    private String anotherUserToken;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up in correct order: user_teams first, then teams and users
        userTeamRepository.delete("user.email like ?1", "team-test-%");
        teamRepository.delete("slug like ?1", "test-%");
        userRepository.delete("email like ?1", "team-test-%");

        testUser = new User("team-test-user@example.com", "Team Test User");
        testUser.setStravaId("team-test-strava-123");
        userRepository.persistAndFlush(testUser);

        anotherUser = new User("team-test-another@example.com", "Another User");
        anotherUser.setStravaId("team-test-strava-456");
        userRepository.persistAndFlush(anotherUser);

        validToken = jwtService.generateToken(testUser);
        anotherUserToken = jwtService.generateToken(anotherUser);
    }

    @Test
    @Transactional
    void createTeam_shouldCreateTeamAndMakeUserAdmin() {
        Team team = teamService.createTeam(
                new TeamService.CreateTeamRequest("Test Cyclists", "A great cycling team", true, null),
                testUser.getId()
        );

        assertNotNull(team.getId());
        assertEquals("Test Cyclists", team.getName());
        assertTrue(team.getSlug().startsWith("test-cyclists"));
        assertTrue(team.isPublic());

        TeamRole role = teamService.getUserRole(testUser.getId(), team.getId()).orElse(null);
        assertEquals(TeamRole.ADMIN, role);
    }

    @Test
    void createTeamViaApi_shouldCreateTeamSuccessfully() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"API Test Team\", \"description\": \"Created via API\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .body("name", equalTo("API Test Team"))
                .body("slug", startsWith("api-test-team"))
                .body("isPublic", equalTo(true));
    }

    @Test
    void getMyTeams_shouldReturnUserTeams() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"My Team\", \"isPublic\": false}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .get("/v1/teams/my")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].role", equalTo("ADMIN"));
    }

    @Test
    void joinPublicTeam_shouldAddUserAsMember() {
        // Create team via HTTP so it's committed before join request
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Public Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201)
                .body("role", equalTo("MEMBER"));
    }

    @Test
    void joinPrivateTeam_shouldBeDenied() {
        // Create private team via HTTP
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Private Team\", \"isPublic\": false}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(403);
    }

    @Test
    void updateTeam_asAdmin_shouldSucceed() {
        // Create team via HTTP
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Original Name\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Updated Name\", \"description\": \"New description\"}")
                .when()
                .put("/v1/teams/" + slug)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Name"))
                .body("description", equalTo("New description"));
    }

    @Test
    void updateTeam_asNonAdmin_shouldBeDenied() {
        // Create public team via HTTP
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Admin Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Another user joins via HTTP
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Non-admin tries to update - should be denied
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .body("{\"name\": \"Hacked Name\"}")
                .when()
                .put("/v1/teams/" + slug)
                .then()
                .statusCode(403);
    }

    @Test
    void leaveTeam_shouldRemoveMembership() {
        // Create public team via HTTP
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Leave Test\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Another user joins via HTTP
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // User leaves the team
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/leave")
                .then()
                .statusCode(204);
    }

    @Test
    void getTeamMembers_shouldReturnMemberList() {
        // Create public team via HTTP
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Members Test\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Another user joins via HTTP
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Get team members
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .get("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(200)
                .body("members", hasSize(2))
                .body("total", equalTo(2));
    }

    @Test
    void deleteTeam_asAdmin_shouldSucceed() {
        // Create team via HTTP with unique name (creator becomes admin)
        String teamName = "Delete Test " + System.currentTimeMillis();
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"" + teamName + "\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Admin deletes the team
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .delete("/v1/teams/" + slug)
                .then()
                .statusCode(204);

        // Verify team is no longer accessible
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .get("/v1/teams/" + slug)
                .then()
                .statusCode(404);
    }

    @Test
    void deleteTeam_asNonAdmin_shouldBeDenied() {
        // Create public team via HTTP
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"No Delete Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Another user joins as member
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Non-admin tries to delete - should be denied
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .when()
                .delete("/v1/teams/" + slug)
                .then()
                .statusCode(403);
    }

    @Test
    void deleteTeam_asNonMember_shouldBeDenied() {
        // Create team via HTTP
        String slug = given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"name\": \"Protected Team\", \"isPublic\": false}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Non-member tries to delete - should be denied
        given()
                .header("Authorization", "Bearer " + anotherUserToken)
                .when()
                .delete("/v1/teams/" + slug)
                .then()
                .statusCode(403);
    }
}
