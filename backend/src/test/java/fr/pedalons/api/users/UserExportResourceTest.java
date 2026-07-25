package fr.pedalons.api.users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TokenUtils;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.domain.user.UserExport;
import fr.pedalons.enums.UserExportStatus;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.service.user.UserExportService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserExportResourceTest extends AbstractResourceTest {

  @Inject StorageService storageService;
  @Inject UserExportService userExportService;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  // ------------------------------------------------------------------ request

  @Test
  void requestExport_shouldQueueAPendingJob() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/users/me/export")
        .then()
        .statusCode(202)
        .body("id", notNullValue())
        .body("status", equalTo("PENDING"));
  }

  @Test
  void requestExport_withoutAuth_shouldReturn401() {
    given().when().post("/api/users/me/export").then().statusCode(401);
  }

  @Test
  void requestExport_whileOneIsRunning_shouldReturn429() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/users/me/export")
        .then()
        .statusCode(202);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/users/me/export")
        .then()
        .statusCode(429)
        .body("code", equalTo("EXPORT_IN_PROGRESS"))
        .header("Retry-After", notNullValue());
  }

  @Test
  void requestExport_withinTheCooldown_shouldReturn429() {
    UserExport previous = dataService.createUserExport(domain, user1, UserExportStatus.READY);
    dataService.backdateUserExport(previous, 30);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/users/me/export")
        .then()
        .statusCode(429)
        .body("code", equalTo("EXPORT_RATE_LIMITED"));
  }

  @Test
  void requestExport_afterTheCooldown_shouldQueueAnother() {
    UserExport previous = dataService.createUserExport(domain, user1, UserExportStatus.READY);
    dataService.backdateUserExport(previous, 120);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/users/me/export")
        .then()
        .statusCode(202)
        .body("status", equalTo("PENDING"));
  }

  @Test
  void requestExport_isPerUser_soAnotherUserIsUnaffected() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .post("/api/users/me/export")
        .then()
        .statusCode(202);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .post("/api/users/me/export")
        .then()
        .statusCode(202);
  }

  // ------------------------------------------------------------------- status

  @Test
  void getLatestExport_withNoHistory_shouldReturn204() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me/export")
        .then()
        .statusCode(204);
  }

  @Test
  void getLatestExport_shouldReturnTheMostRecent() {
    dataService.createUserExport(domain, user1, UserExportStatus.READY);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me/export")
        .then()
        .statusCode(200)
        .body("status", equalTo("READY"));
  }

  @Test
  void getExport_ofAnotherUser_shouldReturn404NotForbidden() {
    UserExport theirs = dataService.createUserExport(domain, user2);

    // 404 rather than 403: a 403 would confirm the job exists.
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me/export/" + TsidUtils.toString(theirs.getId()))
        .then()
        .statusCode(404);
  }

  @Test
  void getExport_ofOwnJob_shouldReturnIt() {
    UserExport mine = dataService.createUserExport(domain, user1);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me/export/" + TsidUtils.toString(mine.getId()))
        .then()
        .statusCode(200)
        .body("id", equalTo(TsidUtils.toString(mine.getId())));
  }

  // ----------------------------------------------------------------- download

  @Test
  void download_withAValidToken_shouldReturnTheArchive() {
    String token = readyExportWithToken(domain, user1, "zip-bytes");

    byte[] body =
        given()
            .when()
            .get("/api/export/download/" + token)
            .then()
            .statusCode(200)
            .header("Content-Disposition", containsString(".zip"))
            .extract()
            .asByteArray();

    assertEquals("zip-bytes", new String(body, StandardCharsets.UTF_8));
  }

  @Test
  void download_withAGarbageToken_shouldReturn404() {
    given().when().get("/api/export/download/not-a-real-token").then().statusCode(404);
  }

  /**
   * The highest-value test here: a token is domain-scoped, so a link leaked from one tenant must not
   * work against another even though the hash is globally unique.
   */
  @Test
  void download_fromAnotherDomain_shouldReturn404() {
    String token = readyExportWithToken(domain, user1, "zip-bytes");

    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    assertNotNull(other);

    given()
        .header("X-Forwarded-Host", "other.localhost")
        .when()
        .get("/api/export/download/" + token)
        .then()
        .statusCode(404);
  }

  @Test
  void download_afterExpiry_shouldReturn404() {
    String token = readyExportWithToken(domain, user1, "zip-bytes");

    UserExport export = dataService.findLatestUserExport(user1);
    export.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
    dataService.updateUserExport(export);

    given().when().get("/api/export/download/" + token).then().statusCode(404);
  }

  @Test
  void download_afterPurge_shouldReturn404() {
    String token = readyExportWithToken(domain, user1, "zip-bytes");

    UserExport export = dataService.findLatestUserExport(user1);
    export.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
    dataService.updateUserExport(export);
    userExportService.purgeExpired();

    given().when().get("/api/export/download/" + token).then().statusCode(404);
  }

  /** Stores an archive and a READY row pointing at it, and returns the plaintext download token. */
  private String readyExportWithToken(Domain domain, User user, String content) {
    UserExport export = dataService.createUserExport(domain, user);
    String key = "exports/" + TsidUtils.toString(user.getId()) + "/test.zip";
    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    storageService.store(key, new ByteArrayInputStream(bytes), "application/zip", bytes.length);

    String token = TokenUtils.generateSecureToken();
    export.setStatus(UserExportStatus.READY);
    export.setStorageKey(key);
    export.setFileSize((long) bytes.length);
    export.setDownloadTokenHash(TokenUtils.hashToken(token));
    export.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
    export.setCompletedAt(Instant.now());
    dataService.updateUserExport(export);
    return token;
  }
}
