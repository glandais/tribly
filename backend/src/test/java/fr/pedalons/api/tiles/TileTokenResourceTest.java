package fr.pedalons.api.tiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import fr.pedalons.api.AbstractResourceTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TileTokenResourceTest extends AbstractResourceTest {

  /**
   * {@link AbstractResourceTest#setUp()} carries no {@code @BeforeEach} of its own — every subclass
   * has to declare it, or the fixture never runs and the test users stay null.
   */
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void mintTileToken_withoutAuth_shouldReturnUnauthorized() {
    given().when().post("/api/tiles/token").then().statusCode(401);
  }

  @Test
  void mintTileToken_asUser_shouldReturnToken() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .post("/api/tiles/token")
        .then()
        .statusCode(200)
        .body("token", notNullValue())
        .body("token", not(emptyString()))
        .body("expiresAt", notNullValue())
        .body("expiresIn", equalTo(900));
  }

  /** A minted credential must never be kept by any cache, shared or private. */
  @Test
  void mintTileToken_shouldNotBeStored() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .post("/api/tiles/token")
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("no-store"));
  }
}
