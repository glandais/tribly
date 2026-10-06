package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The filters and the order the team dashboard needed on {@code GET
 * /api/teams/{teamSlug}/publications}: {@code sortDir}, {@code withoutRoute}, {@code
 * withFullGroup} — and the count that must agree with each.
 */
@QuarkusTest
class TeamPublicationFiltersResourceTest extends AbstractResourceTest {

  private Instant soon;

  private String list() {
    return "/api/teams/" + team1Slug + "/publications";
  }

  private String count() {
    return "/api/teams/" + team1Slug + "/publications/count";
  }

  private RequestSpecification asMember() {
    return given().auth().oauth2(getAccessToken(USER3));
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    soon = Instant.now().plus(7, ChronoUnit.DAYS);
  }

  @Test
  void sortDir_asc_shouldListSoonestFirst_andDescOrOmittedNewestFirst() {
    dataService.createRide(team1, user1, "Later", "later", soon.plus(2, ChronoUnit.DAYS));
    dataService.createRide(team1, user1, "Sooner", "sooner", soon);
    dataService.createRide(team1, user1, "Middle", "middle", soon.plus(1, ChronoUnit.DAYS));

    asMember()
        .queryParam("type", "RIDE")
        .queryParam("sortDir", "ASC")
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body("publications.slug", contains("sooner", "middle", "later"));

    asMember()
        .queryParam("type", "RIDE")
        .queryParam("sortDir", "DESC")
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body("publications.slug", contains("later", "middle", "sooner"));

    asMember()
        .queryParam("type", "RIDE")
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body("publications.slug", contains("later", "middle", "sooner"));
  }

  /** The ascending page keeps the nearest rides, not the furthest. */
  @Test
  void sortDir_asc_withASmallPage_shouldKeepTheNearest() {
    for (int i = 0; i < 4; i++) {
      dataService.createRide(team1, user1, "Ride " + i, "ride-" + i, soon.plusSeconds(i));
    }
    asMember()
        .queryParam("type", "RIDE")
        .queryParam("sortDir", "ASC")
        .queryParam("size", 2)
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body("publications.slug", contains("ride-0", "ride-1"))
        .body("total", equalTo(4));
  }

  @Test
  void withoutRoute_shouldKeepOnlyRidesRoutedNowhere() {
    dataService.createRide(team1, user1, "Bare", "bare", soon);
    Ride bareWithGroup = dataService.createRide(team1, user1, "Bare group", "bare-group", soon);
    dataService.createRideGroup(user1, bareWithGroup, "G", 0);

    Route route = dataService.createRoute(team1, user1, "A route");
    Ride routed = dataService.createRide(team1, user1, "Routed", "routed", soon);
    dataService.setRideRoute(routed, route);

    Ride groupRouted = dataService.createRide(team1, user1, "Group routed", "group-routed", soon);
    dataService.createRideGroup(user1, groupRouted, "Unrouted", 0);
    RideGroup routedGroup = dataService.createRideGroup(user1, groupRouted, "Routed", 1);
    dataService.setRideGroupRoute(routedGroup, route);

    dataService.createPost(team1, user1, "A post", soon);

    asMember()
        .queryParam("withoutRoute", true)
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body("publications.slug", containsInAnyOrder("bare", "bare-group"))
        .body("total", equalTo(2));

    asMember()
        .queryParam("withoutRoute", true)
        .when()
        .get(count())
        .then()
        .statusCode(200)
        .body("total", equalTo(2));

    // Omitted, nothing is filtered.
    asMember()
        .queryParam("type", "RIDE")
        .when()
        .get(count())
        .then()
        .statusCode(200)
        .body("total", equalTo(4));
  }

  @Test
  void withFullGroup_shouldKeepOnlyRidesWithAGroupAtCapacity() {
    Ride oneFull = dataService.createRide(team1, user1, "One full", "one-full", soon);
    RideGroup full = dataService.createRideGroupWithMaxParticipants(user1, oneFull, "Full", 2);
    dataService.createParticipation(full, user1);
    dataService.createParticipation(full, user2);
    dataService.createRideGroup(user1, oneFull, "Open", 1);

    Ride notFull = dataService.createRide(team1, user1, "Not full", "not-full", soon);
    RideGroup roomy = dataService.createRideGroupWithMaxParticipants(user1, notFull, "Roomy", 3);
    dataService.createParticipation(roomy, user1);

    Ride uncapped = dataService.createRide(team1, user1, "Uncapped", "uncapped", soon);
    RideGroup open = dataService.createRideGroup(user1, uncapped, "Open", 0);
    dataService.createParticipation(open, user1);
    dataService.createParticipation(open, user2);

    asMember()
        .queryParam("withFullGroup", true)
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body("publications.slug", contains("one-full"))
        .body("total", equalTo(1));

    asMember()
        .queryParam("withFullGroup", true)
        .when()
        .get(count())
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }
}
