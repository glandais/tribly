package fr.pedalons.infrastructure.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import javax.imageio.ImageIO;

/**
 * Images laden with metadata, for the stripping tests: a real image written by ImageIO, into which
 * EXIF (with a GPS IFD), XMP, IPTC, comments and trailing data are spliced. Every piece of metadata
 * carries {@link #SECRET}, so a test only has to check the marker is gone.
 */
public final class TestImages {

  /** Present in every piece of metadata the test images carry. */
  public static final String SECRET = "SECRET-48.8566N-2.3522E";

  public static final int WIDTH = 24;
  public static final int HEIGHT = 16;

  private TestImages() {}

  public static BufferedImage picture() {
    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setColor(Color.ORANGE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(Color.BLUE);
    g.fillRect(0, 0, WIDTH / 2, HEIGHT / 2);
    g.dispose();
    return image;
  }

  /**
   * An EXIF TIFF structure, little-endian: IFD0 holds the orientation and a pointer to a GPS IFD;
   * the GPS IFD holds a latitude reference; {@link #SECRET} trails the structure.
   */
  public static byte[] exifTiff(int orientation) {
    ByteBuffer b = ByteBuffer.allocate(80).order(ByteOrder.LITTLE_ENDIAN);
    b.put((byte) 'I').put((byte) 'I').putShort((short) 42).putInt(8);
    // IFD0 at 8: two entries
    b.putShort((short) 2);
    b.putShort((short) 0x0112).putShort((short) 3).putInt(1).putShort((short) orientation);
    b.putShort((short) 0);
    b.putShort((short) 0x8825).putShort((short) 4).putInt(1).putInt(38); // GPS IFD pointer
    b.putInt(0);
    // GPS IFD at 38: GPSLatitudeRef = "N"
    b.putShort((short) 1);
    b.putShort((short) 0x0001).putShort((short) 2).putInt(2).put((byte) 'N').put((byte) 0);
    b.putShort((short) 0);
    b.putInt(0);
    byte[] head = java.util.Arrays.copyOf(b.array(), b.position());
    return concat(head, SECRET.getBytes(StandardCharsets.ISO_8859_1));
  }

  // --- JPEG

  /**
   * A JPEG with EXIF (orientation + GPS), XMP, IPTC, a comment, and a "motion photo" appended
   * after EOI.
   */
  public static byte[] jpeg(int orientation) throws IOException {
    byte[] plain = write(picture(), "jpeg");
    // ImageIO writes SOI then JFIF APP0: splice the metadata right after SOI
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(plain, 0, 2);
    out.write(jpegSegment(0xE1, concat(ascii("Exif\0\0"), exifTiff(orientation))));
    out.write(
        jpegSegment(
            0xE1, ascii("http://ns.adobe.com/xap/1.0/\0<x:xmpmeta>" + SECRET + "</x:xmpmeta>")));
    out.write(jpegSegment(0xED, ascii("Photoshop 3.0\08BIM" + SECRET)));
    out.write(jpegSegment(0xE2, ascii("MPF\0" + SECRET)));
    out.write(jpegSegment(0xFE, ascii(SECRET)));
    out.write(plain, 2, plain.length - 2);
    out.write(ascii("ftypmp42 video " + SECRET));
    return out.toByteArray();
  }

  public static byte[] jpegSegment(int marker, byte[] payload) {
    int length = payload.length + 2;
    byte[] s = new byte[payload.length + 4];
    s[0] = (byte) 0xFF;
    s[1] = (byte) marker;
    s[2] = (byte) (length >> 8);
    s[3] = (byte) length;
    System.arraycopy(payload, 0, s, 4, payload.length);
    return s;
  }

  // --- PNG

  /** A PNG with tEXt, iTXt, eXIf (orientation + GPS) and a private chunk, and bytes after IEND. */
  public static byte[] png(int orientation) throws IOException {
    byte[] plain = write(picture(), "png");
    int idat = indexOf(plain, ascii("IDAT")) - 4;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(plain, 0, idat);
    out.write(pngChunk("tEXt", ascii("Comment\0" + SECRET)));
    out.write(pngChunk("iTXt", ascii("XML:com.adobe.xmp\0\0\0\0\0" + SECRET)));
    out.write(pngChunk("eXIf", exifTiff(orientation)));
    out.write(pngChunk("prVt", ascii(SECRET)));
    out.write(plain, idat, plain.length - idat);
    out.write(ascii(SECRET));
    return out.toByteArray();
  }

  public static byte[] pngChunk(String type, byte[] data) {
    byte[] typeBytes = ascii(type);
    CRC32 crc = new CRC32();
    crc.update(typeBytes);
    crc.update(data);
    ByteBuffer chunk = ByteBuffer.allocate(12 + data.length).order(ByteOrder.BIG_ENDIAN);
    chunk.putInt(data.length).put(typeBytes).put(data).putInt((int) crc.getValue());
    return chunk.array();
  }

  // --- GIF

  /** A GIF with a comment and an XMP application extension. */
  public static byte[] gif() throws IOException {
    byte[] plain = write(picture(), "gif");
    // plain ends with the trailer (0x3B): splice the extensions just before it
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(plain, 0, plain.length - 1);
    byte[] secret = ascii(SECRET);
    out.write(new byte[] {0x21, (byte) 0xFE, (byte) secret.length});
    out.write(secret);
    out.write(0);
    out.write(new byte[] {0x21, (byte) 0xFF, 11});
    out.write(ascii("XMP DataXMP"));
    out.write(secret.length);
    out.write(secret);
    out.write(0);
    out.write(0x3B);
    out.write(secret); // after the trailer
    return out.toByteArray();
  }

  // --- WebP

  /** Fake image data: the stripper never decodes it, it only has to come out unchanged. */
  public static final byte[] WEBP_IMAGE_DATA = {0x2F, 1, 2, 3, 4};

  /** An extended WebP (VP8X) with EXIF (orientation + GPS) and XMP chunks, and unknown chunk. */
  public static byte[] webp(int orientation) {
    byte[] vp8x = new byte[10];
    vp8x[0] = 0x08 | 0x04; // EXIF + XMP flags
    vp8x[4] = (byte) (WIDTH - 1);
    vp8x[7] = (byte) (HEIGHT - 1);
    byte[] body =
        concat(
            ascii("WEBP"),
            webpChunk("VP8X", vp8x),
            webpChunk("VP8L", WEBP_IMAGE_DATA),
            webpChunk("EXIF", exifTiff(orientation)),
            webpChunk("XMP ", ascii("<x:xmpmeta>" + SECRET + "</x:xmpmeta>")),
            webpChunk("SCRT", ascii(SECRET)));
    ByteBuffer header = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    header.put(ascii("RIFF")).putInt(body.length);
    return concat(header.array(), body, ascii(SECRET));
  }

  public static byte[] webpChunk(String fourcc, byte[] data) {
    int padded = data.length + (data.length & 1);
    ByteBuffer chunk = ByteBuffer.allocate(8 + padded).order(ByteOrder.LITTLE_ENDIAN);
    chunk.put(ascii(fourcc)).putInt(data.length).put(data);
    return chunk.array();
  }

  // --- helpers

  public static byte[] write(BufferedImage image, String format) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    if (!ImageIO.write(image, format, out)) {
      throw new IllegalStateException("No ImageIO writer for " + format);
    }
    return out.toByteArray();
  }

  public static byte[] ascii(String s) {
    return s.getBytes(StandardCharsets.ISO_8859_1);
  }

  public static byte[] concat(byte[]... parts) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (byte[] part : parts) {
      out.writeBytes(part);
    }
    return out.toByteArray();
  }

  public static int indexOf(byte[] data, byte[] needle) {
    outer:
    for (int i = 0; i + needle.length <= data.length; i++) {
      for (int j = 0; j < needle.length; j++) {
        if (data[i + j] != needle[j]) {
          continue outer;
        }
      }
      return i;
    }
    return -1;
  }

  public static boolean contains(byte[] data, String needle) {
    return indexOf(data, ascii(needle)) >= 0;
  }
}
