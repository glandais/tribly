package com.tribly.api.rides;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.RideDto;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void createRide_asAdmin_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
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
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null, null))))
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(201)
        .body("name", equalTo("Sunday Morning Ride"))
        .body("dateTime", equalTo("2025-01-20T00:00:00Z"))
        .body("status", equalTo("DRAFT"))
        .body("groupCount", equalTo(1));
  }

  @Test
  void createRide_asMember_shouldBeDenied() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
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
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null, null))))
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(403);
  }

  @Test
  void createRide_withoutAuth_shouldReturn401() {
    given()
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
                null,
                null,
                List.of(new GroupRequest(null, "G1", null, null, null, null))))
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(401);
  }

  @Test
  void createRide_withGroups_shouldCreateAllGroups() {
    var body =
        new RideRequest(
            "Multi-Group Ride",
            MediaDto.builder().build(),
            LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
            Status.DRAFT,
            Visibility.PUBLIC,
            null,
            null,
            null,
            null,
            List.of(
                new GroupRequest(null, "Fast", null, 32.0f, 10, null),
                new GroupRequest(null, "Fase", null, 28.0f, 15, null),
                new GroupRequest(null, "Social", null, 25.0f, null, null)));

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(body)
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(201)
        .body("name", equalTo("Multi-Group Ride"))
        .body("groupCount", equalTo(3));
  }

  @Test
  void getRide_shouldReturnRideDetails() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER2))
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
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + rideSlug)
        .then()
        .statusCode(200)
        .body("slug", equalTo(rideSlug))
        .body("name", equalTo("Get Test Ride"))
        .body("groups", hasSize(1));
  }

  @Test
  void getRide_withoutAuth_shouldReturn404() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER2))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Get Test Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-22").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.PUBLISHED,
                    Visibility.TEAM,
                    null,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given().when().get("/api/teams/" + team1Slug + "/rides/" + rideSlug).then().statusCode(404);
  }

  @Test
  void updateRide_asAdmin_shouldSucceed() {
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(
                        new GroupRequest(null, "G1", null, null, null, null),
                        new GroupRequest(null, "G0", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
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
                null,
                null,
                List.of(
                    new GroupRequest(groupId, "G1 modified", null, null, null, null),
                    new GroupRequest(null, "G2", null, null, null, null))))
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + rideSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Title"))
        .body("status", equalTo("PUBLISHED"));
  }

  @Test
  void updateRide_withoutAuth_shouldReturn401() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(
                        new GroupRequest(null, "G1", null, null, null, null),
                        new GroupRequest(null, "G0", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class)
            .getSlug();

    given()
        .contentType("application/json")
        .body("{\"name\": \"Updated Ride\"}")
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + rideSlug)
        .then()
        .statusCode(401);
  }

  @Test
  void deleteRide_asAdmin_shouldSucceed() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug + "/rides/" + rideSlug)
        .then()
        .statusCode(204);

    // Verify it's gone
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + rideSlug)
        .then()
        .statusCode(404);
  }

  @Test
  void deleteRide_withoutAuth_shouldReturn401() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given().when().delete("/api/teams/" + team1Slug + "/rides/" + rideSlug).then().statusCode(401);
  }

  @Test
  void joinRide_shouldAddParticipant() {
    // Create and publish ride
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

    // Member joins the ride
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
        .then()
        .statusCode(201);
  }

  @Test
  void joinRide_whenNotPublished_shouldBeDenied() {
    // Create ride (stays in DRAFT)
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

    // Member tries to join (should fail - ride not published)
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
        .then()
        .statusCode(404);
  }

  @Test
  void leaveRide_shouldRemoveParticipant() {
    // Create and publish ride
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

    // Member joins
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/groups/" + groupId + "/join")
        .then()
        .statusCode(201);

    // Member leaves
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/groups/" + groupId + "/leave")
        .then()
        .statusCode(204);
  }

  @Test
  void leaveRideGroup_withoutAuth_shouldReturn401() {
    // Create and publish ride
    RideDto ride =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
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
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .as(RideDto.class);
    String rideSlug = ride.getSlug();
    String groupId = ride.getGroups().getFirst().id();

    given()
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/groups/" + groupId + "/leave")
        .then()
        .statusCode(401);
  }

  // ==================== Change Slug Tests ====================

  @Test
  void changeSlug_asAdmin_shouldSucceed() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Slug Change Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(new SlugChangeRequest("new-ride-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/slug")
        .then()
        .statusCode(200)
        .body("slug", equalTo("new-ride-slug"));
  }

  @Test
  void changeSlug_asOrganizer_shouldSucceed() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Organizer Slug Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(new SlugChangeRequest("organizer-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/slug")
        .then()
        .statusCode(200)
        .body("slug", equalTo("organizer-slug"));
  }

  @Test
  void changeSlug_asMember_shouldReturn404() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Member Slug Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(new SlugChangeRequest("hacked-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/slug")
        .then()
        .statusCode(404);
  }

  @Test
  void changeSlug_withoutAuth_shouldReturn401() {
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Unauth Slug Ride",
                    MediaDto.builder().build(),
                    LocalDate.parse("2025-01-20").atTime(0, 0).toInstant(ZoneOffset.UTC),
                    Status.DRAFT,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "G1", null, null, null, null))))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .contentType("application/json")
        .body(new SlugChangeRequest("unauth-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/rides/" + rideSlug + "/slug")
        .then()
        .statusCode(401);
  }
}
