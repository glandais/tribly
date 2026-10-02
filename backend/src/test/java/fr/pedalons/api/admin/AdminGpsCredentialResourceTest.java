package fr.pedalons.api.admin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.admin.CreateGpsCredentialRequest;
import fr.pedalons.dto.admin.UpdateGpsCredentialRequest;
import fr.pedalons.enums.GpsOAuthVersion;
import fr.pedalons.enums.GpsServiceType;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A domain's GPS credential and its OAuth protocol (docs/LEDGER_*.md API-62). */
@QuarkusTest
class AdminGpsCredentialResourceTest extends AbstractResourceTest {

  private String adminToken;
  private String credentialsPath;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    adminToken =
        jwtService.generateAccessToken(
            dataService.createPlatformAdminUser("admin@example.com", "Platform Admin"));
    credentialsPath =
        "/api/admin/domains/" + TsidUtils.toString(domain.getId()) + "/gps-credentials";
  }

  private io.restassured.response.ValidatableResponse create(CreateGpsCredentialRequest request) {
    return given()
        .auth()
        .oauth2(adminToken)
        .contentType("application/json")
        .body(request)
        .when()
        .post(credentialsPath)
        .then();
  }

  @Test
  void create_withoutVersion_isOAuth2() {
    create(new CreateGpsCredentialRequest(GpsServiceType.GARMIN, "id", "secret", true, null))
        .statusCode(201)
        .body("oauthVersion", equalTo("OAUTH2"));
  }

  @Test
  void create_garminOAuth1() {
    create(
            new CreateGpsCredentialRequest(
                GpsServiceType.GARMIN,
                "consumer-key",
                "consumer-secret",
                true,
                GpsOAuthVersion.OAUTH1))
        .statusCode(201)
        .body("oauthVersion", equalTo("OAUTH1"))
        .body("clientId", equalTo("consumer-key"));
  }

  @Test
  void create_oauth1ForAnotherService_isRefused() {
    create(
            new CreateGpsCredentialRequest(
                GpsServiceType.WAHOO, "key", "secret", true, GpsOAuthVersion.OAUTH1))
        .statusCode(400)
        .body("code", equalTo("GPS_OAUTH_VERSION_NOT_SUPPORTED"));
  }

  @Test
  void create_oauth1WithoutSecret_isRefused() {
    create(
            new CreateGpsCredentialRequest(
                GpsServiceType.GARMIN, "key", null, true, GpsOAuthVersion.OAUTH1))
        .statusCode(400)
        .body("code", equalTo("GPS_CLIENT_SECRET_REQUIRED"));
  }

  @Test
  void update_switchesTheVersionAndKeepsItWhenOmitted() {
    String id =
        create(new CreateGpsCredentialRequest(GpsServiceType.GARMIN, "id", "secret", true, null))
            .statusCode(201)
            .extract()
            .path("id");

    given()
        .auth()
        .oauth2(adminToken)
        .contentType("application/json")
        .body(new UpdateGpsCredentialRequest("key", null, true, GpsOAuthVersion.OAUTH1))
        .when()
        .put(credentialsPath + "/" + id)
        .then()
        .statusCode(200)
        .body("oauthVersion", equalTo("OAUTH1"));

    given()
        .auth()
        .oauth2(adminToken)
        .contentType("application/json")
        .body(new UpdateGpsCredentialRequest("key", null, false, null))
        .when()
        .put(credentialsPath + "/" + id)
        .then()
        .statusCode(200)
        .body("oauthVersion", equalTo("OAUTH1"));
  }
}
