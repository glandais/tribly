import 'dart:convert';
import 'dart:math';

import 'package:dio/dio.dart';

import 'backend_client.dart';

/// Seeding for the ride page's own states and extras: a ride cancelled behind the app, routes
/// with a real track for the groups map.
///
/// Mirrors `frontend/e2e/support/rides.ts` (`rideRequest`) and `support/routes.ts` (`newRoute`,
/// `windingTrack`, `gpxOf`): same endpoints, same request shapes.
extension RideDetailSeed on BackendClient {
  /// Cancels the ride as [by], its organiser: a PUT of the whole `RideRequest` with the status
  /// changed and every group kept by its id — what the web editor sends once « Annuler la
  /// sortie » is confirmed.
  Future<Json> cancelRide(TestUser by, String teamSlug, String rideSlug) async {
    final current = await ride(by, teamSlug, rideSlug);
    return put(by, '/api/teams/$teamSlug/rides/$rideSlug', {
      'name': current['name'],
      'media': current['media'],
      'dateTime': current['dateTime'],
      'status': 'CANCELLED',
      'visibility': current['visibility'],
      'routeSlug': current['routeSlug'],
      'groups': [
        for (final group in (current['groups'] as List).cast<Json>())
          {
            'id': group['id'],
            'name': group['name'],
            'maxParticipants': group['maxParticipants'],
            'routeSlug': group['routeSlug'],
            'leaderId': (group['leader'] as Json?)?['id'],
          },
      ],
    });
  }

  /// A members-only road route of the team, uploaded with a winding GPX track of [points]
  /// points starting [eastKm] km east of the web suite's origin — two routes of one ride then
  /// draw two distinct tracks.
  Future<Json> newRouteWithTrack(
    TestUser by,
    String teamSlug,
    String label, {
    double eastKm = 0,
    int points = 200,
  }) async {
    final name = unique(label);
    final request = {
      'name': name,
      'media': markdownMedia(),
      'surfaceType': 'ROAD',
      'visibility': 'TEAM',
    };
    return post(
      by,
      '/api/teams/$teamSlug/routes',
      FormData.fromMap({
        'route': MultipartFile.fromString(
          jsonEncode(request),
          filename: 'route.json',
          contentType: DioMediaType('application', 'json'),
        ),
        'gpxFile': MultipartFile.fromString(
          _gpxOf(name, _windingTrack(points, eastKm)),
          filename: 'trace.gpx',
          contentType: DioMediaType('application', 'gpx+xml'),
        ),
      }),
    );
  }
}

/// `windingTrack` of the web suite: heading east through the Beauce, swinging ±300 m every
/// 1.5 km, 50 m between two points — enough bends to survive the backend's simplification.
List<(double lat, double lon, double ele)> _windingTrack(
  int count,
  double eastKm,
) {
  const lat0 = 48.4469;
  const lon0 = 1.4875;
  const metresPerDegLat = 111320.0;
  final metresPerDegLon = metresPerDegLat * cos(lat0 * pi / 180);
  return [
    for (var i = 0; i < count; i++)
      (
        lat0 + 300 * sin(2 * pi * i * 50 / 1500) / metresPerDegLat,
        lon0 + (eastKm * 1000 + i * 50) / metresPerDegLon,
        150 + 20 * sin(2 * pi * i * 50 / 4000),
      ),
  ];
}

String _gpxOf(String name, List<(double lat, double lon, double ele)> points) {
  final trkpts = points
      .map(
        (p) =>
            '<trkpt lat="${p.$1.toStringAsFixed(7)}" lon="${p.$2.toStringAsFixed(7)}">'
            '<ele>${p.$3.toStringAsFixed(1)}</ele></trkpt>',
      )
      .join('\n      ');
  return '''<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="pedalons-e2e" xmlns="http://www.topografix.com/GPX/1/1">
  <trk>
    <name>$name</name>
    <trkseg>
      $trkpts
    </trkseg>
  </trk>
</gpx>
''';
}
