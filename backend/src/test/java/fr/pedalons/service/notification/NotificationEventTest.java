package fr.pedalons.service.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pedalons.enums.NotificationType;
import fr.pedalons.service.notification.event.NotificationEvent;
import java.lang.reflect.RecordComponent;
import org.junit.jupiter.api.Test;

/**
 * Every type has a record that says it is that type, and survives the trip through the payload
 * column. Plain unit test: a default ObjectMapper stands in for Quarkus's.
 */
class NotificationEventTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void everyTypeRoundTripsThroughItsRecord() throws Exception {
    for (NotificationType type : NotificationType.values()) {
      Class<? extends NotificationEvent> recordClass = NotificationEvent.recordClass(type);
      RecordComponent[] components = recordClass.getRecordComponents();
      assertEquals(1, components.length, recordClass + ": update this test for richer payloads");
      NotificationEvent event = recordClass.getDeclaredConstructor(long.class).newInstance(42L);

      assertEquals(type, event.type());
      assertEquals(type.name() + ":42", event.dedupKey());
      assertEquals(
          event,
          objectMapper.treeToValue(objectMapper.valueToTree(event), recordClass),
          "round trip of " + recordClass.getSimpleName());
    }
  }
}
