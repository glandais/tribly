package fr.pedalons.infrastructure.image;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;
import org.jspecify.annotations.Nullable;

/**
 * Rewrites an image without its metadata — EXIF (GPS position, date, camera, embedded thumbnail),
 * XMP, IPTC/Photoshop blocks, comments, textual chunks — keeping only what decoding and display
 * need.
 *
 * <p><strong>Lossless:</strong> the pixels are never decoded. Each format is walked at the level of
 * its segments or chunks, and the ones that carry metadata are dropped; the compressed image data
 * is copied byte for byte. The result is deterministic, and stripping an already stripped file
 * gives back the same bytes, which is what lets the backfill of old files skip the ones already
 * clean.
 *
 * <p><strong>Orientation</strong> is the one piece of EXIF kept: when the original says the
 * picture must be rotated, the output carries a minimal EXIF block holding that single tag (see
 * {@link ExifOrientation}).
 *
 * <p>An <strong>allowlist</strong> decides what stays, per format — anything unknown goes, so a
 * vendor block nobody listed cannot carry a position through. Bytes after the end of the image
 * (JPEG after EOI, PNG after IEND, GIF after the trailer, WebP after the RIFF size) go too: that is
 * where phones append the video of a "motion photo" and the secondary images of a multi-picture
 * file.
 *
 * <p>docs/LEDGER_*.md API-43.
 */
public final class ImageMetadataStripper {

  private ImageMetadataStripper() {}

  /**
   * Copies {@code in} to {@code out} without its metadata.
   *
   * @param format as {@link ImageFormat#sniff sniffed} from the same bytes; must be {@linkplain
   *     ImageFormat#isStrippable() strippable}
   * @throws MalformedImageException when the structure cannot be walked — never stored as is, the
   *     caller rejects the file
   */
  public static void strip(ImageFormat format, InputStream in, OutputStream out)
      throws IOException {
    // The walkers read byte by byte where the format demands it
    InputStream bufferedIn = new BufferedInputStream(in, BUFFER_SIZE);
    BufferedOutputStream bufferedOut = new BufferedOutputStream(out, BUFFER_SIZE);
    switch (format) {
      case JPEG -> stripJpeg(bufferedIn, bufferedOut);
      case PNG -> stripPng(bufferedIn, bufferedOut);
      case WEBP -> stripWebp(bufferedIn, bufferedOut);
      case GIF -> stripGif(bufferedIn, bufferedOut);
      default -> throw new IllegalArgumentException("Cannot strip metadata from " + format);
    }
    bufferedOut.flush();
  }

  private static final int BUFFER_SIZE = 64 * 1024;

  // ---------------------------------------------------------------------------------------------
  // JPEG: markers, then entropy-coded scans

  private static final int SOI = 0xD8;
  private static final int EOI = 0xD9;
  private static final int SOS = 0xDA;
  private static final int APP0 = 0xE0;
  private static final int APP1 = 0xE1;
  private static final int APP2 = 0xE2;
  private static final int APP14 = 0xEE;
  private static final int APP15 = 0xEF;
  private static final int COM = 0xFE;

  private static void stripJpeg(InputStream in, OutputStream out) throws IOException {
    if (in.read() != 0xFF || in.read() != SOI) {
      throw new MalformedImageException("JPEG does not start with SOI");
    }
    // The header segments are small (tables, frame header, ICC profile): buffer them, so that the
    // orientation, read from an APP1 wherever it sits, can be written before them
    List<byte[]> kept = new ArrayList<>();
    Integer orientation = null;
    int marker;
    while (true) {
      marker = nextMarker(in);
      if (marker == SOS || marker == EOI) {
        break;
      }
      if (isStandalone(marker)) {
        kept.add(new byte[] {(byte) 0xFF, (byte) marker});
        continue;
      }
      byte[] payload = readSegmentPayload(in);
      if (marker == APP1 && orientation == null) {
        orientation = ExifOrientation.read(payload, 0, payload.length);
      }
      byte[] segment = keptJpegSegment(marker, payload);
      if (segment != null) {
        kept.add(segment);
      }
    }

    out.write(0xFF);
    out.write(SOI);
    int next = 0;
    // JFIF wants its APP0 first; EXIF readers find the APP1 right after it
    if (!kept.isEmpty() && (kept.getFirst()[1] & 0xFF) == APP0) {
      out.write(kept.getFirst());
      next = 1;
    }
    if (orientation != null) {
      byte[] tiff = ExifOrientation.minimalTiff(orientation);
      byte[] payload = new byte[ExifOrientation.EXIF_HEADER.length + tiff.length];
      System.arraycopy(ExifOrientation.EXIF_HEADER, 0, payload, 0, 6);
      System.arraycopy(tiff, 0, payload, 6, tiff.length);
      out.write(segment(APP1, payload));
    }
    for (int i = next; i < kept.size(); i++) {
      out.write(kept.get(i));
    }
    if (marker == EOI) {
      out.write(0xFF);
      out.write(EOI);
      return;
    }
    out.write(segment(SOS, readSegmentPayload(in)));
    copyScans(in, out);
  }

  /**
   * Copies the entropy-coded data up to EOI. A progressive JPEG interleaves further segments
   * (tables, more scans) with its data; they go through the same allowlist as the header. What
   * follows EOI is dropped.
   */
  private static void copyScans(InputStream in, OutputStream out) throws IOException {
    int b;
    while ((b = in.read()) != -1) {
      if (b != 0xFF) {
        out.write(b);
        continue;
      }
      int m = in.read();
      while (m == 0xFF) {
        // fill bytes before a marker
        m = in.read();
      }
      if (m == -1) {
        // truncated right after an 0xFF: keep what there is, as a decoder would
        out.write(0xFF);
        return;
      }
      if (m == 0x00 || (m >= 0xD0 && m <= 0xD7)) {
        // stuffed 0xFF, restart marker: entropy data
        out.write(0xFF);
        out.write(m);
        continue;
      }
      if (m == EOI) {
        out.write(0xFF);
        out.write(EOI);
        return;
      }
      if (isStandalone(m)) {
        out.write(0xFF);
        out.write(m);
        continue;
      }
      byte[] segment = keptJpegSegment(m, readSegmentPayload(in));
      if (segment != null) {
        out.write(segment);
      }
    }
    // truncated scan without EOI: kept as it is, a browser shows what it can decode
  }

  /** The segment to write back, or null to drop it. */
  private static byte @Nullable [] keptJpegSegment(int marker, byte[] payload) {
    if (marker == APP0) {
      if (startsWith(payload, "JFIF\0") && payload.length >= 12) {
        // JFIF header without its optional thumbnail: a thumbnail may show what a crop removed
        byte[] jfif = new byte[14];
        System.arraycopy(payload, 0, jfif, 0, 12);
        return segment(APP0, jfif);
      }
      return null;
    }
    if (marker == APP2) {
      // ICC colour profile: needed to display the colours right. MPF, FlashPix… go.
      return startsWith(payload, "ICC_PROFILE\0") ? segment(marker, payload) : null;
    }
    if (marker == APP14) {
      // Adobe: tells a decoder how the colour channels are transformed
      return startsWith(payload, "Adobe") ? segment(marker, payload) : null;
    }
    if ((marker >= APP0 && marker <= APP15) || marker == COM) {
      // APP1 (EXIF, XMP), APP13 (IPTC, Photoshop), APP11 (JUMBF, C2PA), vendor blocks, comments
      return null;
    }
    // Tables, frame headers, restart interval…: image data
    return segment(marker, payload);
  }

  private static boolean isStandalone(int marker) {
    return marker == 0x01 || (marker >= 0xD0 && marker <= 0xD7);
  }

  /** Skips to the next marker and returns its code; garbage between segments is dropped. */
  private static int nextMarker(InputStream in) throws IOException {
    int b = in.read();
    while (b != -1 && b != 0xFF) {
      b = in.read();
    }
    while (b == 0xFF) {
      b = in.read();
    }
    if (b == -1) {
      throw new MalformedImageException("JPEG ends before its image data");
    }
    return b;
  }

  private static byte[] readSegmentPayload(InputStream in) throws IOException {
    int hi = in.read();
    int lo = in.read();
    if (hi < 0 || lo < 0) {
      throw new MalformedImageException("JPEG segment length truncated");
    }
    int length = (hi << 8) | lo;
    if (length < 2) {
      throw new MalformedImageException("JPEG segment length " + length);
    }
    return readFully(in, length - 2, "JPEG segment");
  }

  private static byte[] segment(int marker, byte[] payload) {
    int length = payload.length + 2;
    if (length > 0xFFFF) {
      throw new IllegalStateException("JPEG segment too long: " + length);
    }
    byte[] s = new byte[payload.length + 4];
    s[0] = (byte) 0xFF;
    s[1] = (byte) marker;
    s[2] = (byte) (length >> 8);
    s[3] = (byte) length;
    System.arraycopy(payload, 0, s, 4, payload.length);
    return s;
  }

  // ---------------------------------------------------------------------------------------------
  // PNG: chunks

  private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};

  /**
   * Ancillary chunks that change how the image is displayed. Critical chunks (IHDR, PLTE, IDAT,
   * IEND, anything with an upper-case first letter) are always kept. Everything else goes: tEXt,
   * zTXt, iTXt (comments, XMP), eXIf, tIME, and whatever private chunk an editor added.
   */
  private static final Set<String> PNG_KEPT_ANCILLARY =
      Set.of(
          "tRNS", "gAMA", "cHRM", "sRGB", "iCCP", "sBIT", "bKGD", "hIST", "pHYs", "sPLT", "cICP",
          "mDCv", "cLLI", "acTL", "fcTL", "fdAT");

  private static final int MAX_METADATA_CHUNK = 16 * 1024 * 1024;

  private static void stripPng(InputStream in, OutputStream out) throws IOException {
    byte[] signature = readFully(in, PNG_SIGNATURE.length, "PNG signature");
    if (!java.util.Arrays.equals(signature, PNG_SIGNATURE)) {
      throw new MalformedImageException("Not a PNG signature");
    }
    out.write(signature);
    boolean orientationWritten = false;
    while (true) {
      byte[] header = readFully(in, 8, "PNG chunk header");
      long length =
          ByteBuffer.wrap(header, 0, 4).order(ByteOrder.BIG_ENDIAN).getInt() & 0xFFFFFFFFL;
      if (length > Integer.MAX_VALUE) {
        throw new MalformedImageException("PNG chunk length " + length);
      }
      String type = new String(header, 4, 4, StandardCharsets.ISO_8859_1);
      boolean critical = Character.isUpperCase(type.charAt(0));
      if (critical || PNG_KEPT_ANCILLARY.contains(type)) {
        out.write(header);
        copy(in, out, length + 4, "PNG chunk"); // data + CRC
        if (type.equals("IEND")) {
          return;
        }
      } else if (type.equals("eXIf") && !orientationWritten) {
        if (length > MAX_METADATA_CHUNK) {
          throw new MalformedImageException("PNG eXIf chunk of " + length + " bytes");
        }
        byte[] exif = readFully(in, (int) length, "PNG eXIf");
        skip(in, 4, "PNG chunk CRC");
        Integer orientation = ExifOrientation.read(exif, 0, exif.length);
        if (orientation != null) {
          // eXIf must precede IDAT: writing it where the original one was keeps it there
          out.write(pngChunk("eXIf", ExifOrientation.minimalTiff(orientation)));
          orientationWritten = true;
        }
      } else {
        skip(in, length + 4, "PNG chunk");
      }
    }
  }

  private static byte[] pngChunk(String type, byte[] data) {
    byte[] typeBytes = type.getBytes(StandardCharsets.ISO_8859_1);
    CRC32 crc = new CRC32();
    crc.update(typeBytes);
    crc.update(data);
    ByteBuffer chunk = ByteBuffer.allocate(12 + data.length).order(ByteOrder.BIG_ENDIAN);
    chunk.putInt(data.length).put(typeBytes).put(data).putInt((int) crc.getValue());
    return chunk.array();
  }

  // ---------------------------------------------------------------------------------------------
  // WebP: RIFF chunks

  /** Chunks of the image itself; EXIF, XMP and unknown chunks go. */
  private static final Set<String> WEBP_KEPT =
      Set.of("VP8X", "VP8 ", "VP8L", "ALPH", "ANIM", "ANMF", "ICCP");

  private static final int VP8X_EXIF_FLAG = 0x08;
  private static final int VP8X_XMP_FLAG = 0x04;

  /**
   * WebP is rewritten in memory: the RIFF header holds the total size, known only once the chunks
   * are filtered. WebP files are small next to the upload limit.
   */
  private static void stripWebp(InputStream in, OutputStream out) throws IOException {
    byte[] header = readFully(in, 12, "WebP header");
    long riffSize =
        ByteBuffer.wrap(header, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
    if (riffSize < 4 || riffSize > Integer.MAX_VALUE - 8) {
      throw new MalformedImageException("WebP RIFF size " + riffSize);
    }
    // Only what the RIFF size covers: anything appended after it is not part of the image
    byte[] body = in.readNBytes((int) riffSize - 4);

    List<byte[]> kept = new ArrayList<>();
    Integer orientation = null;
    int pos = 0;
    while (pos + 8 <= body.length) {
      String fourcc = new String(body, pos, 4, StandardCharsets.ISO_8859_1);
      long size =
          ByteBuffer.wrap(body, pos + 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
      long padded = size + (size & 1);
      if (pos + 8 + size > body.length) {
        throw new MalformedImageException("WebP chunk " + fourcc + " truncated");
      }
      int chunkEnd = (int) Math.min(body.length, pos + 8 + padded);
      if (WEBP_KEPT.contains(fourcc)) {
        byte[] chunk = java.util.Arrays.copyOfRange(body, pos, chunkEnd);
        if (chunk.length < 8 + padded) {
          // missing final pad byte
          chunk = java.util.Arrays.copyOf(chunk, (int) (8 + padded));
        }
        kept.add(chunk);
      } else if (fourcc.equals("EXIF") && orientation == null) {
        orientation = ExifOrientation.read(body, pos + 8, (int) size);
      }
      pos = chunkEnd;
    }
    if (kept.isEmpty()) {
      throw new MalformedImageException("WebP without image data");
    }

    byte[] first = kept.getFirst();
    boolean extended = new String(first, 0, 4, StandardCharsets.ISO_8859_1).equals("VP8X");
    if (extended && first.length > 8) {
      int flags = first[8] & 0xFF;
      flags &= ~(VP8X_EXIF_FLAG | VP8X_XMP_FLAG);
      if (orientation != null) {
        flags |= VP8X_EXIF_FLAG;
      }
      first[8] = (byte) flags;
    }
    if (orientation != null && extended) {
      // The simple format (no VP8X) cannot carry EXIF, so there was none to keep
      byte[] tiff = ExifOrientation.minimalTiff(orientation);
      ByteBuffer exif = ByteBuffer.allocate(8 + tiff.length).order(ByteOrder.LITTLE_ENDIAN);
      exif.put("EXIF".getBytes(StandardCharsets.ISO_8859_1)).putInt(tiff.length).put(tiff);
      kept.add(exif.array()); // 26 bytes: even, no pad
    }

    long total = 4;
    for (byte[] chunk : kept) {
      total += chunk.length;
    }
    ByteBuffer riff = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
    riff.put("RIFF".getBytes(StandardCharsets.ISO_8859_1))
        .putInt((int) total)
        .put("WEBP".getBytes(StandardCharsets.ISO_8859_1));
    out.write(riff.array());
    for (byte[] chunk : kept) {
      out.write(chunk);
    }
  }

  // ---------------------------------------------------------------------------------------------
  // GIF: blocks

  private static final Set<String> GIF_KEPT_APPLICATIONS = Set.of("NETSCAPE2.0", "ANIMEXTS1.0");

  private static void stripGif(InputStream in, OutputStream out) throws IOException {
    byte[] header = readFully(in, 13, "GIF header"); // signature + logical screen descriptor
    out.write(header);
    copyColorTable(in, out, header[10]);
    while (true) {
      int b = in.read();
      if (b == -1) {
        // truncated before the trailer: close it
        out.write(0x3B);
        return;
      }
      switch (b) {
        case 0x2C -> {
          byte[] descriptor = readFully(in, 9, "GIF image descriptor");
          out.write(b);
          out.write(descriptor);
          copyColorTable(in, out, descriptor[8]);
          copy(in, out, 1, "GIF LZW code size");
          copySubBlocks(in, out);
        }
        case 0x21 -> {
          int label = in.read();
          if (label == 0xF9 || label == 0x01) {
            // graphic control (frame delay, transparency), plain text
            out.write(b);
            out.write(label);
            copySubBlocks(in, out);
          } else if (label == 0xFF) {
            byte[] id = readSubBlock(in);
            String app = new String(id, StandardCharsets.ISO_8859_1);
            if (GIF_KEPT_APPLICATIONS.contains(app)) {
              // animation loop count
              out.write(b);
              out.write(label);
              out.write(id.length);
              out.write(id);
              copySubBlocks(in, out);
            } else {
              // XMP DataXMP, ICC and others
              skipSubBlocks(in);
            }
          } else if (label == -1) {
            throw new MalformedImageException("GIF extension truncated");
          } else {
            // comment (0xFE) and unknown extensions
            skipSubBlocks(in);
          }
        }
        case 0x3B -> {
          out.write(b);
          return;
        }
        default -> throw new MalformedImageException("GIF block " + b);
      }
    }
  }

  private static void copyColorTable(InputStream in, OutputStream out, byte packed)
      throws IOException {
    if ((packed & 0x80) != 0) {
      copy(in, out, 3L * (1 << ((packed & 0x07) + 1)), "GIF colour table");
    }
  }

  private static byte[] readSubBlock(InputStream in) throws IOException {
    int size = in.read();
    if (size < 0) {
      throw new MalformedImageException("GIF sub-block truncated");
    }
    return readFully(in, size, "GIF sub-block");
  }

  private static void copySubBlocks(InputStream in, OutputStream out) throws IOException {
    while (true) {
      int size = in.read();
      if (size < 0) {
        throw new MalformedImageException("GIF sub-block truncated");
      }
      out.write(size);
      if (size == 0) {
        return;
      }
      copy(in, out, size, "GIF sub-block");
    }
  }

  private static void skipSubBlocks(InputStream in) throws IOException {
    while (true) {
      int size = in.read();
      if (size < 0) {
        throw new MalformedImageException("GIF sub-block truncated");
      }
      if (size == 0) {
        return;
      }
      skip(in, size, "GIF sub-block");
    }
  }

  // ---------------------------------------------------------------------------------------------

  private static boolean startsWith(byte[] data, String prefix) {
    if (data.length < prefix.length()) {
      return false;
    }
    for (int i = 0; i < prefix.length(); i++) {
      if ((data[i] & 0xFF) != prefix.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  private static byte[] readFully(InputStream in, int length, String what) throws IOException {
    byte[] data = in.readNBytes(length);
    if (data.length != length) {
      throw new MalformedImageException(what + " truncated");
    }
    return data;
  }

  private static void copy(InputStream in, OutputStream out, long length, String what)
      throws IOException {
    byte[] buffer = new byte[8192];
    long remaining = length;
    while (remaining > 0) {
      int n = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
      if (n < 0) {
        throw new MalformedImageException(what + " truncated");
      }
      out.write(buffer, 0, n);
      remaining -= n;
    }
  }

  private static void skip(InputStream in, long length, String what) throws IOException {
    try {
      in.skipNBytes(length);
    } catch (EOFException e) {
      throw new MalformedImageException(what + " truncated");
    }
  }
}
