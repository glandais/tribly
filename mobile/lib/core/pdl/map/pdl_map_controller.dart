import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:maplibre/maplibre.dart';

import '../../geo/polyline_index.dart';

/// Un tracé à dessiner, **sans aucun DTO**.
///
/// `core/pdl/map` ne connaît ni `RouteDetailDto`, ni `RideGroupDto`, ni
/// `TripStageDto` : il ne sait que des listes de coordonnées, une couleur et un
/// identifiant. C'est `features/…` qui convertit. La règle de revue du module
/// (`README.md`) le rend vérifiable.
@immutable
class PdlMapTrack {
  const PdlMapTrack({
    required this.id,
    required this.lines,
    required this.color,
    this.label,
    this.segmentColors,
  });

  /// Identité du tracé. Elle finit dans le `layerId` de la couche MapLibre,
  /// parce que **c'est le seul endroit où elle survit** à un `queryLayers` :
  /// `QueriedLayer` ne rend pas les propriétés de l'entité (§1.0.3-7).
  ///
  /// Doit donc être un identifiant stable et sans caractère exotique — un TSID
  /// ou un index suffisent.
  final String id;

  /// Une ou plusieurs LineStrings GeoJSON, chacune `[lon, lat]`,
  /// `[lon, lat, alt]` ou `[lon, lat, alt, m]`.
  final List<List<List<double>>> lines;

  /// Couleur du trait. Vient de `kMultiTrackPalette` pour un groupe ou une
  /// étape, de `PdlColors.mapTrack` pour un tracé unique — jamais d'ici.
  final Color color;

  /// Libellé facultatif, à l'usage de l'appelant (légende, carte flottante).
  /// `core/pdl` ne l'affiche pas et ne le traduit pas.
  final String? label;

  /// Couleur **par segment**, une entrée par intervalle entre deux sommets
  /// consécutifs, `lines` aplaties dans l'ordre.
  ///
  /// C'est la colorisation par pente de la fiche parcours. Elle ne passe pas
  /// par `line-gradient`, qui exige `lineMetrics` sur la source et n'accepte
  /// qu'une progression le long d'**une** ligne : chaque segment devient une
  /// entité GeoJSON portant sa couleur, et la couche lit `['get', 'color']`.
  ///
  /// Une entrée manquante ou une liste trop courte fait retomber le segment sur
  /// [color] — on ne devine pas une teinte.
  final List<Color>? segmentColors;

  bool get isSegmented => segmentColors != null && segmentColors!.isNotEmpty;
}

/// Un point remarquable : départ, arrivée, point de passage, borne
/// kilométrique.
@immutable
class PdlMapPoint {
  const PdlMapPoint({required this.lon, required this.lat, this.label});

  final double lon;
  final double lat;

  /// Texte déjà localisé par l'appelant, ou `null`.
  final String? label;
}

/// Une boîte englobante, en degrés WGS84.
///
/// Le pendant DTO-libre de `BoundsDto` : `GET …/routes/bounds` est converti par
/// l'écran, pas par la bibliothèque.
@immutable
class PdlMapBox {
  const PdlMapBox({
    required this.minLon,
    required this.minLat,
    required this.maxLon,
    required this.maxLat,
  });

  final double minLon;
  final double minLat;
  final double maxLon;
  final double maxLat;

  /// Boîte englobant les tracés donnés, ou `null` s'ils sont tous vides.
  static PdlMapBox? ofTracks(Iterable<PdlMapTrack> tracks) {
    double? minLon, minLat, maxLon, maxLat;
    for (final PdlMapTrack t in tracks) {
      for (final List<List<double>> line in t.lines) {
        for (final List<double> c in line) {
          if (c.length < 2) continue;
          minLon = minLon == null || c[0] < minLon ? c[0] : minLon;
          maxLon = maxLon == null || c[0] > maxLon ? c[0] : maxLon;
          minLat = minLat == null || c[1] < minLat ? c[1] : minLat;
          maxLat = maxLat == null || c[1] > maxLat ? c[1] : maxLat;
        }
      }
    }
    if (minLon == null) return null;
    return PdlMapBox(
      minLon: minLon,
      minLat: minLat!,
      maxLon: maxLon!,
      maxLat: maxLat!,
    );
  }

  LngLatBounds toLngLatBounds() => LngLatBounds(
    longitudeWest: minLon,
    longitudeEast: maxLon,
    latitudeSouth: minLat,
    latitudeNorth: maxLat,
  );

  static PdlMapBox fromLngLatBounds(LngLatBounds b) => PdlMapBox(
    minLon: b.longitudeWest,
    minLat: b.latitudeSouth,
    maxLon: b.longitudeEast,
    maxLat: b.latitudeNorth,
  );

  @override
  bool operator ==(Object other) =>
      other is PdlMapBox &&
      other.minLon == minLon &&
      other.minLat == minLat &&
      other.maxLon == maxLon &&
      other.maxLat == maxLat;

  @override
  int get hashCode => Object.hash(minLon, minLat, maxLon, maxLat);

  @override
  String toString() => 'PdlMapBox($minLon, $minLat, $maxLon, $maxLat)';
}

/// Épaisseurs et opacités des tracés (§1.3.2).
///
/// MapLibre n'a pas de `zIndex` sur les lignes : c'est **l'ordre de dessin**
/// qui place le tracé sélectionné en dernier, ce dont
/// [PdlMapController.select] se charge.
@immutable
class PdlMapTrackTheme {
  const PdlMapTrackTheme({
    this.selectedWidth = 8,
    this.selectedOpacity = 0.9,
    this.width = 5,
    this.opacity = 0.5,
  });

  final double selectedWidth;
  final double selectedOpacity;
  final double width;
  final double opacity;
}

/// Une borne kilométrique calculée le long d'un tracé.
@immutable
class PdlKmMarker {
  const PdlKmMarker({
    required this.lon,
    required this.lat,
    required this.label,
  });

  final double lon;
  final double lat;

  /// Le nombre de kilomètres, en clair. `core/pdl` ne formate pas d'unité :
  /// c'est l'appelant qui décide s'il écrit « 10 » ou « 6.2 ».
  final String label;
}

/// Pas des bornes kilométriques, en kilomètres, selon la longueur du tracé.
///
/// Repris tel quel de `route_map.dart` : c'est un des deux morceaux que la
/// réécriture devait **conserver**.
int pdlKmMarkerIntervalKm(double distanceKm) {
  if (distanceKm < 10) return 1;
  if (distanceKm < 20) return 2;
  if (distanceKm < 50) return 5;
  return 10;
}

/// Les bornes kilométriques d'un tracé, interpolées sur la polyligne.
///
/// S'appuie sur [PolylineIndex] (F-TE-4), qui sait déjà lire la 4ᵉ composante
/// `G3DM` quand elle porte le cumul et retomber sur la haversine sinon — le
/// calcul recopié dans `route_map.dart` disparaît donc, sans changer le
/// résultat.
List<PdlKmMarker> computePdlKmMarkers(
  Iterable<List<List<double>>> lines, {
  double? totalMeters,
  String Function(int km)? formatKm,
}) {
  final PolylineIndex index = PolylineIndex.fromLineStrings(lines);
  if (index.isEmpty) return const <PdlKmMarker>[];

  final double total = (totalMeters != null && totalMeters > 0)
      ? totalMeters
      : index.totalDistance;
  if (total <= 0) return const <PdlKmMarker>[];

  final double intervalM = pdlKmMarkerIntervalKm(total / 1000) * 1000.0;
  final String Function(int) label = formatKm ?? (int km) => '$km';

  final List<PdlKmMarker> markers = <PdlKmMarker>[];
  for (double d = intervalM; d < total; d += intervalM) {
    final LngLat? p = index.positionAt(d);
    if (p == null) continue;
    markers.add(
      PdlKmMarker(lon: p.lng, lat: p.lat, label: label((d / 1000).round())),
    );
  }
  return markers;
}

/// Le contrôleur d'une carte Pédalons.
///
/// Il porte ce que `MapLibreMap` ne porte pas : les couches de tracés, la
/// sélection, et le cadrage. Il ne construit rien — il est piloté par [PdlMap],
/// qui lui passe le `MapController` et le `StyleController` dès qu'ils
/// existent, et il survit au rechargement d'un style (changement de fond de
/// carte), où **toutes les couches ajoutées sont perdues** et doivent être
/// réappliquées.
class PdlMapController extends ChangeNotifier {
  PdlMapController({
    this.layerPrefix = 'pdl-track',
    this.trackTheme = const PdlMapTrackTheme(),
  });

  /// Préfixe des identifiants de couche. `ride-track` sur l'écran 12,
  /// `stage-track` sur l'écran 24 — le contrat du §1.3.2.
  final String layerPrefix;

  final PdlMapTrackTheme trackTheme;

  MapController? _map;
  StyleController? _style;

  List<PdlMapTrack> _tracks = const <PdlMapTrack>[];
  List<PdlMapPoint> _waypoints = const <PdlMapPoint>[];
  PdlMapPoint? _start;
  PdlMapPoint? _end;
  String? _selectedId;
  PdlMapPoint? _cursor;
  bool _styleReady = false;

  /// Les couches réellement posées, dans leur ordre de dessin.
  final List<String> _drawnLayers = <String>[];

  String get _sourceId => '$layerPrefix-src';
  String get _pointsSourceId => '$layerPrefix-points';
  String get _waypointsSourceId => '$layerPrefix-waypoints';
  String get _cursorSourceId => '$layerPrefix-cursor';

  MapController? get map => _map;

  /// Vrai quand un style est chargé et les couches posées : avant cela, tout
  /// appel de dessin est mémorisé mais sans effet visible.
  bool get isStyleReady => _styleReady;

  String? get selectedTrackId => _selectedId;

  List<PdlMapTrack> get tracks => List<PdlMapTrack>.unmodifiable(_tracks);

  /// La région visible, ou `null` avant le premier cadre.
  PdlMapBox? get visibleBox {
    final MapController? m = _map;
    if (m == null) return null;
    try {
      return PdlMapBox.fromLngLatBounds(m.getVisibleRegion());
    } catch (_) {
      // `getVisibleRegion` lève tant que la vue native n'a pas de taille.
      return null;
    }
  }

  void attachMap(MapController controller) {
    _map = controller;
  }

  /// Appelé sur `MapEventStyleLoaded`. Repose l'intégralité des couches : un
  /// changement de fond de carte les efface toutes.
  Future<void> attachStyle(StyleController style) async {
    _style = style;
    _styleReady = false;
    _drawnLayers.clear();
    await _applyAll();
    _styleReady = true;
    notifyListeners();
  }

  void detachStyle() {
    _style = null;
    _styleReady = false;
    _drawnLayers.clear();
  }

  Future<void> setContent({
    required List<PdlMapTrack> tracks,
    List<PdlMapPoint> waypoints = const <PdlMapPoint>[],
    PdlMapPoint? start,
    PdlMapPoint? end,
  }) async {
    _tracks = tracks;
    _waypoints = waypoints;
    _start = start;
    _end = end;
    if (_selectedId != null &&
        !tracks.any((PdlMapTrack t) => t.id == _selectedId)) {
      _selectedId = null;
    }
    // Un tracé seul est implicitement le tracé sélectionné : sinon il
    // s'afficherait à l'opacité des tracés de fond (0,5), illisible sur une
    // fiche parcours.
    if (_selectedId == null && tracks.length == 1) {
      _selectedId = tracks.first.id;
    }
    if (_style != null) {
      _drawnLayers.clear();
      await _applyAll();
    }
    notifyListeners();
  }

  /// Change le tracé sélectionné.
  ///
  /// **Ne reconstruit pas les couches** : le trait est piloté par une
  /// expression `case` sur la propriété `sel` de l'entité, et il suffit donc de
  /// repousser la source (un seul `updateGeoJsonSource`). Seul l'ordre de
  /// dessin impose de toucher une couche — celle du tracé nouvellement
  /// sélectionné est retirée puis rajoutée pour repasser au-dessus, MapLibre
  /// n'ayant pas de `zIndex` sur les lignes.
  ///
  /// **Écart assumé** : le plan cite `setLayerProperties`, qui **n'existe pas**
  /// sur le `StyleController` de `maplibre 0.3.5` (vérifié : la classe expose
  /// `addSource`, `addLayer`, `updateGeoJsonSource`, `removeLayer`,
  /// `removeSource`, et rien pour muter une couche en place). L'expression
  /// pilotée par la donnée obtient le même résultat en un appel.
  Future<void> select(String? trackId) async {
    if (_selectedId == trackId) return;
    _selectedId = trackId;
    notifyListeners();

    final StyleController? style = _style;
    if (style == null) return;
    await style.updateGeoJsonSource(id: _sourceId, data: _tracksGeoJson());
    if (trackId != null) await _raise(trackId);
  }

  /// Déplace le marqueur de réticule, ou l'efface avec `null`.
  ///
  /// La couche et sa source sont posées **une fois** au chargement du style,
  /// vides ; ce déplacement n'est qu'un `updateGeoJsonSource`. Ajouter et
  /// retirer une couche à chaque frame de glissement ferait scintiller la
  /// carte et fuiterait des couches (§1.3.1).
  Future<void> setCursor(PdlMapPoint? point) async {
    if (_cursor == point) return;
    _cursor = point;
    final StyleController? style = _style;
    if (style == null) return;
    await _safe(
      () => style.updateGeoJsonSource(
        id: _cursorSourceId,
        data: _cursorGeoJson(),
      ),
    );
  }

  PdlMapPoint? get cursor => _cursor;

  String _cursorGeoJson() {
    final PdlMapPoint? p = _cursor;
    return jsonEncode(<String, Object?>{
      'type': 'FeatureCollection',
      'features': <Map<String, Object?>>[
        if (p != null)
          <String, Object?>{
            'type': 'Feature',
            'properties': <String, Object?>{},
            'geometry': <String, Object?>{
              'type': 'Point',
              'coordinates': <double>[p.lon, p.lat],
            },
          },
      ],
    });
  }

  /// L'identifiant du tracé sous [screenPoint], ou `null`.
  ///
  /// Interroge une petite croix de points autour du doigt : un trait de 5 px
  /// logiques est presque impossible à toucher au pixel près, et `queryLayers`
  /// n'offre pas de tolérance.
  String? trackIdAt(Offset screenPoint, {double tolerance = 12}) {
    final MapController? m = _map;
    if (m == null) return null;
    final List<Offset> probes = <Offset>[
      screenPoint,
      screenPoint.translate(-tolerance, 0),
      screenPoint.translate(tolerance, 0),
      screenPoint.translate(0, -tolerance),
      screenPoint.translate(0, tolerance),
    ];
    for (final Offset probe in probes) {
      late final List<QueriedLayer> hits;
      try {
        hits = m.queryLayers(probe);
      } catch (_) {
        return null;
      }
      for (final QueriedLayer hit in hits) {
        final String? id = _trackIdOfLayer(hit.layerId);
        if (id != null) return id;
      }
    }
    return null;
  }

  String? _trackIdOfLayer(String layerId) {
    final String prefix = '$layerPrefix-';
    if (!layerId.startsWith(prefix)) return null;
    final String candidate = layerId.substring(prefix.length);
    return _tracks.any((PdlMapTrack t) => t.id == candidate) ? candidate : null;
  }

  /// Cadre la carte sur [box].
  ///
  /// À n'appeler que depuis `MapEventStyleLoaded` (ou après) : c'est
  /// précisément ce qui remplace le `Future.delayed(100 ms)` de l'ancienne
  /// implémentation.
  Future<void> fitBox(
    PdlMapBox box, {
    EdgeInsets padding = const EdgeInsets.all(50),
    Duration nativeDuration = const Duration(milliseconds: 1),
  }) async {
    final MapController? m = _map;
    if (m == null) return;
    await m.fitBounds(
      bounds: box.toLngLatBounds(),
      padding: padding,
      nativeDuration: nativeDuration,
      webLinear: true,
    );
  }

  // ── Pose des couches ──────────────────────────────────────────────────

  Future<void> _applyAll() async {
    final StyleController? style = _style;
    if (style == null) return;

    await _safe(() => style.removeLayer(_pointsSourceId));
    await _safe(() => style.removeLayer('$_waypointsSourceId-dot'));

    // Une **seule** source pour tous les tracés (§1.0.3-7) ; c'est le
    // `layerId` qui porte l'identité, chaque couche étant filtrée sur son
    // `trackId`.
    if (_tracks.isNotEmpty) {
      await _safe(() => style.removeSource(_sourceId));
      await style.addSource(
        GeoJsonSource(id: _sourceId, data: _tracksGeoJson()),
      );
      for (final PdlMapTrack track in _tracks) {
        if (track.id == _selectedId) continue;
        await _addTrackLayer(style, track);
      }
      // Le sélectionné en dernier : c'est l'ordre de dessin qui fait le
      // premier plan.
      final PdlMapTrack? selected = _selectedTrack;
      if (selected != null) await _addTrackLayer(style, selected);
    }

    await _addPointLayers(style);
    await _addCursorLayer(style);
  }

  /// Pose la couche du réticule, **vide**, une fois pour toutes.
  ///
  /// Elle est ajoutée en dernier pour rester au-dessus des tracés et des
  /// marqueurs. Son contenu ne bouge ensuite que par [setCursor].
  Future<void> _addCursorLayer(StyleController style) async {
    await _safe(() => style.removeLayer('$_cursorSourceId-dot'));
    await _safe(() => style.removeSource(_cursorSourceId));
    await style.addSource(
      GeoJsonSource(id: _cursorSourceId, data: _cursorGeoJson()),
    );
    await style.addLayer(
      CircleStyleLayer(
        id: '$_cursorSourceId-dot',
        sourceId: _cursorSourceId,
        paint: <String, Object>{
          'circle-radius': 7,
          'circle-color': _cursorColor.toHexString(),
          'circle-stroke-width': 3,
          'circle-stroke-color': _pointStroke.toHexString(),
        },
      ),
    );
  }

  PdlMapTrack? get _selectedTrack {
    for (final PdlMapTrack t in _tracks) {
      if (t.id == _selectedId) return t;
    }
    return null;
  }

  Future<void> _addTrackLayer(StyleController style, PdlMapTrack track) async {
    final String id = '$layerPrefix-${track.id}';
    await style.addLayer(
      LineStyleLayer(
        id: id,
        sourceId: _sourceId,
        filter: <Object>[
          '==',
          <Object>['get', 'trackId'],
          track.id,
        ],
        layout: const <String, Object>{
          'line-cap': 'round',
          'line-join': 'round',
        },
        paint: <String, Object>{
          // Lue sur l'entité, jamais figée sur la couche : c'est ce qui permet
          // à un tracé colorisé par pente de tenir sur **une** couche.
          'line-color': <Object>['get', 'color'],
          'line-width': <Object>[
            'case',
            <Object>[
              '==',
              <Object>['get', 'sel'],
              1,
            ],
            trackTheme.selectedWidth,
            trackTheme.width,
          ],
          'line-opacity': <Object>[
            'case',
            <Object>[
              '==',
              <Object>['get', 'sel'],
              1,
            ],
            trackTheme.selectedOpacity,
            trackTheme.opacity,
          ],
        },
      ),
    );
    _drawnLayers.add(id);
  }

  Future<void> _raise(String trackId) async {
    final StyleController? style = _style;
    if (style == null) return;
    final PdlMapTrack? track = _selectedTrack;
    if (track == null || track.id != trackId) return;
    final String id = '$layerPrefix-$trackId';
    if (_drawnLayers.isEmpty || _drawnLayers.last == id) return;
    await _safe(() => style.removeLayer(id));
    _drawnLayers.remove(id);
    await _addTrackLayer(style, track);
  }

  Future<void> _addPointLayers(StyleController style) async {
    final List<Map<String, Object?>> features = <Map<String, Object?>>[];
    if (_start != null) {
      features.add(_pointFeature(_start!, 'start'));
    }
    if (_end != null) {
      features.add(_pointFeature(_end!, 'end'));
    }
    await _safe(() => style.removeSource(_pointsSourceId));
    if (features.isNotEmpty) {
      await style.addSource(
        GeoJsonSource(
          id: _pointsSourceId,
          data: jsonEncode(<String, Object?>{
            'type': 'FeatureCollection',
            'features': features,
          }),
        ),
      );
      await style.addLayer(
        CircleStyleLayer(
          id: _pointsSourceId,
          sourceId: _pointsSourceId,
          paint: <String, Object>{
            'circle-radius': 8,
            'circle-color': <Object>['get', 'color'],
            'circle-stroke-width': 2,
            'circle-stroke-color': <Object>['get', 'stroke'],
          },
        ),
      );
    }

    await _safe(() => style.removeSource(_waypointsSourceId));
    if (_waypoints.isNotEmpty) {
      await style.addSource(
        GeoJsonSource(
          id: _waypointsSourceId,
          data: jsonEncode(<String, Object?>{
            'type': 'FeatureCollection',
            'features': <Map<String, Object?>>[
              for (final PdlMapPoint w in _waypoints)
                <String, Object?>{
                  'type': 'Feature',
                  'properties': <String, Object?>{'name': w.label ?? ''},
                  'geometry': <String, Object?>{
                    'type': 'Point',
                    'coordinates': <double>[w.lon, w.lat],
                  },
                },
            ],
          }),
        ),
      );
      await style.addLayer(
        CircleStyleLayer(
          id: '$_waypointsSourceId-dot',
          sourceId: _waypointsSourceId,
          paint: <String, Object>{
            'circle-radius': 6,
            'circle-color': _waypointColor.toHexString(),
            'circle-stroke-width': 2,
            'circle-stroke-color': _pointStroke.toHexString(),
          },
        ),
      );
    }
  }

  /// Couleurs des marqueurs, injectées par [PdlMap] depuis `PdlColors`.
  Color _startColor = const Color(0xFF40C057);
  Color _endColor = const Color(0xFFFA5252);
  Color _waypointColor = const Color(0xFFFAB005);
  Color _pointStroke = const Color(0xFFFFFFFF);
  Color _cursorColor = const Color(0xFF4C6EF5);

  /// Injecte les couleurs de marqueur lues sur le thème.
  ///
  /// `core/pdl` interdit les littéraux de couleur : les valeurs par défaut
  /// ci-dessus ne sont qu'un filet avant le premier `build`, et [PdlMap] les
  /// remplace systématiquement par `context.pdl.mapStart` / `mapEnd` /
  /// `mapWaypoint` / `surface`.
  void setMarkerColors({
    required Color start,
    required Color end,
    required Color waypoint,
    required Color stroke,
    Color? cursor,
  }) {
    if (cursor != null) _cursorColor = cursor;
    _startColor = start;
    _endColor = end;
    _waypointColor = waypoint;
    _pointStroke = stroke;
  }

  Map<String, Object?> _pointFeature(PdlMapPoint p, String kind) =>
      <String, Object?>{
        'type': 'Feature',
        'properties': <String, Object?>{
          'kind': kind,
          'color': (kind == 'start' ? _startColor : _endColor).toHexString(),
          'stroke': _pointStroke.toHexString(),
        },
        'geometry': <String, Object?>{
          'type': 'Point',
          'coordinates': <double>[p.lon, p.lat],
        },
      };

  String _tracksGeoJson() {
    final List<Map<String, Object?>> features = <Map<String, Object?>>[];
    for (final PdlMapTrack track in _tracks) {
      final bool selected = track.id == _selectedId;
      if (track.isSegmented) {
        _appendSegments(features, track, selected);
        continue;
      }
      for (final List<List<double>> line in track.lines) {
        if (line.length < 2) continue;
        features.add(<String, Object?>{
          'type': 'Feature',
          'properties': <String, Object?>{
            'trackId': track.id,
            'sel': selected ? 1 : 0,
            'color': track.color.toHexString(),
          },
          'geometry': <String, Object?>{
            'type': 'LineString',
            // Les composantes au-delà de la latitude (altitude, cumul `M`) ne
            // servent à rien au rendu et pèsent lourd sur un long parcours :
            // on ne pousse que `[lon, lat]`.
            'coordinates': <List<double>>[
              for (final List<double> c in line) <double>[c[0], c[1]],
            ],
          },
        });
      }
    }
    return jsonEncode(<String, Object?>{
      'type': 'FeatureCollection',
      'features': features,
    });
  }

  /// Un segment = une entité, portant sa propre couleur.
  ///
  /// Les segments **consécutifs de même couleur sont fusionnés** : un parcours
  /// de 3 000 sommets produirait sinon 2 999 entités, là où une pente réelle
  /// n'a qu'une poignée de teintes distinctes par kilomètre. La fusion divise
  /// typiquement le nombre d'entités par dix sans changer un pixel.
  void _appendSegments(
    List<Map<String, Object?>> features,
    PdlMapTrack track,
    bool selected,
  ) {
    final List<Color> colors = track.segmentColors!;
    int segment = 0;

    for (final List<List<double>> line in track.lines) {
      if (line.length < 2) {
        segment += line.isEmpty ? 0 : line.length - 1;
        continue;
      }
      List<List<double>> run = <List<double>>[
        <double>[line[0][0], line[0][1]],
      ];
      String runColor =
          (segment < colors.length ? colors[segment] : track.color)
              .toHexString();

      for (int i = 1; i < line.length; i++) {
        final int index = segment + i - 1;
        final String color =
            (index < colors.length ? colors[index] : track.color).toHexString();
        if (color != runColor) {
          features.add(
            _segmentFeature(
              track.id,
              selected,
              runColor,
              List<List<double>>.of(run),
            ),
          );
          // Le nouveau tronçon repart du dernier sommet : sans ce
          // recouvrement, un trou d'un segment apparaîtrait à chaque
          // changement de teinte.
          run = <List<double>>[run.last];
          runColor = color;
        }
        run.add(<double>[line[i][0], line[i][1]]);
      }
      if (run.length >= 2) {
        features.add(_segmentFeature(track.id, selected, runColor, run));
      }
      segment += line.length - 1;
    }
  }

  Map<String, Object?> _segmentFeature(
    String trackId,
    bool selected,
    String color,
    List<List<double>> coordinates,
  ) => <String, Object?>{
    'type': 'Feature',
    'properties': <String, Object?>{
      'trackId': trackId,
      'sel': selected ? 1 : 0,
      'color': color,
    },
    'geometry': <String, Object?>{
      'type': 'LineString',
      'coordinates': coordinates,
    },
  };

  /// Avale l'échec d'un retrait : retirer une couche ou une source absente
  /// lève sur certaines plateformes, et c'est le cas normal au premier
  /// chargement.
  Future<void> _safe(Future<void> Function() action) async {
    try {
      await action();
    } catch (_) {
      // Sans effet : la couche n'existait pas.
    }
  }
}
