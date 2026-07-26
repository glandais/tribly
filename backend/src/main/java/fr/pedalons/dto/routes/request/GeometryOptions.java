package fr.pedalons.dto.routes.request;

import org.jspecify.annotations.Nullable;

/**
 * How much of a route's geometry the caller wants back.
 *
 * <p>Both knobs are opt-in and both are clamped here rather than at the edge, so no request can ask
 * the server to allocate an unbounded amount of work: {@link #FULL} — nothing asked — is the
 * pre-existing behaviour and returns the stored track untouched.
 *
 * <p>{@code simplifyMeters} and {@code maxPoints} compose: the tolerance is applied first, the point
 * budget second, so {@code ?simplify=10&points=500} means "drop anything within 10 m of the line,
 * then keep the 500 most significant of what is left".
 */
public record GeometryOptions(double simplifyMeters, int maxPoints) {

  /** A tolerance beyond this would flatten a route into a straight line; nothing asks for that. */
  public static final double MAX_SIMPLIFY_METERS = 1_000;

  /** A point budget beyond this is not a decimation any more, it is the full track. */
  public static final int MAX_POINTS = 100_000;

  /** Below three points a "decimated" line is just its two endpoints. */
  public static final int MIN_POINTS = 2;

  /** The stored track, untouched — what every caller that passes no parameter gets. */
  public static final GeometryOptions FULL = new GeometryOptions(0, 0);

  /**
   * Builds the options from the raw query parameters, clamping both into a range the server is
   * willing to serve. A null or non-positive value means "not asked for".
   */
  public static GeometryOptions of(@Nullable Double simplifyMeters, @Nullable Integer maxPoints) {
    double tolerance =
        simplifyMeters == null || simplifyMeters <= 0
            ? 0
            : Math.min(simplifyMeters, MAX_SIMPLIFY_METERS);
    int points =
        maxPoints == null || maxPoints <= 0
            ? 0
            : Math.min(Math.max(maxPoints, MIN_POINTS), MAX_POINTS);
    if (tolerance == 0 && points == 0) {
      return FULL;
    }
    return new GeometryOptions(tolerance, points);
  }

  /** Whether the stored track should be returned as is. */
  public boolean isFull() {
    return simplifyMeters <= 0 && maxPoints <= 0;
  }
}
