package fr.pedalons.api.rides;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.dto.common.EventDateTime;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.enums.WindDirection;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A ride's wall times are read in the zone the backend resolves for it (docs/LEDGER_*.md API-60,
 * plan §2 and §4): its start place's, else its route's, else the team's. The zone is stored with
 * the instants, the groups get a {@code startAt}, and the old instant format is still accepted for
 * one version.
 */
@QuarkusTest
class RideTimezoneTest extends AbstractResourceTest {

  private Place paris;
  private Place tokyo;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    paris = dataService.createPlaceAt(team1, user1, "Notre-Dame", 48.853, 2.349);
    tokyo = dataService.createPlaceAt(team1, user1, "Shibuya", 35.66, 139.70);
  }

  // ─── Helpers ──────────────────────────────────────────────────────────────

  private static RideRequest request(
      String wallTime,
      @Nullable Place start,
      @Nullable String routeSlug,
      Status status,
      @Nullable String publishAt,
      List<GroupRequest> groups) {
    return new RideRequest(
        "Sortie",
        MediaDto.builder().build(),
        EventDateTime.local(LocalDateTime.parse(wallTime)),
        status,
        Visibility.PUBLIC,
        routeSlug,
        start == null ? null : TsidUtils.toString(start.getId()),
        null,
        publishAt == null ? null : EventDateTime.local(LocalDateTime.parse(publishAt)),
        groups,
        null);
  }

  private static RideRequest request(
      String wallTime, @Nullable Place start, List<GroupRequest> groups) {
    return request(wallTime, start, null, Status.PUBLISHED, null, groups);
  }

  private JsonPath create(Object body) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(body)
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath();
  }

  private JsonPath update(String slug, RideRequest body) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(body)
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + slug)
        .then()
        .statusCode(200)
        .extract()
        .jsonPath();
  }

  private JsonPath get(String slug) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + slug)
        .then()
        .statusCode(200)
        .extract()
        .jsonPath();
  }

  private Route routeAt(String name, double lat, double lon) {
    return dataService.createRouteWithProperties(
        team1,
        user1,
        name,
        Visibility.PUBLIC,
        40_000,
        100,
        SurfaceType.ROAD,
        WindDirection.NORTH,
        lat,
        lon,
        lat + 0.1,
        lon + 0.1);
  }

  private static String rawRide(String dateTime, @Nullable Place start) {
    return """
    {
      "name": "Ancien SPA",
      "media": {},
      "dateTime": "%s",
      %s
      "status": "PUBLISHED",
      "visibility": "PUBLIC",
      "groups": []
    }
    """
        .formatted(
            dateTime,
            start == null
                ? ""
                : "\"startPlaceId\": \"" + TsidUtils.toString(start.getId()) + "\",");
  }

  // ─── Resolution ───────────────────────────────────────────────────────────

  @Test
  void create_theWallTimeIsReadInTheStartPlacesZone() {
    JsonPath ride = create(request("2030-06-02T08:00:00", tokyo, List.of()));

    assertEquals("Asia/Tokyo", ride.getString("timezone"));
    assertEquals("2030-06-01T23:00:00Z", ride.getString("dateTime"));
    assertEquals("Asia/Tokyo", dataService.getTimezone(TsidUtils.toLong(ride.getString("id"))));
  }

  @Test
  void create_withoutStartPlace_theRoutesZone() {
    Route newYork = routeAt("Central Park", 40.78, -73.97);
    JsonPath ride =
        create(
            request(
                "2030-06-02T08:00:00", null, newYork.getSlug(), Status.PUBLISHED, null, List.of()));

    assertEquals("America/New_York", ride.getString("timezone"));
    assertEquals("2030-06-02T12:00:00Z", ride.getString("dateTime"));
  }

  @Test
  void create_withoutPlaceNorRoute_theTeamsZone() {
    JsonPath ride = create(request("2030-06-02T08:00:00", null, List.of()));

    assertEquals("Europe/Paris", ride.getString("timezone"));
    assertEquals("2030-06-02T06:00:00Z", ride.getString("dateTime"));
  }

  @Test
  void create_aScheduledPublication_isReadInTheRidesZone_too() {
    JsonPath ride =
        create(
            request(
                "2030-06-02T08:00:00",
                tokyo,
                null,
                Status.DRAFT,
                "2030-06-01T18:00:00",
                List.of()));

    assertEquals(
        Instant.parse("2030-06-01T09:00:00Z"),
        dataService.getPublishAt(TsidUtils.toLong(ride.getString("id"))));
  }

  // ─── Changing the place keeps the wall time (plan §2.5) ───────────────────

  @Test
  void movingTheStartFromParisToTokyo_keepsTheWallTime_andMovesTheInstant() {
    JsonPath created =
        create(
            request(
                "2030-06-02T08:00:00",
                paris,
                List.of(new GroupRequest(null, "Tardifs", LocalTime.of(9, 0), null, null, null))));
    assertEquals("2030-06-02T06:00:00Z", created.getString("dateTime"));
    assertEquals("2030-06-02T07:00:00Z", created.getString("groups[0].startAt"));
    String groupId = created.getString("groups[0].id");

    JsonPath moved =
        update(
            created.getString("slug"),
            request(
                "2030-06-02T08:00:00",
                tokyo,
                List.of(
                    new GroupRequest(groupId, "Tardifs", LocalTime.of(9, 0), null, null, null))));

    assertEquals("Asia/Tokyo", moved.getString("timezone"));
    assertEquals("2030-06-01T23:00:00Z", moved.getString("dateTime"));
    assertEquals("2030-06-02T00:00:00Z", moved.getString("groups[0].startAt"));
    assertEquals(
        Instant.parse("2030-06-02T00:00:00Z"),
        dataService.getGroupStartAt(TsidUtils.toLong(groupId)));
  }

  // ─── Groups ───────────────────────────────────────────────────────────────

  @Test
  void create_everyGroupGetsItsStart() {
    JsonPath ride =
        create(
            request(
                "2030-06-02T08:00:00",
                paris,
                List.of(
                    new GroupRequest(null, "Avec", null, null, null, null),
                    new GroupRequest(null, "Tard", LocalTime.of(10, 30), null, null, null))));

    assertEquals(
        "2030-06-02T06:00:00Z", ride.getString("groups.find { it.name == 'Avec' }.startAt"));
    assertEquals(
        "2030-06-02T08:30:00Z", ride.getString("groups.find { it.name == 'Tard' }.startAt"));
    // The summaries carry the same start.
    assertEquals(
        "2030-06-02T08:30:00Z",
        ride.getString("groupSummaries.find { it.name == 'Tard' }.startAt"));
  }

  /** Plan §2.6: the date of the ride moves its groups, even those the request left as they were. */
  @Test
  void changingTheRidesDate_movesItsGroupsStart() {
    JsonPath created =
        create(
            request(
                "2030-06-02T08:00:00",
                paris,
                List.of(
                    new GroupRequest(null, "Avec", null, null, null, null),
                    new GroupRequest(null, "Tard", LocalTime.of(10, 0), null, null, null))));
    String with = created.getString("groups.find { it.name == 'Avec' }.id");
    String late = created.getString("groups.find { it.name == 'Tard' }.id");

    update(
        created.getString("slug"),
        request(
            "2030-06-09T08:00:00",
            paris,
            List.of(
                new GroupRequest(with, "Avec", null, null, null, null),
                new GroupRequest(late, "Tard", LocalTime.of(10, 0), null, null, null))));

    assertEquals(
        Instant.parse("2030-06-09T06:00:00Z"), dataService.getGroupStartAt(TsidUtils.toLong(with)));
    assertEquals(
        Instant.parse("2030-06-09T08:00:00Z"), dataService.getGroupStartAt(TsidUtils.toLong(late)));
  }

  /**
   * A group written by the previous release has no {@code start_at}: the response falls back on
   * its time on the ride's local date, in the ride's stored zone, on the detail and in the list.
   */
  @Test
  void aGroupWithoutStoredStart_isServedWithTheFallback() {
    Ride ride =
        dataService.createRide(
            team1, user1, "Ancienne", "ancienne", Instant.parse("2030-06-02T06:00:00Z"));
    RideGroup group = dataService.createRideGroup(user1, ride, "G");
    dataService.setGroupTime(group.getId(), LocalTime.of(10, 0));
    dataService.setGroupStartAt(group.getId(), null);

    JsonPath detail = get("ancienne");
    assertEquals("Europe/Paris", detail.getString("timezone"));
    assertEquals("2030-06-02T08:00:00Z", detail.getString("groups[0].startAt"));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("type", "RIDE")
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications[0].timezone", equalTo("Europe/Paris"))
        .body("publications[0].groupSummaries[0].startAt", equalTo("2030-06-02T08:00:00Z"));
  }

  /** A row without a stored zone (the previous release's) reads the team's. */
  @Test
  void aRideWithoutStoredZone_isServedInTheTeamsZone() {
    Ride ride =
        dataService.createRide(
            team1, user1, "Sans fuseau", "sans-fuseau", Instant.parse("2030-06-02T06:00:00Z"));
    dataService.setTimezone(ride.getId(), null);
    dataService.setTeamTimezone(team1, "America/Montreal");

    assertEquals("America/Montreal", get("sans-fuseau").getString("timezone"));
  }

  // ─── Daylight saving time (plan §6: ZonedDateTime.ofLocal) ─────────────────

  /** Paris springs forward on 2030-03-31: 02:30 does not exist and moves by the gap, to 03:30. */
  @Test
  void aWallTimeInTheSpringGap_isShiftedByTheGap() {
    JsonPath ride =
        create(
            request(
                "2030-03-31T02:30:00",
                paris,
                List.of(new GroupRequest(null, "G", LocalTime.of(2, 45), null, null, null))));

    assertEquals("2030-03-31T01:30:00Z", ride.getString("dateTime"));
    assertEquals("2030-03-31T01:45:00Z", ride.getString("groups[0].startAt"));
  }

  /** Paris falls back on 2030-10-27: 02:30 exists twice, the earlier offset (+02:00) is taken. */
  @Test
  void aWallTimeInTheAutumnOverlap_takesTheEarlierOffset() {
    JsonPath ride =
        create(
            request(
                "2030-10-27T02:30:00",
                paris,
                List.of(new GroupRequest(null, "G", LocalTime.of(2, 45), null, null, null))));

    assertEquals("2030-10-27T00:30:00Z", ride.getString("dateTime"));
    assertEquals("2030-10-27T00:45:00Z", ride.getString("groups[0].startAt"));
  }

  // ─── The old format, tolerated for one version (plan §5, §8) ───────────────

  @Test
  void anInstantWithZ_isStillAccepted_andStoredAtThatInstant() {
    JsonPath ride = create(rawRide("2030-06-02T06:00:00Z", tokyo));

    assertEquals("2030-06-02T06:00:00Z", ride.getString("dateTime"));
    assertEquals("Asia/Tokyo", ride.getString("timezone"));
  }

  @Test
  void anInstantWithAnOffset_isStillAccepted_andStoredAtThatInstant() {
    JsonPath ride = create(rawRide("2030-06-02T08:00:00+02:00", null));

    assertEquals("2030-06-02T06:00:00Z", ride.getString("dateTime"));
  }

  @Test
  void aWallTimeAsTheNewSpaSendsIt_isAccepted() {
    JsonPath ride = create(rawRide("2030-06-02T08:00", tokyo));

    assertEquals("2030-06-01T23:00:00Z", ride.getString("dateTime"));
  }

  @Test
  void aMalformedDate_is400() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(rawRide("samedi prochain", null))
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(400);
  }

  // ─── The other payloads carrying a rendezvous ─────────────────────────────

  /** The agenda and the route usages carry the ride's stored zone (plan §5). */
  @Test
  void theCalendarEvent_andTheRouteUsage_carryTheRidesZone() {
    Instant soon = Instant.now().plus(3, java.time.temporal.ChronoUnit.DAYS);
    Ride ride = dataService.createRide(team1, user1, "Au Japon", "au-japon", soon);
    Route route = routeAt("Tokyo", 35.68, 139.76);
    dataService.setRideRoute(ride, route);
    dataService.setTimezone(ride.getId(), "Asia/Tokyo");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/calendar/events")
        .then()
        .statusCode(200)
        .body("events.find { it.entitySlug == 'au-japon' }.timezone", equalTo("Asia/Tokyo"));

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/routes/" + route.getSlug() + "/usages")
        .then()
        .statusCode(200)
        .body("usages[0].timezone", equalTo("Asia/Tokyo"));
  }
}
