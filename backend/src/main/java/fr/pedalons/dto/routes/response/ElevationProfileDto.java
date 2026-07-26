package fr.pedalons.dto.routes.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.domain.route.Route;
import fr.pedalons.dto.validation.ValidateSchema;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A route's elevation profile, resampled to a size a phone can download and draw.
 *
 * <p>The stored track holds one point every ten meters, which is several megabytes of JSON for a
 * long route — far more than a profile chart a few hundred pixels wide can use. This DTO carries the
 * same curve at the resolution actually asked for, plus the grade of each segment so the client can
 * colour the profile without recomputing anything.
 */
@Schema(description = "Sampled elevation profile of a route, ready to draw")
@ValidateSchema
public record ElevationProfileDto(
    @Schema(description = "Route ID (TSID)", required = true) String routeId,
    @Schema(description = "Route slug", required = true) String slug,
    @Schema(description = "Distance covered by the profile, in meters", required = true)
        double distance,
    @Schema(description = "Lowest elevation of the profile, in meters", required = true)
        double minElevation,
    @Schema(description = "Highest elevation of the profile, in meters", required = true)
        double maxElevation,
    @Schema(
            description =
                "Number of points actually returned. Never more than the number of points stored"
                    + " for the route, so a short track is not artificially upsampled.",
            required = true)
        int samples,
    @Schema(description = "Profile points, by increasing distance", required = true)
        List<ElevationPointDto> points) {

  /**
   * Resamples the route's tracks at {@code samples} evenly spaced distances.
   *
   * <p>The route's tracks are walked in order and concatenated, each one's cumulative distance
   * shifted by the total of those before it, so a multi-track route yields one continuous profile.
   * Elevation is linearly interpolated between the two stored points surrounding each sample, and
   * the grade of a sample is that of the segment ending on it — the segment a client colours.
   *
   * <p>Pure in-memory work on a single already-loaded route: no extra query, whatever {@code
   * samples} is.
   *
   * @param samples the requested number of points, expected to be already clamped by the caller
   */
  public static ElevationProfileDto from(Route route, int samples) {
    Concatenated track = Concatenated.of(route);
    double[] distances = track.distances();
    double[] elevations = track.elevations();
    int stored = distances.length;

    String id = TsidUtils.toString(route.getId());
    if (stored == 0) {
      return new ElevationProfileDto(id, route.getSlug(), 0, 0, 0, 0, List.of());
    }

    // Never invent resolution the stored track does not have.
    int count = Math.min(Math.max(samples, 1), stored);
    double total = distances[stored - 1];

    List<ElevationPointDto> points = new ArrayList<>(count);
    double minElevation = Double.MAX_VALUE;
    double maxElevation = -Double.MAX_VALUE;
    double previousDistance = 0;
    double previousElevation = 0;
    int cursor = 0;
    for (int i = 0; i < count; i++) {
      double target = count == 1 ? total : total * i / (count - 1);
      cursor = advance(distances, cursor, target);
      double elevation = interpolate(distances, elevations, cursor, target);
      double step = target - previousDistance;
      double grade = i == 0 || step <= 0 ? 0 : (elevation - previousElevation) / step * 100;
      points.add(new ElevationPointDto(round(target, 10), round(elevation, 10), round(grade, 100)));
      minElevation = Math.min(minElevation, elevation);
      maxElevation = Math.max(maxElevation, elevation);
      previousDistance = target;
      previousElevation = elevation;
    }
    return new ElevationProfileDto(
        id,
        route.getSlug(),
        round(total, 10),
        round(minElevation, 10),
        round(maxElevation, 10),
        points.size(),
        points);
  }

  /** The index of the last stored point at or before {@code target}, scanned forward only. */
  private static int advance(double[] distances, int cursor, double target) {
    int index = cursor;
    while (index + 1 < distances.length && distances[index + 1] < target) {
      index++;
    }
    return index;
  }

  private static double interpolate(
      double[] distances, double[] elevations, int index, double target) {
    if (index + 1 >= distances.length) {
      return elevations[distances.length - 1];
    }
    double span = distances[index + 1] - distances[index];
    if (span <= 0) {
      return elevations[index];
    }
    double ratio = Math.clamp((target - distances[index]) / span, 0, 1);
    return elevations[index] + ratio * (elevations[index + 1] - elevations[index]);
  }

  /**
   * A route's tracks laid end to end as two primitive arrays.
   *
   * <p>Each track stores its own cumulative distance starting back at zero, so every track after the
   * first is shifted past the end of the previous one — otherwise a two-track route would fold back
   * on itself halfway through the profile.
   */
  private record Concatenated(double[] distances, double[] elevations) {

    static Concatenated of(Route route) {
      List<GpxTrack.TrackPoint> all = new ArrayList<>();
      List<Double> offsets = new ArrayList<>();
      double previousTrackEnd = 0;
      for (GpxTrack track : route.getTracks()) {
        List<GpxTrack.TrackPoint> trackPoints = track.getTrackPoints();
        if (trackPoints == null || trackPoints.isEmpty()) {
          continue;
        }
        double offset = previousTrackEnd - trackPoints.getFirst().dist();
        for (GpxTrack.TrackPoint point : trackPoints) {
          all.add(point);
          offsets.add(offset);
        }
        previousTrackEnd = trackPoints.getLast().dist() + offset;
      }
      double[] distances = new double[all.size()];
      double[] elevations = new double[all.size()];
      for (int i = 0; i < all.size(); i++) {
        distances[i] = all.get(i).dist() + offsets.get(i);
        elevations[i] = all.get(i).ele();
      }
      return new Concatenated(distances, elevations);
    }
  }

  private static double round(double value, double factor) {
    return Math.round(value * factor) / factor;
  }
}
