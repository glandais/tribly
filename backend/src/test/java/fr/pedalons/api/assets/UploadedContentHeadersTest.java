package fr.pedalons.api.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

/** docs/LEDGER_*.md SEC-1: an uploaded file never runs as a page of the application. */
class UploadedContentHeadersTest {

  private static MultivaluedMap<String, Object> download(String contentType, String fileName) {
    return UploadedContentHeaders.download(Response.ok(), contentType, fileName)
        .build()
        .getHeaders();
  }

  @Test
  void anSvgOrAnXmlIsSavedNeverRendered() {
    for (String type :
        new String[] {"image/svg+xml", "application/xml", "application/gpx+xml", "text/html"}) {
      MultivaluedMap<String, Object> headers = download(type, "f");
      assertTrue(
          headers.getFirst("Content-Disposition").toString().startsWith("attachment;"), type);
      assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"), type);
      assertTrue(headers.getFirst("Content-Security-Policy").toString().contains("sandbox"), type);
    }
  }

  @Test
  void aRasterImageOpensInPlace_sandboxed() {
    MultivaluedMap<String, Object> headers = download("image/png; charset=binary", "photo.png");
    assertTrue(headers.getFirst("Content-Disposition").toString().startsWith("inline;"));
    assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"));
    assertTrue(headers.getFirst("Content-Security-Policy").toString().contains("sandbox"));
  }

  @Test
  void aPdfOpensInPlace_withoutTheCspThatWouldStopTheViewer() {
    MultivaluedMap<String, Object> headers = download("application/pdf", "road book.pdf");
    assertTrue(headers.getFirst("Content-Disposition").toString().startsWith("inline;"));
    assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"));
    assertNull(headers.getFirst("Content-Security-Policy"));
  }

  @Test
  void theFileNameIsEncoded() {
    assertEquals(
        "attachment; filename*=UTF-8''r%C3%A9sum%C3%A9%20%22final%22.docx",
        download("application/octet-stream", "résumé \"final\".docx")
            .getFirst("Content-Disposition"));
  }
}
