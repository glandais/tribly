package fr.pedalons.infrastructure.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Real images laden with metadata, for the re-encoding tests, in {@code
 * src/test/resources/images/metadata}. Each was written by ImageMagick (or pillow-heif and
 * pillow-jxl-plugin for HEIC, AVIF and JPEG XL), then given by exiftool a GPS position, and
 * {@link #SECRET} as artist, copyright, camera model, XMP creator — plus IPTC and a comment for the
 * JPEG, a comment for the GIF. {@code photo.jpg}, 40×20, has the EXIF orientation 6 (rotate 90°
 * clockwise). So a test only has to check the marker is gone.
 */
public final class TestImages {

  /** Present in every piece of metadata the test images carry. */
  public static final String SECRET = "SECRET-48.8566N-2.3522E";

  /** 40×20, EXIF orientation 6: upright, it is 20×40. */
  public static final String JPEG = "photo.jpg";

  /** 40×20, with an alpha channel. */
  public static final String PNG = "photo.png";

  public static final String WEBP = "photo.webp";

  /** Three frames. */
  public static final String GIF = "animated.gif";

  public static final String TIFF = "photo.tiff";
  public static final String HEIC = "photo.heic";
  public static final String AVIF = "photo.avif";

  /** In its ISO BMFF container, which is where exiftool writes metadata. */
  public static final String JXL = "photo.jxl";

  /** {@link #PNG}, metadata and all, as the one image of an icon directory. */
  public static final String ICO = "photo.ico";

  private TestImages() {}

  public static byte[] load(String name) {
    try (InputStream in = TestImages.class.getResourceAsStream("/images/metadata/" + name)) {
      if (in == null) {
        throw new IllegalArgumentException("No test image " + name);
      }
      return in.readAllBytes();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static boolean contains(byte[] data, String needle) {
    byte[] n = needle.getBytes(StandardCharsets.ISO_8859_1);
    outer:
    for (int i = 0; i + n.length <= data.length; i++) {
      for (int j = 0; j < n.length; j++) {
        if (data[i + j] != n[j]) {
          continue outer;
        }
      }
      return true;
    }
    return false;
  }
}
