package com.tribly.integration;

import com.tribly.domain.ride.RideGroupRepository;
import com.tribly.domain.ride.RideParticipationRepository;
import com.tribly.domain.ride.RideRepository;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.id.TsidUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class TeamMembershipTest {

    public static final String USERNAME_ADMIN = "user1";
    public static final String USERNAME_TEST = "user2";
    public static final String USERNAME2_TEST = "user3";

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
    private User thirdUser;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
    }

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

        adminUser = new User("user1@example.com", "Admin User");
        userRepository.persistAndFlush(adminUser);

        memberUser = new User("user2@example.com", "Member User");
        userRepository.persistAndFlush(memberUser);

        thirdUser = new User("user3@example.com", "Third User");
        userRepository.persistAndFlush(thirdUser);
    }

    @Test
    void joinTeam_alreadyMember_shouldReturn409() {
        // Create team
        String slug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Membership Test Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Member joins
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Member tries to join again - should fail
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
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
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Role Update Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Member joins
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Admin promotes member to admin
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"role\": \"ADMIN\"}")
                .when()
                .put("/v1/teams/" + slug + "/members/" + TsidUtils.toString(memberUser.getId()))
                .then()
                .statusCode(200)
                .body("role", equalTo("ADMIN"));
    }

    @Test
    void updateMemberRole_asMember_shouldBeDenied() {
        // Create team
        String slug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Role Denied Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Two members join
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        given()
                .auth().oauth2(getAccessToken(USERNAME2_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Member tries to promote another member - should be denied
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .body("{\"role\": \"ADMIN\"}")
                .when()
                .put("/v1/teams/" + slug + "/members/" + TsidUtils.toString(thirdUser.getId()))
                .then()
                .statusCode(403);
    }

    @Test
    void removeMember_asAdmin_shouldSucceed() {
        // Create team
        String slug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Remove Member Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Member joins
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Admin removes member
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .delete("/v1/teams/" + slug + "/members/" + TsidUtils.toString(memberUser.getId()))
                .then()
                .statusCode(204);

        // Verify member count decreased
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
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
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Remove Denied Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Two members join
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        given()
                .auth().oauth2(getAccessToken(USERNAME2_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Member tries to remove another member - should be denied
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .when()
                .delete("/v1/teams/" + slug + "/members/" + TsidUtils.toString(thirdUser.getId()))
                .then()
                .statusCode(403);
    }

    @Test
    void removeLastAdmin_shouldBeDenied() {
        // Create team (admin is the only admin)
        String slug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Last Admin Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Admin tries to leave (self-removal as last admin)
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
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
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Demote Admin Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Admin tries to demote themselves (last admin)
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"role\": \"MEMBER\"}")
                .when()
                .put("/v1/teams/" + slug + "/members/" + TsidUtils.toString(adminUser.getId()))
                .then()
                .statusCode(400)
                .body("code", equalTo("LAST_ADMIN"));
    }

    @Test
    void getTeamMembers_withPagination_shouldWork() {
        // Create team
        String slug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Pagination Team\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Two members join
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        given()
                .auth().oauth2(getAccessToken(USERNAME2_TEST))
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + slug + "/members/join")
                .then()
                .statusCode(201);

        // Get members with pagination
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
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
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
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
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Add Member Team\", \"isPublic\": false}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Admin adds member to private team
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"userId\": \"" + TsidUtils.toString(memberUser.getId()) + "\", \"role\": \"MEMBER\"}")
                .when()
                .post("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(201)
                .body("role", equalTo("MEMBER"));

        // Verify member was added
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/v1/teams/" + slug + "/members")
                .then()
                .statusCode(200)
                .body("total", equalTo(2));
    }
}
