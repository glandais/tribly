package com.tribly.api.auth;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.common.TsidUtils;
import com.tribly.domain.auth.Passkey;
import com.tribly.domain.user.User;
import com.tribly.service.auth.JwtService;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PasskeyResourceTest extends AbstractResourceTest {

  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject JwtService jwtService;

  private User user;
  private String accessToken;

  @BeforeEach
  protected void setUp() {
    super.setUp();
    user = dataService.createVerifiedUser("passkey@example.com", "Passkey User");
    accessToken = jwtService.generateAccessToken(user);
  }

  // --- Registration Options tests ---

  @Test
  void getRegistrationOptions_withAuth_shouldReturn200() {
    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys/registration-options")
        .then()
        .statusCode(200)
        .body("challenge", is(notNullValue()))
        .body("rp", is(notNullValue()))
        .body("rp.id", is(notNullValue()))
        .body("user", is(notNullValue()))
        .body("user.name", equalTo("passkey@example.com"))
        .body("pubKeyCredParams", is(notNullValue()));
  }

  @Test
  void getRegistrationOptions_withoutAuth_shouldReturn401() {
    given().when().get("/api/auth/passkeys/registration-options").then().statusCode(401);
  }

  @Test
  void getRegistrationOptions_shouldExcludeExistingCredentials() {
    dataService.createPasskey(user, "existing-cred".getBytes(), "key".getBytes());

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys/registration-options")
        .then()
        .statusCode(200)
        .body("excludeCredentials", is(notNullValue()))
        .body("excludeCredentials.size()", equalTo(1));
  }

  // --- Register Passkey tests ---

  @Test
  void registerPasskey_withoutAuth_shouldReturn401() {
    given()
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/api/auth/passkeys/register")
        .then()
        .statusCode(401);
  }

  @Test
  void registerPasskey_withInvalidResponse_shouldReturn400() {
    // First generate registration options to create a challenge
    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys/registration-options")
        .then()
        .statusCode(200);

    // Try to register with invalid response
    given()
        .auth()
        .oauth2(accessToken)
        .contentType(ContentType.JSON)
        .body("{\"clientDataJSON\": \"invalid\", \"attestationObject\": \"invalid\"}")
        .when()
        .post("/api/auth/passkeys/register")
        .then()
        .statusCode(400);
  }

  // --- Authentication Options tests ---

  @Test
  void getAuthenticationOptions_shouldReturn200() {
    given()
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/api/auth/passkeys/authentication-options")
        .then()
        .statusCode(200)
        .body("challenge", is(notNullValue()))
        .body("rpId", is(notNullValue()))
        .body("timeout", is(notNullValue()));
  }

  @Test
  void getAuthenticationOptions_withEmail_shouldIncludeAllowCredentials() {
    dataService.createPasskey(user, "user-cred".getBytes(), "key".getBytes());

    given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"passkey@example.com\"}")
        .when()
        .post("/api/auth/passkeys/authentication-options")
        .then()
        .statusCode(200)
        .body("allowCredentials", is(notNullValue()))
        .body("allowCredentials.size()", equalTo(1));
  }

  @Test
  void getAuthenticationOptions_withUnknownEmail_shouldNotIncludeAllowCredentials() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"unknown@example.com\"}")
        .when()
        .post("/api/auth/passkeys/authentication-options")
        .then()
        .statusCode(200)
        .body("allowCredentials", is(nullValue()));
  }

  // --- Authenticate tests ---

  @Test
  void authenticate_withInvalidCredential_shouldReturn403() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {
              "id": "dW5rbm93bg",
              "clientDataJSON": "e30",
              "authenticatorData": "AAAA",
              "signature": "AAAA"
            }
            """)
        .when()
        .post("/api/auth/passkeys/authenticate")
        .then()
        .statusCode(403);
  }

  // --- List Passkeys tests ---

  @Test
  void listPasskeys_withAuth_shouldReturn200() {
    dataService.createPasskey(user, "cred-1".getBytes(), "key-1".getBytes());
    dataService.createPasskey(user, "cred-2".getBytes(), "key-2".getBytes());

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys")
        .then()
        .statusCode(200)
        .body("size()", equalTo(2));
  }

  @Test
  void listPasskeys_withoutAuth_shouldReturn401() {
    given().when().get("/api/auth/passkeys").then().statusCode(401);
  }

  @Test
  void listPasskeys_shouldReturnEmptyForUserWithNoPasskeys() {
    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys")
        .then()
        .statusCode(200)
        .body("size()", equalTo(0));
  }

  @Test
  void listPasskeys_shouldIgnoreDeletedPasskeys() {
    dataService.createPasskey(user, "active".getBytes(), "key".getBytes());
    Passkey deletedPasskey =
        dataService.createPasskey(user, "deleted".getBytes(), "key".getBytes());
    dataService.deletePasskey(deletedPasskey);

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys")
        .then()
        .statusCode(200)
        .body("size()", equalTo(1));
  }

  @Test
  void listPasskeys_shouldOnlyReturnOwnPasskeys() {
    User otherUser = dataService.createVerifiedUser("other@example.com", "Other");
    dataService.createPasskey(user, "my-cred".getBytes(), "key".getBytes());
    dataService.createPasskey(otherUser, "other-cred".getBytes(), "key".getBytes());

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys")
        .then()
        .statusCode(200)
        .body("size()", equalTo(1));
  }

  // --- Delete Passkey tests ---

  @Test
  void deletePasskey_withAuth_shouldReturn204() {
    Passkey passkey = dataService.createPasskey(user, "to-delete".getBytes(), "key".getBytes());

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .delete("/api/auth/passkeys/" + TsidUtils.toString(passkey.getId()))
        .then()
        .statusCode(204);

    // Verify it's deleted
    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .get("/api/auth/passkeys")
        .then()
        .statusCode(200)
        .body("size()", equalTo(0));
  }

  @Test
  void deletePasskey_withoutAuth_shouldReturn401() {
    Passkey passkey = dataService.createPasskey(user, "cred".getBytes(), "key".getBytes());

    given()
        .when()
        .delete("/api/auth/passkeys/" + TsidUtils.toString(passkey.getId()))
        .then()
        .statusCode(401);
  }

  @Test
  void deletePasskey_withNonexistentId_shouldReturn404() {
    // Use a valid TSID format that doesn't exist
    String nonexistentTsid = io.hypersistence.tsid.TSID.fast().toLowerCase();

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .delete("/api/auth/passkeys/" + nonexistentTsid)
        .then()
        .statusCode(404);
  }

  @Test
  void deletePasskey_withWrongUser_shouldReturn404() {
    User otherUser = dataService.createVerifiedUser("other@example.com", "Other");
    Passkey otherPasskey =
        dataService.createPasskey(otherUser, "other-cred".getBytes(), "key".getBytes());

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .delete("/api/auth/passkeys/" + TsidUtils.toString(otherPasskey.getId()))
        .then()
        .statusCode(404);
  }

  @Test
  void deletePasskey_withAlreadyDeletedPasskey_shouldReturn404() {
    Passkey passkey = dataService.createPasskey(user, "deleted".getBytes(), "key".getBytes());
    dataService.deletePasskey(passkey);

    given()
        .auth()
        .oauth2(accessToken)
        .when()
        .delete("/api/auth/passkeys/" + TsidUtils.toString(passkey.getId()))
        .then()
        .statusCode(404);
  }
}
