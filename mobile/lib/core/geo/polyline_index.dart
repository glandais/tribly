import 'dart:math';

/// A geographic position, longitude first (the GeoJSON order).
///
/// Deliberately plain: this library must stay usable from tests, isolates
/// and pure-Dart code, so it depends neither on Flutter nor on the
/// generated API models.
class LngLat {
  /// Longitude in degrees.
  final double lng;

  /// Latitude in degrees.
  final double lat;

  const LngLat(this.lng, this.lat);

  @override
  bool operator ==(Object other) =>
      other is LngLat && other.lng == lng && other.lat == lat;

  @override
  int get hashCode => Object.hash(lng, lat);

  @override
  String toString() => 'LngLat($lng, $lat)';
}

const double _earthRadiusMeters = 6371000.0;
const double _degToRad = pi / 180.0;

/// Cumulative-distance index over a polyline.
///
/// Built once when a route is loaded, it converts back and forth between a
/// distance along the track (in meters, the only value shared between the
/// elevation profile and the map) and a geographic position:
///
/// * [positionAt] — distance → position, by binary search on the cumulative
///   distances then linear interpolation between the two enclosing vertices;
/// * [nearest] — position → distance, by projecting on every segment (not
///   just picking the closest vertex).
///
/// Coordinates follow the GeoJSON convention `[lon, lat]`, optionally with an
/// altitude (`[lon, lat, alt]`) and a measure (`[lon, lat, alt, m]`, the
/// `G3DM` form). When the measure is present *and* actually carries a
/// growing cumulative distance, it is used as-is; otherwise distances are
/// computed with the haversine formula. See [usesMeasures].
class PolylineIndex {
  final List<LngLat> _points;
  final List<double> _cumulative;
  final List<double?> _elevations;

  /// Whether the cumulative distances come from the 4th coordinate component
  /// (`M`) rather than from a haversine computation.
  final bool usesMeasures;

  PolylineIndex._(
    this._points,
    this._cumulative,
    this._elevations,
    this.usesMeasures,
  );

  /// Builds an index from one or more LineStrings.
  ///
  /// Each line is a list of coordinates `[lon, lat]`, `[lon, lat, alt]` or
  /// `[lon, lat, alt, m]`. Consecutive lines are chained: the gap between the
  /// end of a line and the start of the next one is always measured with the
  /// haversine formula, even in measure mode, because nothing guarantees the
  /// measures of two tracks share an origin.
  factory PolylineIndex.fromLineStrings(Iterable<List<List<double>>> lines) {
    final cleaned = <List<List<double>>>[];
    for (final line in lines) {
      final valid = line.where((c) => c.length >= 2).toList();
      if (valid.isNotEmpty) cleaned.add(valid);
    }

    final points = <LngLat>[];
    final cumulative = <double>[];
    final elevations = <double?>[];
    if (cleaned.isEmpty) {
      return PolylineIndex._(points, cumulative, elevations, false);
    }

    final useMeasures = cleaned.every(_hasUsableMeasures);

    var total = 0.0;
    for (final line in cleaned) {
      if (points.isNotEmpty) {
        final previous = points.last;
        final first = LngLat(line.first[0], line.first[1]);
        total += haversineMeters(previous, first);
      }
      final base = total;
      for (var i = 0; i < line.length; i++) {
        final coordinate = line[i];
        final point = LngLat(coordinate[0], coordinate[1]);
        final elevation = coordinate.length >= 3 ? coordinate[2] : null;
        if (i == 0) {
          points.add(point);
          cumulative.add(base);
          elevations.add(elevation);
          continue;
        }
        total = useMeasures
            ? base + (coordinate[3] - line.first[3])
            : total + haversineMeters(points.last, point);
        points.add(point);
        cumulative.add(total);
        elevations.add(elevation);
      }
      total = cumulative.last;
    }

    return PolylineIndex._(points, cumulative, elevations, useMeasures);
  }

  /// Builds an index from a single LineString.
  factory PolylineIndex.fromCoordinates(List<List<double>> coordinates) =>
      PolylineIndex.fromLineStrings([coordinates]);

  /// Whether the index holds no usable geometry.
  bool get isEmpty => _points.isEmpty;

  /// Number of vertices retained.
  int get length => _points.length;

  /// Total length of the polyline, in meters.
  double get totalDistance => _cumulative.isEmpty ? 0.0 : _cumulative.last;

  /// The vertices, in order.
  List<LngLat> get points => List.unmodifiable(_points);

  /// The cumulative distance of each vertex, in meters.
  List<double> get cumulativeDistances => List.unmodifiable(_cumulative);

  /// The altitude of each vertex, in meters, `null` where the coordinate
  /// carried none.
  ///
  /// Kept alongside the cumulative distances rather than recomputed elsewhere
  /// so that anything drawing an elevation profile chains multi-track routes
  /// *exactly* the way [positionAt] and [nearest] do — same rebasing per line,
  /// same haversine bridge between two tracks. A profile built on its own
  /// convention would drift against the map cursor on the routes that have
  /// more than one track.
  List<double?> get elevations => List.unmodifiable(_elevations);

  /// Whether every vertex carries an altitude.
  bool get hasElevations =>
      _elevations.isNotEmpty && _elevations.every((e) => e != null);

  /// The position reached after [meters] along the polyline.
  ///
  /// The value is clamped to `[0, totalDistance]`, so `positionAt(0)` is the
  /// start and `positionAt(totalDistance)` the finish. Returns `null` when
  /// the index is empty.
  LngLat? positionAt(double meters) {
    if (_points.isEmpty) return null;
    if (_points.length == 1) return _points.first;

    final target = meters.clamp(0.0, totalDistance).toDouble();
    final upper = _upperVertexAt(target);
    final lower = upper - 1;

    final span = _cumulative[upper] - _cumulative[lower];
    if (span <= 0) return _points[upper];

    final ratio = (target - _cumulative[lower]) / span;
    final from = _points[lower];
    final to = _points[upper];
    return LngLat(
      from.lng + ratio * (to.lng - from.lng),
      from.lat + ratio * (to.lat - from.lat),
    );
  }

  /// The cumulative distance, in meters, of the point of the polyline closest
  /// to [target].
  ///
  /// The projection is done on the segments, not only on the vertices, so a
  /// point facing the middle of a long straight segment gives the distance of
  /// that facing point. Returns `null` when the index is empty.
  double? nearest(LngLat target) => nearestMatch(target)?.distance;

  /// Same as [nearest], but also exposes the projected position and how far
  /// [target] lies from the polyline.
  PolylineMatch? nearestMatch(LngLat target) {
    if (_points.isEmpty) return null;
    if (_points.length == 1) {
      return PolylineMatch(
        distance: 0,
        position: _points.first,
        offset: haversineMeters(target, _points.first),
      );
    }

    final scale = cos(target.lat * _degToRad);
    final targetX = target.lng * scale;
    final targetY = target.lat;

    var bestSquared = double.infinity;
    var bestDistance = 0.0;
    var bestPosition = _points.first;

    for (var i = 0; i < _points.length - 1; i++) {
      final from = _points[i];
      final to = _points[i + 1];
      final fromX = from.lng * scale;
      final toX = to.lng * scale;
      final deltaX = toX - fromX;
      final deltaY = to.lat - from.lat;

      final squaredLength = deltaX * deltaX + deltaY * deltaY;
      var ratio = 0.0;
      if (squaredLength > 0) {
        ratio =
            ((targetX - fromX) * deltaX + (targetY - from.lat) * deltaY) /
            squaredLength;
        ratio = ratio.clamp(0.0, 1.0).toDouble();
      }

      final projectedX = fromX + ratio * deltaX;
      final projectedY = from.lat + ratio * deltaY;
      final errorX = targetX - projectedX;
      final errorY = targetY - projectedY;
      final squared = errorX * errorX + errorY * errorY;

      if (squared < bestSquared) {
        bestSquared = squared;
        bestDistance =
            _cumulative[i] + ratio * (_cumulative[i + 1] - _cumulative[i]);
        bestPosition = LngLat(
          from.lng + ratio * (to.lng - from.lng),
          from.lat + ratio * (to.lat - from.lat),
        );
      }
    }

    return PolylineMatch(
      distance: bestDistance,
      position: bestPosition,
      offset: haversineMeters(target, bestPosition),
    );
  }

  /// Index of the first vertex whose cumulative distance reaches [target].
  ///
  /// Always at least 1, so that `[result - 1, result]` frames [target].
  int _upperVertexAt(double target) {
    var low = 1;
    var high = _cumulative.length - 1;
    while (low < high) {
      final middle = (low + high) >> 1;
      if (_cumulative[middle] < target) {
        low = middle + 1;
      } else {
        high = middle;
      }
    }
    return low;
  }

  /// Whether a line carries a real cumulative measure on its 4th component:
  /// every coordinate has one, the first is ~0, they never decrease and the
  /// last one is strictly positive.
  static bool _hasUsableMeasures(List<List<double>> line) {
    if (line.length < 2) return false;
    if (line.any((c) => c.length < 4)) return false;
    final first = line.first[3];
    if (first.abs() > 1.0) return false;
    for (var i = 1; i < line.length; i++) {
      if (line[i][3] < line[i - 1][3]) return false;
    }
    return line.last[3] > first;
  }
}

/// Result of a projection on a polyline.
class PolylineMatch {
  /// Cumulative distance of the projected point, in meters.
  final double distance;

  /// The projected point itself.
  final LngLat position;

  /// Distance between the queried point and the polyline, in meters.
  final double offset;

  const PolylineMatch({
    required this.distance,
    required this.position,
    required this.offset,
  });

  @override
  String toString() =>
      'PolylineMatch(distance: $distance, position: $position, '
      'offset: $offset)';
}

/// Great-circle distance between two positions, in meters.
double haversineMeters(LngLat from, LngLat to) {
  final deltaLat = (to.lat - from.lat) * _degToRad;
  final deltaLng = (to.lng - from.lng) * _degToRad;
  final a =
      sin(deltaLat / 2) * sin(deltaLat / 2) +
      cos(from.lat * _degToRad) *
          cos(to.lat * _degToRad) *
          sin(deltaLng / 2) *
          sin(deltaLng / 2);
  return _earthRadiusMeters * 2 * atan2(sqrt(a), sqrt(1 - a));
}
