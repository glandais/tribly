package fr.pedalons.api.device;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.auth.AuthFailure;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.enums.AuthFailureKind;
import fr.pedalons.repository.auth.AuthFailureRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * docs/LEDGER_*.md SEC-4 (audit H5): a pairing code has six characters, so the endpoints that look
 * one up refuse to be used as an oracle.
 */
@QuarkusTest
class DeviceOAuthThrottleTest extends AbstractResourceTest {

  @Inject AuthFailureRepository authFailureRepository;

  /** Clean database and standard fixture (default domain, users, teams) before every test. */
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private String newUserCode() {
    return given()
        .contentType(ContentType.JSON)
        .body("{\"clientId\": \"karoo\"}")
        .when()
        .post("/api/device/oauth/device")
        .then()
        .statusCode(200)
        .extract()
        .path("userCode");
  }

  private ValidatableResponse complete(String user, String userCode) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType(ContentType.JSON)
        .body("{\"userCode\": \"" + userCode + "\", \"confirmed\": true}")
        .when()
        .post("/api/device/oauth/complete")
        .then();
  }

  private ValidatableResponse verify(String userCode) {
    return given().queryParam("code", userCode).when().get("/api/device/oauth/verify").then();
  }

  @Transactional
  void recordAnonymousFailures(Domain onDomain, int count) {
    for (int i = 0; i < count; i++) {
      authFailureRepository.persist(
          new AuthFailure(onDomain.getId(), AuthFailureKind.DEVICE_CODE, "-"));
    }
  }

  @Test
  void complete_afterFiveUnknownCodes_isRefusedToThatAccountOnly() {
    String pending = newUserCode();
    for (int i = 0; i < 5; i++) {
      complete(USER1, "ZZZZZ" + (i + 2)).statusCode(400).body("code", equalTo("TOKEN_INVALID"));
    }

    // Even the right code: the account is past its guesses, the lookup never happens.
    complete(USER1, pending)
        .statusCode(429)
        .header("Retry-After", "600")
        .body("code", equalTo("DEVICE_CODE_RATE_LIMITED"));
    verify(pending).statusCode(200).body("authorized", is(false));

    complete(USER2, pending).statusCode(200);
  }

  @Test
  void complete_rightCodes_countForNothing() {
    for (int i = 0; i < 6; i++) {
      complete(USER1, newUserCode()).statusCode(200);
    }
  }

  @Test
  void verify_pastTheDomainBudget_isRefused() {
    String pending = newUserCode();
    recordAnonymousFailures(domain, 299);
    verify("ZZZZZ2").statusCode(404);

    verify(pending).statusCode(429).body("code", equalTo("DEVICE_CODE_RATE_LIMITED"));
    // The domain's budget binds a signed-in account too: a crowd of throwaway accounts shares it.
    complete(USER1, pending).statusCode(429);
  }

  @Test
  void verify_budgetIsPerDomain() {
    Domain other =
        dataService.createDomain("other.example.com", "Other", "https://other.example.com");
    recordAnonymousFailures(other, 300);

    verify(newUserCode()).statusCode(200);
  }

  @Test
  void verify_codeOfAnotherDomain_isUnknown() {
    String pending = newUserCode();
    dataService.createDomain("other.example.com", "Other", "https://other.example.com");

    given()
        .header("X-Forwarded-Host", "other.example.com")
        .queryParam("code", pending)
        .when()
        .get("/api/device/oauth/verify")
        .then()
        .statusCode(404);
  }
}
