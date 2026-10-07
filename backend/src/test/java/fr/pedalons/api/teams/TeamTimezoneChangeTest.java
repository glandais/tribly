package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.dto.common.EventDateTime;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.dto.teams.request.TeamRequest;
import fr.pedalons.dto.trips.request.StageRequest;
import fr.pedalons.dto.trips.request.TripRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A change of the team's zone (docs/LEDGER_*.md API-60, plan §9): the rides, trips, stages and
 * posts that no place locates and that are stored in the old zone move to the new one — their
 * instants to come at constant wall time, their past ones at constant instant. Anything located,
 * anything stored in another zone is left alone.
 */
@QuarkusTest
class TeamTimezoneChangeTest extends AbstractResourceTest {

  private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
  private static final ZoneId MONTREAL = ZoneId.of("America/Montreal");

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.setTeamModules(team1, true, true);
  }

  private void changeTeamZone(String zone) {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(
            new TeamRequest(
                "Team 1",
                MediaDto.builder().build(),
                Visibility.PUBLIC,
                true,
                true,
                true,
                true,
                true,
                false,
                null,
                null,
                zone))
        .when()
        .put("/api/teams/" + team1Slug)
        .then()
        .statusCode(200);
  }

  private JsonPath createRide(String wallTime, @Nullable Place start, List<GroupRequest> groups) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(
            new RideRequest(
                "Sortie",
                MediaDto.builder().build(),
                EventDateTime.local(LocalDateTime.parse(wallTime)),
                Status.PUBLISHED,
                Visibility.PUBLIC,
                null,
                start == null ? null : TsidUtils.toString(start.getId()),
                null,
                null,
                groups,
                null))
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath();
  }

  private JsonPath getRide(String slug) {
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

  private static Instant sameWallTime(Instant instant) {
    return instant.atZone(PARIS).toLocalDateTime().atZone(MONTREAL).toInstant();
  }

  @Test
  void anUpcomingRideWithoutPlace_keepsItsWallTime_inTheNewZone() {
    JsonPath created =
        createRide(
            "2030-06-02T09:30:00",
            null,
            List.of(new GroupRequest(null, "Tard", LocalTime.of(10, 0), null, null, null)));
    assertEquals("2030-06-02T07:30:00Z", created.getString("dateTime"));

    changeTeamZone("America/Montreal");

    JsonPath ride = getRide(created.getString("slug"));
    assertEquals("America/Montreal", ride.getString("timezone"));
    // 09:30 in Montreal (UTC-4 in June).
    assertEquals("2030-06-02T13:30:00Z", ride.getString("dateTime"));
    // The group follows its ride: 10:00 in Montreal.
    assertEquals("2030-06-02T14:00:00Z", ride.getString("groups[0].startAt"));
  }

  @Test
  void anUpcomingRideWithAPlace_isLeftAlone() {
    Place paris = dataService.createPlaceAt(team1, user1, "Notre-Dame", 48.853, 2.349);
    JsonPath created = createRide("2030-06-02T09:30:00", paris, List.of());

    changeTeamZone("America/Montreal");

    JsonPath ride = getRide(created.getString("slug"));
    assertEquals("Europe/Paris", ride.getString("timezone"));
    assertEquals("2030-06-02T07:30:00Z", ride.getString("dateTime"));
  }

  /** Past: relabelled at constant instant, so that its next save does not move it. */
  @Test
  void aPastRide_keepsItsInstant_inTheNewZone() {
    Instant past = Instant.now().minus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
    Ride ride = dataService.createRide(team1, user1, "Passée", "passee", past);

    changeTeamZone("America/Montreal");

    assertEquals(past, dataService.getDateTime(ride.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(ride.getId()));
  }

  /** A past ride's groups keep their departures: their times are rewritten in the new zone. */
  @Test
  void aPastRideWithAGroup_keepsItsGroupDeparture() {
    JsonPath created =
        createRide(
            "2020-06-02T09:30:00",
            null,
            List.of(new GroupRequest(null, "Tard", LocalTime.of(10, 0), null, null, null)));
    assertEquals("2020-06-02T08:00:00Z", created.getString("groups[0].startAt"));

    changeTeamZone("America/Montreal");

    JsonPath ride = getRide(created.getString("slug"));
    assertEquals("America/Montreal", ride.getString("timezone"));
    assertEquals("2020-06-02T07:30:00Z", ride.getString("dateTime"));
    assertEquals("2020-06-02T08:00:00Z", ride.getString("groups[0].startAt"));
    // 08:00Z is 04:00 in Montreal (UTC-4 in June).
    assertEquals("04:00:00", ride.getString("groups[0].time"));
  }

  /** Stored in a zone that is not the team's: its time was not typed against the team's label. */
  @Test
  void anUpcomingRideStoredInAnotherZone_isLeftAlone() {
    Instant start = Instant.parse("2030-06-02T07:30:00Z");
    Ride ride = dataService.createRide(team1, user1, "Ailleurs", "ailleurs", start);
    dataService.setTimezone(ride.getId(), "Asia/Tokyo");

    changeTeamZone("America/Montreal");

    assertEquals(start, dataService.getDateTime(ride.getId()));
    assertEquals("Asia/Tokyo", dataService.getTimezone(ride.getId()));
  }

  @Test
  void anUpcomingPost_andItsScheduledPublication_keepTheirWallTime() {
    Instant date = Instant.parse("2030-06-02T07:30:00Z");
    Instant publishAt = Instant.parse("2030-06-01T16:00:00Z");
    Post post =
        dataService.createPost(
            team1, user1, "Billet", date, Visibility.PUBLIC, Status.DRAFT, publishAt);

    changeTeamZone("America/Montreal");

    assertEquals(sameWallTime(date), dataService.getDateTime(post.getId()));
    assertEquals(sameWallTime(publishAt), dataService.getPublishAt(post.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(post.getId()));
  }

  @Test
  void aPastPost_keepsItsInstant_inTheNewZone() {
    Instant date = Instant.now().minus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
    Post post = dataService.createPost(team1, user1, "Ancien billet", date);

    changeTeamZone("America/Montreal");

    assertEquals(date, dataService.getDateTime(post.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(post.getId()));
  }

  @Test
  void anUpcomingTripWithoutPlaces_isRewritten_withItsStages() {
    Instant day1 = Instant.parse("2030-06-02T06:00:00Z");
    Instant day2 = day1.plus(Duration.ofDays(1));
    Trip trip = dataService.createTrip(team1, user1, "Voyage", day1);
    TripStage j1 = dataService.createTripStage(user1, trip, "J1", 0, day1);
    TripStage j2 = dataService.createTripStage(user1, trip, "J2", 1, day2);

    changeTeamZone("America/Montreal");

    assertEquals(sameWallTime(day1), dataService.getDateTime(trip.getId()));
    assertEquals(sameWallTime(day1), dataService.getDateTime(j1.getId()));
    assertEquals(sameWallTime(day2), dataService.getDateTime(j2.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(trip.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(j2.getId()));
    // The stored end follows the last stage (docs/LEDGER_*.md API-85).
    assertEquals(
        sameWallTime(day2).plus(Duration.ofHours(3)), dataService.getEndDateTime(trip.getId()));
  }

  /** A trip whose first stage has a place is located: neither it nor that stage move. */
  @Test
  void aTripLocatedByItsFirstStage_isLeftAlone() {
    Place paris = dataService.createPlaceAt(team1, user1, "Notre-Dame", 48.853, 2.349);
    Instant day1 = Instant.parse("2030-06-02T06:00:00Z");
    Instant day2 = day1.plus(Duration.ofDays(1));
    Trip trip = dataService.createTrip(team1, user1, "Voyage situé", day1);
    TripStage j1 = dataService.createTripStage(user1, trip, "J1", 0, day1);
    TripStage j2 = dataService.createTripStage(user1, trip, "J2", 1, day2);
    dataService.setTripStagePlaces(j1, paris, null);

    changeTeamZone("America/Montreal");

    assertEquals(day1, dataService.getDateTime(trip.getId()));
    assertEquals(day1, dataService.getDateTime(j1.getId()));
    // J2 inherits J1's located zone: located too.
    assertEquals(day2, dataService.getDateTime(j2.getId()));
    assertEquals("Europe/Paris", dataService.getTimezone(j2.getId()));
  }

  /**
   * A trip under way: the stage already ridden and the trip's start keep their instants, its stages
   * to come their wall times — and all of them are stored in the new zone.
   */
  @Test
  void aTripUnderWay_onlyItsStagesToComeMove() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    Instant yesterday = now.minus(Duration.ofDays(1));
    Instant tomorrow = now.plus(Duration.ofDays(1));
    Trip trip = dataService.createTrip(team1, user1, "En route", yesterday);
    TripStage j1 = dataService.createTripStage(user1, trip, "J1", 0, yesterday);
    TripStage j2 = dataService.createTripStage(user1, trip, "J2", 1, tomorrow);
    dataService.setEndDateTime(trip.getId(), tomorrow.plus(Duration.ofHours(3)));

    changeTeamZone("America/Montreal");

    assertEquals(yesterday, dataService.getDateTime(trip.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(trip.getId()));
    assertEquals(yesterday, dataService.getDateTime(j1.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(j1.getId()));
    assertEquals(sameWallTime(tomorrow), dataService.getDateTime(j2.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(j2.getId()));
  }

  /**
   * The failure §9 prevents: after the change, an edit of the trip under way resends every stage as
   * the wall times it shows, in the stored zone — and the stage already ridden does not move.
   */
  @Test
  void aTripUnderWay_resavedWithItsWallTimes_doesNotMove() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    Instant yesterday = now.minus(Duration.ofDays(1));
    Instant tomorrow = now.plus(Duration.ofDays(1));
    Trip trip = dataService.createTrip(team1, user1, "En route", yesterday);
    TripStage j1 = dataService.createTripStage(user1, trip, "J1", 0, yesterday);
    TripStage j2 = dataService.createTripStage(user1, trip, "J2", 1, tomorrow);

    changeTeamZone("America/Montreal");

    Instant j2Start = dataService.getDateTime(j2.getId());
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(
            new TripRequest(
                "En route",
                MediaDto.builder().build(),
                wallTimeIn(yesterday, dataService.getTimezone(trip.getId())),
                trip.getStatus(),
                trip.getVisibility(),
                null,
                null,
                List.of(stage(j1, "J1", yesterday), stage(j2, "J2 renommée", j2Start)),
                null))
        .when()
        .put("/api/teams/" + team1Slug + "/trips/" + trip.getSlug())
        .then()
        .statusCode(200);

    assertEquals(yesterday, dataService.getDateTime(trip.getId()));
    assertEquals(yesterday, dataService.getDateTime(j1.getId()));
    assertEquals(j2Start, dataService.getDateTime(j2.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(j1.getId()));
  }

  /** What the editor sends back for a stage: its wall time in its stored zone. */
  private StageRequest stage(TripStage stage, String name, Instant instant) {
    return StageRequest.builder()
        .id(TsidUtils.toString(stage.getId()))
        .name(name)
        .dateTime(wallTimeIn(instant, dataService.getTimezone(stage.getId())))
        .media(MediaDto.builder().build())
        .build();
  }

  private static EventDateTime wallTimeIn(Instant instant, String zone) {
    return EventDateTime.local(instant.atZone(ZoneId.of(zone)).toLocalDateTime());
  }

  @Test
  void theSameZone_changesNothing() {
    Instant start = Instant.parse("2030-06-02T07:30:00Z");
    Ride ride = dataService.createRide(team1, user1, "Inchangée", "inchangee", start);

    changeTeamZone("Europe/Paris");

    assertEquals(start, dataService.getDateTime(ride.getId()));
    assertEquals("Europe/Paris", dataService.getTimezone(ride.getId()));
  }

  // ─── GET …/timezone/change-preview ────────────────────────────────────────

  private JsonPath preview(String user, String zone, int status) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .queryParam("timezone", zone)
        .when()
        .get("/api/teams/" + team1Slug + "/timezone/change-preview")
        .then()
        .statusCode(status)
        .extract()
        .jsonPath();
  }

  /** The preview lists what the change rewrites, and writes nothing itself. */
  @Test
  void preview_listsWhatTheChangeRewrites_andWritesNothing() {
    Place paris = dataService.createPlaceAt(team1, user1, "Notre-Dame", 48.853, 2.349);
    JsonPath placeLess = createRide("2030-06-02T09:30:00", null, List.of());
    JsonPath located = createRide("2030-06-03T09:30:00", paris, List.of());
    Instant past = Instant.now().minus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
    Ride pastRide = dataService.createRide(team1, user1, "Passée", "passee", past);
    Instant day1 = Instant.parse("2030-07-01T06:00:00Z");
    Trip trip = dataService.createTrip(team1, user1, "Voyage", day1);
    TripStage j1 = dataService.createTripStage(user1, trip, "J1", 0, day1);
    Post post =
        dataService.createPost(team1, user1, "Billet", Instant.parse("2030-05-01T08:00:00Z"));

    JsonPath preview = preview(USER1, "America/Montreal", 200);

    assertEquals("Europe/Paris", preview.getString("from"));
    assertEquals("America/Montreal", preview.getString("to"));
    // The post, the place-less ride, the stage and its trip — soonest first.
    assertEquals(4, preview.getInt("upcomingCount"));
    assertEquals(1, preview.getInt("pastCount"));
    assertEquals(List.of("POST", "RIDE", "TRIP_STAGE", "TRIP"), preview.getList("upcoming.type"));
    assertEquals(
        List.of(
            TsidUtils.toString(post.getId()),
            placeLess.getString("id"),
            TsidUtils.toString(j1.getId()),
            TsidUtils.toString(trip.getId())),
        preview.getList("upcoming.id"));
    assertEquals("Voyage", preview.getString("upcoming[2].tripTitle"));
    assertEquals("2030-06-02T07:30:00Z", preview.getString("upcoming[1].dateTime"));
    // Nothing written.
    assertEquals("Europe/Paris", getRide(placeLess.getString("slug")).getString("timezone"));
    assertEquals("Europe/Paris", dataService.getTimezone(pastRide.getId()));

    changeTeamZone("America/Montreal");

    // Exactly what was listed moved; the located ride did not.
    assertEquals("America/Montreal", getRide(placeLess.getString("slug")).getString("timezone"));
    assertEquals("America/Montreal", dataService.getTimezone(pastRide.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(j1.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(trip.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(post.getId()));
    assertEquals("Europe/Paris", getRide(located.getString("slug")).getString("timezone"));
  }

  @Test
  void preview_ofTheSameZone_isEmpty() {
    createRide("2030-06-02T09:30:00", null, List.of());

    JsonPath preview = preview(USER1, "Europe/Paris", 200);

    assertEquals(0, preview.getInt("upcomingCount"));
    assertEquals(0, preview.getInt("pastCount"));
    assertEquals(List.of(), preview.getList("upcoming"));
  }

  @Test
  void preview_ofAnUnknownZone_is400() {
    assertEquals("INVALID_TIMEZONE", preview(USER1, "Mars/Olympus_Mons", 400).getString("code"));
  }

  @Test
  void preview_byAnOrganizer_is403() {
    preview(USER2, "America/Montreal", 403);
  }

  @Test
  void preview_byAMember_is403() {
    preview(USER3, "America/Montreal", 403);
  }
}
