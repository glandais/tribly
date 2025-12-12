package com.tribly.integration;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.service.auth.JwtService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class UserResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    JwtService jwtService;

    private User testUser;
    private String validToken;
    private String testEmail;

    @BeforeEach
    @Transactional
    void setUp() {
        // Use unique email per test to avoid conflicts
        testEmail = "user-test-" + System.currentTimeMillis() + "@example.com";

        testUser = new User(testEmail, "Test User");
        testUser.setStravaId("user-test-strava-" + System.currentTimeMillis());
        testUser.setLocale("en");
        testUser.setTimezone("UTC");
        userRepository.persistAndFlush(testUser);

        validToken = jwtService.generateToken(testUser);
    }

    @Test
    void getCurrentUser_shouldReturnUserDetails() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(200)
                .body("id", equalTo(testUser.getId().intValue()))
                .body("email", equalTo(testEmail))
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
    void updateCurrentUser_shouldUpdateDisplayName() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"displayName\": \"Updated Name\"}")
                .when()
                .put("/v1/users/me")
                .then()
                .statusCode(200)
                .body("displayName", equalTo("Updated Name"));
    }

    @Test
    void updateCurrentUser_shouldUpdateLocaleAndTimezone() {
        given()
                .header("Authorization", "Bearer " + validToken)
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
    void getUserById_shouldReturnPublicProfile() {
        given()
                .header("Authorization", "Bearer " + validToken)
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
    void getUserById_withNonexistentId_shouldReturn404() {
        given()
                .header("Authorization", "Bearer " + validToken)
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
    void deleteCurrentUser_shouldSoftDeleteAccount() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .delete("/v1/users/me")
                .then()
                .statusCode(204);

        // Verify user is no longer accessible
        given()
                .header("Authorization", "Bearer " + validToken)
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
