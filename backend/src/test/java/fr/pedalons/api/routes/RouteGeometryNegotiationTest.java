package fr.pedalons.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The {@code simplify} and {@code points} knobs on {@code GET
 * /api/teams/{teamSlug}/routes/{routeSlug}}.
 *
 * <p>Both are opt-in, and the first thing asserted here is what happens when neither is passed: the
 * stored track, unchanged. A client that has not been updated must not notice this feature exists.
 */
@QuarkusTest
class RouteGeometryNegotiationTest extends AbstractResourceTest {

  private static final int STORED_POINTS = 41;

  private String routeUrl() {
    return "/api/teams/" + team1Slug + "/routes/wobbly-route";
  }

  /** A straight line carrying a one-metre wobble on every other point. */
  private static List<GpxTrack.TrackPoint> wobblyTrack() {
    List<GpxTrack.TrackPoint> points = new ArrayList<>();
    for (int i = 0; i < STORED_POINTS; i++) {
      double wobble = i % 2 == 0 ? 0 : 0.00001; // ~1.1 m
      points.add(new GpxTrack.TrackPoint(45.0 + wobble, 6.0 + i * 0.001, 100 + i, i * 78.0));
    }
    return points;
  }

  /** The returned line, as the raw GeoJSON coordinate arrays. */
  private List<List<Number>> coordinates(RequestSpecification request) {
    return request
        .when()
        .get(routeUrl())
        .then()
        .statusCode(200)
        .extract()
        .path("tracks[0].line.coordinates");
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.createRoute(
        team1, user1, "Wobbly Route", Visibility.PUBLIC, "LINESTRING(6 45,6.04 45)", wobblyTrack());
  }

  @Test
  void getRoute_withoutParameters_shouldReturnTheStoredTrackUnchanged() {
    given()
        .when()
        .get(routeUrl())
        .then()
        .statusCode(200)
        .body("tracks", hasSize(1))
        .body("tracks[0].line.coordinates", hasSize(STORED_POINTS));
  }

  @Test
  void getRoute_withSimplify_shouldDropTheWobbleAndKeepTheEnds() {
    List<List<Number>> stored = coordinates(given());
    List<List<Number>> simplified = coordinates(given().queryParam("simplify", 10));

    // A one-metre wobble is well inside a ten-metre tolerance: the line collapses to its ends.
    assertEquals(2, simplified.size());
    assertEquals(stored.getFirst(), simplified.getFirst());
    assertEquals(stored.getLast(), simplified.getLast());
  }

  @Test
  void getRoute_withSimplifyBelowTheWobble_shouldKeepEveryPoint() {
    assertEquals(STORED_POINTS, coordinates(given().queryParam("simplify", 0.1)).size());
  }

  @Test
  void getRoute_withPoints_shouldHonourTheBudgetAndKeepTheEnds() {
    List<List<Number>> stored = coordinates(given());
    List<List<Number>> decimated = coordinates(given().queryParam("points", 8));

    assertTrue(decimated.size() <= 8, "budget of 8 honoured, got " + decimated.size());
    assertTrue(decimated.size() > 2, "a budget of 8 should keep more than the two ends");
    assertEquals(stored.getFirst(), decimated.getFirst());
    assertEquals(stored.getLast(), decimated.getLast());
  }

  @Test
  void getRoute_withAPointBudgetLargerThanTheTrack_shouldChangeNothing() {
    assertEquals(STORED_POINTS, coordinates(given().queryParam("points", 10_000)).size());
  }

  @Test
  void getRoute_withBothParameters_shouldApplyToleranceThenBudget() {
    List<List<Number>> result =
        coordinates(given().queryParam("simplify", 0.1).queryParam("points", 6));
    assertTrue(result.size() <= 6, "budget of 6 honoured, got " + result.size());
  }

  /** Absurd values are clamped rather than rejected: a filter must not turn into a 500. */
  @Test
  void getRoute_withAbsurdParameters_shouldStillAnswer() {
    List<List<Number>> result =
        coordinates(given().queryParam("simplify", 1_000_000_000).queryParam("points", -5));
    assertEquals(2, result.size());
  }

  @Test
  void getRoute_withParameters_shouldStayInvisibleToANonMemberOfAPrivateTeam() {
    dataService.createRoute(
        team2, user1, "Secret Route", Visibility.TEAM, "LINESTRING(6 45,6.04 45)", wobblyTrack());

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .queryParam("simplify", 10)
        .when()
        .get("/api/teams/" + team2Slug + "/routes/secret-route")
        .then()
        .statusCode(404);
  }
}
