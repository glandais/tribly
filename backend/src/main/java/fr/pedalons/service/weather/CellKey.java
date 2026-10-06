package fr.pedalons.service.weather;

import org.jspecify.annotations.Nullable;

/**
 * The key of a {@code WeatherCell}: a 0.05° cell (~5.5 km north–south, less east–west away from the
 * equator) and, when the altitude is known, a 100 m band.
 *
 * <p>A code constant, not a setting: changing the grid orphans every cached row and re-keys every
 * cell, which is a migration, not a restart.
 *
 * <p>Rounding is {@code floor(x + 0.5)} — {@link Math#round} — on both sides of the query: {@link
 * #SQL_LAT_IDX} and {@link #SQL_LON_IDX} spell the same thing for the list's native query, where a
 * PostgreSQL {@code round()} would round half away from zero and disagree on negative coordinates.
 *
 * @param eleBand metres, a multiple of {@value #ELEVATION_BAND}; null at the departure point, whose
 *     altitude is unknown (the provider's own elevation model applies then)
 */
public record CellKey(int latIdx, int lonIdx, @Nullable Integer eleBand) {

  /** Cell size, in degrees of latitude and of longitude alike. */
  public static final double STEP_DEGREES = 0.05;

  /** Height of an elevation band, in metres. */
  public static final int ELEVATION_BAND = 100;

  /** {@link #latIdx} of the point {@code ?1} (a geometry), in SQL. */
  public static final String SQL_LAT_IDX =
      "cast(floor(st_y(?1) / " + STEP_DEGREES + " + 0.5) as integer)";

  /** {@link #lonIdx} of the point {@code ?1} (a geometry), in SQL. */
  public static final String SQL_LON_IDX =
      "cast(floor(st_x(?1) / " + STEP_DEGREES + " + 0.5) as integer)";

  /** The cell of a point, with its elevation band when {@code elevation} is known. */
  public static CellKey of(double lat, double lon, @Nullable Double elevation) {
    Integer band =
        elevation == null || elevation.isNaN()
            ? null
            : (int) Math.round(elevation / ELEVATION_BAND) * ELEVATION_BAND;
    return new CellKey(index(lat), index(lon), band);
  }

  /** The departure cell of a point: no elevation band. */
  public static CellKey of(double lat, double lon) {
    return of(lat, lon, null);
  }

  static int index(double degrees) {
    return (int) Math.round(degrees / STEP_DEGREES);
  }

  /** Latitude of the centre of the cell — what the provider is asked about. */
  public double centerLat() {
    return center(latIdx);
  }

  public double centerLon() {
    return center(lonIdx);
  }

  /** The same place without its elevation band. */
  public CellKey withoutElevation() {
    return eleBand == null ? this : new CellKey(latIdx, lonIdx, null);
  }

  private static double center(int index) {
    // Rounded to the grid's own precision so 0.05 * 917 prints as 45.85, not 45.850000000000001.
    return Math.round(index * STEP_DEGREES * 100d) / 100d;
  }
}
