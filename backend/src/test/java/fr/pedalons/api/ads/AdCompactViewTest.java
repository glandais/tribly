package fr.pedalons.api.ads;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.AssetType;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** {@code ?view=compact}, {@code excerpt} and {@code thumbnailUrl} on the classifieds listing. */
@QuarkusTest
class AdCompactViewTest extends AbstractResourceTest {

  private static final String BODY =
      "Velo de route **taille 54**, revise, photos sur [le lien](https://example.com/velo).";

  private String list() {
    return "/api/teams/" + team1Slug + "/classifieds";
  }

  private static String row() {
    return "ads.find { it.name == 'Velo de route' }";
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Ad ad = dataService.createAd(team1, user1, "Velo de route", AdType.SALE);
    dataService.setMarkdown(ad, BODY);
    dataService.attachAsset(ad, user1, AssetType.IMAGE, "velo.jpg");
  }

  @Test
  void listWithoutView_shouldStillCarryTheWholeDescriptionAndTheAssets() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body(row() + ".media.markdown", equalTo(BODY))
        .body(row() + ".media.assets.images", hasSize(1))
        .body(row() + ".excerpt", not(emptyOrNullString()))
        .body(row() + ".thumbnailUrl", not(emptyOrNullString()));
  }

  @Test
  void listCompact_shouldDropTheDescriptionAndTheAssets() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("view", "compact")
        .when()
        .get(list())
        .then()
        .statusCode(200)
        .body(row() + ".media.markdown", equalTo(""))
        .body(row() + ".media.assets.images", hasSize(1))
        .body(row() + ".excerpt", not(emptyOrNullString()))
        .body(row() + ".thumbnailUrl", not(emptyOrNullString()));
  }

  @Test
  void listCompact_anonymous_shouldBeRefusedJustAsTheFullListIs() {
    int anonymous =
        given().queryParam("view", "compact").when().get(list()).then().extract().statusCode();
    int anonymousFull = given().when().get(list()).then().extract().statusCode();

    org.junit.jupiter.api.Assertions.assertEquals(
        anonymousFull, anonymous, "view must not change who may read the list");
  }
}
