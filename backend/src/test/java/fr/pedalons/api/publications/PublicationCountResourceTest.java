package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code GET /api/publications/count} and {@code GET /api/teams/{teamSlug}/publications/count}.
 *
 * <p>Same contract as the route counts: the figure must equal the {@code total} of the listing that
 * takes the very same filters, and it must never see a publication the caller could not list.
 */
@QuarkusTest
class PublicationCountResourceTest extends AbstractResourceTest {

  private static final String ALL = "/api/publications";
  private static final String ALL_COUNT = "/api/publications/count";

  private Instant soon;

  private String teamList() {
    return "/api/teams/" + team1Slug + "/publications";
  }

  private String teamCount() {
    return "/api/teams/" + team1Slug + "/publications/count";
  }

  private int total(String url, RequestSpecification request) {
    return request.when().get(url).then().statusCode(200).extract().path("total");
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    soon = Instant.now().plus(7, ChronoUnit.DAYS);
    dataService.createRide(team1, user1, "Public Ride", "public-ride", soon);
    dataService.createPost(team1, user1, "Public Post", soon);
    dataService.createRide(team2, user1, "Private Ride", "private-ride", soon);
  }

  @Test
  void countAllPublications_anonymous_shouldMatchTheListTotal() {
    assertEquals(total(ALL, given()), total(ALL_COUNT, given()));
    given().when().get(ALL_COUNT).then().statusCode(200).body("total", equalTo(2));
  }

  @Test
  void countAllPublications_shouldHonourTheTypeFilter() {
    given()
        .queryParam("type", "RIDE")
        .when()
        .get(ALL_COUNT)
        .then()
        .statusCode(200)
        .body("total", equalTo(1));

    assertEquals(
        total(ALL, given().queryParam("type", "RIDE")),
        total(ALL_COUNT, given().queryParam("type", "RIDE")));
  }

  @Test
  void countAllPublications_shouldNotBeCacheableAcrossUsers() {
    given()
        .when()
        .get(ALL_COUNT)
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("private"));
  }

  /** Same short circuit as the listing: nobody is registered when nobody is logged in. */
  @Test
  void countAllPublications_withParticipating_anonymous_shouldReturnZero() {
    given()
        .queryParam("participating", true)
        .when()
        .get(ALL_COUNT)
        .then()
        .statusCode(200)
        .body("total", equalTo(0));
  }

  @Test
  void countTeamPublications_asMemberOfThePrivateTeam_shouldSeeIts() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team2Slug + "/publications/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }

  /**
   * A count is the listing minus pagination, refusals included: team2 is {@link Visibility#TEAM},
   * and the listing answers 403 to an outsider rather than an empty page. The count must not become
   * the softer of the two — a 200 with a zero would still confirm the team exists.
   */
  @Test
  void countTeamPublications_asNonMemberOfThePrivateTeam_shouldBeRefusedLikeTheListing() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team2Slug + "/publications")
        .then()
        .statusCode(403);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team2Slug + "/publications/count")
        .then()
        .statusCode(403);
  }

  @Test
  void countTeamPublications_shouldMatchTheListTotal() {
    assertEquals(total(teamList(), given()), total(teamCount(), given()));
  }

  @Test
  void countTeamPublications_onNonexistentTeam_shouldReturn404() {
    given().when().get("/api/teams/nonexistent-team/publications/count").then().statusCode(404);
  }

  /** A count is a query like any other: it must never see past its own domain. */
  @Test
  void countAllPublications_shouldNotCountAnotherDomain() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL1, "Other User 1");
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Foreign Team", "foreign-team", Visibility.PUBLIC);
    dataService.createRide(otherTeam, otherUser, "Foreign Ride", "foreign-ride", soon);

    given().when().get(ALL_COUNT).then().statusCode(200).body("total", equalTo(2));
  }
}
