package com.tribly.integration;

import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.ride.RideGroupRepository;
import com.tribly.domain.ride.RideParticipationRepository;
import com.tribly.domain.ride.RideRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.service.auth.JwtService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RideServiceTest {

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

    @Inject
    JwtService jwtService;

    private User adminUser;
    private User memberUser;
    private String adminToken;
    private String memberToken;
    private String teamSlug;
    private Long teamId;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up in correct order
        participationRepository.delete("rideGroup.ride.team.slug like ?1", "ride-test-%");
        rideGroupRepository.delete("ride.team.slug like ?1", "ride-test-%");
        rideRepository.delete("team.slug like ?1", "ride-test-%");
        userTeamRepository.delete("user.email like ?1", "ride-test-%");
        teamRepository.delete("slug like ?1", "ride-test-%");
        userRepository.delete("email like ?1", "ride-test-%");

        adminUser = new User("ride-test-admin@example.com", "Ride Admin");
        adminUser.setStravaId("ride-admin-strava-" + System.currentTimeMillis());
        userRepository.persistAndFlush(adminUser);

        memberUser = new User("ride-test-member@example.com", "Ride Member");
        memberUser.setStravaId("ride-member-strava-" + System.currentTimeMillis());
        userRepository.persistAndFlush(memberUser);

        adminToken = jwtService.generateToken(adminUser);
        memberToken = jwtService.generateToken(memberUser);
    }

    private void createTeamViaHttp() {
        var response = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Ride Test Team " + System.currentTimeMillis() + "\", \"isPublic\": true}")
                .when()
                .post("/v1/teams")
                .then()
                .statusCode(201)
                .extract();

        teamSlug = response.path("slug");
        teamId = ((Number) response.path("id")).longValue();
    }

    private void memberJoinsTeam() {
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + teamSlug + "/members/join")
                .then()
                .statusCode(201);
    }

    @Test
    void createRide_asAdmin_shouldSucceed() {
        createTeamViaHttp();

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Sunday Morning Ride\", \"date\": \"2025-01-20\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
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
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .body("{\"title\": \"Forbidden Ride\", \"date\": \"2025-01-20\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
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
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(body)
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .body("title", equalTo("Multi-Group Ride"))
                .body("groupCount", equalTo(3));
    }

    @Test
    void getRide_shouldReturnRideDetails() {
        createTeamViaHttp();

        Integer rideId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Get Test Ride\", \"date\": \"2025-01-22\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + teamId + "/rides/" + rideId)
                .then()
                .statusCode(200)
                .body("id", equalTo(rideId))
                .body("title", equalTo("Get Test Ride"))
                .body("groups", hasSize(1));
    }

    @Test
    void listRides_shouldReturnTeamRides() {
        createTeamViaHttp();

        // Create two rides
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Ride 1\", \"date\": \"2025-01-23\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Ride 2\", \"date\": \"2025-01-24\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(200)
                .body("rides", hasSize(greaterThanOrEqualTo(2)))
                .body("total", greaterThanOrEqualTo(2));
    }

    @Test
    void updateRide_asAdmin_shouldSucceed() {
        createTeamViaHttp();

        Integer rideId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Original Title\", \"date\": \"2025-01-25\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Updated Title\", \"status\": \"PUBLISHED\"}")
                .when()
                .patch("/v1/teams/" + teamId + "/rides/" + rideId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"))
                .body("status", equalTo("PUBLISHED"));
    }

    @Test
    void deleteRide_asAdmin_shouldSucceed() {
        createTeamViaHttp();

        Integer rideId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"To Be Deleted\", \"date\": \"2025-01-26\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/v1/teams/" + teamId + "/rides/" + rideId)
                .then()
                .statusCode(204);

        // Verify it's gone
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + teamId + "/rides/" + rideId)
                .then()
                .statusCode(404);
    }

    @Test
    void joinRide_shouldAddParticipant() {
        createTeamViaHttp();
        memberJoinsTeam();

        // Create and publish ride
        Integer rideId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Join Test Ride\", \"date\": \"2025-01-27\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Publish the ride
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"status\": \"PUBLISHED\"}")
                .when()
                .patch("/v1/teams/" + teamId + "/rides/" + rideId)
                .then()
                .statusCode(200);

        // Get the group ID
        Integer groupId = given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + teamId + "/rides/" + rideId + "/groups")
                .then()
                .statusCode(200)
                .extract().path("data[0].id");

        // Member joins the ride
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .body("{\"notes\": \"Looking forward to it!\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides/" + rideId + "/groups/" + groupId + "/join")
                .then()
                .statusCode(201)
                .body("status", equalTo("REGISTERED"));
    }

    @Test
    void joinRide_whenNotPublished_shouldBeDenied() {
        createTeamViaHttp();
        memberJoinsTeam();

        // Create ride (stays in DRAFT)
        Integer rideId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Draft Ride\", \"date\": \"2025-01-28\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Get the group ID
        Integer groupId = given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + teamId + "/rides/" + rideId + "/groups")
                .then()
                .statusCode(200)
                .extract().path("data[0].id");

        // Member tries to join (should fail - ride not published)
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + teamId + "/rides/" + rideId + "/groups/" + groupId + "/join")
                .then()
                .statusCode(400);
    }

    @Test
    void leaveRide_shouldRemoveParticipant() {
        createTeamViaHttp();
        memberJoinsTeam();

        // Create and publish ride
        Integer rideId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Leave Test Ride\", \"date\": \"2025-01-29\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Publish the ride
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"status\": \"PUBLISHED\"}")
                .when()
                .patch("/v1/teams/" + teamId + "/rides/" + rideId)
                .then()
                .statusCode(200);

        // Get the group ID
        Integer groupId = given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/v1/teams/" + teamId + "/rides/" + rideId + "/groups")
                .then()
                .statusCode(200)
                .extract().path("data[0].id");

        // Member joins
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + teamId + "/rides/" + rideId + "/groups/" + groupId + "/join")
                .then()
                .statusCode(201);

        // Member leaves
        given()
                .header("Authorization", "Bearer " + memberToken)
                .contentType("application/json")
                .when()
                .post("/v1/teams/" + teamId + "/rides/" + rideId + "/groups/" + groupId + "/leave")
                .then()
                .statusCode(204);
    }

    @Test
    void createGroup_shouldAddGroupToRide() {
        createTeamViaHttp();

        Integer rideId = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"title\": \"Group Test Ride\", \"date\": \"2025-01-30\"}")
                .when()
                .post("/v1/teams/" + teamId + "/rides")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{\"name\": \"Extra Fast Group\", \"averageSpeed\": 35}")
                .when()
                .post("/v1/teams/" + teamId + "/rides/" + rideId + "/groups")
                .then()
                .statusCode(201)
                .body("name", equalTo("Extra Fast Group"))
                .body("averageSpeed", equalTo(35));
    }
}
