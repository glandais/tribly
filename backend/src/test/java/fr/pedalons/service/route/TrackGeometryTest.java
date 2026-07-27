package fr.pedalons.service.route;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.domain.route.GpxTrack.TrackPoint;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TrackGeometry}. No database, no HTTP: pure geometry.
 */
class TrackGeometryTest {

  /** A straight west-east line with a wobble of about a meter on every other point. */
  private static List<TrackPoint> wobblyLine(int count) {
    List<TrackPoint> points = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      double wobble = i % 2 == 0 ? 0 : 0.00001; // ~1.1 m
      points.add(new TrackPoint(45.0 + wobble, 6.0 + i * 0.001, 100, i * 78.0));
    }
    return points;
  }

  @Nested
  @DisplayName("simplify")
  class Simplify {

    @Test
    void shouldReturnSourceWhenToleranceIsZeroOrNegative() {
      List<TrackPoint> points = wobblyLine(21);
      assertSame(points, TrackGeometry.simplify(points, 0));
      assertSame(points, TrackGeometry.simplify(points, -5));
    }

    @Test
    void shouldCollapseAWobbleSmallerThanTheTolerance() {
      List<TrackPoint> simplified = TrackGeometry.simplify(wobblyLine(21), 10);
      assertEquals(2, simplified.size());
    }

    @Test
    void shouldKeepAWobbleLargerThanTheTolerance() {
      assertEquals(21, TrackGeometry.simplify(wobblyLine(21), 0.1).size());
    }

    @Test
    void shouldAlwaysKeepFirstAndLastPoint() {
      List<TrackPoint> source = wobblyLine(21);
      List<TrackPoint> simplified = TrackGeometry.simplify(source, 50);
      assertEquals(source.getFirst(), simplified.getFirst());
      assertEquals(source.getLast(), simplified.getLast());
    }

    @Test
    void shouldHandleTracksTooShortToSimplify() {
      List<TrackPoint> two = wobblyLine(2);
      assertSame(two, TrackGeometry.simplify(two, 100));
      assertEquals(List.of(), TrackGeometry.simplify(List.of(), 100));
    }
  }

  @Nested
  @DisplayName("decimateTo")
  class DecimateTo {

    @Test
    void shouldReturnSourceWhenBudgetIsAtLeastTheSourceSize() {
      List<TrackPoint> points = wobblyLine(21);
      assertSame(points, TrackGeometry.decimateTo(points, 21));
      assertSame(points, TrackGeometry.decimateTo(points, 100));
      assertSame(points, TrackGeometry.decimateTo(points, 0));
    }

    @Test
    void shouldNeverExceedTheBudget() {
      assertTrue(TrackGeometry.decimateTo(wobblyLine(101), 10).size() <= 10);
    }

    @Test
    void shouldAlwaysKeepFirstAndLastPoint() {
      List<TrackPoint> source = wobblyLine(101);
      List<TrackPoint> decimated = TrackGeometry.decimateTo(source, 5);
      assertEquals(source.getFirst(), decimated.getFirst());
      assertEquals(source.getLast(), decimated.getLast());
    }

    /**
     * Defect B1-b: {@code maxPoints <= 2} used to be treated the same as "not asked for" (0) and
     * short-circuited to the untouched source — so a caller asking for the smallest budget the
     * contract allows (2, {@link fr.pedalons.dto.routes.request.GeometryOptions#MIN_POINTS}) got
     * back the full stored track instead, megabytes for a long route. The algorithm already
     * terminates at exactly two points once both endpoints are kept and the budget is met, so all
     * that was needed was to stop taking the shortcut for a small positive budget.
     */
    @Test
    void shouldReturnJustTheEndpointsWhenTheBudgetIsTwo() {
      List<TrackPoint> source = wobblyLine(21);
      List<TrackPoint> decimated = TrackGeometry.decimateTo(source, 2);
      assertEquals(2, decimated.size());
      assertEquals(source.getFirst(), decimated.getFirst());
      assertEquals(source.getLast(), decimated.getLast());
      assertNotSame(source, decimated, "a budget of 2 on a 21-point source must decimate");
    }

    /**
     * A budget below two cannot be honoured literally — both endpoints are always kept — but it
     * must not fall back to the untouched-source shortcut either: two points beats the full track.
     */
    @Test
    void shouldReturnJustTheEndpointsWhenTheBudgetIsOne() {
      List<TrackPoint> source = wobblyLine(21);
      assertEquals(2, TrackGeometry.decimateTo(source, 1).size());
    }

    @Test
    void shouldPreserveOrdering() {
      List<TrackPoint> decimated = TrackGeometry.decimateTo(wobblyLine(101), 12);
      for (int i = 1; i < decimated.size(); i++) {
        assertTrue(decimated.get(i).dist() > decimated.get(i - 1).dist());
      }
    }

    /**
     * The whole point of spending the budget on the deviations rather than on a regular stride: a
     * summit sitting between two otherwise unremarkable points has to survive.
     */
    @Test
    void shouldKeepAnElevationExtremumOnAnOtherwiseFlatLine() {
      List<TrackPoint> points = new ArrayList<>();
      for (int i = 0; i < 101; i++) {
        double elevation = i == 37 ? 500 : 100;
        points.add(new TrackPoint(45.0, 6.0 + i * 0.001, elevation, i * 78.0));
      }
      List<TrackPoint> decimated = TrackGeometry.decimateTo(points, 5);
      assertTrue(
          decimated.stream().anyMatch(p -> p.ele() == 500),
          "the summit must survive a decimation down to five points");
    }
  }
}
