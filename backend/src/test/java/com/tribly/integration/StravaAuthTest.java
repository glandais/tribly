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
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class StravaAuthTest {

    @Inject
    UserRepository userRepository;

    @Inject
    JwtService jwtService;

    private User testUser;
    private String validToken;

    @BeforeEach
    @Transactional
    void setUp() {
        userRepository.delete("stravaId", "test-strava-123");

        testUser = new User("test@example.com", "Test User");
        testUser.setStravaId("test-strava-123");
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        userRepository.persist(testUser);

        validToken = jwtService.generateToken(testUser);
    }

    @Test
    void getCurrentUser_withValidToken_shouldReturnUser() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo("test@example.com"))
                .body("displayName", equalTo("Test User"))
                .body("stravaId", equalTo("test-strava-123"));
    }

    @Test
    void getCurrentUser_withoutToken_shouldReturn401() {
        given()
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    void updateCurrentUser_shouldUpdateProfile() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .contentType("application/json")
                .body("{\"displayName\": \"Updated Name\", \"locale\": \"fr\", \"timezone\": \"Europe/Paris\"}")
                .when()
                .put("/v1/users/me")
                .then()
                .statusCode(200)
                .body("displayName", equalTo("Updated Name"))
                .body("locale", equalTo("fr"))
                .body("timezone", equalTo("Europe/Paris"));
    }

    @Test
    void getUserById_shouldReturnPublicInfo() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .get("/v1/users/" + testUser.getId())
                .then()
                .statusCode(200)
                .body("displayName", equalTo("Test User"))
                .body("$", not(hasKey("email")));
    }

    @Test
    void getUserById_withInvalidId_shouldReturn404() {
        given()
                .header("Authorization", "Bearer " + validToken)
                .when()
                .get("/v1/users/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void jwtService_shouldGenerateValidTokens() {
        JwtService.TokenPair tokenPair = jwtService.generateTokenPair(testUser);

        assertNotNull(tokenPair.accessToken());
        assertNotNull(tokenPair.refreshToken());
        assertTrue(tokenPair.expiresIn() > 0);
        assertNotEquals(tokenPair.accessToken(), tokenPair.refreshToken());
    }
}
