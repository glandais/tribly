package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.nullValue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.enums.TeamRole;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code TeamDetailDto.memberCountByRole}: the split of the members per role, for the team's
 * administrators only. Fixture: team1 has user1 (ADMIN), user2 (ORGANIZER), user3 (MEMBER).
 */
@QuarkusTest
class TeamMemberCountByRoleResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.addUserToTeam(user4, team1, TeamRole.MEMBER);
  }

  @Test
  void admin_shouldGetTheSplit_addingUpToMemberCount() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("memberCount", equalTo(4))
        .body("memberCountByRole.admins", equalTo(1))
        .body("memberCountByRole.organizers", equalTo(1))
        .body("memberCountByRole.members", equalTo(2));
  }

  @Test
  void platformAdmin_shouldGetTheSplit() {
    dataService.createPlatformAdminUser("root@example.com", "Root");
    given()
        .auth()
        .oauth2(getAccessToken("root"))
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("memberCountByRole.members", equalTo(2));
  }

  @Test
  void organizer_member_outsider_andAnonymous_shouldGetNone() {
    for (String user : new String[] {USER2, USER3, USER5}) {
      given()
          .auth()
          .oauth2(getAccessToken(user))
          .when()
          .get("/api/teams/" + team1Slug)
          .then()
          .statusCode(200)
          .body("memberCountByRole", nullValue());
    }
    given()
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("memberCountByRole", nullValue());
  }

  /** A listing never loads it: one query per row is what the team directory must not cost. */
  @Test
  void teamListing_shouldNeverCarryIt() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.memberCountByRole", everyItem(nullValue()));
  }
}
