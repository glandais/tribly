package fr.pedalons.api.ads;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AdType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code GET /api/teams/{teamSlug}/classifieds/count}.
 *
 * <p>Exists so a filter sheet can announce a result count without fetching a page of ads to read its
 * {@code total} (until now the workaround was {@code …/classifieds?size=1&view=compact}). As with the
 * route count endpoint, the property that matters is not the figure on its own but that it always
 * equals the {@code total} of the listing it announces.
 *
 * <p>Unlike routes, classifieds carry no anonymous or public-team access: {@link AdResource} requires
 * {@code @RolesAllowed("user")} and {@code AdAccessChecker} denies {@code LIST} to anyone without a
 * team role, regardless of the team's own visibility.
 */
@QuarkusTest
class AdCountResourceTest extends AbstractResourceTest {

  private String teamList() {
    return "/api/teams/" + team1Slug + "/classifieds";
  }

  private String teamCount() {
    return "/api/teams/" + team1Slug + "/classifieds/count";
  }

  private Ad seedAd(
      Team team, User creator, String name, AdType adType, @Nullable BigDecimal price) {
    Ad ad = dataService.createAd(team, creator, name, adType);
    if (price != null) {
      dataService.setAdDetails(ad, price, null, null, null);
    }
    return ad;
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    seedAd(team1, user1, "Road Bike For Sale", AdType.SALE, BigDecimal.valueOf(200));
    seedAd(team1, user1, "Gravel Bike For Sale", AdType.SALE, BigDecimal.valueOf(800));
    seedAd(team1, user1, "Bike Wanted", AdType.WANTED, null);
    seedAd(team2, user1, "Private Team Bike", AdType.SALE, BigDecimal.valueOf(300));
  }

  /** The {@code total} of either a listing or a count — the two are meant to be interchangeable. */
  private int total(String url, RequestSpecification request) {
    return request.when().get(url).then().statusCode(200).extract().path("total");
  }

  @Test
  void countTeamAds_asMember_shouldMatchTheListTotal() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(3));

    assertEquals(
        total(teamList(), given().auth().oauth2(getAccessToken(USER1))),
        total(teamCount(), given().auth().oauth2(getAccessToken(USER1))));
  }

  @Test
  void countTeamAds_shouldHonourTheSameFiltersAsTheList() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("adType", "SALE")
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(2));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("adType", "SALE")
        .queryParam("minPrice", 500)
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(1));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("search", "Gravel")
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }

  @Test
  void countTeamAds_shouldIgnoreUnrelatedListParams() {
    // page/size/sort are not part of the count contract; passing them changes nothing.
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("page", 5)
        .queryParam("size", 1)
        .queryParam("sortBy", "PRICE")
        .when()
        .get(teamCount())
        .then()
        .statusCode(200)
        .body("total", equalTo(3));
  }

  @Test
  void countTeamAds_anonymous_shouldReturn401() {
    given().when().get(teamCount()).then().statusCode(401);
  }

  @Test
  void countTeamAds_asNonMember_shouldReturn403() {
    given().auth().oauth2(getAccessToken(USER4)).when().get(teamCount()).then().statusCode(403);
  }

  @Test
  void countTeamAds_onPrivateTeam_asMember_shouldSeeThem() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team2Slug + "/classifieds/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }

  @Test
  void countTeamAds_onNonexistentTeam_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/nonexistent-team/classifieds/count")
        .then()
        .statusCode(404);
  }
}
