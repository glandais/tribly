package com.tribly.api.publications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PublicationResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private Visibility getVisibility(String teamSlug) {
    return teamSlug.equals(team1Slug) ? Visibility.PUBLIC : Visibility.TEAM;
  }

  private void createTestRide(String teamSlug, String name) {
    Instant dateTime = Instant.now().plus(7, ChronoUnit.DAYS);
    dataService.createRide(
        teamSlug.equals(team1Slug) ? team1 : team2,
        user1,
        name,
        name.toLowerCase().replace(" ", "-"),
        dateTime,
        getVisibility(teamSlug),
        Status.PUBLISHED);
  }

  private void createTestPost(String teamSlug, String name) {
    PostRequest request =
        new PostRequest(
            name,
            MediaDto.builder().markdown("Post content").build(),
            Instant.now().plus(7, ChronoUnit.DAYS),
            Status.PUBLISHED,
            getVisibility(teamSlug),
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + teamSlug + "/posts")
        .then()
        .statusCode(201);
  }

  // ==================== List All Publications Tests ====================

  @Test
  void listAllPublications_withoutAuth_shouldSucceed() {
    given()
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications", notNullValue())
        .body("total", greaterThanOrEqualTo(0));
  }

  @Test
  void listAllPublications_asMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications", notNullValue());
  }

  @Test
  void listAllPublications_asNonMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications", notNullValue());
  }

  @Test
  void listAllPublications_shouldSupportPagination() {
    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("page", equalTo(0))
        .body("size", equalTo(10));
  }

  @Test
  void listAllPublications_shouldContainRides() {
    createTestRide(team1Slug, "Publication Ride");

    given()
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Publication Ride' }", notNullValue())
        .body("publications.find { it.name == 'Publication Ride' }.type", equalTo("RIDE"));
  }

  @Test
  void listAllPublications_shouldContainPosts() {
    createTestPost(team1Slug, "Publication Post");

    given()
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Publication Post' }", notNullValue())
        .body("publications.find { it.name == 'Publication Post' }.type", equalTo("POST"));
  }

  @Test
  void listAllPublications_shouldContainRidesAndPosts() {
    createTestRide(team1Slug, "All Types Ride");
    createTestPost(team1Slug, "All Types Post");

    given()
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'All Types Ride' }.type", equalTo("RIDE"))
        .body("publications.find { it.name == 'All Types Post' }.type", equalTo("POST"));
  }

  @Test
  void listAllPublications_shouldContainPublicationsFromMultipleTeams() {
    createTestRide(team1Slug, "Team1 Ride");
    createTestPost(team2Slug, "Team2 Post");

    // As member of both teams
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Team1 Ride' }", notNullValue())
        .body("publications.find { it.name == 'Team2 Post' }", notNullValue());
  }

  @Test
  void listAllPublications_shouldSupportSearch() {
    createTestRide(team1Slug, "Searchable Ride");

    given()
        .queryParam("search", "Searchable")
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Searchable Ride' }", notNullValue());
  }

  // ==================== Team Publications Tests ====================

  @Test
  void listTeamPublications_withoutAuth_shouldSucceed() {
    given()
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications", notNullValue())
        .body("total", greaterThanOrEqualTo(0));
  }

  @Test
  void listTeamPublications_asMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications", notNullValue());
  }

  @Test
  void listTeamPublications_asNonMember_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications", notNullValue());
  }

  @Test
  void listTeamPublications_toNonexistentTeam_shouldReturn404() {
    given()
        .when()
        .get("/api/teams/nonexistent-team/publications")
        .then()
        // empty list
        .statusCode(404);
  }

  @Test
  void listTeamPublications_shouldSupportPagination() {
    given()
        .queryParam("page", 0)
        .queryParam("size", 10)
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("page", equalTo(0))
        .body("size", equalTo(10));
  }

  @Test
  void listTeamPublications_shouldContainRides() {
    createTestRide(team1Slug, "Team Ride");

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Team Ride' }", notNullValue())
        .body("publications.find { it.name == 'Team Ride' }.type", equalTo("RIDE"));
  }

  @Test
  void listTeamPublications_shouldContainPosts() {
    createTestPost(team1Slug, "Team Post");

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Team Post' }", notNullValue())
        .body("publications.find { it.name == 'Team Post' }.type", equalTo("POST"));
  }

  @Test
  void listTeamPublications_shouldContainRidesAndPosts() {
    createTestRide(team1Slug, "Team All Ride");
    createTestPost(team1Slug, "Team All Post");

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Team All Ride' }.type", equalTo("RIDE"))
        .body("publications.find { it.name == 'Team All Post' }.type", equalTo("POST"));
  }

  @Test
  void listTeamPublications_shouldOnlyContainTeamPublications() {
    createTestRide(team1Slug, "Team1 Only Ride");
    createTestRide(team2Slug, "Team2 Only Ride");

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Team1 Only Ride' }", notNullValue())
        .body("publications.find { it.name == 'Team2 Only Ride' }", nullValue());
  }

  @Test
  void listTeamPublications_team2_shouldContainItsOwnPublications() {
    createTestPost(team2Slug, "Team2 Post Only");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team2Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Team2 Post Only' }", notNullValue());
  }

  @Test
  void listTeamPublications_shouldSupportSearch() {
    createTestRide(team1Slug, "Findable Ride");

    given()
        .queryParam("search", "Findable")
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Findable Ride' }", notNullValue());
  }
}
