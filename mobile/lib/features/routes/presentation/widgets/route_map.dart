import 'dart:convert';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:maplibre/maplibre.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/theme/pedalons_colors.dart';

class _KmMarkerData {
  final double lng;
  final double lat;
  final String label;

  const _KmMarkerData({required this.lng, required this.lat, required this.label});
}

class RouteMap extends StatefulWidget {
  final RouteDetailDto route;

  const RouteMap({super.key, required this.route});

  @override
  State<RouteMap> createState() => _RouteMapState();
}

class _RouteMapState extends State<RouteMap> {
  MapController? _controller;
  List<_KmMarkerData> _kmMarkers = [];

  String _mapStyle(BuildContext context) {
    final brightness = MediaQuery.platformBrightnessOf(context);
    final styleName =
        brightness == Brightness.dark ? 'eclipse' : 'colorful';
    return 'https://tiles.versatiles.org/assets/styles/$styleName/style.json';
  }

  @override
  Widget build(BuildContext context) {
    return MapLibreMap(
      options: MapOptions(
        initCenter: const Geographic(lon: 2.3, lat: 46.6),
        initZoom: 5,
        initStyle: _mapStyle(context),
      ),
      onMapCreated: (controller) => _controller = controller,
      onStyleLoaded: (style) async {
        final brightness = MediaQuery.platformBrightnessOf(context);
        await _addRouteLayers(style, brightness);
        _computeAndSetKmMarkers();
        // Delay fitBounds to let the map complete its layout
        await Future<void>.delayed(const Duration(milliseconds: 100));
        _fitRouteBounds();
      },
      children: [
        if (_kmMarkers.isNotEmpty)
          WidgetLayer(
            markers: _kmMarkers
                .map((m) => Marker(
                      point: Geographic(lon: m.lng, lat: m.lat),
                      size: const Size(28, 28),
                      child: Container(
                        decoration: BoxDecoration(
                          color: Colors.white,
                          shape: BoxShape.circle,
                          border: Border.all(color: Colors.grey, width: 1.5),
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          m.label,
                          style: const TextStyle(
                            fontSize: 11,
                            fontWeight: FontWeight.bold,
                            color: Colors.black,
                          ),
                        ),
                      ),
                    ))
                .toList(),
          ),
      ],
    );
  }

  Future<void> _addRouteLayers(StyleController style, Brightness brightness) async {
    final coordinates = widget.route.tracks
        .expand((track) => track.line.coordinates)
        .toList();

    if (coordinates.isEmpty) return;

    // Route line source + layer
    final lineGeoJson = jsonEncode({
      'type': 'Feature',
      'geometry': {
        'type': 'LineString',
        'coordinates': coordinates,
      },
    });

    await style.addSource(GeoJsonSource(id: 'route-line', data: lineGeoJson));
    await style.addLayer(
      LineStyleLayer(
        id: 'route-line-layer',
        sourceId: 'route-line',
        paint: {
          'line-color': brightness == Brightness.dark
              ? BrandColors.mapRouteLineHexDark
              : BrandColors.mapRouteLineHexLight,
          'line-width': 4,
          'line-opacity': 0.8,
        },
      ),
    );

    // Start marker
    final start = coordinates.first;
    final startGeoJson = jsonEncode({
      'type': 'Feature',
      'geometry': {
        'type': 'Point',
        'coordinates': start,
      },
    });

    await style.addSource(
        GeoJsonSource(id: 'route-start', data: startGeoJson));
    await style.addLayer(
      const CircleStyleLayer(
        id: 'route-start-layer',
        sourceId: 'route-start',
        paint: {
          'circle-radius': 8,
          'circle-color': BrandColors.mapStartMarkerHex,
          'circle-stroke-width': 2,
          'circle-stroke-color': '#FFFFFF',
        },
      ),
    );

    // End marker
    final end = coordinates.last;
    final endGeoJson = jsonEncode({
      'type': 'Feature',
      'geometry': {
        'type': 'Point',
        'coordinates': end,
      },
    });

    await style.addSource(GeoJsonSource(id: 'route-end', data: endGeoJson));
    await style.addLayer(
      const CircleStyleLayer(
        id: 'route-end-layer',
        sourceId: 'route-end',
        paint: {
          'circle-radius': 8,
          'circle-color': BrandColors.mapEndMarkerHex,
          'circle-stroke-width': 2,
          'circle-stroke-color': '#FFFFFF',
        },
      ),
    );
  }

  void _computeAndSetKmMarkers() {
    final coordinates = widget.route.tracks
        .expand((track) => track.line.coordinates)
        .toList();
    final markers = _computeKmMarkers(coordinates);
    if (markers.isNotEmpty) {
      setState(() => _kmMarkers = markers);
    }
  }

  int _kmMarkerInterval(double distanceKm) {
    if (distanceKm < 10) return 1;
    if (distanceKm < 20) return 2;
    if (distanceKm < 50) return 5;
    return 10;
  }

  double _haversineM(double lng1, double lat1, double lng2, double lat2) {
    const r = 6371000.0;
    final dLat = (lat2 - lat1) * pi / 180;
    final dLon = (lng2 - lng1) * pi / 180;
    final a = sin(dLat / 2) * sin(dLat / 2) +
        cos(lat1 * pi / 180) * cos(lat2 * pi / 180) * sin(dLon / 2) * sin(dLon / 2);
    return r * 2 * atan2(sqrt(a), sqrt(1 - a));
  }

  List<_KmMarkerData> _computeKmMarkers(List<List<double>> coords) {
    final totalM = widget.route.distance;
    if (totalM <= 0 || coords.length < 2) return [];

    final intervalM = _kmMarkerInterval(totalM / 1000) * 1000.0;

    // Use 4th coordinate element (cumulative distance) when present, else compute
    final hasCumDist = coords[0].length >= 4 && coords.last[3] > 0;
    final cumDists = List<double>.filled(coords.length, 0.0);
    if (hasCumDist) {
      for (int i = 0; i < coords.length; i++) {
        cumDists[i] = coords[i][3];
      }
    } else {
      double acc = 0;
      for (int i = 1; i < coords.length; i++) {
        acc += _haversineM(coords[i - 1][0], coords[i - 1][1], coords[i][0], coords[i][1]);
        cumDists[i] = acc;
      }
    }

    final markers = <_KmMarkerData>[];
    double targetDist = intervalM;

    while (targetDist < totalM) {
      final idx = cumDists.indexWhere((d) => d >= targetDist);
      if (idx > 0) {
        final p0 = coords[idx - 1];
        final p1 = coords[idx];
        final span = cumDists[idx] - cumDists[idx - 1];
        final t = span == 0 ? 0.0 : (targetDist - cumDists[idx - 1]) / span;
        markers.add(_KmMarkerData(
          lng: p0[0] + t * (p1[0] - p0[0]),
          lat: p0[1] + t * (p1[1] - p0[1]),
          label: '${(targetDist / 1000).round()}',
        ));
      }
      targetDist += intervalM;
    }

    return markers;
  }

  void _fitRouteBounds() {
    final controller = _controller;
    if (controller == null) return;

    final coordinates = widget.route.tracks
        .expand((track) => track.line.coordinates)
        .toList();

    if (coordinates.isEmpty) return;

    double minLon = coordinates.first[0];
    double maxLon = coordinates.first[0];
    double minLat = coordinates.first[1];
    double maxLat = coordinates.first[1];

    for (final coord in coordinates) {
      if (coord[0] < minLon) minLon = coord[0];
      if (coord[0] > maxLon) maxLon = coord[0];
      if (coord[1] < minLat) minLat = coord[1];
      if (coord[1] > maxLat) maxLat = coord[1];
    }

    controller.fitBounds(
      bounds: LngLatBounds(
        longitudeWest: minLon,
        longitudeEast: maxLon,
        latitudeSouth: minLat,
        latitudeNorth: maxLat,
      ),
      padding: const EdgeInsets.all(50),
    );
  }
}
