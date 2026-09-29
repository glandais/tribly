package fr.pedalons.util;

import static org.junit.jupiter.api.Assertions.*;

import com.garmin.fit.Decode;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.RecordMesgListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * docs/LEDGER_*.md API-44: assertions that a stored GPX or FIT file carries geometry only, for the
 * fixture {@link #ACTIVITY} — an activity export with a timestamp, heart rate, cadence, power and
 * temperature on every point, and an author, e-mail and device serial in its metadata.
 */
public final class GpxPrivacyAssertions {

  public static final String ACTIVITY = "src/test/resources/activity_with_sensors.gpx";

  private GpxPrivacyAssertions() {}

  public static Path activityGpx() {
    return new File(ACTIVITY).toPath();
  }

  /** A GPX serialization with nothing of the fixture's clock, sensors or author left in it. */
  public static void assertGpxHasNoPersonalData(String xml) {
    assertFalse(xml.contains("2025-"), "a recorded timestamp survived");
    assertFalse(xml.contains("<extensions>"), "a sensor extension survived");
    assertFalse(xml.contains("hr>"), "heart rate survived");
    assertFalse(xml.contains("cad>"), "cadence survived");
    assertFalse(xml.contains("atemp>"), "temperature survived");
    assertFalse(xml.contains("power>"), "power survived");
    assertFalse(xml.contains("Jane"), "the author survived");
    assertFalse(xml.contains("example.com"), "the author's e-mail or link survived");
    assertFalse(xml.contains("3412345678"), "the device's serial number survived");
  }

  /**
   * A FIT course with no recorded time, power, heart rate or cadence on any record. A sanitized
   * point's time is the epoch placeholder, which FIT (epoch 1989) cannot represent and decodes as
   * absent — the same as for any route drawn or imported without times.
   */
  public static void assertFitHasNoPersonalData(byte[] fit) throws Exception {
    Decode decode = new Decode();
    MesgBroadcaster broadcaster = new MesgBroadcaster(decode);
    List<RecordMesg> records = new ArrayList<>();
    broadcaster.addListener((RecordMesgListener) records::add);
    decode.read(new ByteArrayInputStream(fit), broadcaster, broadcaster);

    assertFalse(records.isEmpty(), "the FIT course has no record");
    Instant recorded = Instant.parse("2000-01-01T00:00:00Z");
    for (RecordMesg record : records) {
      assertTrue(
          record.getTimestamp() == null
              || record.getTimestamp().getDate().toInstant().isBefore(recorded),
          "a recorded timestamp survived: " + record.getTimestamp());
      assertNull(record.getPower(), "power survived");
      assertNull(record.getHeartRate(), "heart rate survived");
      assertNull(record.getCadence(), "cadence survived");
      assertNull(record.getTemperature(), "temperature survived");
    }
  }
}
