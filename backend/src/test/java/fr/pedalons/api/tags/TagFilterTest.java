package fr.pedalons.api.tags;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.tag.Tag;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.TagTarget;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The {@code ?tags=} filter of a team's dedicated lists (docs/LEDGER_*.md API-59): OR between the
 * tags (D6), each content once, by tag id with an unknown one ignored (D18), and absent from the
 * mixed feed (D13) and the cross-team lists (D7). The count agrees with the list.
 */
@QuarkusTest
class TagFilterTest extends AbstractResourceTest {

  private Tag gravel;
  private Tag col;
  private Tag unused;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    gravel = dataService.createTag(team1, user1, TagTarget.ROUTE, "Gravel");
    col = dataService.createTag(team1, user1, TagTarget.ROUTE, "Col");
    unused = dataService.createTag(team1, user1, TagTarget.ROUTE, "Unused");
    Route both = dataService.createRoute(team1, user1, "Both");
    Route gravelOnly = dataService.createRoute(team1, user1, "Gravel only");
    Route colOnly = dataService.createRoute(team1, user1, "Col only");
    dataService.createRoute(team1, user1, "Untagged");
    dataService.tagContent(both, gravel, col);
    dataService.tagContent(gravelOnly, gravel);
    dataService.tagContent(colOnly, col);
  }

  private static String id(Tag tag) {
    return TsidUtils.toString(tag.getId());
  }

  private String routes() {
    return "/api/teams/" + team1Slug + "/routes";
  }

  // ─── Routes ───────────────────────────────────────────────────────────────

  @Test
  void routes_twoTags_areAnOr_withEachRouteOnce_andTheCountAgrees() {
    String tags = id(gravel) + "," + id(col);

    given()
        .queryParam("tags", tags)
        .when()
        .get(routes())
        .then()
        .statusCode(200)
        .body("routes.name", containsInAnyOrder("Both", "Gravel only", "Col only"))
        .body("total", equalTo(3));
    given()
        .queryParam("tags", tags)
        .when()
        .get(routes() + "/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(3));
  }

  @Test
  void routes_repeatedParameter_isTheSameAsCommaSeparated() {
    given()
        .queryParam("tags", id(gravel))
        .queryParam("tags", id(unused))
        .when()
        .get(routes())
        .then()
        .statusCode(200)
        .body("routes.name", containsInAnyOrder("Both", "Gravel only"));
  }

  @Test
  void routes_listItemsCarryTheirTags_byLabel() {
    given()
        .queryParam("tags", id(gravel))
        .when()
        .get(routes())
        .then()
        .statusCode(200)
        .body("routes.find { it.name == 'Both' }.tags.label", containsInAnyOrder("Col", "Gravel"))
        .body("routes.find { it.name == 'Both' }.tags[0].label", equalTo("Col"));
  }

  @Test
  void routes_anUnknownId_isIgnored_alongsideAKnownOne() {
    String unknown = TsidUtils.toString(unused.getId() + 1000);

    given()
        .queryParam("tags", unknown + "," + id(col) + ",garbage")
        .when()
        .get(routes())
        .then()
        .statusCode(200)
        .body("routes.name", containsInAnyOrder("Both", "Col only"));
  }

  @Test
  void routes_onlyUnknownIds_meanNoFilter_notAnEmptyList() {
    Tag otherKind = dataService.createTag(team1, user1, TagTarget.RIDE, "Gravel");
    Tag otherTeam = dataService.createTag(team2, user1, TagTarget.ROUTE, "Gravel");

    given()
        .queryParam("tags", id(otherKind) + "," + id(otherTeam) + ",garbage")
        .when()
        .get(routes())
        .then()
        .statusCode(200)
        .body("total", equalTo(4));
  }

  @Test
  void routes_aTagNobodyCarries_isAnEmptyList() {
    given()
        .queryParam("tags", id(unused))
        .when()
        .get(routes())
        .then()
        .statusCode(200)
        .body("routes", hasSize(0))
        .body("total", equalTo(0));
  }

  @Test
  void routes_acrossTeams_ignoreTheFilter() {
    given()
        .queryParam("tags", id(gravel))
        .when()
        .get("/api/routes")
        .then()
        .statusCode(200)
        .body("total", equalTo(4));
  }

  // ─── Publications: the dedicated lists filter, the mixed feed does not ────

  @Test
  void publications_ofOneType_filter_butTheMixedFeedIgnoresTags() {
    Tag recit = dataService.createTag(team1, user1, TagTarget.POST, "Récit");
    Instant soon = Instant.now().plus(7, ChronoUnit.DAYS);
    Post tagged = dataService.createPost(team1, user1, "Tagged post", soon);
    dataService.createPost(team1, user1, "Plain post", soon.plusSeconds(1));
    dataService.createRide(
        team1, user1, "A ride", "a-ride", Instant.now().plus(1, ChronoUnit.DAYS));
    dataService.tagContent(tagged, recit);
    String publications = "/api/teams/" + team1Slug + "/publications";

    given()
        .queryParam("type", "POST")
        .queryParam("tags", id(recit))
        .when()
        .get(publications)
        .then()
        .statusCode(200)
        .body("publications.name", containsInAnyOrder("Tagged post"))
        .body("publications[0].tags.label", containsInAnyOrder("Récit"));
    given()
        .queryParam("type", "POST")
        .queryParam("tags", id(recit))
        .when()
        .get(publications + "/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(1));

    given()
        .queryParam("tags", id(recit))
        .when()
        .get(publications)
        .then()
        .statusCode(200)
        .body("publications.name", containsInAnyOrder("Tagged post", "Plain post", "A ride"));
  }

  @Test
  void publications_aRouteTag_doesNotFilterPosts() {
    Instant soon = Instant.now().plus(7, ChronoUnit.DAYS);
    dataService.createPost(team1, user1, "Some post", soon);

    given()
        .queryParam("type", "POST")
        .queryParam("tags", id(gravel))
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.name", containsInAnyOrder("Some post"));
  }

  // ─── Classifieds ──────────────────────────────────────────────────────────

  @Test
  void classifieds_filterByTag_andTheCountAgrees() {
    Tag velo = dataService.createTag(team1, user1, TagTarget.AD, "Vélo");
    Ad tagged = dataService.createAd(team1, user1, "Cadre", AdType.SALE);
    dataService.createAd(team1, user1, "Casque", AdType.SALE);
    dataService.tagContent(tagged, velo);
    String classifieds = "/api/teams/" + team1Slug + "/classifieds";

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("tags", id(velo))
        .when()
        .get(classifieds)
        .then()
        .statusCode(200)
        .body("ads.name", containsInAnyOrder("Cadre"))
        .body("ads[0].tags.label", containsInAnyOrder("Vélo"));
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("tags", id(velo))
        .when()
        .get(classifieds + "/count")
        .then()
        .statusCode(200)
        .body("total", equalTo(1));
  }
}
