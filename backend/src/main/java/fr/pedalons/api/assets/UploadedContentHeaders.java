package fr.pedalons.api.assets;

import jakarta.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

/**
 * How a file somebody uploaded is served: from the application's own origin, so it must never run
 * there as a page (docs/SECURITY_AUDIT.md H2, docs/LEDGER_*.md SEC-1).
 *
 * <ul>
 *   <li><b>Only what cannot carry script opens in the browser</b> — raster images and PDF. Anything
 *       else, whatever its declared type (an SVG or an XML document stored before uploads refused
 *       them, a GPX, an office file), is sent as an {@code attachment}: the browser saves it
 *       instead of rendering it.
 *   <li><b>{@code nosniff} on every response</b>, so no browser second-guesses the content type
 *       into something it renders.
 *   <li><b>A sandboxing CSP on everything but PDF</b>: even rendered, the document gets an opaque
 *       origin, no script and nothing to load. Not on PDF, where it keeps the browsers' own viewer
 *       from starting; a PDF's scripts run in that viewer, not in the application's origin.
 * </ul>
 */
final class UploadedContentHeaders {

  /** The types a browser may display in place of the application. */
  static final Set<String> INLINE_TYPES =
      Set.of("image/png", "image/jpeg", "image/gif", "image/webp", "image/avif", "application/pdf");

  static final String SANDBOX_CSP =
      "default-src 'none'; img-src 'self'; style-src 'unsafe-inline'; sandbox";

  private UploadedContentHeaders() {}

  /** The headers of a download of the original file. */
  static Response.ResponseBuilder download(
      Response.ResponseBuilder response, String contentType, String fileName) {
    String type = baseType(contentType);
    String disposition = INLINE_TYPES.contains(type) ? "inline" : "attachment";
    response
        .header("Content-Disposition", disposition + "; filename*=UTF-8''" + encode(fileName))
        .header("X-Content-Type-Options", "nosniff");
    if (!"application/pdf".equals(type)) {
      response.header("Content-Security-Policy", SANDBOX_CSP);
    }
    return response;
  }

  /**
   * The headers of a resized image. imgproxy answers with a raster image, or with an SVG — cleaned
   * of its scripts — for one stored before uploads refused them: served sandboxed either way.
   */
  static Response.ResponseBuilder image(Response.ResponseBuilder response) {
    return response
        .header("X-Content-Type-Options", "nosniff")
        .header("Content-Security-Policy", SANDBOX_CSP);
  }

  /** {@code image/svg+xml; charset=utf-8} → {@code image/svg+xml}. */
  private static String baseType(String contentType) {
    int semicolon = contentType.indexOf(';');
    return (semicolon < 0 ? contentType : contentType.substring(0, semicolon))
        .trim()
        .toLowerCase(Locale.ROOT);
  }

  /** RFC 5987 {@code ext-value}: percent-encoded UTF-8, spaces as {@code %20}. */
  private static String encode(String fileName) {
    return URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        .replace("+", "%20")
        .replace("*", "%2A");
  }
}
