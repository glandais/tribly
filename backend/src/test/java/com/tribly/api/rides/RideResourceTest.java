package com.tribly.api.rides;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.dto.common.response.AssetsDto;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.RideDto;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
    TeamRequest teamRequest =
        new TeamRequest(
            "Ride Test Team " + System.currentTimeMillis(),
            new MediaDto(null, AssetsDto.builder().build()),
            Visibility.PUBLIC);
    var response =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(teamRequest)
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
                MediaDto.builder().build(),
                LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                Status.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null))))
        .when()
        .post("/api/teams/" + teamSlug + "/rides")
        .then()
        .statusCode(201)
        .body("name", equalTo("Sunday Morning Ride"))
        .body("dateTime", equalTo("2025-01-20T00:00:00Z"))
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
                MediaDto.builder().build(),
                LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                Status.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null))))
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
            MediaDto.builder().build(),
            LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            List.of(
                new GroupRequest(null, "Fast", 32, 10, null),
                new GroupRequest(null, "Fase", 28, 15, null),
                new GroupRequest(null, "Social", 25, null, null)));

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(body)
        .when()
        .post("/api/teams/" + teamSlug + "/rides")
        .then()
        .statusCode(201)
        .body("name", equalTo("Multi-Group Ride"))
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
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-22").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null))))
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
        .body("name", equalTo("Get Test Ride"))
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
                MediaDto.builder().build(),
                LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                Status.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null))))
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
                MediaDto.builder().build(),
                LocalDate.parse("2025-01-27").atTime(0, 0).toInstant(ZoneOffset.UTC),
                Status.DRAFT,
                Visibility.PUBLIC,
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null))))
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

    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Sunday Morning Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    List.of(
                        new GroupRequest(null, "G1", null, null, null),
                        new GroupRequest(null, "G0", null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_ADMIN))
        .contentType("application/json")
        .body(
            new RideRequest(
                "Updated Title",
                MediaDto.builder().build(),
                LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                Status.PUBLISHED,
                Visibility.PUBLIC,
                null,
                null,
                List.of(
                    new GroupRequest(groupId, "G1 modified", null, null, null),
                    new GroupRequest(null, "G2", null, null, null))))
        .when()
        .put("/api/teams/" + teamSlug + "/rides/" + rideSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Title"))
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
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.PUBLISHED,
                    Visibility.PUBLIC,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null))))
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
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.PUBLISHED,
                    Visibility.PUBLIC,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

    // Member joins the ride
    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + teamSlug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
        .then()
        .statusCode(201);
  }

  @Test
  void joinRide_whenNotPublished_shouldBeDenied() {
    createTeamViaHttp();
    memberJoinsTeam();

    // Create ride (stays in DRAFT)
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

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
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USERNAME_ADMIN))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.PUBLISHED,
                    Visibility.PUBLIC,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null))))
            .when()
            .post("/api/teams/" + teamSlug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

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
