package fr.pedalons.common;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

/**
 * Blurs a point to a fixed grid so it can be published without publishing an address.
 *
 * <p>A classified ad's location is, in practice, the seller's home. Showing the exact pin on a map
 * that every team member can open turns "bike for sale" into "this bike is at this address" — an
 * expensive mistake to discover after the fact, and one that no amount of read-only-ness undoes.
 * What a buyer actually needs is whether the bike is a ten-minute ride away or two hours; a cell
 * about a kilometre across answers that and nothing more.
 *
 * <p>Snapping to the <b>centre of a fixed cell</b> rather than adding noise or truncating is what
 * makes the blur hold: the output is stable across calls, so repeated reads cannot be averaged back
 * towards the true position, and it never drifts towards a corner the way truncation does.
 *
 * <p>The cell is defined in degrees of latitude and widened in longitude by {@code 1/cos(lat)}, so
 * it stays roughly square in metres from Marseille to Tromsø instead of collapsing to a sliver.
 */
public final class CoarseLocation {

  /** Cell height in degrees of latitude: 0.01° ≈ 1.11 km. */
  private static final double CELL_DEGREES = 0.01;

  /** The same cell expressed in metres, for quantising proximity radii. */
  private static final double CELL_METRES = 1_110;

  /**
   * Floor on {@code cos(lat)} when widening the cell in longitude. Without it the width explodes at
   * the poles; 0.05 caps it at 20× the cell height, reached around 87° of latitude.
   */
  private static final double MIN_COS_LATITUDE = 0.05;

  private CoarseLocation() {}

  /**
   * The centre of the ~1 km cell containing {@code exact}.
   *
   * @return {@code null} when {@code exact} is null or empty, so an ad with no location stays an ad
   *     with no location rather than gaining a point in the Gulf of Guinea
   */
  public static @Nullable Point<G2D> blur(@Nullable Point<G2D> exact) {
    if (exact == null || exact.isEmpty()) {
      return null;
    }
    G2D position = exact.getPosition();
    double lat = snap(position.getLat(), CELL_DEGREES);
    double cosLat = Math.max(Math.cos(Math.toRadians(lat)), MIN_COS_LATITUDE);
    double lon = snap(position.getLon(), CELL_DEGREES / cosLat);
    return point(WGS84, g(lon, lat));
  }

  /**
   * Rounds a proximity radius up to a whole cell, with one cell as the floor.
   *
   * <p>A filter that reads the exact column while the API only publishes the blurred point is an
   * oracle. Quantising the radius alongside the probe point keeps every answerable question inside
   * the resolution the blurred point already gives away.
   */
  public static double snapRadiusUp(double metres) {
    return Math.max(1, Math.ceil(metres / CELL_METRES)) * CELL_METRES;
  }

  /** The centre of the {@code step}-wide cell containing {@code value}. */
  private static double snap(double value, double step) {
    double centre = (Math.floor(value / step) + 0.5) * step;
    // Six decimals is ~0.1 m: enough to keep the centre exact, short enough to keep the JSON from
    // advertising a precision the value does not have.
    return Math.round(centre * 1_000_000d) / 1_000_000d;
  }
}
