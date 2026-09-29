package fr.pedalons.infrastructure.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.infrastructure.image.TestImages;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * docs/LEDGER_*.md API-43: {@link StorageService#store} is the choke point every file goes
 * through, so it is where images lose their metadata — whoever the caller.
 */
@QuarkusTest
class StorageMetadataStrippingTest extends AbstractBaseTest {

  @Inject StorageService storageService;

  private final String key = "test/metadata/" + UUID.randomUUID();

  @AfterEach
  void cleanup() {
    if (storageService.exists(key)) {
      storageService.delete(key);
    }
  }

  private void store(byte[] content, String contentType) {
    storageService.store(key, new ByteArrayInputStream(content), contentType, content.length);
  }

  private byte[] stored() throws IOException {
    try (InputStream in = storageService.retrieve(key)) {
      return in.readAllBytes();
    }
  }

  @Test
  void stripsAnImageWhateverItsDeclaredType() throws IOException {
    byte[] photo = TestImages.jpeg(6);

    store(photo, "application/octet-stream");

    byte[] stored = stored();
    assertFalse(TestImages.contains(stored, TestImages.SECRET));
    assertTrue(TestImages.contains(stored, "Exif"), "orientation kept");
    assertEquals(stored.length, storageService.size(key));
  }

  @Test
  void storesAnythingElseAsItIs() throws IOException {
    byte[] gpx =
        "<?xml version=\"1.0\"?><gpx><trk><trkseg/></trk></gpx>".getBytes(StandardCharsets.UTF_8);

    store(gpx, "application/gpx+xml");

    assertArrayEquals(gpx, stored());
  }

  @Test
  void refusesAnImageItCannotStrip() {
    byte[] tiff = TestImages.exifTiff(1); // a TIFF header is all it takes

    PedalonsException ex = assertThrows(PedalonsException.class, () -> store(tiff, "image/tiff"));

    assertEquals(ErrorCode.FILE_TYPE_REJECTED, ex.getErrorCode());
    assertFalse(storageService.exists(key));
  }

  @Test
  void refusesABrokenImage() {
    byte[] broken = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1, 0x10, 0x00, 'E', 'x'};

    PedalonsException ex = assertThrows(PedalonsException.class, () -> store(broken, "image/jpeg"));

    assertEquals(ErrorCode.INVALID_FORMAT, ex.getErrorCode());
    assertFalse(storageService.exists(key));
  }
}
