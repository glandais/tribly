package fr.pedalons.api.notifications;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.notification.PushDevice;
import fr.pedalons.dto.notifications.request.PushDeviceRegistration;
import fr.pedalons.enums.PushPlatform;
import fr.pedalons.util.PushDeviceTestData;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Registering and dropping push devices — the address book of the PUSH channel. */
@QuarkusTest
class PushDeviceResourceTest extends AbstractResourceTest {

  private static final String TOKEN = "fcm-token-abcdef";

  @Inject PushDeviceTestData devices;

  private void register(String user, PushDeviceRegistration registration, int status) {
    given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(registration)
        .when()
        .post("/api/push-devices")
        .then()
        .statusCode(status);
  }

  private static PushDeviceRegistration registration(String token, String deviceName) {
    return new PushDeviceRegistration(token, PushPlatform.ANDROID, deviceName, "2.1.0");
  }

  @Test
  void register_thenUnregister() {
    register(USER1, registration(TOKEN, "Pixel 8a"), 204);

    List<PushDevice> registered = devices.of(user1);
    assertEquals(1, registered.size());
    assertEquals(TOKEN, registered.getFirst().getToken());
    assertEquals("Pixel 8a", registered.getFirst().getDeviceName());

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/push-devices/" + TOKEN)
        .then()
        .statusCode(204);

    assertTrue(devices.of(user1).isEmpty());
  }

  /** The app registers at every launch: the second call must refresh, not accumulate. */
  @Test
  void registeringTwice_keepsOneDevice() {
    register(USER1, registration(TOKEN, "Pixel 8a"), 204);
    register(USER1, registration(TOKEN, "Le Pixel de Gaby"), 204);

    List<PushDevice> registered = devices.of(user1);
    assertEquals(1, registered.size());
    assertEquals("Le Pixel de Gaby", registered.getFirst().getDeviceName());
  }

  /**
   * A phone lent to someone else: the token addresses the install, so it follows whoever is signed
   * in. Its previous owner must stop being notified on a device they no longer hold.
   */
  @Test
  void registeringSomeoneElsesToken_movesIt() {
    register(USER1, registration(TOKEN, "Pixel 8a"), 204);
    register(USER2, registration(TOKEN, "Pixel 8a"), 204);

    assertTrue(devices.of(user1).isEmpty());
    assertEquals(1, devices.of(user2).size());
  }

  /** Unregistering is scoped to the caller, and says nothing about tokens that are not theirs. */
  @Test
  void unregisteringSomeoneElsesToken_changesNothing() {
    register(USER1, registration(TOKEN, "Pixel 8a"), 204);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/push-devices/" + TOKEN)
        .then()
        .statusCode(204);

    assertEquals(1, devices.of(user1).size());
  }

  @Test
  void blankToken_isRejected() {
    register(USER1, registration("  ", "Pixel 8a"), 400);
  }

  @Test
  void anonymous_isUnauthorized() {
    given()
        .contentType("application/json")
        .body(registration(TOKEN, "Pixel 8a"))
        .when()
        .post("/api/push-devices")
        .then()
        .statusCode(401);
  }
}
