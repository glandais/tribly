import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:maplibre/maplibre.dart';

import '../../../../api/generated/export.dart';

class RouteMap extends StatefulWidget {
  final RouteDetailDto route;

  const RouteMap({super.key, required this.route});

  @override
  State<RouteMap> createState() => _RouteMapState();
}

class _RouteMapState extends State<RouteMap> {
  MapController? _controller;

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
        await _addRouteLayers(style);
        // Delay fitBounds to let the map complete its layout
        await Future<void>.delayed(const Duration(milliseconds: 100));
        _fitRouteBounds();
      },
    );
  }

  Future<void> _addRouteLayers(StyleController style) async {
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
      const LineStyleLayer(
        id: 'route-line-layer',
        sourceId: 'route-line',
        paint: {
          'line-color': '#1976D2',
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
          'circle-color': '#4CAF50',
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
          'circle-color': '#F44336',
          'circle-stroke-width': 2,
          'circle-stroke-color': '#FFFFFF',
        },
      ),
    );
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
