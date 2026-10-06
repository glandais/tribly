package fr.pedalons.api.trips;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.route.Route;
import fr.pedalons.dto.common.EventDateTime;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.trips.request.StageRequest;
import fr.pedalons.dto.trips.request.TripRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.enums.WindDirection;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import java.time.LocalDateTime;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The stage chain of plan §4 through the API (docs/LEDGER_*.md API-60): a stage reads its wall
 * time in its start place's zone, else its route's, else the previous stage's, else the trip
 * route's, else the team's; the trip in its first stage's.
 */
@QuarkusTest
class TripTimezoneTest extends AbstractResourceTest {

  private Place tokyo;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.setTeamModules(team1, true, true);
    tokyo = dataService.createPlaceAt(team1, user1, "Shibuya", 35.66, 139.70);
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

  private static StageRequest stage(
      String name, String wallTime, @Nullable Place start, @Nullable String routeSlug) {
    return StageRequest.builder()
        .name(name)
        .dateTime(EventDateTime.local(LocalDateTime.parse(wallTime)))
        .startPlaceId(start == null ? null : TsidUtils.toString(start.getId()))
        .routeSlug(routeSlug)
        .media(MediaDto.builder().build())
        .build();
  }

  private static TripRequest trip(
      String wallTime, @Nullable String routeSlug, List<StageRequest> stages) {
    return new TripRequest(
        "Voyage",
        MediaDto.builder().build(),
        EventDateTime.local(LocalDateTime.parse(wallTime)),
        Status.PUBLISHED,
        Visibility.PUBLIC,
        routeSlug,
        null,
        stages,
        null);
  }

  private JsonPath create(TripRequest request) {
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

  /** Plan §1, second scenario: a stage in Japan prepared from Paris, typed at 08:00. */
  @Test
  void aStageInJapan_isReadInTokyo_andTheNextOneInheritsIt() {
    Route paris = routeAt("Paris", 48.85, 2.35);
    JsonPath trip =
        create(
            trip(
                "2030-06-02T08:00:00",
                paris.getSlug(),
                List.of(
                    stage("J1", "2030-06-02T08:00:00", tokyo, null),
                    stage("J2", "2030-06-03T08:00:00", null, null))));

    assertEquals("Asia/Tokyo", trip.getString("stages.find { it.name == 'J1' }.timezone"));
    assertEquals(
        "2030-06-01T23:00:00Z", trip.getString("stages.find { it.name == 'J1' }.dateTime"));
    // Not the trip route's Paris: the previous stage's Tokyo.
    assertEquals("Asia/Tokyo", trip.getString("stages.find { it.name == 'J2' }.timezone"));
    assertEquals(
        "2030-06-02T23:00:00Z", trip.getString("stages.find { it.name == 'J2' }.dateTime"));
    // The trip takes its first stage's zone.
    assertEquals("Asia/Tokyo", trip.getString("timezone"));
    assertEquals("2030-06-01T23:00:00Z", trip.getString("dateTime"));
  }

  @Test
  void aStageWithoutPoints_first_takesTheTripRoute() {
    Route newYork = routeAt("Central Park", 40.78, -73.97);
    JsonPath trip =
        create(
            trip(
                "2030-06-02T08:00:00",
                newYork.getSlug(),
                List.of(stage("J1", "2030-06-02T08:00:00", null, null))));

    assertEquals("America/New_York", trip.getString("stages[0].timezone"));
    assertEquals("2030-06-02T12:00:00Z", trip.getString("stages[0].dateTime"));
    assertEquals("America/New_York", trip.getString("timezone"));
  }

  @Test
  void aStageLocatedByItsRoute() {
    Route newYork = routeAt("Brooklyn", 40.68, -73.94);
    JsonPath trip =
        create(
            trip(
                "2030-06-02T08:00:00",
                null,
                List.of(stage("J1", "2030-06-02T08:00:00", null, newYork.getSlug()))));

    assertEquals("America/New_York", trip.getString("stages[0].timezone"));
  }

  @Test
  void aTripWithoutStage_takesItsRoute() {
    Route newYork = routeAt("Queens", 40.73, -73.79);
    JsonPath trip = create(trip("2030-06-02T08:00:00", newYork.getSlug(), List.of()));

    assertEquals("America/New_York", trip.getString("timezone"));
    assertEquals("2030-06-02T12:00:00Z", trip.getString("dateTime"));
  }

  @Test
  void nothingLocated_theTeamsZone() {
    JsonPath trip =
        create(
            trip(
                "2030-06-02T08:00:00",
                null,
                List.of(stage("J1", "2030-06-02T08:00:00", null, null))));

    assertEquals("Europe/Paris", trip.getString("timezone"));
    assertEquals("Europe/Paris", trip.getString("stages[0].timezone"));
    assertEquals("2030-06-02T06:00:00Z", trip.getString("stages[0].dateTime"));
    assertEquals(
        "Europe/Paris", dataService.getTimezone(TsidUtils.toLong(trip.getString("stages[0].id"))));
  }
}
