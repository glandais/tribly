package fr.pedalons.service.thumbnail;

import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.map.TileMapProducer;
import io.github.glandais.gpx.util.CacheFolderProvider;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * The one way to draw a track on the tileserver's map: every thumbnail (route, ride, trip, GPX
 * preview) goes through here.
 *
 * <p>gpx2web's {@link TileMapProducer} does not check the HTTP status of a tile: a tileserver
 * error body lands in its on-disk tile cache as if it were a tile, {@code ImageIO} cannot decode
 * it, and the tile is silently left out — a black square, drawn over by the track, cached for
 * good. This class makes that failure loud: after each render it looks for such tiles, evicts them
 * so the next render fetches them again, and fails the render instead of returning a false image.
 *
 * <p>It relies on the producer's cache layout — {@code <cache>/<hex(urlPattern.hashCode())>/z/x/y}
 * — which is stable across gpx2web 1.x.
 */
@ApplicationScoped
public class MapThumbnailRenderer {

  private static final Logger LOG = Logger.getLogger(MapThumbnailRenderer.class);

  public static final String LIGHT_STYLE = "colorful";
  public static final String DARK_STYLE = "eclipse";

  private static final int SIZE = 512;
  private static final double MARGIN = 0.1;

  /** mtime granularity and clock skew: a tile written just before the render started counts. */
  private static final long MTIME_SLACK_MS = 2_000;

  private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G'};
  private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
  private static final byte[] RIFF = {'R', 'I', 'F', 'F'};

  @Inject TileMapProducer tileMapProducer;

  @Inject CacheFolderProvider cacheFolderProvider;

  @ConfigProperty(name = "tileserver.url")
  String tileserverUrl;

  /** Tiles cached while the tileserver was failing would blacken every later render: drop them. */
  void onStart(@Observes StartupEvent event) {
    for (String style : List.of(LIGHT_STYLE, DARK_STYLE)) {
      List<Path> broken = evictBrokenTiles(tileCacheOf(urlPattern(style)), 0);
      if (!broken.isEmpty()) {
        LOG.warnv("Evicted {0} broken cached tile(s) for style {1}", broken.size(), style);
      }
    }
  }

  /**
   * Renders {@code gpx} over the {@code style} map into a 512×512 PNG at {@code output}, one colour
   * per path (cycling through {@code colors}).
   *
   * @throws IOException when the map could not be drawn in full; {@code output} is then deleted.
   */
  public void render(File output, GPX gpx, String style, List<Color> colors) throws IOException {
    String urlPattern = urlPattern(style);
    long start = System.currentTimeMillis();
    try {
      tileMapProducer.createTileMap(output, gpx, urlPattern, MARGIN, SIZE, SIZE, colors);
    } catch (IOException | RuntimeException e) {
      Files.deleteIfExists(output.toPath());
      throw e;
    }
    List<Path> broken = evictBrokenTiles(tileCacheOf(urlPattern), start - MTIME_SLACK_MS);
    if (!broken.isEmpty()) {
      Files.deleteIfExists(output.toPath());
      throw new IOException(
          "Tileserver returned no image for "
              + broken.size()
              + " tile(s) of style "
              + style
              + ", e.g. "
              + tileCacheOf(urlPattern).toPath().relativize(broken.getFirst()));
    }
  }

  private String urlPattern(String style) {
    return tileserverUrl + "/styles/" + style + "/256/{z}/{x}/{y}.png";
  }

  /** Where the producer caches the tiles of {@code style}. */
  File tileCache(String style) {
    return tileCacheOf(urlPattern(style));
  }

  private File tileCacheOf(String urlPattern) {
    return new File(
        cacheFolderProvider.getCacheFolder(), Integer.toHexString(urlPattern.hashCode()));
  }

  /** Deletes and returns the cached tiles modified since {@code sinceMillis} that are no image. */
  static List<Path> evictBrokenTiles(File cacheDir, long sinceMillis) {
    if (!cacheDir.isDirectory()) {
      return List.of();
    }
    List<Path> broken = new ArrayList<>();
    try (Stream<Path> files = Files.walk(cacheDir.toPath())) {
      files
          .filter(Files::isRegularFile)
          .filter(p -> p.toFile().lastModified() >= sinceMillis)
          .filter(p -> !looksLikeImage(p))
          .forEach(broken::add);
    } catch (IOException e) {
      LOG.warnv("Could not scan tile cache {0}: {1}", cacheDir, e.getMessage());
    }
    for (Path p : broken) {
      try {
        Files.deleteIfExists(p);
      } catch (IOException e) {
        LOG.warnv("Could not evict broken tile {0}: {1}", p, e.getMessage());
      }
    }
    return broken;
  }

  static boolean looksLikeImage(Path tile) {
    byte[] head = new byte[4];
    int read;
    try (InputStream in = Files.newInputStream(tile)) {
      read = in.readNBytes(head, 0, head.length);
    } catch (IOException e) {
      return false;
    }
    return startsWith(head, read, PNG)
        || startsWith(head, read, JPEG)
        || startsWith(head, read, RIFF);
  }

  private static boolean startsWith(byte[] head, int read, byte[] magic) {
    return read >= magic.length && Arrays.equals(head, 0, magic.length, magic, 0, magic.length);
  }
}
