package fr.pedalons.service.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.domain.route.GpxTrack.TrackPoint;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Where the forecast points of a route fall, and what they keep of its geometry. */
class RouteSampleLookupTest {

  /** A straight track due north from 45°N, one point every 100 m, {@code km} long. */
  private static List<TrackPoint> northbound(double km, double startDist, double ele) {
    List<TrackPoint> points = new ArrayList<>();
    double metresPerDegree = 111_195;
    for (int i = 0; i <= km * 10; i++) {
      double d = i * 100.0;
      points.add(new TrackPoint(45 + d / metresPerDegree, 5, ele, startDist + d));
    }
    return points;
  }

  @Test
  void sample_92km_shouldDropTheSampleTooCloseToTheFinish() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(92, 0, 300)), 15_000);

    List<Double> distances = samples.samples().stream().map(RouteSamples.Sample::distance).toList();
    // 90 km is within 5 km of the 92 km finish: the finish says it.
    assertEquals(
        List.of(0.0, 15_000.0, 30_000.0, 45_000.0, 60_000.0, 75_000.0, 92_000.0), distances);
    assertEquals(92_000, samples.totalDistance(), 0.001);
  }

  @Test
  void sample_shouldKeepTheBearingOfEachStretch() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(30, 0, 300)), 15_000);

    RouteSamples.Sample first = samples.samples().getFirst();
    // Due north: all the length on the cosine side.
    assertEquals(15_000, first.length(), 1);
    assertEquals(15_000, first.sumCos(), 1);
    assertEquals(0, first.sumSin(), 1);
    // The finish opens no stretch.
    assertEquals(0, samples.samples().getLast().length(), 0.001);
  }

  @Test
  void sample_shouldChainTracksWithAContinuousDistance() {
    // Two 20 km tracks, the second one's own distance restarting at 0.
    RouteSamples samples =
        RouteSampleLookup.sample(List.of(northbound(20, 0, 300), northbound(20, 0, 300)), 15_000);

    assertEquals(40_000, samples.totalDistance(), 0.001);
    List<Double> distances = samples.samples().stream().map(RouteSamples.Sample::distance).toList();
    assertEquals(List.of(0.0, 15_000.0, 30_000.0, 40_000.0), distances);
    // The jump from the end of the first track back to the start of the second counts for no
    // length, whatever the distance between them on the map.
    double total = samples.samples().stream().mapToDouble(RouteSamples.Sample::length).sum();
    assertEquals(40_000, total, 1);
  }

  @Test
  void sample_aTrackWithoutAltitude_shouldHaveNoElevationBand() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(10, 0, 0)), 15_000);

    assertTrue(samples.samples().stream().allMatch(s -> s.elevation() == null));
    assertNull(samples.samples().getFirst().cell().eleBand());
  }

  @Test
  void sample_aShortRoute_shouldHaveStartAndFinishOnly() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(3, 0, 300)), 15_000);

    assertEquals(2, samples.samples().size());
  }

  /** The same, due south from {@code fromLat}. */
  private static List<TrackPoint> southbound(double km, double fromLat, double startDist) {
    List<TrackPoint> points = new ArrayList<>();
    for (int i = 0; i <= km * 10; i++) {
      double d = i * 100.0;
      points.add(new TrackPoint(fromLat - d / 111_195, 5, 300, startDist + d));
    }
    return points;
  }

  private static List<Double> distances(RouteSamples samples) {
    return samples.samples().stream().map(RouteSamples.Sample::distance).toList();
  }

  @Test
  void sample_shouldStepEvery15km() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(60, 0, 300)), 15_000);

    // 45 km is 15 km from the finish: kept; 60 km is the finish itself.
    assertEquals(List.of(0.0, 15_000.0, 30_000.0, 45_000.0, 60_000.0), distances(samples));
    assertEquals(
        List.of(0, 1, 2, 3, 4),
        samples.samples().stream().map(RouteSamples.Sample::index).toList());
  }

  @Test
  void sample_justOver5kmFromTheFinish_isKept() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(51, 0, 300)), 15_000);

    assertEquals(List.of(0.0, 15_000.0, 30_000.0, 45_000.0, 51_000.0), distances(samples));
  }

  @Test
  void sample_under5kmFromTheFinish_isDropped() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(49, 0, 300)), 15_000);

    assertEquals(List.of(0.0, 15_000.0, 30_000.0, 49_000.0), distances(samples));
  }

  @Test
  void sample_aRouteShorterThanTheStep_isStartAndFinish() {
    RouteSamples samples = RouteSampleLookup.sample(List.of(northbound(12, 0, 300)), 15_000);

    assertEquals(List.of(0.0, 12_000.0), distances(samples));
    // The start opens the whole route as one stretch.
    assertEquals(12_000, samples.samples().getFirst().length(), 1);
  }

  @Test
  void sample_aOnePointTrack_isItsStartOnly() {
    RouteSamples samples =
        RouteSampleLookup.sample(List.of(List.of(new TrackPoint(45, 5, 300, 0))), 15_000);

    assertEquals(1, samples.samples().size());
    assertEquals(0, samples.totalDistance());
    assertEquals(0, samples.samples().getFirst().length());
  }

  @Test
  void sample_noTrack_isEmpty() {
    assertTrue(RouteSampleLookup.sample(List.of(), 15_000).isEmpty());
    assertTrue(RouteSampleLookup.sample(List.of(List.of()), 15_000).isEmpty());
  }

  @Test
  void sample_chainedTracks_shouldIgnoreTheirOwnDistanceOrigin() {
    // The second track's own distance starts at 7 km (a split file): chained, it starts at 20 km.
    RouteSamples samples =
        RouteSampleLookup.sample(
            List.of(northbound(20, 0, 300), northbound(20, 7_000, 300)), 15_000);

    assertEquals(40_000, samples.totalDistance(), 0.001);
    assertEquals(List.of(0.0, 15_000.0, 30_000.0, 40_000.0), distances(samples));
  }

  @Test
  void sample_chainedTracks_shouldKeepTheirOrderAndBearings() {
    // 20 km north, then 20 km back south from where the first ended.
    double top = 45 + 20_000 / 111_195.0;
    RouteSamples samples =
        RouteSampleLookup.sample(List.of(northbound(20, 0, 300), southbound(20, top, 0)), 15_000);

    List<RouteSamples.Sample> points = samples.samples();
    assertEquals(List.of(0.0, 15_000.0, 30_000.0, 40_000.0), distances(samples));
    // 0 → 15 km: all north.
    assertEquals(15_000, points.get(0).sumCos(), 1);
    // 15 → 30 km: 5 km north, then 10 km south.
    assertEquals(-5_000, points.get(1).sumCos(), 1);
    assertEquals(15_000, points.get(1).length(), 1);
    // 30 → 40 km: all south, on the second track.
    assertEquals(-10_000, points.get(2).sumCos(), 1);
    // The finish is back near the start latitude.
    assertEquals(45, points.getLast().lat(), 0.001);
  }

  @Test
  void bearing_shouldBeClockwiseFromNorth() {
    assertEquals(0, RouteSampleLookup.bearing(45, 5, 46, 5), 0.01);
    assertEquals(90, RouteSampleLookup.bearing(0, 5, 0, 6), 0.01);
    assertEquals(180, RouteSampleLookup.bearing(46, 5, 45, 5), 0.01);
    assertEquals(270, RouteSampleLookup.bearing(0, 6, 0, 5), 0.01);
  }
}
