package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Read-query budget for the publication list endpoints. These assertions guard against N+1
 * regressions: the SELECT count for a list of rides must NOT grow linearly with the number of rows.
 *
 * <p>The bounds are intentionally generous — the goal on this first pass is the end-of-run {@link
 * fr.pedalons.util.QueryCountReport} table (visible in the CI log), not a hard gate. Once you read
 * the real numbers off CI, tighten {@code MAX_*} down to just above the observed value to lock in
 * the win. Run locally with: {@code ./mvnw test -Dtest=PublicationQueryCountTest}.
 */
@QuarkusTest
class PublicationQueryCountTest extends AbstractResourceTest {

  // Batch fetching (batch-size=32) should keep a list bounded regardless of row count.
  private static final long MAX_SELECTS = 60;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private void seedRides(int count) {
    Instant base = Instant.now().plus(7, ChronoUnit.DAYS);
    for (int i = 0; i < count; i++) {
      dataService.createRide(
          team1,
          user1,
          "Budget Ride " + i,
          "budget-ride-" + i,
          base.plusSeconds(i),
          Visibility.PUBLIC,
          Status.PUBLISHED);
    }
  }

  @Test
  void listTeamRides_readQueryBudget_doesNotScaleWithRowCount() {
    seedRides(20);

    long selects =
        queryStats.measure(
            "GET /api/teams/{slug}/publications?type=RIDE (20 rides)",
            () ->
                given()
                    .auth()
                    .oauth2(getAccessToken(USER1))
                    .when()
                    .get("/api/teams/" + team1Slug + "/publications?type=RIDE")
                    .then()
                    .statusCode(200));

    assertTrue(
        selects <= MAX_SELECTS,
        "SELECT count for a 20-ride list was "
            + selects
            + " (budget "
            + MAX_SELECTS
            + "). A count that scales with the number of rides indicates an N+1 regression.");
  }

  @Test
  void listAllPublications_readQueryBudget() {
    seedRides(20);

    long selects =
        queryStats.measure(
            "GET /api/publications (20 rides)",
            () ->
                given()
                    .auth()
                    .oauth2(getAccessToken(USER1))
                    .when()
                    .get("/api/publications")
                    .then()
                    .statusCode(200));

    assertTrue(
        selects <= MAX_SELECTS,
        "SELECT count for the cross-team publication feed was "
            + selects
            + " (budget "
            + MAX_SELECTS
            + ").");
  }
}
