package fr.pedalons.service.timezone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/**
 * The backend copy of the zone naming (docs/LEDGER_*.md API-60): the same cases as {@code
 * frontend/src/utils/zoneLabel.test.ts}, so the three copies stay in step.
 */
class ZoneLabelsTest {

  @Test
  void cityName_isTheLastSegmentOfTheIdentifier() {
    assertEquals("Tokyo", ZoneLabels.cityName("Asia/Tokyo", "fr"));
    assertEquals("Paris", ZoneLabels.cityName("Europe/Paris", "en"));
    assertEquals("Buenos Aires", ZoneLabels.cityName("America/Argentina/Buenos_Aires", "en"));
    assertEquals("New York", ZoneLabels.cityName("America/New_York", "fr"));
  }

  @Test
  void cityName_translatesTheCommonCities_inFrenchOnly() {
    assertEquals("Londres", ZoneLabels.cityName("Europe/London", "fr"));
    assertEquals("Lisbonne", ZoneLabels.cityName("Europe/Lisbon", "fr-FR"));
    assertEquals("Le Caire", ZoneLabels.cityName("Africa/Cairo", "fr"));
    assertEquals("La Réunion", ZoneLabels.cityName("Indian/Reunion", "fr"));
    assertEquals("London", ZoneLabels.cityName("Europe/London", "en"));
    assertEquals("Cairo", ZoneLabels.cityName("Africa/Cairo", "en"));
  }

  @Test
  void cityName_namesUtcAndTheFixedOffsets() {
    assertEquals("UTC", ZoneLabels.cityName("UTC", "fr"));
    assertEquals("UTC", ZoneLabels.cityName("Etc/UTC", "en"));
    assertEquals("UTC", ZoneLabels.cityName("Etc/GMT", "fr"));
    assertEquals("UTC", ZoneLabels.cityName("Etc/GMT+0", "fr"));
    // POSIX inverts the sign: Etc/GMT-9 is nine hours ahead of UTC.
    assertEquals("UTC+9", ZoneLabels.cityName("Etc/GMT-9", "fr"));
    assertEquals("UTC-5", ZoneLabels.cityName("Etc/GMT+5", "en"));
  }

  @Test
  void cityOf_buildsTheFrenchOfPhrase() {
    assertEquals("de Tokyo", ZoneLabels.cityOf("Asia/Tokyo", "fr"));
    assertEquals("d'Athènes", ZoneLabels.cityOf("Europe/Athens", "fr"));
    assertEquals("d'Alger", ZoneLabels.cityOf("Africa/Algiers", "fr"));
    assertEquals("d'Istanbul", ZoneLabels.cityOf("Europe/Istanbul", "fr"));
    assertEquals("d'Helsinki", ZoneLabels.cityOf("Europe/Helsinki", "fr"));
    assertEquals("de Hong Kong", ZoneLabels.cityOf("Asia/Hong_Kong", "fr"));
    assertEquals("du Caire", ZoneLabels.cityOf("Africa/Cairo", "fr"));
    assertEquals("des Açores", ZoneLabels.cityOf("Atlantic/Azores", "fr"));
    assertEquals("des Canaries", ZoneLabels.cityOf("Atlantic/Canary", "fr-FR"));
    assertEquals("de La Réunion", ZoneLabels.cityOf("Indian/Reunion", "fr"));
    assertEquals("UTC+9", ZoneLabels.cityOf("Etc/GMT-9", "fr"));
  }

  @Test
  void cityOf_isTheBareCity_inEnglish() {
    assertEquals("Athens", ZoneLabels.cityOf("Europe/Athens", "en"));
    assertEquals("Cairo", ZoneLabels.cityOf("Africa/Cairo", "en"));
    assertEquals("Azores", ZoneLabels.cityOf("Atlantic/Azores", "en"));
  }

  @Test
  void sameOffsetAt_comparesOffsets_notIdentifiers() {
    Instant summer = Instant.parse("2026-07-11T06:00:00Z");
    Instant winter = Instant.parse("2026-01-11T06:00:00Z");
    ZoneId paris = ZoneId.of("Europe/Paris");
    assertTrue(ZoneLabels.sameOffsetAt(summer, paris, ZoneId.of("Europe/Brussels")));
    assertFalse(ZoneLabels.sameOffsetAt(summer, paris, ZoneId.of("Asia/Tokyo")));
    assertFalse(ZoneLabels.sameOffsetAt(summer, paris, ZoneId.of("Europe/London")));
    // London is UTC in winter, Lisbon too.
    assertTrue(
        ZoneLabels.sameOffsetAt(winter, ZoneId.of("Europe/London"), ZoneId.of("Europe/Lisbon")));
  }
}
