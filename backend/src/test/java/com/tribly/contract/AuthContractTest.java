package com.tribly.contract;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AuthContractTest {

    @Test
    void initiateOAuth_shouldRedirectToStrava() {
        // Don't follow redirects - we just want to test that the endpoint returns a 302
        given()
                .redirects().follow(false)
                .queryParam("redirect_uri", "http://localhost:5173/auth/callback/strava")
                .when()
                .get("/v1/auth/oauth/strava")
                .then()
                .statusCode(302)
                .header("Location", containsString("strava.com/oauth/authorize"));
    }

    @Test
    void initiateOAuth_shouldRejectUnsupportedProvider() {
        given()
                .queryParam("redirect_uri", "http://localhost:5173/auth/callback/google")
                .when()
                .get("/v1/auth/oauth/google")
                .then()
                .statusCode(400)
                .body("code", equalTo("UNSUPPORTED_PROVIDER"));
    }

    @Test
    void oauthCallback_shouldRequireCode() {
        given()
                .queryParam("state", "test-state")
                .when()
                .get("/v1/auth/oauth/strava/callback")
                .then()
                .statusCode(400)
                .body("code", equalTo("MISSING_CODE"));
    }

    @Test
    void oauthCallback_shouldHandleOAuthError() {
        given()
                .queryParam("error", "access_denied")
                .queryParam("state", "test-state")
                .when()
                .get("/v1/auth/oauth/strava/callback")
                .then()
                .statusCode(401)
                .body("code", equalTo("OAUTH_ERROR"));
    }

    @Test
    void refreshToken_shouldRequireToken() {
        given()
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/v1/auth/refresh")
                .then()
                .statusCode(400)
                .body("code", equalTo("MISSING_TOKEN"));
    }

    @Test
    void logout_shouldReturnNoContent() {
        given()
                .contentType("application/json")
                .when()
                .post("/v1/auth/logout")
                .then()
                .statusCode(204);
    }
}
