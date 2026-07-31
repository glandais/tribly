package fr.pedalons.api.teams;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.enums.TeamRole;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Database-cost budget for the member directory.
 *
 * <p>New here because the audience changed, not the code: the directory used to be an
 * administrators-only screen, and is now readable by every member of a team that opens it. On a
 * 1999-member team that is a different order of traffic, and {@code UserTeam.user} is an eager
 * to-one that {@code findByTeam} does not join — so the flatness this asserts rests entirely on
 * {@code quarkus.hibernate-orm.fetch.batch-size}. If someone ever removes that setting, or adds a
 * per-row lookup to the mapping, this is what says so.
 *
 * <p>See {@link AbstractQueryCountTest} for why the assertion is a shape rather than a bound.
 */
@QuarkusTest
class TeamMemberQueryCountTest extends AbstractQueryCountTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private void seedMembers(int count) {
    for (int i = 0; i < count; i++) {
      var user = dataService.createUser("budget" + i + "@example.com", "Budget Member " + i);
      dataService.addUserToTeam(user, team1, TeamRole.MEMBER);
    }
  }

  @Test
  void listTeamMembers_costDoesNotScaleWithRowCount() {
    seedMembers(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/members", asUser1(), "/api/teams/" + team1Slug + "/members");
  }

  /**
   * The open-directory path is the one members will actually hit, and it goes through a different
   * branch of the access checker — worth measuring in its own right rather than assuming it costs
   * the same as the administrator's.
   */
  @Test
  void listTeamMembers_asPlainMember_costDoesNotScaleWithRowCount() {
    dataService.setTeamEnableMemberDirectory(team1, true);
    seedMembers(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/members [member]",
        () -> io.restassured.RestAssured.given().auth().oauth2(getAccessToken(USER3)),
        "/api/teams/" + team1Slug + "/members");
  }
}
