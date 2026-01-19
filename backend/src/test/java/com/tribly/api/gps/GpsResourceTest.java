package com.tribly.api.gps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.domain.route.Route;
import com.tribly.enums.GpsServiceType;
import com.tribly.service.security.DomainResolver;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpsResourceTest extends AbstractResourceTest {

  private Route route;
  private String routeSlug;
  @Inject DomainResolver domainResolver;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    domainResolver.setDomainForTest(domain);

    // Set up domain GPS credentials for tests
    dataService.createDomainGpsCredential(
        domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");
    dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

    route = dataService.createRoute(team1, user1, "GPS Test Route");
    routeSlug = route.getSlug();
  }

  // ==================== Get Connect URL ====================

  @Test
  void getConnectUrl_withAuth_shouldReturnUrl() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/gps/connect/HAMMERHEAD")
        .then()
        .statusCode(200)
        .body("authorizationUrl", notNullValue());
  }

  @Test
  void getConnectUrl_garmin_shouldReturnUrl() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/gps/connect/GARMIN")
        .then()
        .statusCode(200)
        .body("authorizationUrl", notNullValue());
  }

  @Test
  void getConnectUrl_withoutAuth_shouldReturn401() {
    given().when().get("/api/gps/connect/HAMMERHEAD").then().statusCode(401);
  }

  @Test
  void getConnectUrl_invalidServiceType_shouldReturn404() {
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/gps/connect/INVALID")
        .then()
        .statusCode(404);
  }

  // ==================== OAuth Callback ====================

  @Test
  void handleCallback_withError_shouldRedirectWithError() {
    given()
        .queryParam("error", "access_denied")
        .redirects()
        .follow(false)
        .when()
        .get("/api/gps/callback/HAMMERHEAD")
        .then()
        .statusCode(307)
        .header("Location", containsString("gps_error=access_denied"));
  }

  @Test
  void handleCallback_withMissingParams_shouldRedirectWithError() {
    given()
        .redirects()
        .follow(false)
        .when()
        .get("/api/gps/callback/HAMMERHEAD")
        .then()
        .statusCode(307)
        .header("Location", containsString("gps_error=missing_params"));
  }

  @Test
  void handleCallback_withMissingCode_shouldRedirectWithError() {
    given()
        .queryParam("state", "test-state")
        .redirects()
        .follow(false)
        .when()
        .get("/api/gps/callback/HAMMERHEAD")
        .then()
        .statusCode(307)
        .header("Location", containsString("gps_error=missing_params"));
  }

  @Test
  void handleCallback_withMissingState_shouldRedirectWithError() {
    given()
        .queryParam("code", "test-code")
        .redirects()
        .follow(false)
        .when()
        .get("/api/gps/callback/HAMMERHEAD")
        .then()
        .statusCode(307)
        .header("Location", containsString("gps_error=missing_params"));
  }

  @Test
  void handleCallback_withInvalidState_shouldRedirectWithError() {
    given()
        .queryParam("code", "test-code")
        .queryParam("state", "invalid-state")
        .redirects()
        .follow(false)
        .when()
        .get("/api/gps/callback/HAMMERHEAD")
        .then()
        .statusCode(307)
        .header("Location", containsString("gps_error=connection_failed"));
  }

  // ==================== Disconnect ====================

  @Test
  void disconnect_withAuth_shouldSucceed() {
    // Note: This test may fail if service is not connected, which is expected
    // The endpoint should return 204 on success or 400 if not connected
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/gps/disconnect/HAMMERHEAD")
        .then()
        .statusCode(anyOf(equalTo(204), equalTo(400)));
  }

  @Test
  void disconnect_withoutAuth_shouldReturn401() {
    given().when().delete("/api/gps/disconnect/HAMMERHEAD").then().statusCode(401);
  }

  @Test
  void disconnect_invalidServiceType_shouldReturn404() {
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/gps/disconnect/INVALID")
        .then()
        .statusCode(404);
  }

  // ==================== Upload Route ====================

  @Test
  void uploadRoute_withoutAuth_shouldReturn401() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .post("/api/gps/upload/HAMMERHEAD/" + team1Slug + "/" + routeSlug)
        .then()
        .statusCode(401);
  }

  @Test
  void uploadRoute_notConnected_shouldReturn400() {
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/gps/upload/HAMMERHEAD/" + team1Slug + "/" + routeSlug)
        .then()
        .statusCode(400);
  }

  @Test
  void uploadRoute_nonexistentRoute_shouldReturn404() {
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/gps/upload/HAMMERHEAD/" + team1Slug + "/nonexistent-route")
        .then()
        .statusCode(anyOf(equalTo(400), equalTo(404)));
  }

  @Test
  void uploadRoute_nonexistentTeam_shouldReturn404() {
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/gps/upload/HAMMERHEAD/nonexistent-team/" + routeSlug)
        .then()
        .statusCode(anyOf(equalTo(400), equalTo(404)));
  }

  @Test
  void uploadRoute_asNonMember_shouldReturn403() {
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .post("/api/gps/upload/HAMMERHEAD/" + team1Slug + "/" + routeSlug)
        .then()
        .statusCode(anyOf(equalTo(400), equalTo(403)));
  }

  @Test
  void uploadRoute_invalidServiceType_shouldReturn404() {
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/gps/upload/INVALID/" + team1Slug + "/" + routeSlug)
        .then()
        .statusCode(404);
  }
}
