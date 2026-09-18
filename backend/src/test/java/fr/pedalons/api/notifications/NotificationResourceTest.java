package fr.pedalons.api.notifications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.notification.Notification;
import fr.pedalons.dto.notifications.request.NotificationPreferenceUpdate;
import fr.pedalons.dto.notifications.request.NotificationPreferencesRequest;
import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationType;
import fr.pedalons.util.NotificationTestData;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class NotificationResourceTest extends AbstractResourceTest {

  @Inject NotificationTestData notifications;

  private List<Notification> inbox;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    inbox = notifications.seedInbox(user3, team1, 3);
  }

  @Test
  void list_newestFirst_withStructuredFields() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/notifications")
        .then()
        .statusCode(200)
        .header("Cache-Control", "private, no-store")
        .body("items", hasSize(3))
        .body("total", equalTo(3))
        .body("unreadCount", equalTo(3))
        .body("items[0].subjectSlug", equalTo("ride-2"))
        .body("items[0].type", equalTo("RIDE_PUBLISHED"))
        .body("items[0].subjectType", equalTo("RIDE"))
        .body("items[0].teamSlug", equalTo(team1Slug))
        .body("items[0].read", equalTo(false));
  }

  @Test
  void list_isPrivateToItsRecipient() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .get("/api/notifications")
        .then()
        .statusCode(200)
        .body("items", hasSize(0))
        .body("unreadCount", equalTo(0));
  }

  @Test
  void list_requiresAuthentication() {
    given().when().get("/api/notifications").then().statusCode(401);
  }

  @Test
  void markRead_updatesCountsAndFilter() {
    String id = TsidUtils.toString(inbox.getFirst().getId());
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .post("/api/notifications/" + id + "/read")
        .then()
        .statusCode(204);
    // Idempotent.
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .post("/api/notifications/" + id + "/read")
        .then()
        .statusCode(204);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/notifications/unread-count")
        .then()
        .statusCode(200)
        .body("count", equalTo(2));
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/notifications?unreadOnly=true")
        .then()
        .statusCode(200)
        .body("items", hasSize(2))
        .body("total", equalTo(2))
        .body("items.id", not(hasItem(id)));
  }

  @Test
  void markRead_someoneElsesNotification_is404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .post("/api/notifications/" + TsidUtils.toString(inbox.getFirst().getId()) + "/read")
        .then()
        .statusCode(404);
  }

  @Test
  void markAllRead_clearsTheBadge() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .post("/api/notifications/read-all")
        .then()
        .statusCode(204);
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/notifications/unread-count")
        .then()
        .statusCode(200)
        .body("count", equalTo(0));
  }

  @Test
  void preferences_listEveryTypeOnTheAvailableChannels_withDefaults() {
    // E-mail is on in the test profile; push has no sender yet, so it is not offered.
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/notifications/preferences")
        .then()
        .statusCode(200)
        .body("channels", equalTo(List.of("EMAIL")))
        .body("preferences", hasSize(NotificationType.values().length))
        .body("preferences.find { it.type == 'RIDE_CANCELLED' }.enabled", equalTo(true))
        .body("preferences.find { it.type == 'RIDE_PUBLISHED' }.enabled", equalTo(false));
  }

  @Test
  void preferences_update_isPartialAndReturnsTheMatrix() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(
            new NotificationPreferencesRequest(
                List.of(
                    new NotificationPreferenceUpdate(
                        NotificationType.RIDE_PUBLISHED, NotificationChannel.EMAIL, true))))
        .when()
        .put("/api/notifications/preferences")
        .then()
        .statusCode(200)
        .body("preferences.find { it.type == 'RIDE_PUBLISHED' }.enabled", equalTo(true))
        .body("preferences.find { it.type == 'RIDE_PUBLISHED' }.enabledByDefault", equalTo(false))
        .body("preferences.find { it.type == 'RIDE_CANCELLED' }.enabled", equalTo(true));
  }

  @Test
  void preferences_theInboxCannotBeSwitchedOff() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(
            new NotificationPreferencesRequest(
                List.of(
                    new NotificationPreferenceUpdate(
                        NotificationType.RIDE_PUBLISHED, NotificationChannel.IN_APP, false))))
        .when()
        .put("/api/notifications/preferences")
        .then()
        .statusCode(400);
  }
}
