package fr.pedalons.api.config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The cartography settings {@code GET /api/config} now serves, so the clients stop hard-coding them.
 *
 * <p>{@code /api/config} is {@code @PermitAll}: an anonymous visitor must get the map settings too,
 * or the landing page cannot draw a map before login.
 */
@QuarkusTest
class ConfigMapSettingsTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void getConfig_shouldServeTheBasemapsInSwitcherOrder() {
    given()
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("mapStyles", not(empty()))
        .body("mapStyles[0].id", not(emptyOrNullString()))
        .body("mapStyles[0].label", not(emptyOrNullString()))
        .body("mapStyles[0].url", startsWith("http"))
        // The first style is the light default and declares its dark counterpart, which is the
        // whole point of the field: the app switches theme without a second round-trip.
        .body("mapStyles[0].darkVariant", startsWith("http"));
  }

  @Test
  void getConfig_shouldServeTheTileHostAndADefaultCamera() {
    given()
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("tileServerBaseUrl", startsWith("http"))
        .body("defaultCenter.lat", allOf(greaterThan(-90f), lessThan(90f)))
        .body("defaultCenter.lon", allOf(greaterThan(-180f), lessThan(180f)))
        .body("defaultCenter.zoom", greaterThan(0f));
  }

  @Test
  void getConfig_shouldNotLeakTheInternalRenderer() {
    // tileserver.url is the private renderer the thumbnail service talks to (localhost:18080 in
    // test). Handing it to a client would be useless at best and an SSRF hint at worst.
    given()
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("tileServerBaseUrl", not(containsString("localhost")));
  }

  @Test
  void getConfig_anonymous_shouldStillCarryTheMapSettings() {
    given()
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("mapStyles", not(empty()))
        .body("defaultCenter", is(notNullValue()));
  }

  @Test
  void getConfig_withNoFloorConfigured_shouldOmitMinSupportedAppVersion() {
    // %test sets no floor, and Jackson is NON_NULL: the field must be absent rather than "".
    given()
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("$", not(hasKey("minSupportedAppVersion")));
  }

  @Test
  void getConfig_onAMultiTeamDomain_shouldFallBackToTheConfiguredCentre() {
    // The standard fixture's domain is not pinned and not single-team, so no team dictates the
    // camera: it must be the deployment default (46.6 / 2.3), not a team's geometry.
    given()
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("defaultCenter.lat", equalTo(46.6f))
        .body("defaultCenter.lon", equalTo(2.3f));
  }
}
