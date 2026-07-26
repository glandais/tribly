package fr.pedalons.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code GET /api/teams/{teamSlug}/routes/{routeSlug}/elevation-profile}.
 *
 * <p>The endpoint exists so a phone can draw a gradient-coloured profile without downloading the
 * megabytes of track points the full route detail carries — so what is asserted here is the shape of
 * the curve, the clamping of the requested resolution, and that the profile is no more visible than
 * the route it describes.
 */
@QuarkusTest
class RouteElevationProfileTest extends AbstractResourceTest {

  /** Eleven points, a kilometre apart: 5% up for five kilometres, then 5% back down. */
  private static final List<GpxTrack.TrackPoint> ROOF_TRACK = roofTrack();

  private static List<GpxTrack.TrackPoint> roofTrack() {
    List<GpxTrack.TrackPoint> points = new ArrayList<>();
    for (int i = 0; i <= 10; i++) {
      double elevation = i <= 5 ? 100 + 50 * i : 350 - 50 * (i - 5);
      points.add(new GpxTrack.TrackPoint(45.0 + i * 0.01, 6.0, elevation, i * 1000.0));
    }
    return points;
  }

  private String profileUrl(String teamSlug, String routeSlug) {
    return "/api/teams/" + teamSlug + "/routes/" + routeSlug + "/elevation-profile";
  }

  private void seedRoute(Team team, User creator, String name, Visibility visibility) {
    dataService.createRoute(team, creator, name, visibility, "LINESTRING(6 45,6 45.1)", ROOF_TRACK);
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    seedRoute(team1, user1, "Roof Route", Visibility.PUBLIC);
  }

  @Test
  void getProfile_anonymousOnPublicRoute_shouldReturnTheSampledCurve() {
    given()
        .when()
        .get(profileUrl(team1Slug, "roof-route"))
        .then()
        .statusCode(200)
        .body("slug", equalTo("roof-route"))
        .body("distance", equalTo(10000.0f))
        .body("minElevation", equalTo(100.0f))
        .body("maxElevation", equalTo(350.0f))
        // The stored track has eleven points; the default of 300 cannot invent more.
        .body("samples", equalTo(11))
        .body("points", hasSize(11))
        .body("points[0].distance", equalTo(0.0f))
        .body("points[0].elevation", equalTo(100.0f))
        // The first point ends no segment, so it carries no grade.
        .body("points[0].grade", equalTo(0.0f))
        .body("points[1].grade", equalTo(5.0f))
        .body("points[5].elevation", equalTo(350.0f))
        .body("points[6].grade", equalTo(-5.0f))
        .body("points[10].distance", equalTo(10000.0f))
        .body("points[10].elevation", equalTo(100.0f));
  }

  @Test
  void getProfile_shouldSampleAtTheRequestedResolution() {
    given()
        .queryParam("samples", 3)
        .when()
        .get(profileUrl(team1Slug, "roof-route"))
        .then()
        .statusCode(200)
        .body("samples", equalTo(3))
        .body("points", hasSize(3))
        .body("points[0].distance", equalTo(0.0f))
        .body("points[1].distance", equalTo(5000.0f))
        .body("points[1].elevation", equalTo(350.0f))
        .body("points[2].distance", equalTo(10000.0f));
  }

  /** No ceiling on a size is how a single request turns into an unbounded allocation. */
  @Test
  void getProfile_shouldClampAnAbsurdSampleCount() {
    List<GpxTrack.TrackPoint> dense = new ArrayList<>();
    for (int i = 0; i < 1500; i++) {
      dense.add(new GpxTrack.TrackPoint(45.0 + i * 0.0001, 6.0, 100 + i % 50, i * 10.0));
    }
    dataService.createRoute(
        team1, user1, "Dense Route", Visibility.PUBLIC, "LINESTRING(6 45,6 45.1)", dense);

    given()
        .queryParam("samples", 10_000_000)
        .when()
        .get(profileUrl(team1Slug, "dense-route"))
        .then()
        .statusCode(200)
        .body("samples", equalTo(1000))
        .body("points", hasSize(1000));
  }

  @Test
  void getProfile_shouldClampASampleCountBelowTwo() {
    given()
        .queryParam("samples", 0)
        .when()
        .get(profileUrl(team1Slug, "roof-route"))
        .then()
        .statusCode(200)
        .body("points", hasSize(2));
  }

  @Test
  void getProfile_onNonexistentRoute_shouldReturn404() {
    given().when().get(profileUrl(team1Slug, "no-such-route")).then().statusCode(404);
  }

  @Test
  void getProfile_anonymousOnPrivateTeamRoute_shouldReturn404() {
    seedRoute(team2, user1, "Secret Route", Visibility.TEAM);

    given().when().get(profileUrl(team2Slug, "secret-route")).then().statusCode(404);
  }

  @Test
  void getProfile_asNonMemberOnPrivateTeamRoute_shouldReturn404() {
    seedRoute(team2, user1, "Secret Route", Visibility.TEAM);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get(profileUrl(team2Slug, "secret-route"))
        .then()
        .statusCode(404);
  }

  @Test
  void getProfile_asMemberOnPrivateTeamRoute_shouldSucceed() {
    seedRoute(team2, user1, "Secret Route", Visibility.TEAM);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get(profileUrl(team2Slug, "secret-route"))
        .then()
        .statusCode(200)
        .body("slug", equalTo("secret-route"));
  }

  /**
   * A team slug and a route slug are only unique within a domain: the very same pair on another
   * domain must be invisible from this one, whatever the caller's token says.
   */
  @Test
  void getProfile_shouldNotReachAnotherDomain() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL1, "Other User 1");
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Foreign Team", "foreign-team", Visibility.PUBLIC);
    seedRoute(otherTeam, otherUser, "Foreign Route", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get(profileUrl("foreign-team", "foreign-route"))
        .then()
        .statusCode(404);
  }
}
