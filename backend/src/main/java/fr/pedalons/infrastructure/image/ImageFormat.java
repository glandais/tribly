package fr.pedalons.infrastructure.image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * What an uploaded file is, as far as its metadata is concerned, read from its first bytes — never
 * from the declared content type nor from Magika's label, which a renamed or mislabelled file
 * would get past.
 *
 * <p>Three outcomes:
 *
 * <ul>
 *   <li>{@linkplain #isReencoded() re-encoded}: storage has imgproxy decode the pixels and write a
 *       fresh file, which carries no metadata. JPEG, PNG, WebP and GIF keep their format; TIFF (and
 *       the raw files built on it), HEIF/HEIC/AVIF and JPEG XL, which browsers and apps do not all
 *       display, become a JPEG; an ICO, whose images can be PNGs with their text and EXIF chunks,
 *       becomes a PNG (docs/LEDGER_*.md API-48);
 *   <li>{@linkplain #isRefused() refused}: a JPEG 2000 container, which carries EXIF and XMP and
 *       that imgproxy cannot read;
 *   <li>{@link #OTHER}: not an image container we know of (GPX, PDF, zip, BMP…), stored as it
 *       is. BMP has no metadata to carry; SVG is refused at upload (docs/LEDGER_*.md SEC-1).
 * </ul>
 *
 * <p>docs/LEDGER_*.md API-43.
 */
public enum ImageFormat {
  JPEG("jpg", "image/jpeg"),
  PNG("png", "image/png"),
  WEBP("webp", "image/webp"),
  GIF("gif", "image/gif"),
  TIFF("jpg", "image/jpeg"),
  HEIF("jpg", "image/jpeg"),
  JPEG_XL("jpg", "image/jpeg"),
  ICO("png", "image/png"),
  JPEG_2000(null, null),
  OTHER(null, null);

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

  private final @Nullable String storedExtension;
  private final @Nullable String storedMimeType;

  ImageFormat(@Nullable String storedExtension, @Nullable String storedMimeType) {
    this.storedExtension = storedExtension;
    this.storedMimeType = storedMimeType;
  }

  /** Storage stores a re-encoded copy of this image, never the uploaded bytes. */
  public boolean isReencoded() {
    return storedExtension != null;
  }

  /** An image that carries metadata and that cannot be re-encoded: never store it. */
  public boolean isRefused() {
    return this == JPEG_2000;
  }

  /** The extension of the re-encoded file, which is also the format imgproxy is asked for. */
  public String storedExtension() {
    if (storedExtension == null) {
      throw new IllegalStateException(this + " is not re-encoded");
    }
    return storedExtension;
  }

  /** The content type of the re-encoded file. */
  public String storedMimeType() {
    if (storedMimeType == null) {
      throw new IllegalStateException(this + " is not re-encoded");
    }
    return storedMimeType;
  }

  /**
   * {@code fileName} with the extension of the re-encoded file when it announced another format
   * ({@code IMG_0042.HEIC} → {@code IMG_0042.jpg}), unchanged otherwise.
   */
  public String storedFileName(String fileName) {
    if (!isReencoded()) {
      return fileName;
    }
    int dot = fileName.lastIndexOf('.');
    String base = dot > 0 ? fileName.substring(0, dot) : fileName;
    String extension = dot > 0 ? fileName.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
    boolean matches =
        extension.equals(storedExtension)
            || (storedExtension.equals("jpg") && extension.equals("jpeg"));
    return matches ? fileName : base + "." + storedExtension;
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
    // A bare JPEG XL codestream, or its ISO BMFF container
    if ((n >= 2 && u(head, 0) == 0xFF && u(head, 1) == 0x0A) || startsWith(head, JXL_CONTAINER)) {
      return JPEG_XL;
    }
    if (startsWith(head, JP2_CONTAINER)) {
      return JPEG_2000;
    }
    if (isHeif(head)) {
      return HEIF;
    }
    if (isIco(head)) {
      return ICO;
    }
    return OTHER;
  }

  /**
   * An icon directory: reserved 0, type 1, at least one image, and the first entry's reserved byte
   * 0 — enough to tell it from the other formats that open with {@code 00 00 01}.
   */
  private static boolean isIco(byte[] head) {
    return head.length >= 22
        && u(head, 0) == 0
        && u(head, 1) == 0
        && u(head, 2) == 1
        && u(head, 3) == 0
        && (u(head, 4) | (u(head, 5) << 8)) > 0
        && u(head, 9) == 0;
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
