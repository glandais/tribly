package com.tribly.integration;

import com.tribly.domain.ride.RideGroupRepository;
import com.tribly.domain.ride.RideParticipationRepository;
import com.tribly.domain.ride.RideRepository;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RideServiceTest {

    public static final String USERNAME_ADMIN = "user1";
    public static final String USERNAME_TEST = "user2";

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

    private String teamSlug;

    final KeycloakTestClient keycloakClient = new KeycloakTestClient();

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
    }

    private void createTeamViaHttp() {
        var response = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Ride Test Team " + System.currentTimeMillis() + "\", \"visibility\": \"PUBLIC\"}")
                .when()
                .post("/api/teams")
                .then()
                .statusCode(201)
                .extract();

        teamSlug = response.path("slug");
    }

    private void memberJoinsTeam() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/api/teams/" + teamSlug + "/members/join")
                .then()
                .statusCode(201);
    }

    @Test
    void createRide_asAdmin_shouldSucceed() {
        createTeamViaHttp();

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Sunday Morning Ride\", \"date\": \"2025-01-20\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .body("title", equalTo("Sunday Morning Ride"))
                .body("date", equalTo("2025-01-20"))
                .body("status", equalTo("DRAFT"))
                .body("groupCount", equalTo(1));
    }

    @Test
    void createRide_asMember_shouldBeDenied() {
        createTeamViaHttp();
        memberJoinsTeam();

        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .body("{\"title\": \"Forbidden Ride\", \"date\": \"2025-01-20\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(403);
    }

    @Test
    void createRide_withGroups_shouldCreateAllGroups() {
        createTeamViaHttp();

        String body = """
                {
                    "title": "Multi-Group Ride",
                    "date": "2025-01-21",
                    "groups": [
                        {"name": "Fast", "averageSpeed": 32, "maxParticipants": 10},
                        {"name": "Medium", "averageSpeed": 28, "maxParticipants": 15},
                        {"name": "Social", "averageSpeed": 25}
                    ]
                }
                """;

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .body("title", equalTo("Multi-Group Ride"))
                .body("groupCount", equalTo(3));
    }

    @Test
    void getRide_shouldReturnRideDetails() {
        createTeamViaHttp();

        String rideSlug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Get Test Ride\", \"date\": \"2025-01-22\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .extract().path("slug");

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/api/teams/" + teamSlug + "/rides/" + rideSlug)
                .then()
                .statusCode(200)
                .body("slug", equalTo(rideSlug))
                .body("title", equalTo("Get Test Ride"))
                .body("groups", hasSize(1));
    }

    @Test
    void listRides_shouldReturnTeamRides() {
        createTeamViaHttp();

        // Create two rides
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Ride 1\", \"date\": \"2025-01-23\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201);

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Ride 2\", \"date\": \"2025-01-24\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201);

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(200)
                .body("rides", hasSize(greaterThanOrEqualTo(2)))
                .body("total", greaterThanOrEqualTo(2));
    }

    @Test
    void updateRide_asAdmin_shouldSucceed() {
        createTeamViaHttp();

        String rideSlug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Original Title\", \"date\": \"2025-01-25\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .extract().path("slug");

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Updated Title\", \"status\": \"PUBLISHED\"}")
                .when()
                .patch("/api/teams/" + teamSlug + "/rides/" + rideSlug)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"))
                .body("status", equalTo("PUBLISHED"));
    }

    @Test
    void deleteRide_asAdmin_shouldSucceed() {
        createTeamViaHttp();

        String rideSlug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"To Be Deleted\", \"date\": \"2025-01-26\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .extract().path("slug");

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .delete("/api/teams/" + teamSlug + "/rides/" + rideSlug)
                .then()
                .statusCode(204);

        // Verify it's gone
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/api/teams/" + teamSlug + "/rides/" + rideSlug)
                .then()
                .statusCode(404);
    }

    @Test
    void joinRide_shouldAddParticipant() {
        createTeamViaHttp();
        memberJoinsTeam();

        // Create and publish ride
        String rideSlug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Join Test Ride\", \"date\": \"2025-01-27\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Publish the ride
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"status\": \"PUBLISHED\"}")
                .when()
                .patch("/api/teams/" + teamSlug + "/rides/" + rideSlug)
                .then()
                .statusCode(200);

        // Get the group ID
        String groupId = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
                .then()
                .statusCode(200)
                .extract().path("data[0].id");

        // Member joins the ride
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .body("{\"notes\": \"Looking forward to it!\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
                .then()
                .statusCode(201)
                .body("status", equalTo("REGISTERED"));
    }

    @Test
    void joinRide_whenNotPublished_shouldBeDenied() {
        createTeamViaHttp();
        memberJoinsTeam();

        // Create ride (stays in DRAFT)
        String rideSlug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Draft Ride\", \"date\": \"2025-01-28\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Get the group ID
        String groupId = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
                .then()
                .statusCode(200)
                .extract().path("data[0].id");

        // Member tries to join (should fail - ride not published)
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
                .then()
                .statusCode(404);
    }

    @Test
    void leaveRide_shouldRemoveParticipant() {
        createTeamViaHttp();
        memberJoinsTeam();

        // Create and publish ride
        String rideSlug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Leave Test Ride\", \"date\": \"2025-01-29\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .extract().path("slug");

        // Publish the ride
        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"status\": \"PUBLISHED\"}")
                .when()
                .patch("/api/teams/" + teamSlug + "/rides/" + rideSlug)
                .then()
                .statusCode(200);

        // Get the group ID
        String groupId = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .when()
                .get("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
                .then()
                .statusCode(200)
                .extract().path("data[0].id");

        // Member joins
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
                .then()
                .statusCode(201);

        // Member leaves
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .contentType("application/json")
                .when()
                .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups/" + groupId + "/leave")
                .then()
                .statusCode(204);
    }

    @Test
    void createGroup_shouldAddGroupToRide() {
        createTeamViaHttp();

        String rideSlug = given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"title\": \"Group Test Ride\", \"date\": \"2025-01-30\"}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides")
                .then()
                .statusCode(201)
                .extract().path("slug");

        given()
                .auth().oauth2(getAccessToken(USERNAME_ADMIN))
                .contentType("application/json")
                .body("{\"name\": \"Extra Fast Group\", \"averageSpeed\": 35}")
                .when()
                .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
                .then()
                .statusCode(201)
                .body("name", equalTo("Extra Fast Group"))
                .body("averageSpeed", equalTo(35));
    }
}
