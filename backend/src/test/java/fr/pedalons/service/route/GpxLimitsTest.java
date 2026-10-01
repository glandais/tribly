package fr.pedalons.service.route;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.GPXPathType;
import io.github.glandais.gpx.data.Point;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Plain unit test of the bounds of the GPX pipeline (docs/LEDGER_*.md SEC-6). */
class GpxLimitsTest {

  private static GPXPath path(double[]... latLngDeg) {
    GPXPath path = new GPXPath("test", GPXPathType.TRACK);
    for (double[] p : latLngDeg) {
      Point point = new Point();
      point.setLat(Math.toRadians(p[0]));
      point.setLon(Math.toRadians(p[1]));
      point.setEle(0.0);
      path.addPoint(point);
    }
    return path;
  }

  private static ErrorCode refusal(GPXPath... paths) {
    BusinessException e =
        assertThrows(BusinessException.class, () -> GpxLimits.checkTracks(List.of(paths)));
    return e.getErrorCode();
  }

  @Test
  void aRealisticTrack_isAccepted() {
    // Lyon to Paris and back, about 800 km.
    assertDoesNotThrow(
        () ->
            GpxLimits.checkTracks(
                List.of(
                    path(
                        new double[] {45.76, 4.84},
                        new double[] {48.86, 2.35},
                        new double[] {45.76, 4.84}))));
  }

  @Test
  void aTrackAHemisphereLong_isRefused() {
    assertEquals(ErrorCode.GPX_TOO_LONG, refusal(path(new double[] {0, 0}, new double[] {0, 179})));
  }

  /** The bound is on the sum over the tracks of a file, not per track. */
  @Test
  void tracksAddingUpPastTheBound_areRefused() {
    // Each about 2 100 km (Lyon to the Atlantic off Portugal), over 4 000 km together.
    GPXPath first = path(new double[] {45.76, 4.84}, new double[] {38.72, -20.0});
    GPXPath second = path(new double[] {45.76, 4.84}, new double[] {38.72, -20.0});
    assertDoesNotThrow(() -> GpxLimits.checkTracks(List.of(first)));
    assertEquals(ErrorCode.GPX_TOO_LONG, refusal(first, second));
  }

  @Test
  void anOutOfRangeLatitude_isRefused() {
    assertEquals(ErrorCode.GPX_FAILURE, refusal(path(new double[] {0, 0}, new double[] {91, 0})));
  }

  @Test
  void aNaNCoordinate_isRefused() {
    assertEquals(
        ErrorCode.GPX_FAILURE, refusal(path(new double[] {0, 0}, new double[] {Double.NaN, 0})));
  }
}
