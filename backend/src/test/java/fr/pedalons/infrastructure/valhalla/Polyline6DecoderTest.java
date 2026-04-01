package fr.pedalons.infrastructure.valhalla;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class Polyline6DecoderTest {

  private static final double DELTA = 1e-6;

  @Test
  void decode_shouldReturnEmptyListForNullInput() {
    List<double[]> result = Polyline6Decoder.decode(null);

    assertTrue(result.isEmpty());
  }

  @Test
  void decode_shouldReturnEmptyListForEmptyString() {
    List<double[]> result = Polyline6Decoder.decode("");

    assertTrue(result.isEmpty());
  }

  @Test
  void decode_shouldDecodeSinglePoint() {
    // Encode (lat=38.5, lon=-120.2) at precision 6:
    // lat=38500000 -> zigzag=77000000, lon=-120200000 -> zigzag=240399999
    String encoded = encode(new double[][] {{-120.2, 38.5}});

    List<double[]> result = Polyline6Decoder.decode(encoded);

    assertEquals(1, result.size());
    assertEquals(-120.2, result.get(0)[0], DELTA); // lon
    assertEquals(38.5, result.get(0)[1], DELTA); // lat
  }

  @Test
  void decode_shouldDecodeMultiplePoints() {
    double[][] points = {{2.3522, 48.8566}, {2.2945, 48.8584}, {2.3364, 48.8606}};
    String encoded = encode(points);

    List<double[]> result = Polyline6Decoder.decode(encoded);

    assertEquals(3, result.size());
    for (int i = 0; i < points.length; i++) {
      assertEquals(points[i][0], result.get(i)[0], DELTA, "lon at index " + i);
      assertEquals(points[i][1], result.get(i)[1], DELTA, "lat at index " + i);
    }
  }

  @Test
  void decode_shouldHandleNegativeCoordinates() {
    // Southern and western hemisphere
    double[][] points = {{-43.1729, -22.9068}, {-73.9857, 40.7484}};
    String encoded = encode(points);

    List<double[]> result = Polyline6Decoder.decode(encoded);

    assertEquals(2, result.size());
    assertEquals(-43.1729, result.get(0)[0], DELTA);
    assertEquals(-22.9068, result.get(0)[1], DELTA);
    assertEquals(-73.9857, result.get(1)[0], DELTA);
    assertEquals(40.7484, result.get(1)[1], DELTA);
  }

  @Test
  void decode_shouldOutputLonLatOrder() {
    // Paris: lat ~48.8, lon ~2.3
    double[][] points = {{2.3522, 48.8566}};
    String encoded = encode(points);

    List<double[]> result = Polyline6Decoder.decode(encoded);

    double lon = result.get(0)[0];
    double lat = result.get(0)[1];
    // lon should be small (~2), lat should be large (~48)
    assertTrue(lon < 10, "index 0 should be lon (small value), got " + lon);
    assertTrue(lat > 40, "index 1 should be lat (large value), got " + lat);
  }

  @Test
  void decode_shouldHandleZeroCoordinates() {
    double[][] points = {{0.0, 0.0}};
    String encoded = encode(points);

    List<double[]> result = Polyline6Decoder.decode(encoded);

    assertEquals(1, result.size());
    assertEquals(0.0, result.get(0)[0], DELTA);
    assertEquals(0.0, result.get(0)[1], DELTA);
  }

  @Test
  void decode_shouldRoundTripWithEncoder() {
    // A realistic route segment
    double[][] route = {
      {-1.6778, 48.1173}, // Rennes
      {-1.5534, 47.2184}, // Nantes
      {-0.5792, 44.8378}, // Bordeaux
    };
    String encoded = encode(route);

    List<double[]> result = Polyline6Decoder.decode(encoded);

    assertEquals(route.length, result.size());
    for (int i = 0; i < route.length; i++) {
      assertEquals(route[i][0], result.get(i)[0], DELTA);
      assertEquals(route[i][1], result.get(i)[1], DELTA);
    }
  }

  /** Reference encoder for polyline6 format — mirrors the decoder logic in reverse. */
  private static String encode(double[][] lonLatPoints) {
    StringBuilder sb = new StringBuilder();
    int prevLat = 0;
    int prevLon = 0;
    for (double[] point : lonLatPoints) {
      int lat = (int) Math.round(point[1] * 1e6);
      int lon = (int) Math.round(point[0] * 1e6);
      encodeValue(sb, lat - prevLat);
      encodeValue(sb, lon - prevLon);
      prevLat = lat;
      prevLon = lon;
    }
    return sb.toString();
  }

  private static void encodeValue(StringBuilder sb, int value) {
    int v = value < 0 ? ~(value << 1) : (value << 1);
    while (v >= 0x20) {
      sb.append((char) (((v & 0x1f) | 0x20) + 63));
      v >>= 5;
    }
    sb.append((char) (v + 63));
  }
}
