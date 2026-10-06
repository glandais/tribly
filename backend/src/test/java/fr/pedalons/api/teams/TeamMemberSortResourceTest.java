package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.enums.TeamRole;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code GET /api/teams/{teamSlug}/members?sortBy=JOINED_AT&sortDir=}: the « Nouveaux membres » of
 * the administration panel. Fixture order of arrival on team1: user1, user2, user3, then user4 and
 * user5 added here.
 */
@QuarkusTest
class TeamMemberSortResourceTest extends AbstractResourceTest {

  private String members() {
    return "/api/teams/" + team1Slug + "/members";
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.addUserToTeam(user4, team1, TeamRole.MEMBER);
    dataService.addUserToTeam(user5, team1, TeamRole.MEMBER);
  }

  @Test
  void joinedAt_desc_orOmittedDirection_shouldListNewestFirst() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("sortBy", "JOINED_AT")
        .queryParam("size", 3)
        .when()
        .get(members())
        .then()
        .statusCode(200)
        .body("members.user.displayName", contains("Test User 5", "Test User 4", "Test User 3"))
        .body("total", equalTo(5));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("sortBy", "JOINED_AT")
        .queryParam("sortDir", "DESC")
        .queryParam("size", 2)
        .when()
        .get(members())
        .then()
        .statusCode(200)
        .body("members.user.displayName", contains("Test User 5", "Test User 4"));
  }

  @Test
  void joinedAt_asc_shouldListOldestFirst() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("sortBy", "JOINED_AT")
        .queryParam("sortDir", "ASC")
        .queryParam("size", 2)
        .when()
        .get(members())
        .then()
        .statusCode(200)
        .body("members.user.displayName", contains("Test User 1", "Test User 2"));
  }

  /** The order would hand back the join dates the response hides from an organizer. */
  @Test
  void joinedAt_asOrganizerOfAClosedDirectory_shouldBeRefused() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .queryParam("sortBy", "JOINED_AT")
        .when()
        .get(members())
        .then()
        .statusCode(403);
  }

  @Test
  void joinedAt_asMemberOfAnOpenDirectory_shouldBeAllowed() {
    dataService.setTeamEnableMemberDirectory(team1, true);
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("sortBy", "JOINED_AT")
        .queryParam("size", 1)
        .when()
        .get(members())
        .then()
        .statusCode(200)
        .body("members.user.displayName", contains("Test User 5"));
  }
}
