import 'dart:math' as math;

import 'package:flutter/widgets.dart';

import 'pdl_map_controller.dart';

/// Une position de caméra : un centre et un niveau de zoom.
@immutable
class PdlMapCamera {
  const PdlMapCamera({
    required this.lon,
    required this.lat,
    required this.zoom,
  });

  final double lon;
  final double lat;
  final double zoom;

  @override
  bool operator ==(Object other) =>
      other is PdlMapCamera &&
      other.lon == lon &&
      other.lat == lat &&
      other.zoom == zoom;

  @override
  int get hashCode => Object.hash(lon, lat, zoom);

  @override
  String toString() => 'PdlMapCamera($lon, $lat, z$zoom)';
}

/// La latitude au-delà de laquelle Web Mercator diverge. C'est la borne du
/// carré [0,1]² dans lequel toute la projection tient.
const double kWebMercatorMaxLat = 85.05112877980659;

/// x de Web Mercator, normalisé sur [0, 1] — 0 à 180° ouest, 1 à 180° est.
double pdlMercatorX(double lon) => (lon + 180) / 360;

/// y de Web Mercator, normalisé sur [0, 1] et **croissant vers le sud**,
/// comme l'axe des ordonnées de l'écran.
double pdlMercatorY(double lat) {
  final double clamped = lat.clamp(-kWebMercatorMaxLat, kWebMercatorMaxLat);
  final double rad = clamped * math.pi / 180;
  return 0.5 - math.log(math.tan(math.pi / 4 + rad / 2)) / (2 * math.pi);
}

double pdlLonFromMercatorX(double x) => x * 360 - 180;

double pdlLatFromMercatorY(double y) {
  final double n = math.pi * (1 - 2 * y);
  // atan(sinh(n)), sans `sinh` dans `dart:math`.
  final double sinh = (math.exp(n) - math.exp(-n)) / 2;
  return math.atan(sinh) * 180 / math.pi;
}

/// La caméra qui montre [box] dans une vue de [size], en lui réservant
/// [padding], ou `null` si la vue n'a pas de place utile.
///
/// **Pourquoi ce calcul plutôt que `MapController.fitBounds`.** `fitBounds`
/// délègue à la vue native, qui l'exécute sans erreur *et sans effet* quand
/// elle n'a pas encore sa taille — et qui, une fois cadrée, ignore un
/// changement de marge seule. Ici tout est déduit de [size], que le
/// `LayoutBuilder` de `PdlMap` donne de façon fiable et à l'avance : le
/// résultat ne dépend plus de l'état interne de MapLibre, et
/// `MapController.moveCamera` n'a plus qu'à s'y rendre — sans animation, ce
/// que `fitBounds` ne permet pas sur iOS où `animated: true` est en dur.
///
/// [worldPixelsAtZoom0] est la largeur du monde entier, en pixels logiques, au
/// zoom 0. C'est la taille de tuile de la plateforme (512 pour MapLibre GL,
/// 256 pour certaines vues natives) : elle n'est pas devinée mais **mesurée**
/// une fois par [PdlMapController.calibrate], parce que s'y tromper d'un
/// facteur deux coûte exactement un niveau de zoom.
///
/// Le zoom retenu est le plus petit des deux que chaque axe autorise : c'est ce
/// qui garantit que la boîte tient *entièrement*. Pour un parcours plus large
/// que haut, c'est la largeur qui commande — réduire la marge basse ne change
/// alors pas le zoom, seulement le centre, et c'est correct.
PdlMapCamera? pdlCameraForBox(
  PdlMapBox box, {
  required Size size,
  EdgeInsets padding = EdgeInsets.zero,
  double worldPixelsAtZoom0 = 512,
  double minZoom = 0,
  double maxZoom = 20,
}) {
  final double usableWidth = size.width - padding.horizontal;
  final double usableHeight = size.height - padding.vertical;
  if (usableWidth <= 0 || usableHeight <= 0 || worldPixelsAtZoom0 <= 0) {
    return null;
  }

  final double x0 = pdlMercatorX(box.minLon);
  final double x1 = pdlMercatorX(box.maxLon);
  // `maxLat` d'abord : y croît vers le sud.
  final double y0 = pdlMercatorY(box.maxLat);
  final double y1 = pdlMercatorY(box.minLat);

  final double spanX = (x1 - x0).abs();
  final double spanY = (y1 - y0).abs();

  // Une boîte dégénérée sur un axe ne contraint pas cet axe.
  final double zoomX = spanX <= 0
      ? maxZoom
      : _log2(usableWidth / (worldPixelsAtZoom0 * spanX));
  final double zoomY = spanY <= 0
      ? maxZoom
      : _log2(usableHeight / (worldPixelsAtZoom0 * spanY));

  final double zoom = math.min(zoomX, zoomY).clamp(minZoom, maxZoom);
  final double scale = worldPixelsAtZoom0 * math.pow(2, zoom);

  // Le centre de la boîte doit tomber au centre de la *zone utile*, pas au
  // centre de la vue. L'écart entre les deux, en pixels, est la demi-asymétrie
  // des marges ; converti en unités de projection, il décale la caméra. C'est
  // lui — et lui seul — qui pousse le tracé au-dessus d'une feuille, et qui
  // bouge quand elle change de cran.
  final double offsetX = (padding.right - padding.left) / 2 / scale;
  final double offsetY = (padding.bottom - padding.top) / 2 / scale;

  return PdlMapCamera(
    lon: pdlLonFromMercatorX(((x0 + x1) / 2 + offsetX).clamp(0.0, 1.0)),
    lat: pdlLatFromMercatorY(((y0 + y1) / 2 + offsetY).clamp(0.0, 1.0)),
    zoom: zoom,
  );
}

double _log2(double value) => math.log(value) / math.ln2;
