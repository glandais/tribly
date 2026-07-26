package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The excerpt, the logo and the clickable statistics row of the team screen, plus the {@code
 * joinable} filter of the team directory.
 */
@QuarkusTest
class TeamStatsResourceTest extends AbstractResourceTest {

  private static final String ABOUT = "Le **club** de la vallee, sorties tous les [samedis](/s).";

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.setTeamAboutMarkdown(team1, ABOUT);

    // Two rides ahead, one behind: only the two ahead are "upcoming".
    Instant now = Instant.now();
    dataService.createRide(
        team1, user1, "Sortie dimanche", "sortie-dimanche", now.plus(3, ChronoUnit.DAYS));
    dataService.createRide(
        team1, user1, "Sortie samedi", "sortie-samedi", now.plus(10, ChronoUnit.DAYS));
    dataService.createRide(
        team1, user1, "Sortie passee", "sortie-passee", now.minus(10, ChronoUnit.DAYS));

    dataService.createRoute(team1, user1, "Boucle du lac");
    dataService.createRoute(team1, user1, "Col de la Croix");
    dataService.createRoute(team1, user1, "Tour de la plaine");
  }

  @Test
  void getTeam_shouldCarryTheExcerptOfItsAboutPage() {
    given()
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        // Plain text: the bold markers and the link syntax are gone, the label stays.
        .body("excerpt", containsString("club"))
        .body("excerpt", not(containsString("**")))
        .body("excerpt", not(containsString("](")))
        .body("about.markdown", equalTo(ABOUT));
  }

  @Test
  void getTeam_shouldCountUpcomingRidesAndRoutes() {
    given()
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("memberCount", greaterThanOrEqualTo(1))
        .body("upcomingRideCount", equalTo(2))
        .body("routeCount", equalTo(3));
  }

  @Test
  void listTeams_shouldCountEachTeamOnItsOwn() {
    given()
        .when()
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.find { it.slug == '" + team1Slug + "' }.upcomingRideCount", equalTo(2))
        .body("teams.find { it.slug == '" + team1Slug + "' }.routeCount", equalTo(3));
  }

  @Test
  void getTeam_anonymous_shouldNotCountWhatItCannotOpen() {
    // A TEAM-only ride and a TEAM-only route exist, but an anonymous visitor may not list them —
    // the counters must not announce them either, or the card promises a page that 404s.
    dataService.createRide(
        team1,
        user1,
        "Sortie privee",
        "sortie-privee",
        Instant.now().plus(2, ChronoUnit.DAYS),
        Visibility.TEAM);
    dataService.createRoute(team1, user1, "Parcours prive", Visibility.TEAM);

    given()
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("upcomingRideCount", equalTo(2))
        .body("routeCount", equalTo(3));
  }

  @Test
  void getTeam_member_shouldCountTheTeamOnlyContentToo() {
    dataService.createRide(
        team1,
        user1,
        "Sortie privee",
        "sortie-privee",
        Instant.now().plus(2, ChronoUnit.DAYS),
        Visibility.TEAM);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("upcomingRideCount", equalTo(3));
  }

  @Test
  void getTeam_shouldNotCountAnotherDomainsContent() {
    Domain other = dataService.createDomain("other-stats.test", "Other", "https://other.test");
    User otherUser = dataService.createUser(other, "user1@example.com", "Other user 1");
    // Same slug as team1, on another domain, stuffed with content that must never be counted here.
    // createTeam already enrols its creator as ADMIN.
    Team otherTeam = dataService.createTeam(otherUser, "Other", team1Slug, Visibility.PUBLIC);
    dataService.createRide(
        otherTeam,
        otherUser,
        "Sortie etrangere",
        "sortie-etrangere",
        Instant.now().plus(1, ChronoUnit.DAYS));
    dataService.createRoute(otherTeam, otherUser, "Parcours etranger");

    given()
        .when()
        .get("/api/teams/" + team1Slug)
        .then()
        .statusCode(200)
        .body("upcomingRideCount", equalTo(2))
        .body("routeCount", equalTo(3));
  }

  @Test
  void listTeams_joinableTrue_shouldKeepOnlyTeamsAcceptingJoinRequests() {
    dataService.setTeamJoinable(team1, true);

    given()
        .when()
        .queryParam("joinable", true)
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.slug", hasItem(team1Slug))
        .body("teams.joinable", everyItem(equalTo(true)));
  }

  @Test
  void listTeams_joinableFalse_shouldExcludeThem() {
    dataService.setTeamJoinable(team1, true);

    given()
        .when()
        .queryParam("joinable", false)
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.slug", not(hasItem(team1Slug)));
  }

  @Test
  void listTeams_joinableFilter_shouldNotRevealAPrivateTeam() {
    // team2 is private. Marking it joinable must not lift it into an anonymous listing: the filter
    // narrows, it never widens.
    dataService.setTeamJoinable(team2, true);

    given()
        .when()
        .queryParam("joinable", true)
        .get("/api/teams")
        .then()
        .statusCode(200)
        .body("teams.slug", not(hasItem(team2Slug)));
  }

  @Test
  void listTeams_withoutTheFilter_shouldBeUnchanged() {
    dataService.setTeamJoinable(team1, true);

    given().when().get("/api/teams").then().statusCode(200).body("teams.slug", hasItem(team1Slug));
  }
}
