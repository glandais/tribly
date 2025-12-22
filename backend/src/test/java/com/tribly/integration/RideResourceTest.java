package com.tribly.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.enums.RideStatus;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideResourceTest {

  public static final String USERNAME_ADMIN = "user1";
  public static final String USERNAME_TEST = "user2";

  @Inject TestDataCleaner dataCleaner;

  private String teamSlug;

  final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  protected String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
  }

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  private void createTeamViaHttp() {
    var response =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                "{\"name\": \"Ride Test Team "
                    + System.currentTimeMillis()
                    + "\", \"visibility\": \"PUBLIC\"}")
            .when()
            .post("/api/teams")
            .then()
            .statusCode(201)
            .extract();

    teamSlug = response.path("slug");
  }

  private void memberJoinsTeam() {
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
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
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(
            new RideRequest(
                "Sunday Morning Ride",
                null,
                LocalDate.parse("2025-01-20"),
                null,
                RideStatus.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null, null))))
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
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .body(
            new RideRequest(
                "Sunday Morning Ride",
                null,
                LocalDate.parse("2025-01-20"),
                null,
                RideStatus.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null, null))))
        .when()
        .post("/api/teams/" + teamSlug + "/rides")
        .then()
        .statusCode(403);
  }

  @Test
  void createRide_withGroups_shouldCreateAllGroups() {
    createTeamViaHttp();

    var body =
        new RideRequest(
            "Multi-Group Ride",
            null,
            LocalDate.parse("2025-01-20"),
            null,
            RideStatus.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            List.of(
                new GroupRequest(null, "Fast", null, 32, 10, null),
                new GroupRequest(null, "Fase", null, 28, 15, null),
                new GroupRequest(null, "Social", null, 25, null, null)));

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
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

    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Get Test Ride",
                    null,
                    LocalDate.parse("2025-01-22"),
                    null,
                    RideStatus.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
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
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(
            new RideRequest(
                "Ride 1",
                null,
                LocalDate.parse("2025-01-20"),
                null,
                RideStatus.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null, null))))
        .when()
        .post("/api/teams/" + teamSlug + "/rides")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(
            new RideRequest(
                "Ride 2",
                null,
                LocalDate.parse("2025-01-27"),
                null,
                RideStatus.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null, null))))
        .when()
        .post("/api/teams/" + teamSlug + "/rides")
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
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

    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Sunday Morning Ride",
                    null,
                    LocalDate.parse("2025-01-20"),
                    null,
                    RideStatus.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    List.of(
                        new GroupRequest(null, "G1", null, null, null, null),
                        new GroupRequest(null, "G0", null, null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Get the group ID
    String groupId =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .when()
            .get("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
            .then()
            .statusCode(200)
            .extract()
            .path("data[0].id");

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(
            new RideRequest(
                "Updated Title",
                null,
                LocalDate.parse("2025-01-20"),
                null,
                RideStatus.PUBLISHED,
                Visibility.PUBLIC,
                null,
                null,
                null,
                List.of(
                    new GroupRequest(groupId, "G1 modified", null, null, null, null),
                    new GroupRequest(null, "G2", null, null, null, null))))
        .when()
        .put("/api/teams/" + teamSlug + "/rides/" + rideSlug)
        .then()
        .statusCode(200)
        .body("title", equalTo("Updated Title"))
        .body("status", equalTo("PUBLISHED"));
  }

  @Test
  void deleteRide_asAdmin_shouldSucceed() {
    createTeamViaHttp();

    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "To be deleted",
                    null,
                    LocalDate.parse("2025-01-20"),
                    null,
                    RideStatus.PUBLISHED,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .when()
        .delete("/api/teams/" + teamSlug + "/rides/" + rideSlug)
        .then()
        .statusCode(204);

    // Verify it's gone
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
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
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Ride",
                    null,
                    LocalDate.parse("2025-01-20"),
                    null,
                    RideStatus.PUBLISHED,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Get the group ID
    String groupId =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_TEST))
            .when()
            .get("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
            .then()
            .statusCode(200)
            .extract()
            .path("data[0].id");

    // Member joins the ride
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
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
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Ride",
                    null,
                    LocalDate.parse("2025-01-20"),
                    null,
                    RideStatus.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Get the group ID
    String groupId =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .when()
            .get("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
            .then()
            .statusCode(200)
            .extract()
            .path("data[0].id");

    // Member tries to join (should fail - ride not published)
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
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
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Ride",
                    null,
                    LocalDate.parse("2025-01-20"),
                    null,
                    RideStatus.PUBLISHED,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    // Get the group ID
    String groupId =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_TEST))
            .when()
            .get("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups")
            .then()
            .statusCode(200)
            .extract()
            .path("data[0].id");

    // Member joins
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
        .then()
        .statusCode(201);

    // Member leaves
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups/" + groupId + "/leave")
        .then()
        .statusCode(204);
  }
}
