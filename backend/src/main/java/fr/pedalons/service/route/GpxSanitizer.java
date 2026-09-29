package fr.pedalons.service.route;

import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.GPXWaypoint;
import io.github.glandais.gpx.data.Point;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Reduces a parsed {@link GPX} to what a route is: the geometry (latitude, longitude, altitude) of
 * its tracks, and its waypoints (position and name). Everything an activity recorder adds on top is
 * dropped — per-point timestamps, heart rate, cadence, power, temperature (docs/LEDGER_*.md API-44).
 *
 * <p>An activity export is health data (heart rate) and a movement log (when the member rode,
 * how fast, where they stopped). Pédalons never uses any of it: distance, elevation, climbs, wind
 * and the track itself are pure geometry, and no client reads a {@code <time>} from a served file.
 *
 * <p><b>Timestamps become {@link Instant#EPOCH}, not {@code null}.</b> That is what the GPX reader
 * already assigns to a point without a {@code <time>} — the common case for a planned route — and
 * what {@link GpxProcessingService#fromPoints} assigns to a drawn one; the pipeline (per-point
 * arrays, FIT course timestamps) relies on a non-null instant. A sanitized track is therefore
 * indistinguishable from a route that never had times, and every stored file (original, filtered,
 * FIT) carries the same constant instead of the ride's clock.
 *
 * <p>Document metadata needs no work here: the reader keeps only its name (the route's name), so
 * author, e-mail, link, creator and {@code <metadata><time>} never survive parsing. Waypoints keep
 * the position and name the reader extracts — it drops their time, description and type already.
 */
public final class GpxSanitizer {

  private GpxSanitizer() {}

  /**
   * Strips timestamps and sensor values from every track point, in place, and rebuilds each
   * waypoint from its position and name alone. Idempotent.
   *
   * <p>In place because the route pipeline mutates the {@link GPX} it is handed and callers keep
   * using that same object afterwards (to write the FIT course): a sanitized copy would leave them
   * holding the raw one.
   */
  public static void sanitize(GPX gpx) {
    for (GPXPath path : gpx.paths()) {
      List<Point> points = path.getPoints();
      List<Point> clean = new ArrayList<>(points.size());
      for (Point point : points) {
        clean.add(trackPoint(point));
      }
      // setPoints copies the list and recomputes the per-point arrays (distance, elevation, time).
      path.setPoints(clean);
    }
    List<GPXWaypoint> waypoints = gpx.waypoints();
    if (!waypoints.isEmpty()) {
      ListIterator<GPXWaypoint> it = waypoints.listIterator();
      while (it.hasNext()) {
        GPXWaypoint waypoint = it.next();
        it.set(new GPXWaypoint(waypoint.name(), position(waypoint.point())));
      }
    }
  }

  private static Point trackPoint(Point source) {
    Point point = position(source);
    point.setEle(source.getEle());
    point.setInstant(null, Instant.EPOCH);
    return point;
  }

  private static Point position(Point source) {
    Point point = new Point();
    point.setLat(source.getLat());
    point.setLon(source.getLon());
    return point;
  }
}
