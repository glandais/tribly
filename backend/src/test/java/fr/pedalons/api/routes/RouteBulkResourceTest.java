package fr.pedalons.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.route.RouteService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code GET /api/teams/{teamSlug}/routes/bulk}.
 *
 * <p>The endpoint exists so a screen showing several routes at once (a ride's stages, a comparison
 * view) does not cost one request per route. What is asserted here is the batching contract itself
 * — several slugs answered in one call, unknown/unreadable ones silently dropped rather than
 * failing the batch, the geometry and elevation knobs honoured per route, the combined extent, and
 * the size cap — not the geometry or elevation-profile math itself, which {@link
 * RouteGeometryNegotiationTest} and {@link RouteElevationProfileTest} already cover.
 */
@QuarkusTest
class RouteBulkResourceTest extends AbstractResourceTest {

  private String bulkUrl(String teamSlug) {
    return "/api/teams/" + teamSlug + "/routes/bulk";
  }

  private RequestSpecification withSlugs(String teamSlug, String... slugs) {
    RequestSpecification spec = given();
    for (String slug : slugs) {
      spec = spec.queryParam("slug", slug);
    }
    return spec;
  }

  private void seedRouteAt(
      Team team,
      User creator,
      String name,
      Visibility visibility,
      double lat1,
      double lon1,
      double lat2,
      double lon2) {
    List<GpxTrack.TrackPoint> points =
        List.of(
            new GpxTrack.TrackPoint(lat1, lon1, 500.0, 0.0),
            new GpxTrack.TrackPoint(lat2, lon2, 510.0, 10_000.0));
    String geometry = String.format("LINESTRING(%f %f,%f %f)", lon1, lat1, lon2, lat2);
    dataService.createRoute(team, creator, name, visibility, geometry, points);
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void getRoutesBulk_withSeveralSlugs_shouldReturnAllOfThemInRequestOrder() {
    seedRouteAt(team1, user1, "Route A", Visibility.PUBLIC, 45.0, 6.0, 45.1, 6.1);
    seedRouteAt(team1, user1, "Route B", Visibility.PUBLIC, 46.0, 7.0, 46.2, 7.2);

    // Requested in reverse alphabetical order: the response must follow the request, not the DB.
    withSlugs(team1Slug, "route-b", "route-a")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(2))
        .body("routes[0].slug", equalTo("route-b"))
        .body("routes[1].slug", equalTo("route-a"));
  }

  @Test
  void getRoutesBulk_withAnUnknownSlug_shouldSilentlyOmitIt() {
    seedRouteAt(team1, user1, "Route A", Visibility.PUBLIC, 45.0, 6.0, 45.1, 6.1);

    withSlugs(team1Slug, "route-a", "no-such-route")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(1))
        .body("routes[0].slug", equalTo("route-a"));
  }

  @Test
  void getRoutesBulk_withNoSlugs_shouldReturnAnEmptyAnswer() {
    given()
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(0))
        .body("extent", nullValue());
  }

  @Test
  void getRoutesBulk_withSimplify_shouldDecimateEveryRouteOfTheBatch() {
    int storedPoints = 41;
    List<GpxTrack.TrackPoint> wobbly =
        IntStream.range(0, storedPoints)
            .mapToObj(
                i -> {
                  double wobble = i % 2 == 0 ? 0 : 0.00001; // ~1.1 m
                  return new GpxTrack.TrackPoint(45.0 + wobble, 6.0 + i * 0.001, 100 + i, i * 78.0);
                })
            .toList();
    dataService.createRoute(
        team1, user1, "Wobbly Route", Visibility.PUBLIC, "LINESTRING(6 45,6.04 45)", wobbly);

    withSlugs(team1Slug, "wobbly-route")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes[0].tracks[0].line.coordinates", hasSize(storedPoints));

    // A one-metre wobble is well inside a ten-metre tolerance: the line collapses to its ends.
    withSlugs(team1Slug, "wobbly-route")
        .queryParam("simplify", 10)
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes[0].tracks[0].line.coordinates", hasSize(2));
  }

  @Test
  void getRoutesBulk_withPoints_shouldHonourTheBudget() {
    int storedPoints = 41;
    List<GpxTrack.TrackPoint> wobbly =
        IntStream.range(0, storedPoints)
            .mapToObj(
                i -> {
                  double wobble = i % 2 == 0 ? 0 : 0.00001;
                  return new GpxTrack.TrackPoint(45.0 + wobble, 6.0 + i * 0.001, 100 + i, i * 78.0);
                })
            .toList();
    dataService.createRoute(
        team1, user1, "Wobbly Route", Visibility.PUBLIC, "LINESTRING(6 45,6.04 45)", wobbly);

    List<List<Number>> decimated =
        withSlugs(team1Slug, "wobbly-route")
            .queryParam("points", 8)
            .when()
            .get(bulkUrl(team1Slug))
            .then()
            .statusCode(200)
            .extract()
            .path("routes[0].tracks[0].line.coordinates");

    assertTrue(decimated.size() <= 8, "budget of 8 honoured, got " + decimated.size());
    assertTrue(decimated.size() > 2, "a budget of 8 should keep more than the two ends");
  }

  private List<GpxTrack.TrackPoint> wobblyTrack(int storedPoints) {
    return IntStream.range(0, storedPoints)
        .mapToObj(
            i -> {
              double wobble = i % 2 == 0 ? 0 : 0.00001; // ~1.1 m, well inside any tolerance
              return new GpxTrack.TrackPoint(45.0 + wobble, 6.0 + i * 0.0001, 100 + i, i * 7.8);
            })
        .toList();
  }

  /**
   * {@link RouteService#DEFAULT_BULK_MAX_POINTS_PER_ROUTE}: a batch of more than one route that
   * asks for neither 'simplify' nor 'points' must not hand back the full stored track of every
   * route — see defect B1. A single-route batch is exempt (asserted separately below).
   */
  @Test
  void getRoutesBulk_withMoreThanOneRouteAndNoGeometryKnobs_shouldApplyTheDefaultPointBudget() {
    int storedPoints = RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE + 500;
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route A",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route B",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));

    withSlugs(team1Slug, "wobbly-route-a", "wobbly-route-b")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(2))
        .body(
            "routes[0].tracks[0].line.coordinates.size()",
            lessThanOrEqualTo(RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE))
        .body(
            "routes[1].tracks[0].line.coordinates.size()",
            lessThanOrEqualTo(RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE));
  }

  /**
   * The single-slug batch is exempt from the default budget above: it must return the stored
   * track byte for byte, exactly like {@code GET /{routeSlug}} with nothing asked — the default
   * exists for the "many routes at once" case, not the one a batch degenerates to here.
   */
  @Test
  void getRoutesBulk_withOneRouteAndNoGeometryKnobs_shouldReturnTheFullStoredTrack() {
    int storedPoints = RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE + 500;
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));

    withSlugs(team1Slug, "wobbly-route")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes[0].tracks[0].line.coordinates", hasSize(storedPoints));
  }

  /**
   * A duplicate slug requested twice still resolves to one distinct route: the default budget
   * must not kick in just because the raw {@code slug} list has more than one entry.
   */
  @Test
  void getRoutesBulk_withTheSameSlugTwiceAndNoGeometryKnobs_shouldReturnTheFullStoredTrack() {
    int storedPoints = RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE + 500;
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));

    withSlugs(team1Slug, "wobbly-route", "wobbly-route")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(1))
        .body("routes[0].tracks[0].line.coordinates", hasSize(storedPoints));
  }

  /**
   * Defect B1, bypass 1: an explicit {@code points} above the budget must not let a caller of a
   * multi-route batch skip {@link RouteService#DEFAULT_BULK_MAX_POINTS_PER_ROUTE} — a prior fix
   * only clamped when the caller asked for nothing at all ({@code geometry.isFull()}), so naming
   * any {@code points} value (however large) restored the unbounded response.
   */
  @Test
  void getRoutesBulk_withPointsAboveTheBudgetAndMoreThanOneRoute_shouldStillHonourTheBudget() {
    int storedPoints = RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE + 500;
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route A",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route B",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));

    withSlugs(team1Slug, "wobbly-route-a", "wobbly-route-b")
        .queryParam("points", 100_000) // GeometryOptions.MAX_POINTS: the largest a caller can ask
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(2))
        .body(
            "routes[0].tracks[0].line.coordinates.size()",
            lessThanOrEqualTo(RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE))
        .body(
            "routes[1].tracks[0].line.coordinates.size()",
            lessThanOrEqualTo(RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE));
  }

  /**
   * Defect B1, bypass 2: a near-zero {@code simplify} tolerance keeps essentially every point of
   * the source track, which made {@code geometry.isFull()} false and — before the fix — skipped
   * the default budget entirely, since {@code points} was left unset. The batch got full tracks
   * back, plus a wasted Douglas-Peucker pass over every one of them.
   */
  @Test
  void getRoutesBulk_withATinySimplifyAndMoreThanOneRoute_shouldStillHonourTheBudget() {
    int storedPoints = RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE + 500;
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route A",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route B",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));

    withSlugs(team1Slug, "wobbly-route-a", "wobbly-route-b")
        .queryParam("simplify", 0.0000001) // keeps virtually every point on its own
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(2))
        .body(
            "routes[0].tracks[0].line.coordinates.size()",
            lessThanOrEqualTo(RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE))
        .body(
            "routes[1].tracks[0].line.coordinates.size()",
            lessThanOrEqualTo(RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE));
  }

  /**
   * Secondary fix on B1: the clamp is keyed on how many distinct routes the batch actually
   * resolves to (as the {@code getRoutesBulk} javadoc promises), not on how many slugs were
   * requested — an unreadable/unknown slug alongside one real route must not trigger it, exactly
   * like requesting that one real route alone.
   */
  @Test
  void
      getRoutesBulk_withOneRealSlugAndOneUnknownSlugAndNoGeometryKnobs_shouldReturnTheFullStoredTrack() {
    int storedPoints = RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE + 500;
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));

    withSlugs(team1Slug, "wobbly-route", "no-such-route")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(1))
        .body("routes[0].tracks[0].line.coordinates", hasSize(storedPoints));
  }

  /**
   * Defect B1-a: the per-route point budget was applied to every track of a route independently
   * ({@link RouteService#DEFAULT_BULK_MAX_POINTS_PER_ROUTE} points PER TRACK), not to the route as
   * a whole. A route can hold any number of tracks (one per GPX {@code trk}/{@code rte} element,
   * uncapped at import), so a route with N tracks returned up to N times the promised budget. Here
   * each of 50 tracks stores 100 points, well above its post-division share (1000 / 50 = 20): sans
   * the fix this would return close to 50 * 100 = 5000 points for ONE route, not <= 1000.
   */
  @Test
  void getRoutesBulk_withAManyTracksRouteAndMoreThanOneRoute_shouldBoundTheTotalPerRoute() {
    int tracksPerRoute = 50;
    int pointsPerTrack = 100;
    List<List<GpxTrack.TrackPoint>> tracks =
        IntStream.range(0, tracksPerRoute).mapToObj(t -> wobblyTrack(pointsPerTrack)).toList();
    dataService.createRouteWithTracks(team1, user1, "Many Tracks A", Visibility.PUBLIC, tracks);
    dataService.createRouteWithTracks(team1, user1, "Many Tracks B", Visibility.PUBLIC, tracks);

    List<Map<String, Object>> tracksJson =
        withSlugs(team1Slug, "many-tracks-a", "many-tracks-b")
            .when()
            .get(bulkUrl(team1Slug))
            .then()
            .statusCode(200)
            .body("routes", hasSize(2))
            .body("routes[0].tracks", hasSize(tracksPerRoute))
            .extract()
            .path("routes[0].tracks");

    int totalPointsRouteA = 0;
    for (Map<String, Object> track : tracksJson) {
      @SuppressWarnings("unchecked")
      Map<String, Object> line = (Map<String, Object>) track.get("line");
      List<?> coordinates = (List<?>) line.get("coordinates");
      assertTrue(coordinates.size() >= 2, "every track must keep at least its two endpoints");
      totalPointsRouteA += coordinates.size();
    }

    assertTrue(
        totalPointsRouteA <= RouteService.DEFAULT_BULK_MAX_POINTS_PER_ROUTE,
        "a route with "
            + tracksPerRoute
            + " tracks must not exceed the per-route budget summed across all of them, got "
            + totalPointsRouteA);
    assertTrue(
        totalPointsRouteA < tracksPerRoute * pointsPerTrack,
        "the per-track leak (budget applied to each track independently) would return close to "
            + (tracksPerRoute * pointsPerTrack)
            + " points; got "
            + totalPointsRouteA);
  }

  /**
   * Defect B1-b: {@code points=2} on a batch that resolves to a single route used to return the
   * complete stored track — {@link fr.pedalons.service.route.TrackGeometry#decimateTo} treated any
   * {@code maxPoints <= 2} as "not worth decimating". This is the production path all three web
   * call sites that only need the route's asset links exercise (they request {@code points=2}
   * expecting the minimum, not the full geometry).
   */
  @Test
  void getRoutesBulk_withPointsEqualsTwoAndOneRoute_shouldReturnJustTheEndpoints() {
    int storedPoints = 50;
    dataService.createRoute(
        team1,
        user1,
        "Wobbly Route",
        Visibility.PUBLIC,
        "LINESTRING(6 45,6.1 45)",
        wobblyTrack(storedPoints));

    withSlugs(team1Slug, "wobbly-route")
        .queryParam("points", 2)
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes[0].tracks[0].line.coordinates", hasSize(2));
  }

  @Test
  void getRoutesBulk_withElevationFalse_shouldNotAttachAProfile() {
    seedRouteAt(team1, user1, "Route A", Visibility.PUBLIC, 45.0, 6.0, 45.1, 6.1);

    withSlugs(team1Slug, "route-a")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes[0]", not(hasKey("elevationProfile")));
  }

  @Test
  void getRoutesBulk_withElevationTrue_shouldAttachAProfilePerRoute() {
    List<GpxTrack.TrackPoint> roof =
        List.of(
            new GpxTrack.TrackPoint(45.0, 6.0, 100.0, 0.0),
            new GpxTrack.TrackPoint(45.05, 6.0, 200.0, 5_000.0),
            new GpxTrack.TrackPoint(45.1, 6.0, 100.0, 10_000.0));
    dataService.createRoute(
        team1, user1, "Roof Route", Visibility.PUBLIC, "LINESTRING(6 45,6 45.1)", roof);

    withSlugs(team1Slug, "roof-route")
        .queryParam("elevation", true)
        .queryParam("elevationSamples", 3)
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes[0].elevationProfile.slug", equalTo("roof-route"))
        .body("routes[0].elevationProfile.samples", equalTo(3))
        .body("routes[0].elevationProfile.points", hasSize(3))
        .body("routes[0].elevationProfile.distance", equalTo(10_000.0f))
        .body("routes[0].elevationProfile.maxElevation", equalTo(200.0f));
  }

  @Test
  void getRoutesBulk_extent_shouldCoverEveryReturnedRoute() {
    seedRouteAt(team1, user1, "Route A", Visibility.PUBLIC, 45.0, 6.0, 45.1, 6.1);
    seedRouteAt(team1, user1, "Route B", Visibility.PUBLIC, 46.0, 7.0, 46.2, 7.2);

    withSlugs(team1Slug, "route-a", "route-b")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("extent.minLon", equalTo(6.0f))
        .body("extent.minLat", equalTo(45.0f))
        .body("extent.maxLon", equalTo(7.2f))
        .body("extent.maxLat", equalTo(46.2f));
  }

  @Test
  void getRoutesBulk_extent_shouldIgnoreARouteDroppedForUnreadability() {
    seedRouteAt(team1, user1, "Route A", Visibility.PUBLIC, 45.0, 6.0, 45.1, 6.1);
    // A private route far away: must not widen the extent once it is filtered out.
    seedRouteAt(team1, user1, "Secret Route", Visibility.TEAM, 60.0, 20.0, 61.0, 21.0);

    withSlugs(team1Slug, "route-a", "secret-route")
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(1))
        .body("extent.minLon", equalTo(6.0f))
        .body("extent.maxLon", equalTo(6.1f));
  }

  /**
   * Defect B3: the extent must be framed on the track geometry only. A waypoint imported from a
   * GPX (a meeting cafe, a car park) can sit tens of km off the actual loop; since no renderer
   * draws waypoints (see {@code routeToGeoJSON} on the web), including it in the extent would
   * zoom a map out until the ride is a squiggle in one corner.
   */
  @Test
  void getRoutesBulk_extent_shouldIgnoreAWaypointFarFromTheTrack() {
    Route route =
        dataService.createRoute(
            team1,
            user1,
            "Route With A Distant Waypoint",
            Visibility.PUBLIC,
            "LINESTRING(6 45,6.1 45.1)",
            List.of(
                new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
                new GpxTrack.TrackPoint(45.1, 6.1, 510.0, 10_000.0)));
    dataService.addWaypoint(route, user1, "Meeting Cafe", 60.0, 20.0);

    withSlugs(team1Slug, route.getSlug())
        .when()
        .get(bulkUrl(team1Slug))
        .then()
        .statusCode(200)
        .body("routes[0].waypoints", hasSize(1))
        .body("extent.minLon", equalTo(6.0f))
        .body("extent.minLat", equalTo(45.0f))
        .body("extent.maxLon", equalTo(6.1f))
        .body("extent.maxLat", equalTo(45.1f));
  }

  @Test
  void getRoutesBulk_withTooManySlugs_shouldReturn400() {
    List<String> tooMany =
        IntStream.rangeClosed(0, RouteService.MAX_BULK_SLUGS).mapToObj(i -> "slug-" + i).toList();

    RequestSpecification spec = given();
    for (String slug : tooMany) {
      spec = spec.queryParam("slug", slug);
    }

    spec.when().get(bulkUrl(team1Slug)).then().statusCode(400);
  }

  @Test
  void getRoutesBulk_atTheCap_shouldStillAnswer() {
    List<String> atCap =
        IntStream.range(0, RouteService.MAX_BULK_SLUGS).mapToObj(i -> "slug-" + i).toList();

    RequestSpecification spec = given();
    for (String slug : atCap) {
      spec = spec.queryParam("slug", slug);
    }

    spec.when().get(bulkUrl(team1Slug)).then().statusCode(200).body("routes", hasSize(0));
  }

  @Test
  void getRoutesBulk_shouldNotLeakAPrivateRouteOfAnotherTeamToANonMember() {
    seedRouteAt(team2, user1, "Secret Route", Visibility.TEAM, 45.0, 6.0, 45.1, 6.1);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .queryParam("slug", "secret-route")
        .when()
        .get(bulkUrl(team2Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(0))
        .body("extent", nullValue());
  }

  @Test
  void getRoutesBulk_shouldExposeAPrivateRouteOfAnotherTeamToItsMember() {
    seedRouteAt(team2, user1, "Secret Route", Visibility.TEAM, 45.0, 6.0, 45.1, 6.1);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("slug", "secret-route")
        .when()
        .get(bulkUrl(team2Slug))
        .then()
        .statusCode(200)
        .body("routes", hasSize(1))
        .body("routes[0].slug", equalTo("secret-route"));
  }

  @Test
  void getRoutesBulk_toNonexistentTeam_shouldReturn404() {
    given()
        .queryParam("slug", "whatever")
        .when()
        .get(bulkUrl("no-such-team"))
        .then()
        .statusCode(404);
  }

  /** The empty-batch fast path must not skip the team lookup every sibling endpoint does. */
  @Test
  void getRoutesBulk_toNonexistentTeamWithNoSlugs_shouldStillReturn404() {
    given().when().get(bulkUrl("no-such-team")).then().statusCode(404);
  }
}
