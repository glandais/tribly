package fr.pedalons.common;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UrlUtilsTest {

  @Test
  void stripTrailingSlash_trimsAndDropsEverySlash() {
    assertEquals("https://a.example", UrlUtils.stripTrailingSlash(" https://a.example// "));
    assertEquals("https://a.example/x", UrlUtils.stripTrailingSlash("https://a.example/x"));
  }

  @Test
  void requireHttps_acceptsHttps_andHttpToLocalhostOnly() {
    assertDoesNotThrow(() -> UrlUtils.requireHttps("https://biketeam.example", "X"));
    assertDoesNotThrow(() -> UrlUtils.requireHttps("http://127.0.0.1:8080", "X"));
    assertDoesNotThrow(() -> UrlUtils.requireHttps("http://localhost", "X"));
    IllegalStateException e =
        assertThrows(
            IllegalStateException.class,
            () -> UrlUtils.requireHttps("http://biketeam.example", "EXPORT_URL"));
    assertTrue(e.getMessage().startsWith("EXPORT_URL"));
    assertThrows(IllegalStateException.class, () -> UrlUtils.requireHttps("ht tp://x", "X"));
  }

  @Test
  void constantTimeEquals_comparesContent() {
    assertTrue(TokenUtils.constantTimeEquals("secret", "secret"));
    assertTrue(!TokenUtils.constantTimeEquals("secret", "secreT"));
    assertTrue(!TokenUtils.constantTimeEquals("secret", "secret-longer"));
  }
}
