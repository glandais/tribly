package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Ride list rows carry {@code groupSummaries} and the ride's route metrics ({@code distance},
 * {@code elevationGain}, {@code surfaceType}), so a card draws its per-group fill without opening
 * the ride — and the detail carries the very same figures.
 */
@QuarkusTest
class RideListGroupSummariesTest extends AbstractResourceTest {

  private Instant soon;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    soon = Instant.now().plus(7, ChronoUnit.DAYS);
  }

  private ValidatableResponse listRides() {
    return given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("type", "RIDE")
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200);
  }

  @Test
  void listRow_shouldCarryEveryGroupWithItsFill_inSortOrder() {
    Ride ride = dataService.createRide(team1, user1, "Groups", "groups", soon);
    RideGroup full = dataService.createRideGroupWithMaxParticipants(user1, ride, "A", 2);
    dataService.createParticipation(full, user1);
    dataService.createParticipation(full, user2);
    RideGroup open = dataService.createRideGroup(user1, ride, "B", 1);
    dataService.createParticipation(open, user3);
    // A leader is designated: the summary still carries none (the detail's groups do).
    dataService.setRideGroupLeader(open, user2);

    listRides()
        .body("publications[0].groups", empty())
        .body("publications[0].groupSummaries", hasSize(2))
        .body("publications[0].groupSummaries.name", contains("A", "B"))
        .body("publications[0].groupSummaries[0].countParticipants", equalTo(2))
        .body("publications[0].groupSummaries[0].maxParticipants", equalTo(2))
        .body("publications[0].groupSummaries[0].full", equalTo(true))
        .body("publications[0].groupSummaries[1].countParticipants", equalTo(1))
        .body("publications[0].groupSummaries[1].maxParticipants", nullValue())
        .body("publications[0].groupSummaries[1].full", equalTo(false))
        .body("publications[0].groupSummaries[1].leader", nullValue())
        .body("publications[0].groupSummaries[0].id", equalTo(tsid(full)))
        .body("publications[0].distance", nullValue())
        .body("publications[0].surfaceType", nullValue());
  }

  @Test
  void listRow_withoutGroups_shouldCarryAnEmptySummaryList() {
    dataService.createRide(team1, user1, "No group", "no-group", soon);
    listRides().body("publications[0].groupSummaries", empty());
  }

  @Test
  void listRow_routeMetrics_shouldComeFromTheRideRoute() {
    Route route = dataService.createRoute(team1, user1, "Ride route");
    dataService.setRouteMetrics(route, 72000f, 980f);
    Ride ride = dataService.createRide(team1, user1, "Routed", "routed", soon);
    dataService.setRideRoute(ride, route);

    listRides()
        .body("publications[0].distance", equalTo(72000f))
        .body("publications[0].elevationGain", equalTo(980f))
        .body("publications[0].surfaceType", equalTo("ROAD"));
  }

  /** A ride without its own route shows the first routed group's, in sort order. */
  @Test
  void listRow_routeMetrics_shouldFallBackOnTheFirstRoutedGroup() {
    Route first = dataService.createRoute(team1, user1, "First");
    dataService.setRouteMetrics(first, 58000f, 320f);
    Route second = dataService.createRoute(team1, user1, "Second");
    dataService.setRouteMetrics(second, 90000f, 1500f);

    Ride ride = dataService.createRide(team1, user1, "Group routed", "group-routed", soon);
    dataService.createRideGroup(user1, ride, "Unrouted", 0);
    RideGroup a = dataService.createRideGroup(user1, ride, "A", 1);
    dataService.setRideGroupRoute(a, first);
    RideGroup b = dataService.createRideGroup(user1, ride, "B", 2);
    dataService.setRideGroupRoute(b, second);

    listRides()
        .body("publications[0].distance", equalTo(58000f))
        .body("publications[0].elevationGain", equalTo(320f))
        .body("publications[0].groupSummaries[0].routeSlug", nullValue())
        .body("publications[0].groupSummaries[1].routeSlug", not(nullValue()))
        .body("publications[0].groupSummaries[1].distance", equalTo(58000f))
        .body("publications[0].groupSummaries[2].distance", equalTo(90000f));
  }

  /** The detail and the list row agree on every figure of the summaries. */
  @Test
  void detail_shouldCarryTheSameSummaries() {
    Route route = dataService.createRoute(team1, user1, "Group route");
    dataService.setRouteMetrics(route, 34000f, 610f);
    Ride ride = dataService.createRide(team1, user1, "Detail", "detail", soon);
    RideGroup group = dataService.createRideGroupWithMaxParticipants(user1, ride, "Only", 20);
    // Route first: merging the detached group after a registration would orphan it.
    dataService.setRideGroupRoute(group, route);
    dataService.createParticipation(group, user3);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/detail")
        .then()
        .statusCode(200)
        .body("groupSummaries", hasSize(1))
        .body("groupSummaries[0].countParticipants", equalTo(1))
        .body("groupSummaries[0].maxParticipants", equalTo(20))
        .body("groupSummaries[0].full", equalTo(false))
        .body("distance", equalTo(34000f))
        .body("elevationGain", equalTo(610f));
  }

  private static String tsid(RideGroup group) {
    return fr.pedalons.common.TsidUtils.toString(group.getId());
  }
}
