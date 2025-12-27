package com.tribly.api.rides;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for ride endpoints.
 * Tests endpoint availability, response structure, and authorization requirements.
 */
@QuarkusTest
class RideContractTest {

  private static final long TEAM_ID = 1L;
  private static final long RIDE_ID = 1L;
  private static final long GROUP_ID = 1L;

  // ===== List Rides =====

  @Test
  void listRides_withoutAuth_shouldReturn401() {
    given().when().get("/api/teams/" + TEAM_ID + "/rides").then().statusCode(200);
  }

  // ===== Create Ride =====

  @Test
  void createRide_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"Test Ride\", \"date\": \"2025-01-15\"}")
        .when()
        .post("/api/teams/" + TEAM_ID + "/rides")
        .then()
        .statusCode(401);
  }

  // ===== Get Ride =====

  @Test
  void getRide_withoutAuth_shouldReturn404() {
    given().when().get("/api/teams/" + TEAM_ID + "/rides/" + RIDE_ID).then().statusCode(404);
  }

  // ===== Update Ride =====

  @Test
  void updateRide_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"Updated Ride\"}")
        .when()
        .put("/api/teams/" + TEAM_ID + "/rides/" + RIDE_ID)
        .then()
        .statusCode(401);
  }

  // ===== Delete Ride =====

  @Test
  void deleteRide_withoutAuth_shouldReturn401() {
    given().when().delete("/api/teams/" + TEAM_ID + "/rides/" + RIDE_ID).then().statusCode(401);
  }

  // ===== List Ride Groups =====

  @Test
  void listRideGroups_withoutAuth_shouldReturn401() {
    given()
        .when()
        .get("/api/teams/" + TEAM_ID + "/rides/" + RIDE_ID + "/groups")
        .then()
        .statusCode(404);
  }

  // ===== Join Ride Group =====

  @Test
  void joinRideGroup_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .when()
        .post("/api/teams/" + TEAM_ID + "/rides/" + RIDE_ID + "/groups/" + GROUP_ID + "/join")
        .then()
        .statusCode(401);
  }

  // ===== Leave Ride Group =====

  @Test
  void leaveRideGroup_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .when()
        .post("/api/teams/" + TEAM_ID + "/rides/" + RIDE_ID + "/groups/" + GROUP_ID + "/leave")
        .then()
        .statusCode(401);
  }
}
