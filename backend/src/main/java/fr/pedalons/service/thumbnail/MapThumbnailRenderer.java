package fr.pedalons.service.thumbnail;

import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.map.TileMapProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * The one way to draw a track on the tileserver's map: every thumbnail (route, ride, trip, GPX
 * preview) goes through here.
 *
 * <p>A tile the tileserver fails to serve fails the whole render (gpx2web ≥ 1.5.1 checks the HTTP
 * status and caches only real tiles): a thumbnail is either a full map or nothing, never a track
 * on a black background.
 */
@ApplicationScoped
public class MapThumbnailRenderer {

  public static final String LIGHT_STYLE = "colorful";
  public static final String DARK_STYLE = "eclipse";

  private static final int SIZE = 512;
  private static final double MARGIN = 0.1;

  @Inject TileMapProducer tileMapProducer;

  @ConfigProperty(name = "tileserver.url")
  String tileserverUrl;

  /**
   * Renders {@code gpx} over the {@code style} map into a 512×512 PNG at {@code output}, one colour
   * per path (cycling through {@code colors}).
   *
   * @throws IOException when the map could not be drawn in full; {@code output} is then deleted.
   */
  public void render(File output, GPX gpx, String style, List<Color> colors) throws IOException {
    String urlPattern = tileserverUrl + "/styles/" + style + "/256/{z}/{x}/{y}.png";
    try {
      tileMapProducer.createTileMap(output, gpx, urlPattern, MARGIN, SIZE, SIZE, colors);
    } catch (IOException | RuntimeException e) {
      Files.deleteIfExists(output.toPath());
      throw e;
    }
  }
}
