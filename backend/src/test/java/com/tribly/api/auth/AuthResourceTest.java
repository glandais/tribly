package com.tribly.api.auth;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.domain.auth.AuthToken;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.user.User;
import com.tribly.enums.AuthTokenType;
import com.tribly.repository.auth.AuthTokenRepository;
import com.tribly.service.auth.JwtService;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthResourceTest extends AbstractResourceTest {

  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject JwtService jwtService;
  @Inject MockMailbox mailbox;

  private Domain domain;

  @BeforeEach
  protected void setUp() {
    super.setUp();
    domain = dataService.getOrCreateDefaultDomain();
    mailbox.clear();
  }

  // --- Register tests ---

  @Test
  void register_shouldReturn200AndSendEmail() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
              "email": "newuser@example.com",
              "displayName": "New User"
            }
            """)
        .when()
        .post("/api/auth/register")
        .then()
        .statusCode(200)
        .body("message", containsString("Verification"));
  }

  @Test
  void register_withInvalidEmail_shouldReturn400() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
              "email": "not-an-email",
              "displayName": "Test"
            }
            """)
        .when()
        .post("/api/auth/register")
        .then()
        .statusCode(400);
  }

  @Test
  void register_withExistingEmail_shouldReturn400() {
    dataService.createVerifiedUser("existing@example.com", "Existing");

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
              "email": "existing@example.com",
              "displayName": "Test"
            }
            """)
        .when()
        .post("/api/auth/register")
        .then()
        .statusCode(400)
        .body("code", equalTo("EMAIL_ALREADY_EXISTS"));
  }

  @Test
  void register_withMissingFields_shouldReturn400() {
    given()
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/api/auth/register")
        .then()
        .statusCode(400);
  }

  // --- Verify Email tests ---

  @Test
  void verifyEmail_withValidToken_shouldReturn200() {
    createVerificationToken("verify@example.com", "Test User", "valid-token");

    given()
        .contentType(ContentType.JSON)
        .body("{\"token\": \"valid-token\"}")
        .when()
        .post("/api/auth/verify-email")
        .then()
        .statusCode(200)
        .body("accessToken", is(notNullValue()))
        .body("user.email", equalTo("verify@example.com"))
        .cookie("refresh_token", is(notNullValue()));
  }

  @Test
  void verifyEmail_withInvalidToken_shouldReturn400() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"token\": \"invalid-token\"}")
        .when()
        .post("/api/auth/verify-email")
        .then()
        .statusCode(400)
        .body("code", equalTo("TOKEN_INVALID"));
  }

  @Test
  void verifyEmail_withExpiredToken_shouldReturn400() {
    createExpiredVerificationToken("expired@example.com", "Test", "expired-token");

    given()
        .contentType(ContentType.JSON)
        .body("{\"token\": \"expired-token\"}")
        .when()
        .post("/api/auth/verify-email")
        .then()
        .statusCode(400)
        .body("code", equalTo("TOKEN_INVALID"));
  }

  // --- OTP tests ---

  @Test
  void requestOtp_shouldReturn200() {
    dataService.createVerifiedUser("otp@example.com", "OTP User");

    given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"otp@example.com\"}")
        .when()
        .post("/api/auth/otp")
        .then()
        .statusCode(200)
        .body("message", containsString("code"));
  }

  @Test
  void requestOtp_withNonexistentEmail_shouldStillReturn200() {
    // Should not reveal if email exists
    given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"nonexistent@example.com\"}")
        .when()
        .post("/api/auth/otp")
        .then()
        .statusCode(200);
  }

  @Test
  void verifyOtp_withValidCode_shouldReturn200() {
    User user = dataService.createVerifiedUser("otp@example.com", "OTP User");
    createOtpToken(user, "123456");

    given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"otp@example.com\", \"code\": \"123456\"}")
        .when()
        .post("/api/auth/otp/verify")
        .then()
        .statusCode(200)
        .body("accessToken", is(notNullValue()))
        .body("user.email", equalTo("otp@example.com"))
        .cookie("refresh_token", is(notNullValue()));
  }

  @Test
  void verifyOtp_withInvalidCode_shouldReturn400() {
    User user = dataService.createVerifiedUser("otp@example.com", "OTP User");
    createOtpToken(user, "123456");

    given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"otp@example.com\", \"code\": \"000000\"}")
        .when()
        .post("/api/auth/otp/verify")
        .then()
        .statusCode(400)
        .body("code", equalTo("TOKEN_INVALID"));
  }

  // --- Refresh tests ---

  @Test
  void refresh_withValidCookie_shouldReturn200() {
    User user = dataService.createVerifiedUser("refresh@example.com", "Refresh User");
    String refreshToken = dataService.createRefreshTokenForUser(user);

    given()
        .contentType(ContentType.JSON)
        .cookie("refresh_token", refreshToken)
        .when()
        .post("/api/auth/refresh")
        .then()
        .statusCode(200)
        .body("accessToken", is(notNullValue()))
        .body("user.email", equalTo("refresh@example.com"));
  }

  @Test
  void refresh_withoutCookie_shouldReturn403() {
    given().contentType(ContentType.JSON).when().post("/api/auth/refresh").then().statusCode(403);
  }

  @Test
  void refresh_withInvalidCookie_shouldReturn403() {
    given()
        .contentType(ContentType.JSON)
        .cookie("refresh_token", "invalid-token")
        .when()
        .post("/api/auth/refresh")
        .then()
        .statusCode(403);
  }

  // --- Logout tests ---

  @Test
  void logout_shouldReturn204AndDeleteCookie() {
    User user = dataService.createVerifiedUser("logout@example.com", "Logout User");
    String refreshToken = dataService.createRefreshTokenForUser(user);

    given()
        .contentType(ContentType.JSON)
        .cookie("refresh_token", refreshToken)
        .when()
        .post("/api/auth/logout")
        .then()
        .statusCode(204)
        .cookie("refresh_token", equalTo(""));
  }

  @Test
  void logout_withoutCookie_shouldStillReturn204() {
    given().contentType(ContentType.JSON).when().post("/api/auth/logout").then().statusCode(204);
  }

  @Test
  void logout_shouldInvalidateRefreshToken() {
    User user = dataService.createVerifiedUser("logout@example.com", "Logout User");
    String refreshToken = dataService.createRefreshTokenForUser(user);

    // Logout
    given()
        .contentType(ContentType.JSON)
        .cookie("refresh_token", refreshToken)
        .when()
        .post("/api/auth/logout")
        .then()
        .statusCode(204);

    // Try to refresh with same token
    given()
        .contentType(ContentType.JSON)
        .cookie("refresh_token", refreshToken)
        .when()
        .post("/api/auth/refresh")
        .then()
        .statusCode(403);
  }

  // --- Logout All tests ---

  @Test
  void logoutAll_withAuth_shouldReturn204() {
    User user = dataService.createVerifiedUser("logoutall@example.com", "Logout All User");
    String accessToken = jwtService.generateAccessToken(user);

    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(accessToken)
        .when()
        .post("/api/auth/logout-all")
        .then()
        .statusCode(204);
  }

  @Test
  void logoutAll_withoutAuth_shouldReturn401() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .post("/api/auth/logout-all")
        .then()
        .statusCode(401);
  }

  @Test
  void logoutAll_shouldInvalidateAllSessions() {
    User user = dataService.createVerifiedUser("logoutall@example.com", "User");
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken1 = dataService.createRefreshTokenForUser(user);
    String refreshToken2 = dataService.createRefreshTokenForUser(user);

    // Logout all
    given()
        .contentType(ContentType.JSON)
        .auth()
        .oauth2(accessToken)
        .when()
        .post("/api/auth/logout-all")
        .then()
        .statusCode(204);

    // Both refresh tokens should be invalid
    given()
        .contentType(ContentType.JSON)
        .cookie("refresh_token", refreshToken1)
        .when()
        .post("/api/auth/refresh")
        .then()
        .statusCode(403);

    given()
        .contentType(ContentType.JSON)
        .cookie("refresh_token", refreshToken2)
        .when()
        .post("/api/auth/refresh")
        .then()
        .statusCode(403);
  }

  // --- Helper methods ---

  @Transactional
  void createVerificationToken(String email, String displayName, String token) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        new AuthToken(
            email,
            tokenHash,
            AuthTokenType.EMAIL_VERIFICATION,
            Instant.now().plus(24, ChronoUnit.HOURS));
    authToken.setPendingDisplayName(displayName);
    authToken.setPendingDomainId(domain.getId());
    authTokenRepository.persist(authToken);
  }

  @Transactional
  void createExpiredVerificationToken(String email, String displayName, String token) {
    String tokenHash = hashToken(token);
    AuthToken authToken =
        new AuthToken(
            email,
            tokenHash,
            AuthTokenType.EMAIL_VERIFICATION,
            Instant.now().minus(1, ChronoUnit.HOURS));
    authToken.setPendingDisplayName(displayName);
    authToken.setPendingDomainId(domain.getId());
    authTokenRepository.persist(authToken);
  }

  @Transactional
  void createOtpToken(User user, String code) {
    String tokenHash = hashToken(code);
    AuthToken authToken =
        new AuthToken(
            user,
            user.getEmail(),
            tokenHash,
            AuthTokenType.OTP,
            Instant.now().plus(5, ChronoUnit.MINUTES));
    authTokenRepository.persist(authToken);
  }

  private String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }
}
