package fr.pedalons.infrastructure.image;

import static fr.pedalons.infrastructure.image.TestImages.SECRET;
import static fr.pedalons.infrastructure.image.TestImages.contains;
import static fr.pedalons.infrastructure.image.TestImages.indexOf;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** docs/LEDGER_*.md API-43: images are stored without their metadata, orientation kept. */
class ImageMetadataStripperTest {

  static byte[] strip(byte[] image) throws IOException {
    ImageFormat format = ImageFormat.sniff(image);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageMetadataStripper.strip(format, new ByteArrayInputStream(image), out);
    return out.toByteArray();
  }

  /** Index of the {@code skip}+1-th occurrence of {@code needle}, or -1. */
  static int nthIndexOf(byte[] data, byte[] needle, int skip) {
    int from = 0;
    for (int n = 0; ; n++) {
      int i = TestImages.indexOf(java.util.Arrays.copyOfRange(data, from, data.length), needle);
      if (i < 0) {
        return -1;
      }
      if (n == skip) {
        return from + i;
      }
      from += i + needle.length;
    }
  }

  static void assertDecodes(byte[] image) throws IOException {
    BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(image));
    assertNotNull(decoded, "stripped image no longer decodes");
    assertEquals(TestImages.WIDTH, decoded.getWidth());
    assertEquals(TestImages.HEIGHT, decoded.getHeight());
  }

  @Nested
  class Jpeg {

    @Test
    void removesExifGpsXmpIptcCommentsAndTrailer() throws IOException {
      byte[] original = TestImages.jpeg(6);
      assertTrue(contains(original, SECRET));

      byte[] stripped = strip(original);

      assertFalse(contains(stripped, SECRET));
      assertFalse(contains(stripped, "http://ns.adobe.com/xap"));
      assertFalse(contains(stripped, "Photoshop"));
      assertFalse(contains(stripped, "MPF"));
      assertFalse(contains(stripped, "ftyp"), "motion photo after EOI must go");
      assertEquals(0xFF, stripped[stripped.length - 2] & 0xFF);
      assertEquals(0xD9, stripped[stripped.length - 1] & 0xFF);
      assertDecodes(stripped);
    }

    @Test
    void keepsOrientationAlone() throws IOException {
      byte[] stripped = strip(TestImages.jpeg(6));

      int exif = indexOf(stripped, TestImages.ascii("Exif\0\0"));
      assertTrue(exif > 0, "orientation needs an EXIF block");
      assertEquals(6, ExifOrientation.read(stripped, exif, stripped.length - exif));
      // No GPS IFD pointer (tag 0x8825) left, in either byte order
      assertEquals(-1, indexOf(stripped, new byte[] {(byte) 0x88, 0x25}));
      assertEquals(-1, indexOf(stripped, new byte[] {0x25, (byte) 0x88}));
      // JFIF stays first
      assertEquals(0xE0, stripped[3] & 0xFF);
    }

    @Test
    void writesNoExifWhenOrientationIsNormal() throws IOException {
      byte[] stripped = strip(TestImages.jpeg(1));

      assertEquals(-1, indexOf(stripped, TestImages.ascii("Exif")));
      assertDecodes(stripped);
    }

    @Test
    void keepsImageDataByteForByte() throws IOException {
      byte[] plain = TestImages.write(TestImages.picture(), "jpeg");

      // A JPEG with no metadata at all comes out unchanged
      assertArrayEquals(plain, strip(plain));
    }

    @Test
    void keepsAProgressiveJpegIntact() throws IOException {
      // Several scans, with tables between them: all image data, all kept
      javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
      javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();
      param.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_DEFAULT);
      ByteArrayOutputStream progressive = new ByteArrayOutputStream();
      try (javax.imageio.stream.ImageOutputStream ios =
          ImageIO.createImageOutputStream(progressive)) {
        writer.setOutput(ios);
        writer.write(null, new javax.imageio.IIOImage(TestImages.picture(), null, null), param);
      } finally {
        writer.dispose();
      }
      byte[] plain = progressive.toByteArray();
      int scans = 0;
      for (int i = 0; i + 1 < plain.length; i++) {
        if ((plain[i] & 0xFF) == 0xFF && (plain[i + 1] & 0xFF) == 0xDA) {
          scans++;
        }
      }
      assertTrue(scans > 1, "not a progressive JPEG");
      assertArrayEquals(plain, strip(plain));

      // The same with metadata spliced after SOI and a comment between two scans
      int secondScan = nthIndexOf(plain, new byte[] {(byte) 0xFF, (byte) 0xDA}, 1);
      byte[] laden =
          TestImages.concat(
              java.util.Arrays.copyOfRange(plain, 0, 2),
              TestImages.jpegSegment(
                  0xE1, TestImages.concat(TestImages.ascii("Exif\0\0"), TestImages.exifTiff(1))),
              java.util.Arrays.copyOfRange(plain, 2, secondScan),
              TestImages.jpegSegment(0xFE, TestImages.ascii(SECRET)),
              java.util.Arrays.copyOfRange(plain, secondScan, plain.length));
      assertArrayEquals(plain, strip(laden));
    }

    @Test
    void isIdempotent() throws IOException {
      byte[] once = strip(TestImages.jpeg(8));
      assertArrayEquals(once, strip(once));
    }

    @Test
    void dropsTheJfifThumbnail() throws IOException {
      byte[] plain = TestImages.write(TestImages.picture(), "jpeg");
      // JFIF with a 1x1 RGB thumbnail spliced in place of ImageIO's APP0
      int app0Length = ((plain[4] & 0xFF) << 8) | (plain[5] & 0xFF);
      byte[] jfif = new byte[14 + 3];
      System.arraycopy(plain, 6, jfif, 0, 12);
      jfif[12] = 1;
      jfif[13] = 1;
      jfif[14] = 'S';
      jfif[15] = 'E';
      jfif[16] = 'C';
      byte[] withThumbnail =
          TestImages.concat(
              new byte[] {(byte) 0xFF, (byte) 0xD8},
              TestImages.jpegSegment(0xE0, jfif),
              java.util.Arrays.copyOfRange(plain, 4 + app0Length, plain.length));

      byte[] stripped = strip(withThumbnail);

      assertEquals(0, stripped[4 + 2 + 12] & 0xFF, "thumbnail width");
      assertEquals(0, stripped[4 + 2 + 13] & 0xFF, "thumbnail height");
      // ImageIO writes a JFIF without thumbnail: that is exactly what comes back
      assertArrayEquals(plain, stripped);
    }

    @Test
    void rejectsATruncatedHeader() {
      byte[] truncated = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1, 0x10, 0x00, 'E'};
      assertThrows(MalformedImageException.class, () -> strip(truncated));
    }
  }

  @Nested
  class Png {

    @Test
    void removesTextExifPrivateChunksAndTrailer() throws IOException {
      byte[] stripped = strip(TestImages.png(1));

      assertFalse(contains(stripped, SECRET));
      assertFalse(contains(stripped, "tEXt"));
      assertFalse(contains(stripped, "iTXt"));
      assertFalse(contains(stripped, "eXIf"));
      assertFalse(contains(stripped, "prVt"));
      assertTrue(
          TestImages.indexOf(stripped, TestImages.ascii("IEND")) == stripped.length - 8,
          "nothing after IEND");
      assertDecodes(stripped);
    }

    @Test
    void keepsOrientationInAMinimalExifChunk() throws IOException {
      byte[] stripped = strip(TestImages.png(8));

      int type = indexOf(stripped, TestImages.ascii("eXIf"));
      assertTrue(type > 0);
      int length = ByteBuffer.wrap(stripped, type - 4, 4).order(ByteOrder.BIG_ENDIAN).getInt();
      assertEquals(26, length);
      assertEquals(8, ExifOrientation.read(stripped, type + 4, length));
      assertTrue(type < indexOf(stripped, TestImages.ascii("IDAT")), "eXIf must precede IDAT");
      CRC32 crc = new CRC32();
      crc.update(stripped, type, 4 + length);
      int storedCrc =
          ByteBuffer.wrap(stripped, type + 4 + length, 4).order(ByteOrder.BIG_ENDIAN).getInt();
      assertEquals((int) crc.getValue(), storedCrc);
      assertDecodes(stripped);
    }

    @Test
    void leavesACleanPngUnchanged() throws IOException {
      byte[] plain = TestImages.write(TestImages.picture(), "png");
      assertArrayEquals(plain, strip(plain));
    }

    @Test
    void isIdempotent() throws IOException {
      byte[] once = strip(TestImages.png(3));
      assertArrayEquals(once, strip(once));
    }
  }

  @Nested
  class Gif {

    @Test
    void removesCommentsAndXmp() throws IOException {
      byte[] stripped = strip(TestImages.gif());

      assertFalse(contains(stripped, SECRET));
      assertFalse(contains(stripped, "XMP DataXMP"));
      assertEquals(0x3B, stripped[stripped.length - 1]);
      assertDecodes(stripped);
    }

    @Test
    void isIdempotent() throws IOException {
      byte[] once = strip(TestImages.gif());
      assertArrayEquals(once, strip(once));
    }
  }

  @Nested
  class Webp {

    @Test
    void removesExifXmpAndUnknownChunks() throws IOException {
      byte[] stripped = strip(TestImages.webp(1));

      assertFalse(contains(stripped, SECRET));
      assertFalse(contains(stripped, "EXIF"));
      assertFalse(contains(stripped, "XMP "));
      assertFalse(contains(stripped, "SCRT"));
      int riffSize = ByteBuffer.wrap(stripped, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
      assertEquals(stripped.length - 8, riffSize);
      int vp8x = indexOf(stripped, TestImages.ascii("VP8X"));
      assertEquals(0, stripped[vp8x + 8] & 0x0C, "EXIF and XMP flags cleared");
      int vp8l = indexOf(stripped, TestImages.ascii("VP8L"));
      assertArrayEquals(
          TestImages.WEBP_IMAGE_DATA,
          java.util.Arrays.copyOfRange(
              stripped, vp8l + 8, vp8l + 8 + TestImages.WEBP_IMAGE_DATA.length));
    }

    @Test
    void keepsOrientationInAMinimalExifChunk() throws IOException {
      byte[] stripped = strip(TestImages.webp(3));

      int exif = indexOf(stripped, TestImages.ascii("EXIF"));
      assertTrue(exif > 0);
      int size = ByteBuffer.wrap(stripped, exif + 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
      assertEquals(26, size);
      assertEquals(3, ExifOrientation.read(stripped, exif + 8, size));
      int vp8x = indexOf(stripped, TestImages.ascii("VP8X"));
      assertEquals(0x08, stripped[vp8x + 8] & 0x0C, "EXIF flag only");
      assertEquals(
          stripped.length - 8,
          ByteBuffer.wrap(stripped, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
    }

    @Test
    void isIdempotent() throws IOException {
      byte[] once = strip(TestImages.webp(5));
      assertArrayEquals(once, strip(once));
    }
  }

  @Test
  void refusesAFormatItCannotStrip() {
    byte[] tiff = "II*\0rest".getBytes(StandardCharsets.ISO_8859_1);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            ImageMetadataStripper.strip(
                ImageFormat.TIFF, new ByteArrayInputStream(tiff), new ByteArrayOutputStream()));
  }
}
