package fr.pedalons.api.geocode;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.infrastructure.nominatim.NominatimLookup;
import fr.pedalons.infrastructure.nominatim.NominatimPlace;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The geocoding proxy, which exists so the Nominatim usage policy can be met at all.
 *
 * <p>The clients used to call {@code nominatim.openstreetmap.org} from the browser, where the
 * identifying {@code User-Agent} the policy demands is a forbidden header. The upstream client is
 * mocked here: these tests are about what we expose and to whom, not about OpenStreetMap's data —
 * and a test suite that queried the real shared instance would be exactly the abuse the policy
 * warns about.
 *
 * <p>{@link NominatimLookup} is what is mocked, not the REST client behind it: it is the seam
 * between our layer and OpenStreetMap's, and it carries the cache. What it does when the provider
 * misbehaves is its own test, {@code NominatimLookupTest}.
 */
@QuarkusTest
class GeocodeResourceTest extends AbstractResourceTest {

  @InjectMock NominatimLookup nominatimLookup;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void search_anonymous_shouldBeRefused() {
    // Public data, but not a public relay: an anonymous endpoint here would be a free Nominatim
    // proxy carrying our User-Agent, and the operators hold us responsible for what it sends.
    given().when().get("/api/geocode/search?q=chambery-anonymous").then().statusCode(401);
  }

  @Test
  void search_shouldReturnPlacesWithNumericCoordinates() {
    when(nominatimLookup.search(anyString(), anyString()))
        .thenReturn(
            List.of(new NominatimPlace(123L, "Chambéry, Savoie, France", "45.5646", "5.9178")));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/geocode/search?q=chambery-nominal")
        .then()
        .statusCode(200)
        .body("size()", is(1))
        .body("[0].displayName", equalTo("Chambéry, Savoie, France"))
        // Nominatim serves coordinates as strings; a client should not have to parse them.
        .body("[0].lat", equalTo(45.5646f))
        .body("[0].lon", equalTo(5.9178f));
  }

  @Test
  void search_belowThreeCharacters_shouldNotReachTheProvider() {
    // The clients already gate on three characters. Asking upstream anyway would spend a request
    // of a shared instance's budget on a query that matches half of France.
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/geocode/search?q=ch")
        .then()
        .statusCode(200)
        .body("size()", is(0));

    org.mockito.Mockito.verifyNoInteractions(nominatimLookup);
  }

  @Test
  void search_shouldDropAHitWithNoCoordinate() {
    when(nominatimLookup.search(anyString(), anyString()))
        .thenReturn(
            List.of(
                new NominatimPlace(1L, "Sans coordonnée", null, null),
                new NominatimPlace(2L, "Annecy, Haute-Savoie, France", "45.8992", "6.1294")));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/geocode/search?q=annecy-partiel")
        .then()
        .statusCode(200)
        .body("size()", is(1))
        .body("[0].displayName", equalTo("Annecy, Haute-Savoie, France"));
  }
}
