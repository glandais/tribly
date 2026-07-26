package fr.pedalons.service.route;

import fr.pedalons.domain.route.GpxTrack.TrackPoint;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import org.jspecify.annotations.Nullable;

/**
 * In-memory decimation of a stored track.
 *
 * <p>What a client renders is not the PostGIS {@code LineString} — that one is 2D and exists only so
 * spatial queries have something to index — but the jsonb {@code trackPoints}, which carry the
 * elevation and the cumulative distance the profile and the tooltips need. {@code ST_Simplify} is
 * therefore not usable here: the decimation has to happen in Java, once the row has been read.
 *
 * <p>Both algorithms below always keep the first and the last point, so the decimated line still
 * starts and ends where the route does, and both preserve the source ordering.
 *
 * <p>Distances are computed on a local equirectangular projection centred on the track: over a
 * single route (a few hundred kilometres at most) the error stays far below any tolerance a caller
 * would ask for, and it costs two multiplications per point instead of a haversine.
 */
public final class TrackGeometry {

  private static final double METERS_PER_DEGREE_LAT = 110_574.0;
  private static final double METERS_PER_DEGREE_LON = 111_320.0;

  private TrackGeometry() {}

  /**
   * Douglas-Peucker: drops every point lying closer than {@code toleranceMeters} to the line joining
   * the points that survive around it. The classic "simplify" semantics — the result is guaranteed
   * to stay within {@code toleranceMeters} of the source line, but its size is not known in advance.
   *
   * @param toleranceMeters a tolerance of zero or less returns the source list untouched
   */
  public static List<TrackPoint> simplify(List<TrackPoint> points, double toleranceMeters) {
    int n = points.size();
    if (toleranceMeters <= 0 || n <= 2) {
      return points;
    }
    Projected projected = Projected.of(points);

    boolean[] keep = new boolean[n];
    keep[0] = true;
    keep[n - 1] = true;

    Deque<int[]> stack = new ArrayDeque<>();
    stack.push(new int[] {0, n - 1});
    while (!stack.isEmpty()) {
      int[] segment = stack.pop();
      Deviation deviation = projected.farthest(segment[0], segment[1], false);
      if (deviation != null && deviation.distance() > toleranceMeters) {
        keep[deviation.index()] = true;
        stack.push(new int[] {segment[0], deviation.index()});
        stack.push(new int[] {deviation.index(), segment[1]});
      }
    }
    return collect(points, keep);
  }

  /**
   * Decimates down to at most {@code maxPoints}, keeping the points that matter rather than one out
   * of every k.
   *
   * <p>Same recursive split as Douglas-Peucker, but driven by a priority queue: the segment whose
   * worst point deviates the most is always the next to be split, so the budget goes to the corners
   * and the summits and never to a straight, flat stretch. The deviation measured here is the larger
   * of the horizontal offset and the elevation offset from the chord, both in meters — which is what
   * makes the local extrema (a col, a dip) survive instead of being averaged away.
   *
   * @param maxPoints two or less, or a value at least as large as the source, returns the source
   *     list untouched
   */
  public static List<TrackPoint> decimateTo(List<TrackPoint> points, int maxPoints) {
    int n = points.size();
    if (maxPoints <= 2 || maxPoints >= n || n <= 2) {
      return points;
    }
    Projected projected = Projected.of(points);

    boolean[] keep = new boolean[n];
    keep[0] = true;
    keep[n - 1] = true;
    int kept = 2;

    PriorityQueue<Deviation> queue =
        new PriorityQueue<>(Comparator.comparingDouble(Deviation::distance).reversed());
    offer(queue, projected, 0, n - 1);
    while (kept < maxPoints && !queue.isEmpty()) {
      Deviation deviation = queue.poll();
      if (deviation.distance() <= 0) {
        // Everything left sits exactly on its chord: no remaining point is worth the budget.
        break;
      }
      keep[deviation.index()] = true;
      kept++;
      offer(queue, projected, deviation.first(), deviation.index());
      offer(queue, projected, deviation.index(), deviation.last());
    }
    return collect(points, keep);
  }

  private static void offer(
      PriorityQueue<Deviation> queue, Projected projected, int first, int last) {
    Deviation deviation = projected.farthest(first, last, true);
    if (deviation != null && deviation.distance() > 0) {
      queue.add(deviation);
    }
  }

  private static List<TrackPoint> collect(List<TrackPoint> points, boolean[] keep) {
    List<TrackPoint> result = new ArrayList<>();
    for (int i = 0; i < keep.length; i++) {
      if (keep[i]) {
        result.add(points.get(i));
      }
    }
    return result;
  }

  /** The track flattened into primitive arrays: metric x/y, elevation and cumulative distance. */
  private record Projected(double[] x, double[] y, double[] ele, double[] dist) {

    static Projected of(List<TrackPoint> points) {
      int n = points.size();
      double[] x = new double[n];
      double[] y = new double[n];
      double[] ele = new double[n];
      double[] dist = new double[n];
      double latitudeScale = Math.cos(Math.toRadians(points.getFirst().lat()));
      for (int i = 0; i < n; i++) {
        TrackPoint point = points.get(i);
        x[i] = point.lng() * METERS_PER_DEGREE_LON * latitudeScale;
        y[i] = point.lat() * METERS_PER_DEGREE_LAT;
        ele[i] = point.ele();
        dist[i] = point.dist();
      }
      return new Projected(x, y, ele, dist);
    }

    /**
     * The point of {@code ]first, last[} lying farthest from the {@code first -> last} chord, or null
     * when the segment holds no intermediate point.
     */
    @Nullable Deviation farthest(int first, int last, boolean withElevation) {
      if (last <= first + 1) {
        return null;
      }
      double ax = x[first];
      double ay = y[first];
      double dx = x[last] - ax;
      double dy = y[last] - ay;
      double lengthSquared = dx * dx + dy * dy;
      double span = dist[last] - dist[first];

      int bestIndex = -1;
      double bestDistance = -1;
      for (int i = first + 1; i < last; i++) {
        double px = x[i] - ax;
        double py = y[i] - ay;
        double t = lengthSquared == 0 ? 0 : (px * dx + py * dy) / lengthSquared;
        double clamped = t < 0 ? 0 : Math.min(t, 1);
        double offsetX = px - clamped * dx;
        double offsetY = py - clamped * dy;
        double distance = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        if (withElevation) {
          double ratio = span == 0 ? clamped : (dist[i] - dist[first]) / span;
          double chordEle = ele[first] + ratio * (ele[last] - ele[first]);
          distance = Math.max(distance, Math.abs(ele[i] - chordEle));
        }
        if (distance > bestDistance) {
          bestDistance = distance;
          bestIndex = i;
        }
      }
      return bestIndex < 0 ? null : new Deviation(first, last, bestIndex, bestDistance);
    }
  }

  /** The worst point of a chord, and how far off it is, in meters. */
  private record Deviation(int first, int last, int index, double distance) {}
}
