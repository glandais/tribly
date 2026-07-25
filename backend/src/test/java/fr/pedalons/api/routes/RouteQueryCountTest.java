package fr.pedalons.api.routes;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Database-cost budget for the route list endpoints.
 *
 * <p>Routes carry a GPX track with its point array, so a list row that touched the track would move
 * a lot more than a few extra entities. See {@link AbstractQueryCountTest}.
 */
@QuarkusTest
class RouteQueryCountTest extends AbstractQueryCountTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private void seedRoutes(int count) {
    for (int i = 0; i < count; i++) {
      dataService.createRoute(team1, user1, "Budget Route " + i, Visibility.PUBLIC);
    }
  }

  @Test
  void listTeamRoutes_costDoesNotScaleWithRowCount() {
    seedRoutes(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/routes", asUser1(), "/api/teams/" + team1Slug + "/routes");
  }

  @Test
  void listAllRoutes_costDoesNotScaleWithRowCount() {
    seedRoutes(LARGE_PAGE);
    assertFlatQueryCount("GET /api/routes", asUser1(), "/api/routes");
  }

  @Test
  void listAllRoutesAnonymous_costDoesNotScaleWithRowCount() {
    seedRoutes(LARGE_PAGE);
    assertFlatQueryCount("GET /api/routes anonymous", anonymous(), "/api/routes");
  }
}
