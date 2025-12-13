package com.tribly.integration;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.TeamRole;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.service.team.TeamMembershipService;
import com.tribly.service.team.TeamService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class TeamServiceTest {

    @Inject
    TeamService teamService;

    @Inject
    TeamMembershipService teamMembershipService;

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    private User adminUser;
    private User memberUser;
    private Team publicTeam;
    private Team privateTeam;

    private static final String ADMIN_EMAIL = "team-admin@example.com";
    private static final String ADMIN_STRAVA_ID = "team-admin-strava";
    private static final String MEMBER_EMAIL = "team-member@example.com";
    private static final String MEMBER_STRAVA_ID = "team-member-strava";

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up in correct order: user_teams first, then teams and users
        userTeamRepository.delete("user.email in (?1, ?2)", ADMIN_EMAIL, MEMBER_EMAIL);
        teamRepository.delete("slug like ?1", "test-%");
        userRepository.delete("email in (?1, ?2)", ADMIN_EMAIL, MEMBER_EMAIL);

        // Create admin user
        adminUser = new User(ADMIN_EMAIL, "Admin User");
        adminUser.setStravaId(ADMIN_STRAVA_ID);
        userRepository.persistAndFlush(adminUser);

        // Create member user
        memberUser = new User(MEMBER_EMAIL, "Member User");
        memberUser.setStravaId(MEMBER_STRAVA_ID);
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
    @TestSecurity(user = "admin-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = ADMIN_EMAIL),
            @Claim(key = "name", value = "Admin User"),
            @Claim(key = "strava_id", value = ADMIN_STRAVA_ID)
    })
    void createTeamViaApi_shouldCreateTeamSuccessfully() {
        given()
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
    @TestSecurity(user = "admin-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = ADMIN_EMAIL),
            @Claim(key = "name", value = "Admin User"),
            @Claim(key = "strava_id", value = ADMIN_STRAVA_ID)
    })
    void getMyTeams_shouldReturnUserTeams() {
        given()
                .when()
                .get("/v1/teams/my")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(2)))
                .body("[0].role", equalTo("ADMIN"));
    }

    @Test
    @TestSecurity(user = "member-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = MEMBER_EMAIL),
            @Claim(key = "name", value = "Member User"),
            @Claim(key = "strava_id", value = MEMBER_STRAVA_ID)
    })
    void joinPublicTeam_shouldAddUserAsMember() {
        given()
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + publicTeam.getSlug() + "/members/join")
                .then()
                .statusCode(201)
                .body("role", equalTo("MEMBER"));
    }

    @Test
    @TestSecurity(user = "member-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = MEMBER_EMAIL),
            @Claim(key = "name", value = "Member User"),
            @Claim(key = "strava_id", value = MEMBER_STRAVA_ID)
    })
    void joinPrivateTeam_shouldBeDenied() {
        given()
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + privateTeam.getSlug() + "/members/join")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = ADMIN_EMAIL),
            @Claim(key = "name", value = "Admin User"),
            @Claim(key = "strava_id", value = ADMIN_STRAVA_ID)
    })
    void updateTeam_asAdmin_shouldSucceed() {
        given()
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
    @Transactional
    @TestSecurity(user = "member-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = MEMBER_EMAIL),
            @Claim(key = "name", value = "Member User"),
            @Claim(key = "strava_id", value = MEMBER_STRAVA_ID)
    })
    void updateTeam_asNonAdmin_shouldBeDenied() {
        // Add member to team first
        teamMembershipService.addMember(publicTeam.getId(), memberUser.getId(), TeamRole.MEMBER, adminUser.getId());

        given()
                .contentType("application/json")
                .body("{\"name\": \"Hacked Name\"}")
                .when()
                .put("/v1/teams/" + publicTeam.getSlug())
                .then()
                .statusCode(403);
    }

    @Test
    @Transactional
    @TestSecurity(user = "member-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = MEMBER_EMAIL),
            @Claim(key = "name", value = "Member User"),
            @Claim(key = "strava_id", value = MEMBER_STRAVA_ID)
    })
    void leaveTeam_shouldRemoveMembership() {
        // Add member to team first
        teamMembershipService.addMember(publicTeam.getId(), memberUser.getId(), TeamRole.MEMBER, adminUser.getId());

        given()
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + publicTeam.getSlug() + "/members/leave")
                .then()
                .statusCode(204);
    }

    @Test
    @Transactional
    @TestSecurity(user = "admin-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = ADMIN_EMAIL),
            @Claim(key = "name", value = "Admin User"),
            @Claim(key = "strava_id", value = ADMIN_STRAVA_ID)
    })
    void getTeamMembers_shouldReturnMemberList() {
        // Add member to team first
        teamMembershipService.addMember(publicTeam.getId(), memberUser.getId(), TeamRole.MEMBER, adminUser.getId());

        given()
                .when()
                .get("/v1/teams/" + publicTeam.getSlug() + "/members")
                .then()
                .statusCode(200)
                .body("members", hasSize(2))
                .body("total", equalTo(2));
    }

    @Test
    @TestSecurity(user = "admin-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = ADMIN_EMAIL),
            @Claim(key = "name", value = "Admin User"),
            @Claim(key = "strava_id", value = ADMIN_STRAVA_ID)
    })
    void deleteTeam_asAdmin_shouldSucceed() {
        given()
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
    @Transactional
    @TestSecurity(user = "member-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = MEMBER_EMAIL),
            @Claim(key = "name", value = "Member User"),
            @Claim(key = "strava_id", value = MEMBER_STRAVA_ID)
    })
    void deleteTeam_asNonAdmin_shouldBeDenied() {
        // Add member to team first
        teamMembershipService.addMember(publicTeam.getId(), memberUser.getId(), TeamRole.MEMBER, adminUser.getId());

        given()
                .when()
                .delete("/v1/teams/" + publicTeam.getSlug())
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "member-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = MEMBER_EMAIL),
            @Claim(key = "name", value = "Member User"),
            @Claim(key = "strava_id", value = MEMBER_STRAVA_ID)
    })
    void deleteTeam_asNonMember_shouldBeDenied() {
        given()
                .when()
                .delete("/v1/teams/" + privateTeam.getSlug())
                .then()
                .statusCode(403);
    }
}
