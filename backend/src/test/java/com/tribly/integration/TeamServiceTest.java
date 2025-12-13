package com.tribly.integration;

import com.tribly.domain.ride.RideGroupRepository;
import com.tribly.domain.ride.RideParticipationRepository;
import com.tribly.domain.ride.RideRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.TeamRole;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.service.team.TeamService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TeamServiceTest {

    public static final String USERNAME_ADMIN = "user1";
    public static final String USERNAME_TEST = "user2";

    @Inject
    TeamService teamService;

    @Inject
    RideRepository rideRepository;

    @Inject
    RideGroupRepository rideGroupRepository;

    @Inject
    RideParticipationRepository participationRepository;

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    @Inject
    UserRepository userRepository;

    private User adminUser;
    private User memberUser;
    private Team publicTeam;
    private Team privateTeam;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

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
        publicTeam = teamService.createTeam(
                new TeamService.CreateTeamRequest("Test Public Team", "A public team", true, null),
                adminUser.getId()
        );

        // Create private team with admin as owner
        privateTeam = teamService.createTeam(
                new TeamService.CreateTeamRequest("Test Private Team", "A private team", false, null),
                adminUser.getId()
        );
    }

    @Test
    @Transactional
    void createTeam_shouldCreateTeamAndMakeUserAdmin() {
        Team team = teamService.createTeam(
                new TeamService.CreateTeamRequest("Test Cyclists", "A great cycling team", true, null),
                adminUser.getId()
        );

        assertNotNull(team.getId());
        assertEquals("Test Cyclists", team.getName());
        assertTrue(team.getSlug().startsWith("test-cyclists"));
        assertTrue(team.isPublic());

        TeamRole role = teamService.getUserRole(adminUser.getId(), team.getId()).orElse(null);
        assertEquals(TeamRole.ADMIN, role);
    }

    @Test
    void createTeamViaApi_shouldCreateTeamSuccessfully() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
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
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/v1/teams/my")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(2)))
                .body("[0].role", equalTo("ADMIN"));
    }

    @Test
    void joinPublicTeam_shouldAddUserAsMember() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + publicTeam.getSlug() + "/members/join")
                .then()
                .statusCode(201)
                .body("role", equalTo("MEMBER"));
    }

    @Test
    void joinPrivateTeam_shouldBeDenied() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + privateTeam.getSlug() + "/members/join")
                .then()
                .statusCode(403);
    }

    @Test
    void updateTeam_asAdmin_shouldSucceed() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Updated Name\", \"description\": \"New description\"}")
                .when()
                .put("/v1/teams/" + publicTeam.getSlug())
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
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .body("{\"name\": \"Hacked Name\"}")
                .when()
                .put("/v1/teams/" + publicTeam.getSlug())
                .then()
                .statusCode(403);
    }

    private void addMemberViaApi(String teamSlug, Long userId) {
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"userId\": " + userId + ", \"role\": \"MEMBER\"}")
                .when()
                .post("/v1/teams/" + teamSlug + "/members")
                .then()
                .statusCode(201);
    }

    @Test
    void leaveTeam_shouldRemoveMembership() {
        // Add member to team first via HTTP (pure HTTP pattern)
        addMemberViaApi(publicTeam.getSlug(), memberUser.getId());

        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + publicTeam.getSlug() + "/members/leave")
                .then()
                .statusCode(204);
    }

    @Test
    void getTeamMembers_shouldReturnMemberList() {
        // Add member to team first via HTTP (pure HTTP pattern)
        addMemberViaApi(publicTeam.getSlug(), memberUser.getId());

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/v1/teams/" + publicTeam.getSlug() + "/members")
                .then()
                .statusCode(200)
                .body("members", hasSize(2))
                .body("total", equalTo(2));
    }

    @Test
    void deleteTeam_asAdmin_shouldSucceed() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .delete("/v1/teams/" + publicTeam.getSlug())
                .then()
                .statusCode(204);

        // Verify team is no longer accessible
        given()
                .when()
                .get("/v1/teams/" + publicTeam.getSlug())
                .then()
                .statusCode(404);
    }

    @Test
    void deleteTeam_asNonAdmin_shouldBeDenied() {
        // Add member to team first via HTTP (pure HTTP pattern)
        addMemberViaApi(publicTeam.getSlug(), memberUser.getId());

        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .when()
                .delete("/v1/teams/" + publicTeam.getSlug())
                .then()
                .statusCode(403);
    }

    @Test
    void deleteTeam_asNonMember_shouldBeDenied() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .when()
                .delete("/v1/teams/" + privateTeam.getSlug())
                .then()
                .statusCode(403);
    }
}
