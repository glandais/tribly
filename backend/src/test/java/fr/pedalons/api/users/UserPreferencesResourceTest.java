package fr.pedalons.api.users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.users.request.UserPreferencesRequest;
import fr.pedalons.enums.ThemePreference;
import fr.pedalons.enums.UnitSystem;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** {@code PATCH /api/users/me/preferences}: units, theme and language, persisted server-side. */
@QuarkusTest
class UserPreferencesResourceTest extends AbstractResourceTest {

  private static final String URL = "/api/users/me/preferences";

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void getMe_beforeAnyChoice_shouldReportNoPreference() {
    // Null, not a default: "never chosen" and "chose SYSTEM" are different answers, and a client
    // reads null as "follow the device".
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me")
        .then()
        .statusCode(200)
        .body("$", not(hasKey("theme")))
        .body("$", not(hasKey("language")));
  }

  @Test
  void patchPreferences_shouldPersistAllThree() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(
            UserPreferencesRequest.builder()
                .unitSystem(UnitSystem.IMPERIAL)
                .theme(ThemePreference.DARK)
                .language("fr")
                .build())
        .when()
        .patch(URL)
        .then()
        .statusCode(200)
        .body("unitSystem", equalTo("IMPERIAL"))
        .body("theme", equalTo("DARK"))
        .body("language", equalTo("fr"));

    // Persisted, not just echoed.
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me")
        .then()
        .statusCode(200)
        .body("unitSystem", equalTo("IMPERIAL"))
        .body("theme", equalTo("DARK"))
        .body("language", equalTo("fr"));
  }

  @Test
  void patchPreferences_shouldLeaveOmittedFieldsAlone() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(
            UserPreferencesRequest.builder()
                .unitSystem(UnitSystem.IMPERIAL)
                .theme(ThemePreference.DARK)
                .language("fr")
                .build())
        .when()
        .patch(URL)
        .then()
        .statusCode(200);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(UserPreferencesRequest.builder().theme(ThemePreference.LIGHT).build())
        .when()
        .patch(URL)
        .then()
        .statusCode(200)
        .body("theme", equalTo("LIGHT"))
        // Untouched by a partial update — that is what makes it a PATCH.
        .body("unitSystem", equalTo("IMPERIAL"))
        .body("language", equalTo("fr"));
  }

  @Test
  void patchPreferences_shouldForbidSharedCaching() {
    // D3: the body describes exactly one user.
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(UserPreferencesRequest.builder().theme(ThemePreference.LIGHT).build())
        .when()
        .patch(URL)
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("private"))
        .header("Cache-Control", containsString("no-store"));
  }

  @Test
  void patchPreferences_withAMalformedLanguage_shouldBeRejected() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(UserPreferencesRequest.builder().language("not a tag!").build())
        .when()
        .patch(URL)
        .then()
        .statusCode(400);
  }

  @Test
  void patchPreferences_anonymous_shouldBeRefused() {
    given()
        .contentType(ContentType.JSON)
        .body(UserPreferencesRequest.builder().theme(ThemePreference.DARK).build())
        .when()
        .patch(URL)
        .then()
        .statusCode(401);
  }

  @Test
  void patchPreferences_shouldOnlyTouchThisDomainsAccount() {
    // The same email exists on two domains and they are two different people. A preference set on
    // one must not follow the other.
    Domain other = dataService.createDomain("other-prefs.test", "Other", "https://other.test");
    User twin = dataService.createUser(other, EMAIL1, "Same email, other domain");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(UserPreferencesRequest.builder().theme(ThemePreference.DARK).build())
        .when()
        .patch(URL)
        .then()
        .statusCode(200)
        .body("id", not(equalTo(fr.pedalons.common.TsidUtils.toString(twin.getId()))));

    org.junit.jupiter.api.Assertions.assertNull(
        dataService.findUserByEmail(other, EMAIL1).getTheme(),
        "the account of the other domain must be untouched");
  }
}
