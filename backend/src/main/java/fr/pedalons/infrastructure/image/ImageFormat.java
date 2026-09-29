package fr.pedalons.infrastructure.image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

/**
 * What an uploaded file is, as far as its metadata is concerned, read from its first bytes — never
 * from the declared content type nor from Magika's label, which a renamed or mislabelled file
 * would get past.
 *
 * <p>Three outcomes:
 *
 * <ul>
 *   <li>{@linkplain #isStrippable() strippable}: {@link ImageMetadataStripper} rewrites the file
 *       without its metadata;
 *   <li>{@linkplain #isRefused() refused}: an image container that carries EXIF (GPS position
 *       included) and that we cannot clean without re-encoding — TIFF and everything built on it
 *       (DNG and most camera raw files), HEIF/HEIC/AVIF, JPEG XL and JPEG 2000 containers. Such a
 *       file is rejected at upload rather than stored with its position;
 *   <li>{@link #OTHER}: not an image container we know of (GPX, PDF, zip, BMP, ICO, SVG…), stored
 *       as it is.
 * </ul>
 *
 * <p>docs/LEDGER_*.md API-43.
 */
public enum ImageFormat {
  JPEG(true, false),
  PNG(true, false),
  WEBP(true, false),
  GIF(true, false),
  TIFF(false, true),
  HEIF(false, true),
  JPEG_XL(false, true),
  JPEG_2000(false, true),
  OTHER(false, false);

  /** How many leading bytes {@link #sniff(byte[])} needs to decide. */
  public static final int SNIFF_LENGTH = 64;

  private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};
  private static final byte[] JXL_CONTAINER = {
    0, 0, 0, 0x0C, 'J', 'X', 'L', ' ', '\r', '\n', (byte) 0x87, '\n'
  };
  private static final byte[] JP2_CONTAINER = {
    0, 0, 0, 0x0C, 'j', 'P', ' ', ' ', '\r', '\n', (byte) 0x87, '\n'
  };

  /**
   * ISO base media brands of still images: HEIF/HEIC (and their sequences), AVIF, and Canon's CR3
   * raw. A video ({@code isom}, {@code mp42}, {@code qt  }…) is not in the list.
   */
  private static final Set<String> HEIF_BRANDS =
      Set.of(
          "heic", "heix", "hevc", "hevx", "heim", "heis", "hevm", "hevs", "mif1", "mif2", "msf1",
          "avif", "avis", "avio", "crx ");

  private final boolean strippable;
  private final boolean refused;

  ImageFormat(boolean strippable, boolean refused) {
    this.strippable = strippable;
    this.refused = refused;
  }

  /** {@link ImageMetadataStripper} knows how to remove this format's metadata. */
  public boolean isStrippable() {
    return strippable;
  }

  /** An image that carries metadata we cannot remove: never store it. */
  public boolean isRefused() {
    return refused;
  }

  /** Reads the format from the first bytes of {@code file}. */
  public static ImageFormat sniff(Path file) throws IOException {
    try (InputStream in = Files.newInputStream(file)) {
      return sniff(in.readNBytes(SNIFF_LENGTH));
    }
  }

  /**
   * Reads the format from the first bytes of a file ({@link #SNIFF_LENGTH} are enough; fewer are
   * accepted, a file that short being {@link #OTHER} or a truncated image).
   */
  public static ImageFormat sniff(byte[] head) {
    int n = head.length;
    if (n >= 3 && u(head, 0) == 0xFF && u(head, 1) == 0xD8 && u(head, 2) == 0xFF) {
      return JPEG;
    }
    if (startsWith(head, PNG_SIGNATURE)) {
      return PNG;
    }
    if (n >= 6 && (ascii(head, 0, 6).equals("GIF87a") || ascii(head, 0, 6).equals("GIF89a"))) {
      return GIF;
    }
    if (n >= 12 && ascii(head, 0, 4).equals("RIFF") && ascii(head, 8, 4).equals("WEBP")) {
      return WEBP;
    }
    if (n >= 4) {
      String magic = ascii(head, 0, 4);
      // Classic TIFF and BigTIFF, in both byte orders
      if (magic.equals("II*\0")
          || magic.equals("MM\0*")
          || magic.equals("II+\0")
          || magic.equals("MM\0+")) {
        return TIFF;
      }
    }
    if (startsWith(head, JXL_CONTAINER)) {
      return JPEG_XL;
    }
    if (startsWith(head, JP2_CONTAINER)) {
      return JPEG_2000;
    }
    if (isHeif(head)) {
      return HEIF;
    }
    return OTHER;
  }

  /** An ISO base media {@code ftyp} box whose major or one compatible brand is a still image. */
  private static boolean isHeif(byte[] head) {
    if (head.length < 12 || !ascii(head, 4, 4).equals("ftyp")) {
      return false;
    }
    long boxSize =
        ((long) u(head, 0) << 24) | ((long) u(head, 1) << 16) | (u(head, 2) << 8) | u(head, 3);
    int end = (int) Math.min(boxSize, head.length);
    if (HEIF_BRANDS.contains(ascii(head, 8, 4))) {
      return true;
    }
    // major brand (4) + minor version (4), then the compatible brands
    for (int i = 16; i + 4 <= end; i += 4) {
      if (HEIF_BRANDS.contains(ascii(head, i, 4))) {
        return true;
      }
    }
    return false;
  }

  private static boolean startsWith(byte[] head, byte[] prefix) {
    return head.length >= prefix.length
        && Arrays.equals(head, 0, prefix.length, prefix, 0, prefix.length);
  }

  private static String ascii(byte[] b, int offset, int length) {
    return new String(b, offset, length, StandardCharsets.ISO_8859_1);
  }

  private static int u(byte[] b, int i) {
    return b[i] & 0xFF;
  }
}
