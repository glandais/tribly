package fr.pedalons.service.weather;

import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * The forecast points of a route: the start, one every {@code sample-step-km}, the finish. Internal
 * to the server — the coordinates here never reach a DTO.
 *
 * @param totalDistance metres, all tracks chained
 */
public record RouteSamples(List<Sample> samples, double totalDistance) {

  public static final RouteSamples EMPTY = new RouteSamples(List.of(), 0);

  public boolean isEmpty() {
    return samples.isEmpty();
  }

  /**
   * One point, and the geometry of the stretch to the next one reduced to what the wind needs.
   *
   * <p>The head component averaged over a stretch, weighted by length, is {@code V · (cos φ · Σ
   * l·cos b + sin φ · Σ l·sin b) / Σ l} for a wind of speed {@code V} from {@code φ} over
   * sub-segments of length {@code l} and bearing {@code b}: the two sums are all there is to keep,
   * whatever the number of track points.
   *
   * @param distance metres from the start, the track point's own cumulative distance
   * @param elevation metres; null when the track carries no altitude
   * @param sumCos {@code Σ l·cos b} over the sub-segments up to the next sample (0 for the finish)
   * @param sumSin {@code Σ l·sin b}
   * @param length {@code Σ l}, metres
   */
  public record Sample(
      int index,
      double distance,
      double lat,
      double lon,
      @Nullable Double elevation,
      double sumCos,
      double sumSin,
      double length) {

    public CellKey cell() {
      return CellKey.of(lat, lon, elevation);
    }
  }
}
