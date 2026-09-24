package fr.pedalons.service.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.pedalons.service.migration.BiketeamModel.BtTeam;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** The legacy source's files: digested from disk once, streamed. */
// REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — whole class: tests the dump import's source.
@SuppressWarnings("removal")
class LegacyJdbcBiketeamSourceTest {

  @TempDir Path dataDir;

  private static String md5Hex(byte[] bytes) throws NoSuchAlgorithmException {
    return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(bytes));
  }

  @Test
  void aGpxFingerprint_isSizeAndMd5_andTheFileIsDigestedOnlyOnce()
      throws IOException, NoSuchAlgorithmException {
    Path gpx = dataDir.resolve("gpx/club/map-1.gpx");
    Files.createDirectories(gpx.getParent());
    byte[] bytes = "<gpx>trace</gpx>".repeat(10_000).getBytes(StandardCharsets.UTF_8);
    Files.write(gpx, bytes);
    BtTeam team = new BtTeam("club", "Club", null, null, null, null, false);
    LegacyJdbcBiketeamSource source = new LegacyJdbcBiketeamSource(null, team, dataDir.toString());

    SourceFile file = source.gpx("map-1");
    assertNotNull(file);
    String md5 = md5Hex(bytes);
    assertEquals(md5, file.md5());
    // Rewritten behind its back: the digest is not read again — the fingerprint reuses it.
    Files.writeString(gpx, "changed");
    assertEquals(md5, file.md5());
    assertEquals(
        bytes.length + ":" + md5, file.fingerprint(), "size and digest, read together once");
    // A new handle on the file digests it afresh.
    assertEquals(md5Hex("changed".getBytes(StandardCharsets.UTF_8)), source.gpx("map-1").md5());
  }
}
