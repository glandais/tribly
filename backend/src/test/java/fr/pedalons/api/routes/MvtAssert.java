package fr.pedalons.api.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;

/**
 * Assertions on Mapbox vector tiles.
 *
 * <p>A tile is protobuf, but feature property values are stored as plain length-prefixed strings,
 * so a substring search is enough to tell which routes made it into the tile. That is all these
 * tests care about — the visibility filter, not the geometry encoding.
 */
final class MvtAssert {

  private static final String MVT_CONTENT_TYPE = "application/vnd.mapbox-vector-tile";

  private MvtAssert() {}

  /** Asserts the response is a vector tile and returns its bytes as a searchable string. */
  static String decode(Response response) {
    io.restassured.response.ExtractableResponse<Response> extracted =
        response.then().statusCode(200).extract();
    assertEquals(MVT_CONTENT_TYPE, extracted.contentType());
    return new String(extracted.asByteArray(), StandardCharsets.ISO_8859_1);
  }

  static void assertContainsSlugs(String tile, String... slugs) {
    for (String slug : slugs) {
      assertTrue(tile.contains(slug), "tile should contain route '" + slug + "'");
    }
  }

  static void assertMissingSlugs(String tile, String... slugs) {
    for (String slug : slugs) {
      assertFalse(tile.contains(slug), "tile leaked route '" + slug + "'");
    }
  }
}
