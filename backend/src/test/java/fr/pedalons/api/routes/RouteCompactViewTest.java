package fr.pedalons.api.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code ?view=compact}, {@code excerpt} and {@code thumbnailUrl} on the route listings.
 *
 * <p>A route row was the worst of the four: it carried the full description <em>and</em> the whole
 * asset inventory (the GPX, the FIT, both thumbnails) for a card that shows a name, a distance and a
 * map preview.
 */
@QuarkusTest
class RouteCompactViewTest extends AbstractResourceTest {

  private static final String ALL = "/api/routes";

  private static final String BODY =
      "Boucle **facile** au depart de la [gare](https://example.com/gare), 45 km de plat.";

  private String teamList() {
    return "/api/teams/" + team1Slug + "/routes";
  }

  private static String row() {
    return "routes.find { it.name == 'Boucle du lac' }";
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Route route = dataService.createRoute(team1, user1, "Boucle du lac");
    dataService.setMarkdown(route, BODY);
    // The metrics a card shows: they must survive ?view=compact, which only drops the description
    // and the asset inventory. createRoute leaves them null, so set them explicitly.
    dataService.setRouteMetrics(route, 45000f, 320f);
    dataService.attachAsset(route, user1, AssetType.ROUTE_THUMBNAIL_LIGHT, "thumb-light.png");
    dataService.attachAsset(route, user1, AssetType.ROUTE_FILTERED_GPX, "trace.gpx");
  }

  @Test
  void listWithoutView_shouldStillCarryTheWholeDescriptionAndTheAssets() {
    given()
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row() + ".media.markdown", equalTo(BODY))
        .body(row() + ".media.assets.thumbnailLight", notNullValue())
        .body(row() + ".media.assets.gpx", notNullValue());
  }

  @Test
  void listWithoutView_shouldAlsoCarryTheNewSummaryFields() {
    given()
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row() + ".excerpt", not(emptyOrNullString()))
        .body(row() + ".thumbnailUrl", not(emptyOrNullString()));
  }

  @Test
  void listCompact_shouldDropTheDescriptionAndTheAssets() {
    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row() + ".media.markdown", equalTo(""))
        .body(row() + ".media.assets.gpx", nullValue())
        .body(row() + ".media.assets.thumbnailLight", notNullValue())
        .body(row() + ".excerpt", not(emptyOrNullString()))
        .body(row() + ".thumbnailUrl", not(emptyOrNullString()))
        .body(row() + ".distance", notNullValue());
  }

  @Test
  void teamListing_shouldAcceptViewToo() {
    given()
        .queryParam("view", "COMPACT")
        .when()
        .get(teamList())
        .then()
        .statusCode(200)
        .body(row() + ".media.markdown", equalTo(""))
        .body(row() + ".excerpt", not(emptyOrNullString()));
  }

  @Test
  void listCompact_anonymous_shouldNotSeeAPrivateTeamsRoutes() {
    Route secret = dataService.createRoute(team2, user1, "Trace privee", Visibility.TEAM);
    dataService.setMarkdown(secret, BODY);

    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("routes.name", not(hasItem("Trace privee")))
        .body("routes.name", hasItem("Boucle du lac"));
  }

  @Test
  void listCompact_shouldNotReachIntoAnotherDomain() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL1, "Other User 1");
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Foreign Team", "foreign-team", Visibility.PUBLIC);
    dataService.createRoute(otherTeam, otherUser, "Trace etrangere");

    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("routes.name", not(hasItem("Trace etrangere")))
        .body("total", equalTo(1));
  }
}
