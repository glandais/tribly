package com.tribly.integration;

import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class TeamMembershipTest {

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    private User adminUser;
    private User memberUser;
    private User thirdUser;
    private String adminToken;
    private String memberToken;
    private String thirdUserToken;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up in correct order
        userTeamRepository.delete("user.email like ?1", "membership-test-%");
        teamRepository.delete("slug like ?1", "membership-test-%");
        userRepository.delete("email like ?1", "membership-test-%");

        long timestamp = System.currentTimeMillis();

        adminUser = new User("membership-test-admin@example.com", "Admin User");
        adminUser.setStravaId("membership-admin-strava-" + timestamp);
        userRepository.persistAndFlush(adminUser);

        memberUser = new User("membership-test-member@example.com", "Member User");
        memberUser.setStravaId("membership-member-strava-" + timestamp);
        userRepository.persistAndFlush(memberUser);

        thirdUser = new User("membership-test-third@example.com", "Third User");
        thirdUser.setStravaId("membership-third-strava-" + timestamp);
        userRepository.persistAndFlush(thirdUser);

        adminToken = generateToken(
                "keycloak-admin-" + timestamp,
                adminUser.getEmail(),
                adminUser.getDisplayName(),
                adminUser.getStravaId()
        );
        memberToken = generateToken(
                "keycloak-member-" + timestamp,
                memberUser.getEmail(),
                memberUser.getDisplayName(),
                memberUser.getStravaId()
        );
        thirdUserToken = generateToken(
                "keycloak-third-" + timestamp,
                thirdUser.getEmail(),
                thirdUser.getDisplayName(),
                thirdUser.getStravaId()
        );
    }

    /**
     * Generate a JWT token compatible with OidcWiremock.
     * The WireMock server automatically configures the test environment to accept these tokens.
     */
    private String generateToken(String subject, String email, String name, String stravaId) {
        return Jwt.subject(subject)
                .issuer("https://server.example.com")
                .audience("https://service.example.com")
                .groups(Set.of("user"))
                .claim("email", email)
                .claim("name", name)
                .claim("strava_id", stravaId)
                .sign();
    }

    @Test
    void joinTeam_alreadyMember_shouldReturn409() {
        // Create team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Membership Test Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Member joins
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Member tries to join again - should fail
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(409)
                .body("code", equalTo("ALREADY_MEMBER"));
    }

    @Test
    void updateMemberRole_asAdmin_shouldSucceed() {
        // Create team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Role Update Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Member joins
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Admin promotes member to admin
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"role\": \"ADMIN\"}")
                .when()
                .put("/v1/teams/" + slug + "/members/" + memberUser.getId())
                .then()
                .statusCode(200)
                .body("role", equalTo("ADMIN"));
    }

    @Test
    void updateMemberRole_asMember_shouldBeDenied() {
        // Create team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Role Denied Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Two members join
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + thirdUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Member tries to promote another member - should be denied
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .body("{\"role\": \"ADMIN\"}")
                .when()
                .put("/v1/teams/" + slug + "/members/" + thirdUser.getId())
                .then()
                .statusCode(403);
    }

    @Test
    void removeMember_asAdmin_shouldSucceed() {
        // Create team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Remove Member Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Member joins
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Admin removes member
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/v1/teams/" + slug + "/members/" + memberUser.getId())
                .then()
                .statusCode(204);

        // Verify member count decreased
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(200)
                .body("total", equalTo(1));
    }

    @Test
    void removeMember_asMember_shouldBeDenied() {
        // Create team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Remove Denied Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Two members join
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + thirdUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Member tries to remove another member - should be denied
        given()
                .header("Authorization", "Bearer " + memberToken)
                .when()
                .delete("/v1/teams/" + slug + "/members/" + thirdUser.getId())
                .then()
                .statusCode(403);
    }

    @Test
    void removeLastAdmin_shouldBeDenied() {
        // Create team (admin is the only admin)
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Last Admin Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Admin tries to leave (self-removal as last admin)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/leave")
                .then()
                .statusCode(400)
                .body("code", equalTo("LAST_ADMIN"));
    }

    @Test
    void demoteLastAdmin_shouldBeDenied() {
        // Create team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Demote Admin Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Admin tries to demote themselves (last admin)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"role\": \"MEMBER\"}")
                .when()
                .put("/v1/teams/" + slug + "/members/" + adminUser.getId())
                .then()
                .statusCode(400)
                .body("code", equalTo("LAST_ADMIN"));
    }

    @Test
    void getTeamMembers_withPagination_shouldWork() {
        // Create team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Pagination Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Two members join
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + thirdUserToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Get members with pagination
        given()
                .header("Authorization", "Bearer " + adminToken)
                .queryParam("page", 0)
                .queryParam("size", 2)
                .when()
                .get("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(200)
                .body("members", hasSize(2))
                .body("total", equalTo(3))
                .body("page", equalTo(0))
                .body("size", equalTo(2));

        // Get second page
        given()
                .header("Authorization", "Bearer " + adminToken)
                .queryParam("page", 1)
                .queryParam("size", 2)
                .when()
                .get("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(200)
                .body("members", hasSize(1))
                .body("total", equalTo(3));
    }

    @Test
    void addMember_byAdmin_shouldSucceed() {
        // Create private team
        String slug = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Add Member Team\", \"isPublic\": false}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Admin adds member to private team
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"userId\": " + memberUser.getId() + ", \"role\": \"MEMBER\"}")
                .when()
                .post("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(201)
                .body("role", equalTo("MEMBER"));

        // Verify member was added
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(200)
                .body("total", equalTo(2));
    }
}
