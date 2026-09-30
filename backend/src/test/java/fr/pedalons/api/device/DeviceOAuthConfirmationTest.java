package fr.pedalons.api.device;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * docs/LEDGER_*.md SEC-2 (audit H3): a device is paired only by an explicit « Autoriser », and the
 * user can refuse. Opening a link that carries a code must never be enough.
 */
@QuarkusTest
class DeviceOAuthConfirmationTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  /** userCode and deviceCode of a fresh pairing request. */
  private JsonPath newDeviceCode(String clientId) {
    return given()
        .contentType(ContentType.JSON)
        .body("{\"clientId\": \"" + clientId + "\"}")
        .when()
        .post("/api/device/oauth/device")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath();
  }

  private ValidatableResponse post(String path, String body) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post("/api/device/oauth/" + path)
        .then();
  }

  private ValidatableResponse verify(String userCode) {
    return given().queryParam("code", userCode).when().get("/api/device/oauth/verify").then();
  }

  private ValidatableResponse token(String deviceCode) {
    return given()
        .contentType(ContentType.JSON)
        .body(
            "{\"grantType\": \"urn:ietf:params:oauth:grant-type:device_code\", \"deviceCode\": \""
                + deviceCode
                + "\"}")
        .when()
        .post("/api/device/oauth/token")
        .then();
  }

  @Test
  void complete_withoutConfirmation_isRefusedAndPairsNothing() {
    String userCode = newDeviceCode("karoo").getString("userCode");

    // What a client that approved on its own, the moment a link opened, still sends.
    post("complete", "{\"userCode\": \"" + userCode + "\"}")
        .statusCode(400)
        .body("code", equalTo("VALIDATION"));
    post("complete", "{\"userCode\": \"" + userCode + "\", \"confirmed\": false}")
        .statusCode(400)
        .body("code", equalTo("VALIDATION"));

    verify(userCode).statusCode(200).body("authorized", is(false));
  }

  @Test
  void complete_confirmed_pairsTheDevice() {
    JsonPath codes = newDeviceCode("karoo");
    token(codes.getString("deviceCode"))
        .statusCode(400)
        .body("code", equalTo("AUTHORIZATION_PENDING"));

    post("complete", "{\"userCode\": \"" + codes.getString("userCode") + "\", \"confirmed\": true}")
        .statusCode(200);

    token(codes.getString("deviceCode")).statusCode(200).body("accessToken", notNullValue());
  }

  @Test
  void verify_namesTheDeviceAndWhenItAsked() {
    String userCode = newDeviceCode("garmin").getString("userCode");

    verify(userCode)
        .statusCode(200)
        .body("clientId", equalTo("garmin"))
        .body("requestedAt", notNullValue())
        .body("authorized", is(false));
  }

  @Test
  void deny_expiresTheCodeForEveryone() {
    JsonPath codes = newDeviceCode("karoo");
    String userCode = codes.getString("userCode");

    post("deny", "{\"userCode\": \"" + userCode + "\"}").statusCode(200);

    verify(userCode).statusCode(404);
    post("complete", "{\"userCode\": \"" + userCode + "\", \"confirmed\": true}")
        .statusCode(400)
        .body("code", equalTo("TOKEN_INVALID"));
    // The device hears the answer Karoo and Garmin already handle by starting over.
    token(codes.getString("deviceCode")).statusCode(400).body("code", equalTo("TOKEN_EXPIRED"));
  }

  @Test
  void deny_unknownCode_isRefused() {
    post("deny", "{\"userCode\": \"ZZZZZ2\"}")
        .statusCode(400)
        .body("code", equalTo("TOKEN_INVALID"));
  }

  @Test
  void deny_withoutAuth_isRefused() {
    String userCode = newDeviceCode("karoo").getString("userCode");

    given()
        .contentType(ContentType.JSON)
        .body("{\"userCode\": \"" + userCode + "\"}")
        .when()
        .post("/api/device/oauth/deny")
        .then()
        .statusCode(401);
    verify(userCode).statusCode(200);
  }
}
