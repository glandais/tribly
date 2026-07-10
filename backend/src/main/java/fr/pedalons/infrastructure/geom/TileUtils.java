package fr.pedalons.infrastructure.geom;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.dto.error.ErrorCode;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Polygon;

/**
 * Slippy map tile helpers (XYZ scheme, Web Mercator).
 */
public final class TileUtils {

  /** Beyond this zoom a tile is smaller than a GPS point's accuracy. */
  public static final int MAX_ZOOM = 22;

  private TileUtils() {}

  /**
   * Bounds of tile {@code z/x/y} as a WGS84 polygon.
   *
   * <p>PostGIS has {@code ST_TileEnvelope}, but it returns Web Mercator. Comparing against the
   * 4326 geometries directly lets {@code idx_gpx_tracks_geometry} (a GIST index on the 4326
   * column) do the work, whereas {@code ST_Transform(geometry, 3857) && ...} cannot use it.
   */
  public static Polygon<G2D> tileEnvelope4326(int z, int x, int y) {
    validate(z, x, y);
    double tiles = Math.pow(2, z);
    double west = x / tiles * 360.0 - 180.0;
    double east = (x + 1) / tiles * 360.0 - 180.0;
    double north = latitudeOf(y, tiles);
    double south = latitudeOf(y + 1, tiles);
    return polygon(
        WGS84,
        ring(g(west, south), g(east, south), g(east, north), g(west, north), g(west, south)));
  }

  public static void validate(int z, int x, int y) {
    int max = 1 << z;
    if (z < 0 || z > MAX_ZOOM || x < 0 || x >= max || y < 0 || y >= max) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST);
    }
  }

  private static double latitudeOf(int y, double tiles) {
    return Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * y / tiles))));
  }
}
