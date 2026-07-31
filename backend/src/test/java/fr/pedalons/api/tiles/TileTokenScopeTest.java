package fr.pedalons.api.tiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.route.Route;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.TileTokenService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The guard rail for the one property that makes the tile token safe: it grants tiles and nothing
 * else.
 *
 * <p>{@code TileTokenFilter} is bound, by JAX-RS name binding, to the two {@code .mvt} methods
 * only, so {@code PedalonsQueryContext} can never see tile claims anywhere else. That is a
 * structural guarantee, not a behavioural one — which is exactly why it needs a test: a future
 * refactoring that moved the filter to a global {@code @Provider}, or dropped the name binding,
 * would silently turn this token into general-purpose authentication and break nothing visible.
 *
 * <p>Every case below asserts the token buys <em>strictly</em> what an anonymous visitor gets.
 */
@QuarkusTest
class TileTokenScopeTest extends AbstractResourceTest {

  @Inject TileTokenService tileTokenService;

  private Route teamRoute;
  private String tileToken;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    // user3 is a member of team1, so a route only they can see is the perfect canary.
    teamRoute = dataService.createRoute(team1, user1, "Team Route", Visibility.TEAM);
    tileToken = tileTokenService.issue(user3.getId(), domain.getId(), Instant.now()).value();
  }

  @Test
  void tileToken_onRouteList_shouldNotIdentifyTheCaller() {
    given()
        .queryParam("t", tileToken)
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes.slug", not(Matchers.hasItem(teamRoute.getSlug())));
  }

  @Test
  void tileToken_onRouteDetail_shouldNotUnlockATeamRoute() {
    given()
        .queryParam("t", tileToken)
        .when()
        .get("/api/teams/" + team1Slug + "/routes/" + teamRoute.getSlug())
        .then()
        .statusCode(404);
  }

  @Test
  void tileToken_onMinRoleFilter_shouldYieldNothing() {
    given()
        .queryParam("t", tileToken)
        .queryParam("minRole", "MEMBER")
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("routes", hasSize(0));
  }

  @Test
  void tileToken_onCalendarEvents_shouldStayUnauthorized() {
    given().queryParam("t", tileToken).when().get("/api/calendar/events").then().statusCode(401);
  }

  @Test
  void tileToken_onMintingAnotherToken_shouldStayUnauthorized() {
    given().queryParam("t", tileToken).when().post("/api/tiles/token").then().statusCode(401);
  }
}
