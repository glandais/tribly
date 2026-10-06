package fr.pedalons.dto.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/**
 * The date of a request (docs/LEDGER_*.md API-60, plan §5): a wall time read in the entity's zone,
 * or — for one version — an instant in the old format, kept as the instant it is.
 */
class EventDateTimeTest {

  private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
  private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void aWallTime_isReadInTheGivenZone() {
    EventDateTime value = EventDateTime.parse("2030-06-02T08:00:00");
    assertFalse(value.isLegacy());
    assertEquals(Instant.parse("2030-06-02T06:00:00Z"), value.toInstant(PARIS));
    assertEquals(Instant.parse("2030-06-01T23:00:00Z"), value.toInstant(TOKYO));
  }

  @Test
  void aWallTime_withoutSeconds_isAccepted() {
    assertEquals(
        EventDateTime.local(LocalDateTime.parse("2030-06-02T08:00")),
        EventDateTime.parse("2030-06-02T08:00"));
  }

  @Test
  void anInstantWithZ_isTheOldFormat_keptAsIs_whateverTheZone() {
    EventDateTime value = EventDateTime.parse("2030-06-02T06:00:00Z");
    assertTrue(value.isLegacy());
    assertEquals(Instant.parse("2030-06-02T06:00:00Z"), value.toInstant(PARIS));
    assertEquals(Instant.parse("2030-06-02T06:00:00Z"), value.toInstant(TOKYO));
  }

  @Test
  void anInstantWithAnOffset_isTheOldFormat_too() {
    EventDateTime value = EventDateTime.parse("2030-06-02T08:00:00+02:00");
    assertTrue(value.isLegacy());
    assertEquals(Instant.parse("2030-06-02T06:00:00Z"), value.toInstant(TOKYO));
  }

  /**
   * The second 02:30 of the autumn overlap, sent with its offset, stays that instant: going through
   * the wall time would move it an hour earlier.
   */
  @Test
  void anInstantInTheAutumnOverlap_isNotMoved() {
    EventDateTime value = EventDateTime.parse("2030-10-27T02:30:00+01:00");
    assertEquals(Instant.parse("2030-10-27T01:30:00Z"), value.toInstant(PARIS));
  }

  /** Paris springs forward on 2030-03-31: a wall time in the gap is shifted by the gap. */
  @Test
  void aWallTimeInTheSpringGap_isShiftedByTheGap() {
    assertEquals(
        Instant.parse("2030-03-31T01:30:00Z"),
        EventDateTime.parse("2030-03-31T02:30:00").toInstant(PARIS));
  }

  /** Paris falls back on 2030-10-27: a wall time that exists twice takes the earlier offset. */
  @Test
  void aWallTimeInTheAutumnOverlap_takesTheEarlierOffset() {
    assertEquals(
        Instant.parse("2030-10-27T00:30:00Z"),
        EventDateTime.parse("2030-10-27T02:30:00").toInstant(PARIS));
  }

  @Test
  void garbage_isRefused() {
    assertThrows(DateTimeException.class, () -> EventDateTime.parse("next saturday"));
    assertThrows(DateTimeException.class, () -> EventDateTime.parse("2030-06-02"));
  }

  @Test
  void aNullValue_givesNoInstant() {
    assertNull(EventDateTime.toInstant(null, PARIS));
  }

  // ─── JSON ─────────────────────────────────────────────────────────────────

  record Body(EventDateTime dateTime) {}

  @Test
  void json_readsBothForms() throws Exception {
    assertEquals(
        EventDateTime.local(LocalDateTime.parse("2030-06-02T08:00:00")),
        mapper.readValue("{\"dateTime\":\"2030-06-02T08:00:00\"}", Body.class).dateTime());
    assertEquals(
        EventDateTime.legacy(Instant.parse("2030-06-02T06:00:00Z")),
        mapper.readValue("{\"dateTime\":\"2030-06-02T06:00:00Z\"}", Body.class).dateTime());
  }

  /** An InvalidFormatException: the resource layer answers 400, as for any malformed body. */
  @Test
  void json_garbage_isAnInvalidFormat() {
    assertThrows(
        InvalidFormatException.class,
        () -> mapper.readValue("{\"dateTime\":\"tomorrow\"}", Body.class));
  }

  @Test
  void json_writesTheFormItWasGiven() throws Exception {
    assertEquals(
        "{\"dateTime\":\"2030-06-02T08:00\"}",
        mapper.writeValueAsString(
            new Body(EventDateTime.local(LocalDateTime.parse("2030-06-02T08:00:00")))));
    assertEquals(
        "{\"dateTime\":\"2030-06-02T06:00:00Z\"}",
        mapper.writeValueAsString(
            new Body(EventDateTime.legacy(Instant.parse("2030-06-02T06:00:00Z")))));
  }
}
