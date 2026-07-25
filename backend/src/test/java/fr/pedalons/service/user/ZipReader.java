package fr.pedalons.service.user;

import fr.pedalons.infrastructure.storage.StorageService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Reads an export archive back, so tests can assert on what actually landed in it. */
final class ZipReader {

  private ZipReader() {}

  static Map<String, byte[]> readAll(Path zipFile) throws IOException {
    try (InputStream in = Files.newInputStream(zipFile)) {
      return readAll(in);
    }
  }

  static Map<String, byte[]> readAll(InputStream in) throws IOException {
    Map<String, byte[]> entries = new LinkedHashMap<>();
    try (ZipInputStream zip = new ZipInputStream(in)) {
      ZipEntry entry;
      while ((entry = zip.getNextEntry()) != null) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        zip.transferTo(out);
        entries.put(entry.getName(), out.toByteArray());
      }
    }
    return entries;
  }

  static Set<String> entryNames(Path zipFile) throws IOException {
    return readAll(zipFile).keySet();
  }

  static String text(Map<String, byte[]> entries, String path) {
    byte[] bytes = entries.get(path);
    if (bytes == null) {
      throw new AssertionError("missing zip entry: " + path + " (have " + entries.keySet() + ")");
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }

  /** Every entry concatenated — for "this secret must appear nowhere" assertions. */
  static String allText(Map<String, byte[]> entries) {
    StringBuilder sb = new StringBuilder();
    entries.forEach((name, bytes) -> sb.append(new String(bytes, StandardCharsets.UTF_8)));
    return sb.toString();
  }

  static String entryText(StorageService storageService, String storageKey, String path)
      throws AssertionError {
    try (InputStream in = storageService.retrieve(storageKey)) {
      return text(readAll(in), path);
    } catch (IOException e) {
      throw new AssertionError("could not read " + path + " from " + storageKey, e);
    }
  }
}
