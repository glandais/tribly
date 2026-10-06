package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.dto.trips.request.StageRequest;
import fr.pedalons.dto.trips.request.TripRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.enums.WindDirection;
import fr.pedalons.service.publication.PublicationEndBackfill;
import fr.pedalons.service.weather.RideWeatherCalculator;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The end stored in {@code team_entities.end_date_time} at every entry point that can move it
 * (docs/LEDGER_*.md API-85, plan {@code 2026-10-06-team-agenda.md} §3.1): the value in the
 * database, read back, not only what the calculator would say. The biketeam import is covered by
 * {@code BiketeamLiveMigrationTest#importedRidesAndTrips_haveTheirEndStored}, the scheduled
 * publication by {@code PublicationPublishSchedulerTest#autoPublish_storesTheEndOfARideThatHadNone};
 * the rule itself by
 * {@code PublicationEndCalculatorTest}.
 */
@QuarkusTest
class PublicationEndStoredTest extends AbstractResourceTest {

  private static final Instant START = Instant.parse("2030-06-02T07:00:00Z");
  private static final Duration DEFAULT = Duration.ofHours(3);

  @Inject PublicationEndBackfill backfill;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.setTeamModules(team1, true, true);
  }

  private Route route(String name, int meters) {
    return dataService.createRouteWithProperties(
        team1,
        user1,
        name,
        Visibility.PUBLIC,
        meters,
        100,
        SurfaceType.ROAD,
        WindDirection.NORTH,
        45.0,
        5.0,
        45.1,
        5.1);
  }

  private static RideRequest rideRequest(Instant dateTime, List<GroupRequest> groups) {
    return new RideRequest(
        "Sortie du dimanche",
        MediaDto.builder().build(),
        dateTime,
        Status.PUBLISHED,
        Visibility.PUBLIC,
        null,
        null,
        null,
        null,
        groups);
  }

  private JsonPath postRide(RideRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath();
  }

  private JsonPath putRide(String slug, RideRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + slug)
        .then()
        .statusCode(200)
        .extract()
        .jsonPath();
  }

  private Instant storedEnd(String tsid) {
    return dataService.getEndDateTime(TsidUtils.toLong(tsid));
  }

  // ==================== Ride: creation and update ====================

  @Test
  void createRide_storesTheEndOfItsLatestGroup() {
    Route route = route("Boucle 50", 50_000);
    JsonPath ride =
        postRide(
            rideRequest(
                START,
                List.of(
                    new GroupRequest(null, "Rapides", null, 25f, null, route.getSlug()),
                    new GroupRequest(null, "Cool", null, null, null, null))));

    // Rapides: 50 km at 25 km/h = 2 h; Cool: no speed, the default 3 h.
    assertEquals(START.plus(DEFAULT), storedEnd(ride.getString("id")));
  }

  @Test
  void createRide_withoutGroup_storesTheDefaultDuration() {
    JsonPath ride = postRide(rideRequest(START, List.of()));
    assertEquals(START.plus(DEFAULT), storedEnd(ride.getString("id")));
  }

  @Test
  void createRide_aGroupAtItsOwnTime_endsLater() {
    Route route = route("Boucle 100", 100_000);
    JsonPath ride =
        postRide(
            rideRequest(
                START,
                List.of(
                    new GroupRequest(
                        null,
                        "Tardifs",
                        java.time.LocalTime.of(10, 0),
                        20f,
                        null,
                        route.getSlug()))));
    // The route starts at 45.0 N 5.0 E (Europe/Paris, UTC+2 in June): 10:00 local is 08:00 UTC,
    // plus 100 km at 20 km/h.
    assertEquals(
        Instant.parse("2030-06-02T08:00:00Z").plus(Duration.ofHours(5)),
        storedEnd(ride.getString("id")));
  }

  /**
   * A ride created from a template goes through the same creation: the client prefills the form
   * from the template, groups included, and posts it (there is no server-side "apply").
   */
  @Test
  void createRide_withATemplatesGroups_storesTheirEnd() {
    Route route = route("Gabarit", 60_000);
    JsonPath ride =
        postRide(
            rideRequest(
                START,
                List.of(new GroupRequest(null, "Groupe 1", null, 30f, null, route.getSlug()))));
    assertEquals(START.plus(Duration.ofHours(2)), storedEnd(ride.getString("id")));
  }

  @Test
  void updateRide_movesTheEnd_withTheDate_andTheGroupsLeft() {
    Route route = route("Boucle 80", 80_000);
    JsonPath ride =
        postRide(
            rideRequest(
                START,
                List.of(
                    new GroupRequest(null, "Longue", null, 20f, null, route.getSlug()),
                    new GroupRequest(null, "Courte", null, null, null, null))));
    assertEquals(START.plus(Duration.ofHours(4)), storedEnd(ride.getString("id")));

    // A day later, the long group dropped: only the default duration of the short one remains.
    Instant later = START.plus(Duration.ofDays(1));
    String shortId = ride.getString("groups.find { it.name == 'Courte' }.id");
    putRide(
        ride.getString("slug"),
        rideRequest(later, List.of(new GroupRequest(shortId, "Courte", null, null, null, null))));

    assertEquals(later.plus(DEFAULT), storedEnd(ride.getString("id")));
  }

  // ==================== Trip stages ====================

  private static StageRequest stage(
      String id, String name, Instant dateTime, Float speed, String routeSlug) {
    return StageRequest.builder()
        .id(id)
        .name(name)
        .dateTime(dateTime)
        .averageSpeed(speed)
        .routeSlug(routeSlug)
        .media(MediaDto.builder().build())
        .build();
  }

  private static TripRequest tripRequest(List<StageRequest> stages) {
    return new TripRequest(
        "Tour des Alpes",
        MediaDto.builder().build(),
        START,
        Status.PUBLISHED,
        Visibility.PUBLIC,
        null,
        null,
        stages);
  }

  private JsonPath postTrip(TripRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/trips")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath();
  }

  private JsonPath putTrip(String slug, TripRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/trips/" + slug)
        .then()
        .statusCode(200)
        .extract()
        .jsonPath();
  }

  @Test
  void createTrip_storesTheEndOfEachStage_andOfItsLatest() {
    Route route = route("Étape 1", 80_000);
    Instant day2 = START.plus(Duration.ofDays(1));
    JsonPath trip =
        postTrip(
            tripRequest(
                List.of(
                    stage(null, "J1", START, 20f, route.getSlug()),
                    stage(null, "J2", day2, null, null))));

    assertEquals(day2.plus(DEFAULT), storedEnd(trip.getString("id")));
    assertEquals(
        START.plus(Duration.ofHours(4)),
        storedEnd(trip.getString("stages.find { it.name == 'J1' }.id")));
  }

  @Test
  void createTrip_withoutStage_storesTheDefaultDuration() {
    JsonPath trip = postTrip(tripRequest(List.of()));
    assertEquals(START.plus(DEFAULT), storedEnd(trip.getString("id")));
  }

  @Test
  void addingAStage_movesTheEnd() {
    JsonPath trip = postTrip(tripRequest(List.of(stage(null, "J1", START, null, null))));
    String j1 = trip.getString("stages[0].id");
    Instant day3 = START.plus(Duration.ofDays(2));

    putTrip(
        trip.getString("slug"),
        tripRequest(
            List.of(stage(j1, "J1", START, null, null), stage(null, "J3", day3, null, null))));

    assertEquals(day3.plus(DEFAULT), storedEnd(trip.getString("id")));
  }

  @Test
  void editingAStage_movesTheEnd() {
    Route route = route("Col", 60_000);
    JsonPath trip = postTrip(tripRequest(List.of(stage(null, "J1", START, null, null))));
    String j1 = trip.getString("stages[0].id");

    putTrip(
        trip.getString("slug"), tripRequest(List.of(stage(j1, "J1", START, 15f, route.getSlug()))));

    // 60 km at 15 km/h.
    assertEquals(START.plus(Duration.ofHours(4)), storedEnd(trip.getString("id")));
    assertEquals(START.plus(Duration.ofHours(4)), storedEnd(j1));
  }

  @Test
  void removingTheLastStage_bringsTheEndBack() {
    Instant day2 = START.plus(Duration.ofDays(1));
    JsonPath trip =
        postTrip(
            tripRequest(
                List.of(
                    stage(null, "J1", START, null, null), stage(null, "J2", day2, null, null))));
    assertEquals(day2.plus(DEFAULT), storedEnd(trip.getString("id")));
    String j1 = trip.getString("stages.find { it.name == 'J1' }.id");

    putTrip(trip.getString("slug"), tripRequest(List.of(stage(j1, "J1", START, null, null))));

    assertEquals(START.plus(DEFAULT), storedEnd(trip.getString("id")));
  }

  @Test
  void reorderingTheStages_keepsTheEndOfTheLatest() {
    Instant day2 = START.plus(Duration.ofDays(1));
    JsonPath trip =
        postTrip(
            tripRequest(
                List.of(
                    stage(null, "J1", START, null, null), stage(null, "J2", day2, null, null))));
    String j1 = trip.getString("stages.find { it.name == 'J1' }.id");
    String j2 = trip.getString("stages.find { it.name == 'J2' }.id");
    // Stale on purpose: the reorder must write it again.
    dataService.setEndDateTime(TsidUtils.toLong(trip.getString("id")), START);

    putTrip(
        trip.getString("slug"),
        tripRequest(
            List.of(stage(j2, "J2", day2, null, null), stage(j1, "J1", START, null, null))));

    // The latest stage, not the one sorted last.
    assertEquals(day2.plus(DEFAULT), storedEnd(trip.getString("id")));
  }

  // ==================== Route track replaced ====================

  @Test
  void replacingARoutesTrack_movesTheEndOfEveryRideGroupAndStageOnIt() {
    Route route = route("Remplacée", 50_000);
    JsonPath ride =
        postRide(
            rideRequest(
                START, List.of(new GroupRequest(null, "G", null, 25f, null, route.getSlug()))));
    JsonPath trip = postTrip(tripRequest(List.of(stage(null, "J1", START, 25f, route.getSlug()))));
    assertEquals(START.plus(Duration.ofHours(2)), storedEnd(ride.getString("id")));

    float newDistance =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .multiPart(
                "route",
                new RouteRequest(
                    "Remplacée",
                    MediaDto.builder().build(),
                    SurfaceType.ROAD,
                    Visibility.PUBLIC,
                    null),
                MediaType.APPLICATION_JSON)
            .multiPart("gpxFile", new File("src/test/resources/example.gpx"), "application/gpx+xml")
            .when()
            .put("/api/teams/" + team1Slug + "/routes/" + route.getSlug())
            .then()
            .statusCode(200)
            .extract()
            .path("distance");
    assertNotEquals(50_000f, newDistance);

    Instant expected = RideWeatherCalculator.passage(START, newDistance, 25);
    assertEquals(expected, storedEnd(ride.getString("id")));
    assertEquals(expected, storedEnd(trip.getString("id")));
    assertEquals(expected, storedEnd(trip.getString("stages[0].id")));
  }

  // ==================== Startup backfill ====================

  @Test
  void backfill_fillsOnlyTheNullEnds_ofRidesAndTrips() {
    Ride ride = dataService.createRide(team1, user1, "Ancienne", "ancienne", START);
    Trip trip = dataService.createTrip(team1, user1, "Ancien voyage", START);
    Ride alreadyFilled = dataService.createRide(team1, user1, "Déjà", "deja", START);
    Instant kept = START.plus(Duration.ofMinutes(42));
    dataService.setEndDateTime(alreadyFilled.getId(), kept);
    var post = dataService.createPost(team1, user1, "Billet", START);

    backfill.run();
    // Idempotent: a second run finds nothing left.
    assertEquals(0, backfill.run());

    assertEquals(START.plus(DEFAULT), dataService.getEndDateTime(ride.getId()));
    assertEquals(START.plus(DEFAULT), dataService.getEndDateTime(trip.getId()));
    assertEquals(kept, dataService.getEndDateTime(alreadyFilled.getId()));
    assertNull(dataService.getEndDateTime(post.getId()));
    assertNotNull(dataService.getEndDateTime(ride.getId()));
  }
}
