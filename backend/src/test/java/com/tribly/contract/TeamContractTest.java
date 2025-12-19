package com.tribly.contract;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamContractTest {

  @Test
  void listPublicTeams_shouldReturnEmptyList() {
    given()
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams", is(notNullValue()))
        .body("total", greaterThanOrEqualTo(0))
        .body("page", equalTo(0))
        .body("size", equalTo(20));
  }

  @Test
  void listPublicTeams_shouldSupportPagination() {
    given()
        .queryParam("page", 1)
        .queryParam("size", 10)
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("page", equalTo(1))
        .body("size", equalTo(10));
  }

  @Test
  void listPublicTeams_shouldSupportSearch() {
    given()
        .queryParam("search", "cycling")
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams", is(notNullValue()));
  }

  @Test
  void getTeam_withNonexistentSlug_shouldReturn404() {
    given().when().get("/api/teams/nonexistent-team-slug").then().statusCode(404);
  }

  @Test
  void createTeam_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"Test Team\"}")
        .when()
        .post("/api/teams")
        .then()
        .statusCode(401);
  }

  @Test
  void joinTeam_withoutAuth_shouldReturn401() {
    given()
        .contentType("application/json")
        .when()
        .post("/api/teams/some-team/members/join")
        .then()
        .statusCode(401);
  }

  @Test
  void getMembers_withoutAuth_shouldReturn401() {
    given().when().get("/api/teams/some-team/members").then().statusCode(401);
  }
}
