package com.tribly.integration;

import com.tribly.domain.ride.RideGroupRepository;
import com.tribly.domain.ride.RideParticipationRepository;
import com.tribly.domain.ride.RideRepository;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.id.TsidUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class UserResourceTest {

    public static final String USERNAME_TEST = "user1";
    public static final String USERNAME2_TEST = "user2";

    private static final String TEST_EMAIL = "user1@example.com";

    @Inject
    RideRepository rideRepository;

    @Inject
    RideGroupRepository rideGroupRepository;

    @Inject
    RideParticipationRepository participationRepository;

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    @Inject
    UserRepository userRepository;

    private User testUser;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
    }

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up in correct order
        participationRepository.deleteAll();
        rideGroupRepository.deleteAll();
        rideRepository.deleteAll();
        userTeamRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User(TEST_EMAIL, "Test User");
        testUser.setLocale("en");
        testUser.setTimezone("UTC");
        userRepository.persistAndFlush(testUser);
    }

    @Test
    void getCurrentUser_shouldReturnUserDetails() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(TEST_EMAIL))
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
                .auth().oauth2(getAccessToken(USERNAME_TEST))
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
                .auth().oauth2(getAccessToken(USERNAME_TEST))
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
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .when()
                .get("/v1/users/" + TsidUtils.toString(testUser.getId()))
                .then()
                .statusCode(200)
                .body("id", equalTo(TsidUtils.toString(testUser.getId())))
                .body("displayName", equalTo("User One"))
                // Public profile should not include email
                .body("$", not(hasKey("email")));
    }

    @Test
    void getUserById_withNonexistentId_shouldReturn404() {
        given()
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .when()
                .get("/v1/users/" + TsidUtils.toString(999999L))
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
                .auth().oauth2(getAccessToken(USERNAME_TEST))
                .when()
                .delete("/v1/users/me")
                .then()
                .statusCode(204);

        // Verify user is no longer accessible (will return 404 because user is deleted)
        given()
                .auth().oauth2(getAccessToken(USERNAME2_TEST))
                .when()
                .get("/v1/users/" + TsidUtils.toString(testUser.getId()))
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
