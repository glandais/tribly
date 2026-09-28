package fr.pedalons.infrastructure.push;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.enums.PushPlatform;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** The platform-specific shape of an FCM message — plain unit tests, no Quarkus. */
class FcmMessageTest {

  private static final Map<String, String> DATA = Map.of("path", "/teams/t/rides/r");

  @Test
  void web_isDataOnly_withTitleAndBodyInData() {
    Map<String, Object> message =
        FcmClient.message("token", PushPlatform.WEB, "Title", "Body", DATA);

    // The service worker shows the notification: a notification block would show it twice.
    assertFalse(message.containsKey("notification"));
    @SuppressWarnings("unchecked")
    Map<String, String> data = (Map<String, String>) message.get("data");
    assertEquals("Title", data.get("title"));
    assertEquals("Body", data.get("body"));
    assertEquals("/teams/t/rides/r", data.get("path"));
    assertTrue(message.containsKey("webpush"));
    assertFalse(message.containsKey("android"));
    assertFalse(message.containsKey("apns"));
  }

  @Test
  void android_keepsTheNotificationBlock() {
    Map<String, Object> message =
        FcmClient.message("token", PushPlatform.ANDROID, "Title", "Body", DATA);

    assertEquals(Map.of("title", "Title", "body", "Body"), message.get("notification"));
    assertEquals(DATA, message.get("data"));
    assertTrue(message.containsKey("android"));
  }
}
