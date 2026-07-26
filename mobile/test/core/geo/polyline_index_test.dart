import 'dart:math';

import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/geo/polyline_index.dart';

const double _earthRadiusMeters = 6371000.0;
const double _degToRad = pi / 180.0;

/// A north-going ladder along a meridian: `count` points spaced by
/// `stepDegrees` of latitude, starting at (lon, lat).
List<List<double>> _meridianLine({
  double lon = 2.35,
  double lat = 48.85,
  double stepDegrees = 0.01,
  int count = 20,
}) => List.generate(count, (i) => [lon, lat + i * stepDegrees]);

/// Exact spherical length of a meridian ladder.
double _meridianReference({double stepDegrees = 0.01, int count = 20}) =>
    _earthRadiusMeters * (count - 1) * stepDegrees * _degToRad;

/// A closed loop approximating a circle of [radiusMeters] around a center.
List<List<double>> _circleLine({
  double lon = 6.05,
  double lat = 45.9,
  double radiusMeters = 2000,
  int segments = 360,
}) {
  final latDegrees = radiusMeters / (_earthRadiusMeters * _degToRad);
  final lngDegrees = latDegrees / cos(lat * _degToRad);
  return List.generate(segments + 1, (i) {
    final angle = 2 * pi * i / segments;
    return [lon + lngDegrees * cos(angle), lat + latDegrees * sin(angle)];
  });
}

/// Moves a position east by [meters].
LngLat _shiftEast(LngLat from, double meters) {
  final degrees =
      meters / (_earthRadiusMeters * _degToRad * cos(from.lat * _degToRad));
  return LngLat(from.lng + degrees, from.lat);
}

void main() {
  group('PolylineIndex — haversine mode', () {
    test('an empty input yields an empty index', () {
      final index = PolylineIndex.fromLineStrings([]);

      expect(index.isEmpty, isTrue);
      expect(index.totalDistance, 0);
      expect(index.positionAt(0), isNull);
      expect(index.nearest(const LngLat(2.0, 48.0)), isNull);
    });

    test('coordinates without a measure fall back to haversine', () {
      final index = PolylineIndex.fromCoordinates(_meridianLine());

      expect(index.usesMeasures, isFalse);
      expect(
        index.totalDistance,
        closeTo(_meridianReference(), _meridianReference() * 0.001),
      );
    });

    test('positionAt(0) is the start and positionAt(total) the finish', () {
      final coordinates = _meridianLine();
      final index = PolylineIndex.fromCoordinates(coordinates);

      expect(
        index.positionAt(0),
        LngLat(coordinates.first[0], coordinates.first[1]),
      );
      expect(
        index.positionAt(index.totalDistance),
        LngLat(coordinates.last[0], coordinates.last[1]),
      );
    });

    test('positionAt clamps out-of-range distances', () {
      final index = PolylineIndex.fromCoordinates(_meridianLine());

      expect(index.positionAt(-1000), index.positionAt(0));
      expect(
        index.positionAt(index.totalDistance + 5000),
        index.positionAt(index.totalDistance),
      );
    });

    test('positionAt interpolates between the enclosing vertices', () {
      final index = PolylineIndex.fromCoordinates(
        _meridianLine(count: 3, stepDegrees: 0.01),
      );

      final middle = index.positionAt(index.totalDistance / 2)!;

      expect(middle.lat, closeTo(48.86, 1e-6));
      expect(middle.lng, closeTo(2.35, 1e-9));
    });

    test('a total on a realistic loop stays within 0.5% of the reference', () {
      const radius = 2000.0;
      final index = PolylineIndex.fromCoordinates(_circleLine());
      final reference = 2 * pi * radius;

      expect(index.totalDistance, closeTo(reference, reference * 0.005));
    });
  });

  group('PolylineIndex — nearest', () {
    test('a point ~5 m off the track gives the right distance', () {
      final index = PolylineIndex.fromCoordinates(_meridianLine());
      final expected = index.totalDistance * 0.37;
      final onTrack = index.positionAt(expected)!;

      final match = index.nearestMatch(_shiftEast(onTrack, 5))!;

      expect(match.offset, closeTo(5, 0.5));
      expect(match.distance, closeTo(expected, 10));
    });

    test('the projection lands on the segment, not on a vertex', () {
      final index = PolylineIndex.fromCoordinates([
        [2.0, 48.0],
        [2.0, 48.1],
      ]);
      final middle = index.positionAt(index.totalDistance / 2)!;

      final distance = index.nearest(_shiftEast(middle, 5))!;

      expect(distance, closeTo(index.totalDistance / 2, 10));
      expect(distance, greaterThan(1000));
    });

    test('nearest and positionAt are inverse of each other', () {
      final index = PolylineIndex.fromCoordinates(_meridianLine());

      for (final ratio in [0.0, 0.13, 0.5, 0.81, 1.0]) {
        final target = index.totalDistance * ratio;
        final position = index.positionAt(target)!;
        expect(index.nearest(position), closeTo(target, 1));
      }
    });

    test('a point before the start snaps to distance 0', () {
      final coordinates = _meridianLine();
      final index = PolylineIndex.fromCoordinates(coordinates);

      final before = LngLat(coordinates.first[0], coordinates.first[1] - 0.02);

      expect(index.nearest(before), closeTo(0, 1));
    });
  });

  group('PolylineIndex — G3DM measures', () {
    /// The same geometry as the meridian ladder, but with measures that are
    /// deliberately half of the real ground distance: if they are used, the
    /// total is halved.
    List<List<double>> measuredLine() {
      final coordinates = _meridianLine();
      final step = _meridianReference() / (coordinates.length - 1) / 2;
      return [
        for (var i = 0; i < coordinates.length; i++)
          [coordinates[i][0], coordinates[i][1], 100.0 + i, i * step],
      ];
    }

    test('a growing measure is used as the cumulative distance', () {
      final index = PolylineIndex.fromCoordinates(measuredLine());

      expect(index.usesMeasures, isTrue);
      expect(index.totalDistance, closeTo(_meridianReference() / 2, 1));
    });

    test('positionAt still frames the endpoints in measure mode', () {
      final coordinates = measuredLine();
      final index = PolylineIndex.fromCoordinates(coordinates);

      expect(
        index.positionAt(0),
        LngLat(coordinates.first[0], coordinates.first[1]),
      );
      expect(
        index.positionAt(index.totalDistance),
        LngLat(coordinates.last[0], coordinates.last[1]),
      );
      expect(
        index.positionAt(index.totalDistance / 2)!.lat,
        closeTo(48.85 + 0.19 / 2, 1e-6),
      );
    });

    test('an all-zero measure is rejected', () {
      final index = PolylineIndex.fromCoordinates([
        for (final c in _meridianLine()) [c[0], c[1], 100.0, 0.0],
      ]);

      expect(index.usesMeasures, isFalse);
      expect(index.totalDistance, closeTo(_meridianReference(), 20));
    });

    test('a non-monotonic measure is rejected', () {
      final coordinates = measuredLine();
      coordinates[5][3] = coordinates[4][3] - 10;
      final index = PolylineIndex.fromCoordinates(coordinates);

      expect(index.usesMeasures, isFalse);
      expect(index.totalDistance, closeTo(_meridianReference(), 20));
    });

    test('a measure not starting near zero is rejected', () {
      final coordinates = [
        for (final c in measuredLine()) [c[0], c[1], c[2], c[3] + 5000],
      ];
      final index = PolylineIndex.fromCoordinates(coordinates);

      expect(index.usesMeasures, isFalse);
      expect(index.totalDistance, closeTo(_meridianReference(), 20));
    });

    test('an altitude without a measure is not mistaken for one', () {
      final index = PolylineIndex.fromCoordinates([
        for (final c in _meridianLine()) [c[0], c[1], 100.0],
      ]);

      expect(index.usesMeasures, isFalse);
      expect(index.totalDistance, closeTo(_meridianReference(), 20));
    });
  });

  group('PolylineIndex — several LineStrings', () {
    test('lines are chained and the gap is measured', () {
      final first = _meridianLine(count: 5);
      final second = _meridianLine(lat: 48.90, count: 5);

      final index = PolylineIndex.fromLineStrings([first, second]);
      final single = PolylineIndex.fromCoordinates(_meridianLine(count: 10));

      expect(index.length, 10);
      expect(index.totalDistance, closeTo(single.totalDistance, 1));
      expect(index.positionAt(0), LngLat(first[0][0], first[0][1]));
      expect(
        index.positionAt(index.totalDistance),
        LngLat(second.last[0], second.last[1]),
      );
    });

    test('per-line measures restarting at zero are re-based', () {
      final step = _meridianReference(count: 5) / 4;
      List<List<double>> measured(double lat) => [
        for (var i = 0; i < 5; i++) [2.35, lat + i * 0.01, 0.0, i * step],
      ];

      final index = PolylineIndex.fromLineStrings([
        measured(48.85),
        measured(48.90),
      ]);

      expect(index.usesMeasures, isTrue);
      expect(index.cumulativeDistances.first, 0);
      expect(index.length, 10);
      for (var i = 1; i < index.length; i++) {
        expect(
          index.cumulativeDistances[i],
          greaterThan(index.cumulativeDistances[i - 1]),
        );
      }
      expect(index.totalDistance, closeTo(_meridianReference(count: 10), 1));
    });

    test('empty lines and malformed coordinates are dropped', () {
      final index = PolylineIndex.fromLineStrings([
        <List<double>>[],
        [
          [2.35],
          ..._meridianLine(count: 5),
        ],
      ]);

      expect(index.length, 5);
      expect(index.positionAt(0), const LngLat(2.35, 48.85));
    });
  });
}
