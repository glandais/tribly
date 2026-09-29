package fr.pedalons.infrastructure.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** docs/LEDGER_*.md API-43: the format is read from the bytes, never from a declared type. */
class ImageFormatTest {

  private static byte[] ftyp(String major, String... compatible) {
    ByteBuffer b = ByteBuffer.allocate(16 + 4 * compatible.length);
    b.putInt(16 + 4 * compatible.length).put(ascii("ftyp")).put(ascii(major)).putInt(0);
    for (String brand : compatible) {
      b.put(ascii(brand));
    }
    return b.array();
  }

  private static byte[] ascii(String s) {
    return s.getBytes(StandardCharsets.ISO_8859_1);
  }

  @Test
  void strippableImages() throws IOException {
    assertEquals(ImageFormat.JPEG, ImageFormat.sniff(TestImages.jpeg(1)));
    assertEquals(ImageFormat.PNG, ImageFormat.sniff(TestImages.png(1)));
    assertEquals(ImageFormat.GIF, ImageFormat.sniff(TestImages.gif()));
    assertEquals(ImageFormat.WEBP, ImageFormat.sniff(TestImages.webp(1)));
    for (ImageFormat f :
        new ImageFormat[] {ImageFormat.JPEG, ImageFormat.PNG, ImageFormat.GIF, ImageFormat.WEBP}) {
      assertTrue(f.isStrippable(), f.name());
      assertFalse(f.isRefused(), f.name());
    }
  }

  @Test
  void refusedImages() {
    assertEquals(ImageFormat.TIFF, ImageFormat.sniff(ascii("II*\0\u0008\0\0\0")));
    assertEquals(ImageFormat.TIFF, ImageFormat.sniff(ascii("MM\0*\0\0\0\u0008")));
    assertEquals(ImageFormat.HEIF, ImageFormat.sniff(ftyp("heic", "mif1", "heic")));
    assertEquals(ImageFormat.HEIF, ImageFormat.sniff(ftyp("avif", "avif", "mif1")));
    // A generic major brand, the still-image brand only among the compatible ones
    assertEquals(ImageFormat.HEIF, ImageFormat.sniff(ftyp("mp41", "isom", "mif1")));
    assertEquals(ImageFormat.HEIF, ImageFormat.sniff(ftyp("crx ", "crx ")));
    assertEquals(
        ImageFormat.JPEG_XL,
        ImageFormat.sniff(
            new byte[] {0, 0, 0, 0x0C, 'J', 'X', 'L', ' ', '\r', '\n', (byte) 0x87, '\n'}));
    assertEquals(
        ImageFormat.JPEG_2000,
        ImageFormat.sniff(
            new byte[] {0, 0, 0, 0x0C, 'j', 'P', ' ', ' ', '\r', '\n', (byte) 0x87, '\n'}));
    for (ImageFormat f :
        new ImageFormat[] {
          ImageFormat.TIFF, ImageFormat.HEIF, ImageFormat.JPEG_XL, ImageFormat.JPEG_2000
        }) {
      assertTrue(f.isRefused(), f.name());
      assertFalse(f.isStrippable(), f.name());
    }
  }

  @Test
  void everythingElseIsStoredAsItIs() {
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ftyp("isom", "isom", "mp41")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("<?xml version=\"1.0\"?><gpx>")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("%PDF-1.7")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("PK\u0003\u0004")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("BM")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(new byte[0]));
    assertFalse(ImageFormat.OTHER.isStrippable());
    assertFalse(ImageFormat.OTHER.isRefused());
  }
}
