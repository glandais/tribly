package fr.pedalons.infrastructure.image;

import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.Nullable;

/**
 * The only EXIF tag an image keeps once stripped: Orientation (0x0112), which says how the camera
 * was held. Dropping it would show every portrait photo taken with a phone lying on its side, since
 * browsers and imgproxy rotate the pixels according to it.
 *
 * <p>Reads the tag from a TIFF structure (the body of an EXIF block) and writes a TIFF structure
 * holding that single tag and nothing else — no sub-IFD, so no GPS, no date, no camera, no
 * thumbnail.
 */
final class ExifOrientation {

  static final byte[] EXIF_HEADER = "Exif\0\0".getBytes(StandardCharsets.ISO_8859_1);

  private static final int TAG_ORIENTATION = 0x0112;
  private static final int TYPE_SHORT = 3;

  private ExifOrientation() {}

  /**
   * The orientation (2 to 8) of an EXIF block, or null when it has none, says "normal" (1), or
   * cannot be read. The block may start with the {@code Exif\0\0} header of JPEG APP1 segments or
   * directly with the TIFF header, as in PNG {@code eXIf} and WebP {@code EXIF} chunks.
   */
  static @Nullable Integer read(byte[] data, int offset, int length) {
    int end = Math.min(data.length, offset + length);
    int tiff = offset;
    if (startsWith(data, tiff, end, EXIF_HEADER)) {
      tiff += EXIF_HEADER.length;
    }
    if (tiff + 8 > end) {
      return null;
    }
    boolean little;
    if (data[tiff] == 'I' && data[tiff + 1] == 'I') {
      little = true;
    } else if (data[tiff] == 'M' && data[tiff + 1] == 'M') {
      little = false;
    } else {
      return null;
    }
    if (u16(data, tiff + 2, little) != 42) {
      return null;
    }
    long ifd0 = u32(data, tiff + 4, little);
    if (ifd0 < 8 || tiff + ifd0 + 2 > end) {
      return null;
    }
    int ifd = (int) (tiff + ifd0);
    int count = u16(data, ifd, little);
    for (int i = 0; i < count; i++) {
      int entry = ifd + 2 + i * 12;
      if (entry + 12 > end) {
        return null;
      }
      if (u16(data, entry, little) == TAG_ORIENTATION) {
        if (u16(data, entry + 2, little) != TYPE_SHORT) {
          return null;
        }
        int value = u16(data, entry + 8, little);
        return value >= 2 && value <= 8 ? value : null;
      }
    }
    return null;
  }

  /**
   * A big-endian TIFF structure whose only entry is Orientation — 26 bytes. Callers prefix it with
   * {@link #EXIF_HEADER} where the container wants one (JPEG APP1).
   */
  static byte[] minimalTiff(int orientation) {
    return new byte[] {
      'M',
      'M',
      0,
      42, // byte order, magic
      0,
      0,
      0,
      8, // IFD0 right after the header
      0,
      1, // one entry
      0x01,
      0x12,
      0,
      TYPE_SHORT,
      0,
      0,
      0,
      1,
      0,
      (byte) orientation,
      0,
      0, // Orientation, SHORT, 1
      0,
      0,
      0,
      0 // no next IFD
    };
  }

  private static boolean startsWith(byte[] data, int offset, int end, byte[] prefix) {
    if (offset + prefix.length > end) {
      return false;
    }
    for (int i = 0; i < prefix.length; i++) {
      if (data[offset + i] != prefix[i]) {
        return false;
      }
    }
    return true;
  }

  private static int u16(byte[] b, int i, boolean little) {
    int b0 = b[i] & 0xFF;
    int b1 = b[i + 1] & 0xFF;
    return little ? (b1 << 8) | b0 : (b0 << 8) | b1;
  }

  private static long u32(byte[] b, int i, boolean little) {
    long v = 0;
    for (int k = 0; k < 4; k++) {
      int shift = little ? 8 * k : 8 * (3 - k);
      v |= (long) (b[i + k] & 0xFF) << shift;
    }
    return v;
  }
}
