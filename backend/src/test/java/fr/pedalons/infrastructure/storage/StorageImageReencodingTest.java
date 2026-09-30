package fr.pedalons.infrastructure.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.infrastructure.image.ImageFormat;
import fr.pedalons.infrastructure.image.TestImages;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

/**
 * docs/LEDGER_*.md API-43: {@link StorageService#store} is the choke point every file goes
 * through, so it is where images are re-encoded without their metadata — whoever the caller.
 */
@QuarkusTest
class StorageImageReencodingTest extends AbstractBaseTest {

  @Inject StorageService storageService;
  @Inject S3Client s3Client;

  @ConfigProperty(name = "storage.bucket", defaultValue = "pedalons")
  String bucket;

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

  private String storedContentType() {
    return s3Client
        .headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build())
        .contentType();
  }

  private boolean originalsLeft() {
    return s3Client
            .listObjectsV2(
                ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(S3StorageService.REENCODE_PREFIX)
                    .build())
            .keyCount()
        > 0;
  }

  @ParameterizedTest
  @CsvSource({
    "photo.jpg, application/octet-stream, JPEG",
    "photo.png, image/png, PNG",
    "photo.webp, image/webp, WEBP",
    "animated.gif, image/gif, GIF",
    "photo.tiff, image/tiff, JPEG",
    "photo.heic, image/heic, JPEG",
    "photo.avif, image/avif, JPEG",
    "photo.jxl, image/jxl, JPEG",
    // docs/LEDGER_*.md API-48
    "photo.ico, image/x-icon, PNG",
  })
  void reencodesAnImageWithoutItsMetadata(String testImage, String declaredType, String format)
      throws IOException {
    byte[] image = TestImages.load(testImage);
    assertTrue(TestImages.contains(image, TestImages.SECRET), "the test image carries metadata");

    store(image, declaredType);

    byte[] stored = stored();
    assertFalse(TestImages.contains(stored, TestImages.SECRET), "metadata left in storage");
    assertEquals(ImageFormat.valueOf(format), ImageFormat.sniff(stored));
    assertEquals(ImageFormat.valueOf(format).storedMimeType(), storedContentType());
    assertEquals(stored.length, storageService.size(key));
    assertFalse(originalsLeft(), "the original must not stay in the bucket");
  }

  @Test
  void rotatesAPhotoUpright() throws IOException {
    store(TestImages.load(TestImages.JPEG), "image/jpeg");

    BufferedImage upright = ImageIO.read(new ByteArrayInputStream(stored()));
    assertEquals(20, upright.getWidth());
    assertEquals(40, upright.getHeight());
  }

  @Test
  void keepsTheFramesOfAnAnimation() throws IOException {
    store(TestImages.load(TestImages.GIF), "image/gif");

    try (var in = ImageIO.createImageInputStream(new ByteArrayInputStream(stored()))) {
      var reader = ImageIO.getImageReadersByFormatName("gif").next();
      reader.setInput(in);
      assertEquals(3, reader.getNumImages(true));
    }
  }

  @Test
  void storesAnythingElseAsItIs() throws IOException {
    byte[] gpx =
        "<?xml version=\"1.0\"?><gpx><trk><trkseg/></trk></gpx>".getBytes(StandardCharsets.UTF_8);

    store(gpx, "application/gpx+xml");

    assertArrayEquals(gpx, stored());
    assertEquals("application/gpx+xml", storedContentType());
  }

  @Test
  void refusesAJpeg2000() {
    byte[] jp2 = {0, 0, 0, 0x0C, 'j', 'P', ' ', ' ', '\r', '\n', (byte) 0x87, '\n', 0, 0, 0, 0};

    PedalonsException ex = assertThrows(PedalonsException.class, () -> store(jp2, "image/jp2"));

    assertEquals(ErrorCode.FILE_TYPE_REJECTED, ex.getErrorCode());
    assertFalse(storageService.exists(key));
  }

  @Test
  void refusesABrokenImage() {
    byte[] broken = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1, 0x10, 0x00, 'E', 'x'};

    PedalonsException ex = assertThrows(PedalonsException.class, () -> store(broken, "image/jpeg"));

    assertEquals(ErrorCode.INVALID_FORMAT, ex.getErrorCode());
    assertFalse(storageService.exists(key));
    assertFalse(originalsLeft(), "the original must not stay in the bucket");
  }
}
