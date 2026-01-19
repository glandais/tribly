package com.tribly.api.garmin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.dto.garmin.request.GarminCallbackRequest;
import com.tribly.dto.garmin.request.GarminTokenRequest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GarminOAuthResourceTest extends AbstractResourceTest {

  private static final String GARMIN_CLIENT_ID = "garmin-connect-iq";
  private static final String VALID_REDIRECT_URI = "connectiq://oauth";

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  // ==================== Authorize Endpoint ====================

  @Test
  void authorize_withValidParams_shouldRedirect() {
    given()
        .queryParam("client_id", GARMIN_CLIENT_ID)
        .queryParam("redirect_uri", VALID_REDIRECT_URI)
        .queryParam("response_type", "code")
        .queryParam("state", "test-state")
        .redirects()
        .follow(false)
        .when()
        .get("/api/garmin/oauth/authorize")
        .then()
        .statusCode(303)
        .header("Location", containsString("/garmin/login"))
        .header("Location", containsString("client_id=garmin-connect-iq"))
        .header("Location", containsString("state=test-state"));
  }

  @Test
  void authorize_withoutState_shouldRedirect() {
    given()
        .queryParam("client_id", GARMIN_CLIENT_ID)
        .queryParam("redirect_uri", VALID_REDIRECT_URI)
        .queryParam("response_type", "code")
        .redirects()
        .follow(false)
        .when()
        .get("/api/garmin/oauth/authorize")
        .then()
        .statusCode(303)
        .header("Location", containsString("/garmin/login"));
  }

  @Test
  void authorize_withInvalidClientId_shouldReturn400() {
    given()
        .queryParam("client_id", "invalid-client")
        .queryParam("redirect_uri", VALID_REDIRECT_URI)
        .queryParam("response_type", "code")
        .when()
        .get("/api/garmin/oauth/authorize")
        .then()
        .statusCode(400);
  }

  @Test
  void authorize_withInvalidRedirectUri_shouldReturn400() {
    given()
        .queryParam("client_id", GARMIN_CLIENT_ID)
        .queryParam("redirect_uri", "https://evil.com/callback")
        .queryParam("response_type", "code")
        .when()
        .get("/api/garmin/oauth/authorize")
        .then()
        .statusCode(400);
  }

  @Test
  void authorize_withMissingRedirectUri_shouldReturn400() {
    given()
        .queryParam("client_id", GARMIN_CLIENT_ID)
        .queryParam("response_type", "code")
        .when()
        .get("/api/garmin/oauth/authorize")
        .then()
        .statusCode(400);
  }

  // ==================== Complete Endpoint ====================

  @Test
  void complete_withValidToken_shouldReturnRedirectUrl() {
    String accessToken = getAccessToken(USER1);
    GarminCallbackRequest request =
        new GarminCallbackRequest(accessToken, VALID_REDIRECT_URI, "test-state");

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/complete")
        .then()
        .statusCode(200)
        .body("redirectUrl", startsWith(VALID_REDIRECT_URI))
        .body("redirectUrl", containsString("code="))
        .body("redirectUrl", containsString("state=test-state"));
  }

  @Test
  void complete_withoutState_shouldReturnRedirectUrlWithoutState() {
    String accessToken = getAccessToken(USER1);
    GarminCallbackRequest request =
        new GarminCallbackRequest(accessToken, VALID_REDIRECT_URI, null);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/complete")
        .then()
        .statusCode(200)
        .body("redirectUrl", startsWith(VALID_REDIRECT_URI))
        .body("redirectUrl", containsString("code="))
        .body("redirectUrl", not(containsString("state=")));
  }

  @Test
  void complete_withInvalidToken_shouldReturn403() {
    GarminCallbackRequest request =
        new GarminCallbackRequest("invalid-token", VALID_REDIRECT_URI, null);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/complete")
        .then()
        .statusCode(403);
  }

  @Test
  void complete_withMissingToken_shouldReturn400() {
    given()
        .contentType("application/json")
        .body("{\"redirectUri\":\"" + VALID_REDIRECT_URI + "\"}")
        .when()
        .post("/api/garmin/oauth/complete")
        .then()
        .statusCode(400);
  }

  // ==================== Token Endpoint ====================

  @Test
  void token_withAuthorizationCode_shouldReturnTokens() {
    // First get an auth code
    String accessToken = getAccessToken(USER1);
    GarminCallbackRequest callbackRequest =
        new GarminCallbackRequest(accessToken, VALID_REDIRECT_URI, null);

    String redirectUrl =
        given()
            .contentType("application/json")
            .body(callbackRequest)
            .when()
            .post("/api/garmin/oauth/complete")
            .then()
            .statusCode(200)
            .extract()
            .path("redirectUrl");

    // Extract code from redirect URL
    String code = extractCodeFromUrl(redirectUrl);

    // Exchange code for tokens
    GarminTokenRequest tokenRequest = new GarminTokenRequest("authorization_code", code, null);

    given()
        .contentType("application/json")
        .body(tokenRequest)
        .when()
        .post("/api/garmin/oauth/token")
        .then()
        .statusCode(200)
        .body("accessToken", notNullValue())
        .body("tokenType", equalTo("Bearer"))
        .body("expiresIn", greaterThan(0))
        .body("refreshToken", notNullValue());
  }

  @Test
  void token_withRefreshToken_shouldReturnNewTokens() {
    // First get tokens
    String accessToken = getAccessToken(USER1);
    GarminCallbackRequest callbackRequest =
        new GarminCallbackRequest(accessToken, VALID_REDIRECT_URI, null);

    String redirectUrl =
        given()
            .contentType("application/json")
            .body(callbackRequest)
            .when()
            .post("/api/garmin/oauth/complete")
            .then()
            .statusCode(200)
            .extract()
            .path("redirectUrl");

    String code = extractCodeFromUrl(redirectUrl);
    GarminTokenRequest tokenRequest = new GarminTokenRequest("authorization_code", code, null);

    String refreshToken =
        given()
            .contentType("application/json")
            .body(tokenRequest)
            .when()
            .post("/api/garmin/oauth/token")
            .then()
            .statusCode(200)
            .extract()
            .path("refreshToken");

    // Use refresh token to get new tokens
    GarminTokenRequest refreshRequest = new GarminTokenRequest("refresh_token", null, refreshToken);

    given()
        .contentType("application/json")
        .body(refreshRequest)
        .when()
        .post("/api/garmin/oauth/token")
        .then()
        .statusCode(200)
        .body("accessToken", notNullValue())
        .body("tokenType", equalTo("Bearer"))
        .body("expiresIn", greaterThan(0));
  }

  @Test
  void token_withInvalidGrantType_shouldReturn400() {
    GarminTokenRequest request = new GarminTokenRequest("invalid_grant", null, null);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/token")
        .then()
        .statusCode(400);
  }

  @Test
  void token_withAuthorizationCodeButMissingCode_shouldReturn400() {
    GarminTokenRequest request = new GarminTokenRequest("authorization_code", null, null);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/token")
        .then()
        .statusCode(400);
  }

  @Test
  void token_withRefreshTokenButMissingToken_shouldReturn400() {
    GarminTokenRequest request = new GarminTokenRequest("refresh_token", null, null);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/token")
        .then()
        .statusCode(400);
  }

  @Test
  void token_withInvalidCode_shouldReturn400() {
    GarminTokenRequest request = new GarminTokenRequest("authorization_code", "invalid-code", null);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/token")
        .then()
        .statusCode(400);
  }

  @Test
  void token_withInvalidRefreshToken_shouldReturn403() {
    GarminTokenRequest request = new GarminTokenRequest("refresh_token", null, "invalid-token");

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/garmin/oauth/token")
        .then()
        .statusCode(403);
  }

  private String extractCodeFromUrl(String url) {
    // URL format: connectiq://oauth?code=XXX&state=YYY
    int codeStart = url.indexOf("code=") + 5;
    int codeEnd = url.indexOf("&", codeStart);
    if (codeEnd == -1) {
      codeEnd = url.length();
    }
    return url.substring(codeStart, codeEnd);
  }
}
