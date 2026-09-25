package fr.pedalons.api.admin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThan;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.admin.ThumbnailRegenerationRequest;
import fr.pedalons.enums.AssetType;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminThumbnailResourceTest extends AbstractResourceTest {

  private User platformAdmin;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    platformAdmin = dataService.createPlatformAdminUser("admin@example.com", "Platform Admin");
  }

  private String platformAdminToken() {
    return jwtService.generateAccessToken(platformAdmin);
  }

  @Test
  void regenerate_asNonAdmin_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(new ThumbnailRegenerationRequest(null, null, null, true, true, null))
        .when()
        .post("/api/admin/thumbnails/regenerate")
        .then()
        .statusCode(403);
  }

  @Test
  void regenerate_withoutCriterion_shouldReturn400() {
    given()
        .auth()
        .oauth2(platformAdminToken())
        .contentType("application/json")
        .body(new ThumbnailRegenerationRequest(null, null, null, false, true, null))
        .when()
        .post("/api/admin/thumbnails/regenerate")
        .then()
        .statusCode(400);
  }

  @Test
  void regenerate_dryRun_listsARouteWithoutThumbnailAndDrawsNothing() {
    Route route = dataService.createRoute(team1, user1, "No Thumbnail Route");
    String routeId = TsidUtils.toString(route.getId());

    given()
        .auth()
        .oauth2(platformAdminToken())
        .contentType("application/json")
        .body(new ThumbnailRegenerationRequest(null, null, null, true, true, null))
        .when()
        .post("/api/admin/thumbnails/regenerate")
        .then()
        .statusCode(200)
        .body("dryRun", equalTo(true))
        .body("entities.id", hasItem(routeId))
        .body("entities.find { it.id == '" + routeId + "' }.kind", equalTo("ROUTE"))
        .body("entities.find { it.id == '" + routeId + "' }.reasons", hasItem("MISSING"))
        .body("entities.find { it.id == '" + routeId + "' }.outcome", equalTo("PENDING"))
        .body("entities.find { it.id == '" + routeId + "' }.before", empty());
  }

  @Test
  void regenerate_withSizeThreshold_selectsOnlySmallThumbnails() {
    Route route = dataService.createRoute(team1, user1, "Tiny Thumbnail Route");
    // attachAsset stores a few dozen bytes of dummy content, far below any real map
    dataService.attachAsset(route, user1, AssetType.ROUTE_THUMBNAIL_LIGHT, "thumbnail-light.png");
    String routeId = TsidUtils.toString(route.getId());

    given()
        .auth()
        .oauth2(platformAdminToken())
        .contentType("application/json")
        .body(new ThumbnailRegenerationRequest(null, null, 10_000, false, true, null))
        .when()
        .post("/api/admin/thumbnails/regenerate")
        .then()
        .statusCode(200)
        .body("entities.id", hasItem(routeId))
        .body("entities.find { it.id == '" + routeId + "' }.reasons", hasItem("SMALL_FILE"))
        .body("entities.find { it.id == '" + routeId + "' }.before[0].bytes", lessThan(10_000));
  }
}
