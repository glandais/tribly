package fr.pedalons.service.route;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.util.GpxPrivacyAssertions;
import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.Point;
import io.github.glandais.gpx.io.read.GPXFileReader;
import io.github.glandais.gpx.io.write.GPXFileWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * docs/LEDGER_*.md API-44: what survives {@link GpxSanitizer} — geometry and waypoint names — and
 * what does not — timestamps, heart rate, cadence, power, temperature, document metadata. A plain
 * unit test: the reader and writer are the library's, no Quarkus needed.
 */
class GpxSanitizerTest {

  static GPX parseActivity() throws Exception {
    try (InputStream is = new FileInputStream(GpxPrivacyAssertions.ACTIVITY)) {
      return new GPXFileReader().parseGPX(is);
    }
  }

  /** Both serializations the pipeline stores: {@code filtered = true} is the one with extensions. */
  static String write(GPX gpx, boolean filtered) throws Exception {
    StringWriter writer = new StringWriter();
    new GPXFileWriter().writeGPX(gpx, writer, filtered);
    return writer.toString();
  }

  @Test
  void fixtureReallyCarriesTheDataToStrip() throws Exception {
    // Guards the test itself: if the reader stopped picking these up, every other test here
    // would pass for the wrong reason.
    String raw = write(parseActivity(), true);

    assertTrue(raw.contains("<time>2025-11-22T07:00:00Z</time>"));
    assertTrue(raw.contains("<gpxtpx:hr>"));
    assertTrue(raw.contains("<power>"));
  }

  @Test
  void sanitize_stripsTimestampsAndSensorsFromBothSerializations() throws Exception {
    GPX gpx = parseActivity();

    GpxSanitizer.sanitize(gpx);

    GpxPrivacyAssertions.assertGpxHasNoPersonalData(write(gpx, false));
    GpxPrivacyAssertions.assertGpxHasNoPersonalData(write(gpx, true));
  }

  @Test
  void sanitize_keepsGeometry() throws Exception {
    GPX raw = parseActivity();
    List<Point> before = raw.paths().getFirst().getPoints();
    GPX gpx = parseActivity();

    GpxSanitizer.sanitize(gpx);

    List<Point> after = gpx.paths().getFirst().getPoints();
    assertEquals(before.size(), after.size());
    for (int i = 0; i < before.size(); i++) {
      assertEquals(before.get(i).getLat(), after.get(i).getLat());
      assertEquals(before.get(i).getLon(), after.get(i).getLon());
      assertEquals(before.get(i).getEle(), after.get(i).getEle());
      assertNull(after.get(i).getHeartRate());
      assertNull(after.get(i).getPower());
      assertEquals(Instant.EPOCH, after.get(i).getInstant());
    }
    assertEquals(
        raw.paths().getFirst().getDist(), gpx.paths().getFirst().getDist(), 0.001, "distance");
  }

  @Test
  void sanitize_keepsNamesAndWaypoints() throws Exception {
    GPX gpx = parseActivity();

    GpxSanitizer.sanitize(gpx);

    assertEquals("Morning Ride", gpx.name());
    assertEquals("Morning Ride", gpx.paths().getFirst().getName());
    assertEquals(1, gpx.waypoints().size());
    assertEquals("Café stop", gpx.waypoints().getFirst().name());
    assertEquals(47.21, gpx.waypoints().getFirst().point().getLatDeg(), 1e-9);
    assertEquals(-1.545, gpx.waypoints().getFirst().point().getLonDeg(), 1e-9);
    assertTrue(write(gpx, true).contains("<wpt lat=\"47.21\" lon=\"-1.545\">"));
  }

  @Test
  void sanitize_isIdempotent() throws Exception {
    GPX gpx = parseActivity();
    GpxSanitizer.sanitize(gpx);
    String once = write(gpx, true);

    GpxSanitizer.sanitize(gpx);

    assertEquals(once, write(gpx, true));
  }

  @Test
  void sanitize_acceptsAGpxWithImmutableEmptyWaypoints() {
    // What GpxProcessingService.fromPoints builds for a drawn route.
    GPXPath path = new GPXPath("drawn", io.github.glandais.gpx.data.GPXPathType.TRACK);
    Point point = new Point();
    point.setLat(Math.toRadians(47.2));
    point.setLon(Math.toRadians(-1.5));
    point.setEle(0.0);
    point.setInstant(null, Instant.EPOCH);
    path.addPoint(point);
    path.computeArrays();
    GPX gpx = new GPX("drawn", List.of(path), List.of());

    assertDoesNotThrow(() -> GpxSanitizer.sanitize(gpx));
    assertEquals(1, gpx.paths().getFirst().getPoints().size());
  }
}
