package fr.pedalons.infrastructure.filetype;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.AssetType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@QuarkusTest
class FileTypeDetectorTest extends AbstractBaseTest {

  @Inject FileTypeDetector detector;

  @TempDir Path tempDir;

  private File pngFile() {
    return new File("src/test/resources/image.png");
  }

  private File gpxFile() {
    return new File("src/test/resources/example.gpx");
  }

  private File emptyGpxFile() {
    return new File("src/test/resources/empty.gpx");
  }

  private File writeText(String name, String content) throws IOException {
    Path path = tempDir.resolve(name);
    Files.writeString(path, content, StandardCharsets.UTF_8);
    return path.toFile();
  }

  @Nested
  class ResolveMime {

    @Test
    void pngReturnsImagePngMime() {
      DetectedFileType detected = detector.detect(pngFile(), "image.png");
      assertEquals("png", detected.label());
      assertEquals("image/png", detected.mimeType());
    }

    @Test
    void xmlLabelWithGpxExtensionPrefersGpxMime() {
      DetectedFileType detected = detector.detect(gpxFile(), "example.gpx");
      assertEquals("application/gpx+xml", detected.mimeType());
      assertTrue(
          Set.of("xml", "gpx", "txt").contains(detected.label()),
          "expected text-like label, got " + detected.label());
    }

    @Test
    void xmlLabelWithXmlExtensionPrefersXmlMime() {
      DetectedFileType detected = detector.detect(gpxFile(), "renamed.xml");
      assertEquals("application/xml", detected.mimeType());
    }

    @Test
    void txtLabelWithJsonExtensionPrefersJsonMime() throws IOException {
      File textFile =
          writeText(
              "data.json",
              "This is plain text content long enough for Magika to classify it as txt"
                  + " rather than something more specific.");
      DetectedFileType detected = detector.detect(textFile, "data.json");
      assertEquals("application/json", detected.mimeType());
    }

    @Test
    void emptyLabelFallsBackToExtensionMime() throws IOException {
      File empty = writeText("blank.gpx", "");
      DetectedFileType detected = detector.detect(empty, "blank.gpx");
      assertEquals("application/gpx+xml", detected.mimeType());
    }

    @Test
    void unmappedLabelWithoutExtensionReturnsOctetStream() throws IOException {
      File noExt = writeText("noext", "");
      DetectedFileType detected = detector.detect(noExt, null);
      assertEquals("application/octet-stream", detected.mimeType());
    }
  }

  @Nested
  class MatchesByExtensionFallback {

    @Test
    void gpxExtensionRescuesNonAllowlistLabel() {
      assertDoesNotThrow(
          () -> detector.detectAndValidate(pngFile(), "tricky.gpx", AssetType.ROUTE_ORIGINAL_GPX),
          "extension fallback should accept .gpx filename even when Magika returns a non-allowlist"
              + " label like png");
    }

    @Test
    void fitExtensionRescuesNonAllowlistLabel() {
      assertDoesNotThrow(
          () -> detector.detectAndValidate(pngFile(), "tricky.fit", AssetType.ROUTE_FIT),
          "extension fallback should accept .fit filename even when Magika returns a non-allowlist"
              + " label like png");
    }

    @Test
    void noExtensionFallbackForImageCategory() {
      PedalonsException ex =
          assertThrows(
              PedalonsException.class,
              () -> detector.detectAndValidate(gpxFile(), "evil.png", AssetType.IMAGE));
      assertEquals(ErrorCode.FILE_TYPE_REJECTED, ex.getErrorCode());
    }

    @Test
    void noExtensionFallbackForAttachmentCategory() throws IOException {
      File htmlFile =
          writeText(
              "evil.html",
              "<!DOCTYPE html>\n<html><head><title>X</title></head>"
                  + "<body><h1>Hello</h1><p>Long enough html content for Magika to detect"
                  + " the html type reliably.</p><script>alert('xss');</script></body></html>");
      PedalonsException ex =
          assertThrows(
              PedalonsException.class,
              () -> detector.detectAndValidate(htmlFile, "evil.html", AssetType.ATTACHMENT));
      assertEquals(ErrorCode.FILE_TYPE_REJECTED, ex.getErrorCode());
    }
  }

  @Nested
  class DetectAndValidate {

    @Test
    void emptyGpxAcceptedByLabelOrExtension() {
      assertDoesNotThrow(
          () ->
              detector.detectAndValidate(
                  emptyGpxFile(), "empty.gpx", AssetType.ROUTE_ORIGINAL_GPX));
    }

    @Test
    void detectionFailureWrappedAsFileDetectionFailed() {
      File missing = tempDir.resolve("does-not-exist.png").toFile();
      PedalonsException ex =
          assertThrows(
              PedalonsException.class,
              () -> detector.detectAndValidate(missing, "missing.png", AssetType.IMAGE));
      assertEquals(ErrorCode.FILE_DETECTION_FAILED, ex.getErrorCode());
    }
  }
}
