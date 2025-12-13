package com.tribly.integration;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
class UserResourceTest {

    @Inject
    UserRepository userRepository;

    private User testUser;
    private static final String TEST_EMAIL = "test-user@example.com";
    private static final String TEST_STRAVA_ID = "test-strava-12345";

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up existing test user
        userRepository.delete("email", TEST_EMAIL);

        testUser = new User(TEST_EMAIL, "Test User");
        testUser.setStravaId(TEST_STRAVA_ID);
        testUser.setLocale("en");
        testUser.setTimezone("UTC");
        userRepository.persistAndFlush(testUser);
    }

    @Test
    @TestSecurity(user = "test-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = TEST_EMAIL),
            @Claim(key = "name", value = "Test User"),
            @Claim(key = "strava_id", value = TEST_STRAVA_ID)
    })
    void getCurrentUser_shouldReturnUserDetails() {
        given()
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(TEST_EMAIL))
                .body("displayName", equalTo("Test User"))
                .body("locale", equalTo("en"))
                .body("timezone", equalTo("UTC"));
    }

    @Test
    void getCurrentUser_withoutAuth_shouldReturn401() {
        given()
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = TEST_EMAIL),
            @Claim(key = "name", value = "Test User"),
            @Claim(key = "strava_id", value = TEST_STRAVA_ID)
    })
    void updateCurrentUser_shouldUpdateDisplayName() {
        given()
                .contentType("application/json")
                .body("{\"displayName\": \"Updated Name\"}")
                .when()
                .put("/v1/users/me")
                .then()
                .statusCode(200)
                .body("displayName", equalTo("Updated Name"));
    }

    @Test
    @TestSecurity(user = "test-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = TEST_EMAIL),
            @Claim(key = "name", value = "Test User"),
            @Claim(key = "strava_id", value = TEST_STRAVA_ID)
    })
    void updateCurrentUser_shouldUpdateLocaleAndTimezone() {
        given()
                .contentType("application/json")
                .body("{\"locale\": \"fr\", \"timezone\": \"Europe/Paris\"}")
                .when()
                .put("/v1/users/me")
                .then()
                .statusCode(200)
                .body("locale", equalTo("fr"))
                .body("timezone", equalTo("Europe/Paris"));
    }

    @Test
    void updateCurrentUser_withoutAuth_shouldReturn401() {
        given()
                .contentType("application/json")
                .body("{\"displayName\": \"Hacker\"}")
                .when()
                .put("/v1/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = TEST_EMAIL),
            @Claim(key = "name", value = "Test User"),
            @Claim(key = "strava_id", value = TEST_STRAVA_ID)
    })
    void getUserById_shouldReturnPublicProfile() {
        given()
                .when()
                .get("/v1/users/" + testUser.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(testUser.getId().intValue()))
                .body("displayName", equalTo("Test User"))
                // Public profile should not include email
                .body("$", not(hasKey("email")))
                .body("$", not(hasKey("stravaId")));
    }

    @Test
    @TestSecurity(user = "test-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = TEST_EMAIL),
            @Claim(key = "name", value = "Test User"),
            @Claim(key = "strava_id", value = TEST_STRAVA_ID)
    })
    void getUserById_withNonexistentId_shouldReturn404() {
        given()
                .when()
                .get("/v1/users/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void getUserById_withoutAuth_shouldReturn401() {
        given()
                .when()
                .get("/v1/users/" + testUser.getId())
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test-keycloak-id", roles = "user")
    @OidcSecurity(claims = {
            @Claim(key = "email", value = TEST_EMAIL),
            @Claim(key = "name", value = "Test User"),
            @Claim(key = "strava_id", value = TEST_STRAVA_ID)
    })
    void deleteCurrentUser_shouldSoftDeleteAccount() {
        given()
                .when()
                .delete("/v1/users/me")
                .then()
                .statusCode(204);

        // Verify user is no longer accessible (will return 404 because user is deleted)
        given()
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteCurrentUser_withoutAuth_shouldReturn401() {
        given()
                .when()
                .delete("/v1/users/me")
                .then()
                .statusCode(401);
    }
}
