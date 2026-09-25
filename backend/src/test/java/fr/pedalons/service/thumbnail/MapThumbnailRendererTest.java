package fr.pedalons.service.thumbnail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.route.GpxTrack;
import io.github.glandais.gpx.data.GPX;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@QuarkusTest
class MapThumbnailRendererTest extends AbstractBaseTest {

  @Inject MapThumbnailRenderer renderer;

  @TempDir Path tmp;

  private static GPX gpx() {
    return new GPX(
        "test",
        ThumbnailService.buildPaths(
            List.of(
                new ThumbnailService.ThumbnailTrack(
                    "track",
                    List.of(
                        new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
                        new GpxTrack.TrackPoint(45.1, 6.1, 510.0, 10000.0))))),
        List.of());
  }

  /** The tileserver answers an error body, not a tile: the render must fail, not come out black. */
  @Test
  void render_withTileErrors_failsAndLeavesNoImage() {
    File output = tmp.resolve("thumbnail.png").toFile();

    assertThrows(
        IOException.class,
        () -> renderer.render(output, gpx(), "no-such-style", List.of(Color.RED)));

    assertFalse(output.exists());
  }

  /** An error body must not stay in the tile cache, or every later render would be black too. */
  @Test
  void render_withTileErrors_evictsThemFromTheCache() throws IOException {
    File output = tmp.resolve("thumbnail.png").toFile();
    assertThrows(
        IOException.class,
        () -> renderer.render(output, gpx(), "no-such-style", List.of(Color.RED)));

    // Only this style's cache: the shared test cache outlives the run, and holds other styles'
    // tiles, elevation tiles, and directories keyed by the tileserver port of earlier runs
    Path styleCache = renderer.tileCache("no-such-style").toPath();
    if (Files.exists(styleCache)) {
      try (Stream<Path> files = Files.walk(styleCache)) {
        List<Path> nonImages =
            files
                .filter(Files::isRegularFile)
                .filter(p -> !MapThumbnailRenderer.looksLikeImage(p))
                .toList();
        assertEquals(List.of(), nonImages);
      }
    }
  }

  @Test
  void evictBrokenTiles_keepsImagesAndDropsTheRest() throws IOException {
    Path cache = tmp.resolve("cache");
    Path tile = cache.resolve("10/530/368");
    Path broken = cache.resolve("10/530/369");
    Path empty = cache.resolve("10/530/370");
    Files.createDirectories(tile.getParent());
    Files.write(tile, new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A});
    Files.writeString(broken, "{}");
    Files.write(empty, new byte[0]);

    List<Path> evicted = MapThumbnailRenderer.evictBrokenTiles(cache.toFile(), 0);

    assertEquals(2, evicted.size());
    assertTrue(Files.exists(tile));
    assertFalse(Files.exists(broken));
    assertFalse(Files.exists(empty));
  }
}
