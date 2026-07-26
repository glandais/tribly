import 'package:flutter/foundation.dart';

import '../../../../core/geo/polyline_index.dart';
import '../../../../core/pdl/map/pdl_map_controller.dart';

/// Le réticule, **partagé entre le profil et la carte**.
///
/// Un unique `ValueNotifier<double?>` : une distance cumulée en mètres, ou
/// `null` quand rien n'est pointé. Le lien entre les deux vues est **toujours**
/// une distance, jamais un index de point — le profil est échantillonné à ~300
/// points, la géométrie en compte jusqu'à 3 000, et faire correspondre deux
/// index de tailles différentes serait faux d'un bout à l'autre.
///
/// **Ce n'est pas un provider Riverpod, et c'est délibéré.** Il change à chaque
/// frame de glissement ; passer par Riverpod reconstruirait l'arbre à 60 fps.
/// Ici, seuls deux `CustomPaint` et une source GeoJSON écoutent.
class RouteCursorController {
  RouteCursorController({required List<List<List<double>>> lines})
    : _index = PolylineIndex.fromLineStrings(lines);

  final PolylineIndex _index;

  /// La distance survolée, en mètres. Lue par `PdlElevationProfile` comme
  /// `repaint` de son painter de réticule.
  final ValueNotifier<double?> distance = ValueNotifier<double?>(null);

  PolylineIndex get index => _index;

  double get totalDistance => _index.totalDistance;

  /// Profil → carte : la distance devient une position.
  PdlMapPoint? positionFor(double? meters) {
    if (meters == null) return null;
    final LngLat? p = _index.positionAt(meters);
    return p == null ? null : PdlMapPoint(lon: p.lng, lat: p.lat);
  }

  /// Carte → profil : un tap devient une distance.
  ///
  /// [maxOffsetMeters] borne la tolérance : taper à trois kilomètres du tracé
  /// ne doit pas y accrocher un réticule. Le seuil est en mètres terrain, non
  /// en pixels — c'est ce qui le rend indépendant du zoom, au prix d'être plus
  /// permissif de loin, ce qui est le comportement attendu.
  double? distanceNear(double lon, double lat, {double maxOffsetMeters = 300}) {
    final PolylineMatch? match = _index.nearestMatch(LngLat(lon, lat));
    if (match == null || match.offset > maxOffsetMeters) return null;
    return match.distance;
  }

  void moveTo(double? meters) => distance.value = meters;

  void clear() => distance.value = null;

  void dispose() => distance.dispose();
}
