package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The {@code participating} and {@code status} filters on the two publication listings. */
@QuarkusTest
class PublicationParticipatingFilterTest extends AbstractResourceTest {

  private Instant base;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    base = Instant.now().plus(7, ChronoUnit.DAYS);
  }

  /** Returns the group the user was signed up in. */
  private RideGroup joinedRide(String name, String slug, User user) {
    Ride ride = dataService.createRide(team1, user1, name, slug, base);
    RideGroup group = dataService.createRideGroup(user1, ride, "Group");
    dataService.createParticipation(group, user);
    return group;
  }

  @Test
  void listAll_participating_shouldKeepOnlyWhatIJoined() {
    RideGroup joinedGroup = joinedRide("Joined", "joined-ride", user3);
    joinedRide("Theirs", "theirs-ride", user2);
    dataService.createRide(team1, user1, "Untouched", "untouched-ride", base);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("participating", true)
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("total", equalTo(1))
        .body("publications[0].name", equalTo("Joined"))
        .body("publications[0].registered", equalTo(true))
        .body(
            "publications[0].registeredGroupId", equalTo(TsidUtils.toString(joinedGroup.getId())));
  }

  @Test
  void listAll_participating_shouldMatchTripsToo() {
    Trip trip = dataService.createTrip(team1, user1, "Joined Trip", base);
    dataService.createTripParticipation(trip, user3);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("participating", true)
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("total", equalTo(1))
        .body("publications[0].type", equalTo("TRIP"))
        .body("publications[0].registered", equalTo(true));
  }

  /** Nobody participates in anything when nobody is logged in — empty list, not an error. */
  @Test
  void listAll_participating_anonymous_shouldReturnNothing() {
    joinedRide("Joined", "joined-ride", user3);

    given()
        .queryParam("participating", true)
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("total", equalTo(0))
        .body("publications", hasSize(0));
  }

  @Test
  void listAll_withoutParticipating_shouldStillReturnEverythingVisible() {
    joinedRide("Joined", "joined-ride", user3);
    dataService.createRide(team1, user1, "Untouched", "untouched-ride", base);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("total", equalTo(2))
        .body("publications.find { it.name == 'Joined' }.registered", equalTo(true))
        .body("publications.find { it.name == 'Untouched' }.registered", equalTo(false));
  }

  @Test
  void listTeam_participating_shouldKeepOnlyWhatIJoined() {
    joinedRide("Joined", "joined-ride", user3);
    dataService.createRide(team1, user1, "Untouched", "untouched-ride", base);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("participating", true)
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("total", equalTo(1))
        .body("publications[0].name", equalTo("Joined"));
  }

  /**
   * A status filter narrows the visibility rules, it never unlocks them: a plain member asking for
   * DRAFT still sees nothing, while the admin who owns the draft does.
   */
  @Test
  void listAll_statusFilter_shouldNotWidenVisibility() {
    dataService.createRide(
        team1, user1, "Secret Draft", "secret-draft", base, Visibility.PUBLIC, Status.DRAFT);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("status", "DRAFT")
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("total", equalTo(0));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("status", "DRAFT")
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Secret Draft' }", notNullValue());
  }

  @Test
  void listAll_statusFilter_shouldExcludeOtherStatuses() {
    dataService.createRide(
        team1, user1, "Draft", "draft-ride", base, Visibility.PUBLIC, Status.DRAFT);
    dataService.createRide(team1, user1, "Published", "published-ride", base);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("status", "PUBLISHED")
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.name == 'Draft' }", nullValue())
        .body("publications.find { it.name == 'Published' }", notNullValue());
  }

  @Test
  void listAll_shouldNotBeCacheableAcrossUsers() {
    given()
        .when()
        .get("/api/publications")
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("private"));
  }
}
