package fr.pedalons.infrastructure.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  private static ImageFormat sniff(String testImage) {
    return ImageFormat.sniff(TestImages.load(testImage));
  }

  @Test
  void imagesKeepingTheirFormat() {
    assertEquals(ImageFormat.JPEG, sniff(TestImages.JPEG));
    assertEquals(ImageFormat.PNG, sniff(TestImages.PNG));
    assertEquals(ImageFormat.GIF, sniff(TestImages.GIF));
    assertEquals(ImageFormat.WEBP, sniff(TestImages.WEBP));
    assertEquals("image/jpeg", ImageFormat.JPEG.storedMimeType());
    assertEquals("image/png", ImageFormat.PNG.storedMimeType());
    assertEquals("image/gif", ImageFormat.GIF.storedMimeType());
    assertEquals("image/webp", ImageFormat.WEBP.storedMimeType());
  }

  @Test
  void imagesBecomingJpegs() {
    assertEquals(ImageFormat.TIFF, sniff(TestImages.TIFF));
    assertEquals(ImageFormat.TIFF, ImageFormat.sniff(ascii("MM\0*\0\0\0\u0008")));
    assertEquals(ImageFormat.HEIF, sniff(TestImages.HEIC));
    assertEquals(ImageFormat.HEIF, sniff(TestImages.AVIF));
    // A generic major brand, the still-image brand only among the compatible ones
    assertEquals(ImageFormat.HEIF, ImageFormat.sniff(ftyp("mp41", "isom", "mif1")));
    assertEquals(ImageFormat.JPEG_XL, sniff(TestImages.JXL));
    assertEquals(ImageFormat.JPEG_XL, ImageFormat.sniff(new byte[] {(byte) 0xFF, 0x0A, 0, 0}));
    for (ImageFormat f :
        new ImageFormat[] {ImageFormat.TIFF, ImageFormat.HEIF, ImageFormat.JPEG_XL}) {
      assertTrue(f.isReencoded(), f.name());
      assertFalse(f.isRefused(), f.name());
      assertEquals("image/jpeg", f.storedMimeType(), f.name());
    }
  }

  @Test
  void jpeg2000IsRefused() {
    assertEquals(
        ImageFormat.JPEG_2000,
        ImageFormat.sniff(
            new byte[] {0, 0, 0, 0x0C, 'j', 'P', ' ', ' ', '\r', '\n', (byte) 0x87, '\n'}));
    assertTrue(ImageFormat.JPEG_2000.isRefused());
    assertFalse(ImageFormat.JPEG_2000.isReencoded());
  }

  @Test
  void everythingElseIsStoredAsItIs() {
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ftyp("isom", "isom", "mp41")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("<?xml version=\"1.0\"?><gpx>")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("%PDF-1.7")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("PK\u0003\u0004")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(ascii("BM")));
    assertEquals(ImageFormat.OTHER, ImageFormat.sniff(new byte[0]));
    assertFalse(ImageFormat.OTHER.isReencoded());
    assertFalse(ImageFormat.OTHER.isRefused());
  }

  @Test
  void theFileNameFollowsTheStoredFormat() {
    assertEquals("IMG_0042.jpg", ImageFormat.HEIF.storedFileName("IMG_0042.HEIC"));
    assertEquals("scan.jpg", ImageFormat.TIFF.storedFileName("scan.tif"));
    assertEquals("photo.JPEG", ImageFormat.JPEG.storedFileName("photo.JPEG"));
    assertEquals("photo.jpg", ImageFormat.JPEG.storedFileName("photo.jpg"));
    assertEquals("photo.png", ImageFormat.PNG.storedFileName("photo.png"));
    // A name that lied about the format is corrected, a name without extension gets one
    assertEquals("photo.jpg", ImageFormat.JPEG.storedFileName("photo.png"));
    assertEquals("scan.webp", ImageFormat.WEBP.storedFileName("scan"));
    assertEquals(".hidden.gif", ImageFormat.GIF.storedFileName(".hidden"));
    assertEquals("notes.txt", ImageFormat.OTHER.storedFileName("notes.txt"));
  }
}
