/// Le modèle de données du profil altimétrique, et son agrégation.
///
/// **Pur Dart, aucun DTO.** `core/pdl` ne connaît pas le client d'API généré
/// (contrat du module, §3) : le widget prend un [ElevationSamples], et c'est
/// l'écran qui le construit depuis la géométrie du parcours — la conversion vit
/// dans `features/routes/domain/route_elevation_builder.dart`.
///
/// L'agrégation est ici, pas dans le painter : elle ne dépend ni du thème ni de
/// la taille de la boîte, elle se calcule une fois par profil et se teste sans
/// binding Flutter.
library;

import 'package:flutter/foundation.dart';

/// Nombre de barres visé par l'agrégation.
///
/// Le profil est dérivé de la géométrie stockée, soit des centaines à des
/// milliers de sommets ; les dessiner tous sur 402 pt donnerait des barres d'un
/// pixel et un histogramme illisible. 76 barres à gap 1 px tiennent exactement
/// la largeur d'un écran de téléphone.
const int kElevationBarTarget = 76;

/// Un point de profil : distance cumulée et altitude, toutes deux en mètres.
///
/// [grade] est la pente **en pourcent du segment qui se termine sur ce point**,
/// nulle quand elle est inconnue — le premier point d'un profil n'en a pas.
@immutable
class ElevationPoint {
  const ElevationPoint({
    required this.distance,
    required this.elevation,
    this.grade,
  });

  final double distance;
  final double elevation;
  final double? grade;

  @override
  bool operator ==(Object other) =>
      other is ElevationPoint &&
      other.distance == distance &&
      other.elevation == elevation &&
      other.grade == grade;

  @override
  int get hashCode => Object.hash(distance, elevation, grade);

  @override
  String toString() => 'ElevationPoint($distance m, $elevation m, $grade %)';
}

/// Une barre de l'histogramme : une tranche de distance, une altitude, une
/// pente moyenne.
@immutable
class ElevationBar {
  const ElevationBar({
    required this.start,
    required this.end,
    required this.elevation,
    required this.grade,
  });

  /// Bornes de la tranche, en mètres cumulés.
  final double start;
  final double end;

  /// Altitude retenue pour la hauteur de la barre, en mètres.
  final double elevation;

  /// Pente moyenne **pondérée par la distance** des segments couverts, en
  /// pourcent. Nulle quand aucun segment couvert n'a de pente connue : la barre
  /// est alors peinte en `slopeNeutral`.
  final double? grade;

  double get center => (start + end) / 2;

  @override
  bool operator ==(Object other) =>
      other is ElevationBar &&
      other.start == start &&
      other.end == end &&
      other.elevation == elevation &&
      other.grade == grade;

  @override
  int get hashCode => Object.hash(start, end, elevation, grade);
}

/// Ce que l'info-bulle affiche à une abscisse donnée.
@immutable
class ElevationReading {
  const ElevationReading({
    required this.distance,
    required this.elevation,
    required this.grade,
  });

  /// Abscisse exacte du réticule, en mètres — **pas** le centre de la barre :
  /// c'est elle qui est envoyée à la carte, et une carte au mètre près ne se
  /// contente pas d'un centre de tranche.
  final double distance;

  /// Altitude interpolée entre les deux points encadrants, en mètres.
  final double elevation;

  /// Pente de la barre survolée, en pourcent, nulle si inconnue.
  final double? grade;

  @override
  bool operator ==(Object other) =>
      other is ElevationReading &&
      other.distance == distance &&
      other.elevation == elevation &&
      other.grade == grade;

  @override
  int get hashCode => Object.hash(distance, elevation, grade);
}

/// Un profil prêt à peindre : les points source, et leur agrégation en barres.
///
/// L'objet est **immuable et partagé par identité** : `shouldRepaint` du
/// painter des barres compare `identical`, donc reconstruire le même profil à
/// chaque frame le repeindrait. Construisez-le une fois, gardez-le.
@immutable
class ElevationSamples {
  const ElevationSamples({
    required this.points,
    required this.bars,
    required this.totalDistance,
    required this.minElevation,
    required this.maxElevation,
  });

  /// Agrège [points] vers [targetBars] barres.
  ///
  /// Deux précautions :
  ///
  /// * un tracé très court peut n'avoir que trois sommets. Le nombre de barres
  ///   est donc plafonné au nombre de segments disponibles — on n'invente pas
  ///   de résolution.
  /// * Les points ne sont **pas** équidistants : le tracé stocké est filtré
  ///   Douglas-Peucker, donc dense dans les lacets et clairsemé en ligne
  ///   droite. La pente d'une barre est la moyenne des pentes des segments
  ///   qu'elle couvre **pondérée par la longueur du recouvrement**, jamais une
  ///   moyenne arithmétique.
  ///
  /// [minElevation] / [maxElevation] / [totalDistance] priment sur les valeurs
  /// déduites des points quand elles sont fournies.
  factory ElevationSamples.fromPoints(
    List<ElevationPoint> points, {
    int targetBars = kElevationBarTarget,
    double? totalDistance,
    double? minElevation,
    double? maxElevation,
  }) {
    if (points.length < 2) {
      return ElevationSamples(
        points: List<ElevationPoint>.unmodifiable(points),
        bars: const <ElevationBar>[],
        totalDistance: totalDistance ?? 0,
        minElevation:
            minElevation ?? (points.isEmpty ? 0 : points.first.elevation),
        maxElevation:
            maxElevation ?? (points.isEmpty ? 0 : points.first.elevation),
      );
    }

    final double first = points.first.distance;
    final double last = points.last.distance;
    final double span = last - first;
    final double total = totalDistance ?? span;

    double lo = points.first.elevation;
    double hi = points.first.elevation;
    for (final ElevationPoint p in points) {
      if (p.elevation < lo) lo = p.elevation;
      if (p.elevation > hi) hi = p.elevation;
    }
    lo = minElevation ?? lo;
    hi = maxElevation ?? hi;

    // `points.length - 1` segments : au-delà, des barres seraient des copies.
    final int barCount = targetBars.clamp(1, points.length - 1);

    final List<ElevationBar> bars = <ElevationBar>[];
    if (span <= 0) {
      // Profil dégénéré (tous les points à la même abscisse) : une barre plate
      // plutôt qu'une division par zéro.
      bars.add(
        ElevationBar(
          start: first,
          end: last,
          elevation: points.first.elevation,
          grade: points.last.grade,
        ),
      );
    } else {
      final double step = span / barCount;
      int cursor = 1;
      for (int b = 0; b < barCount; b++) {
        final double start = first + step * b;
        final double end = b == barCount - 1 ? last : first + step * (b + 1);

        // Moyenne pondérée : on ne recule jamais `cursor`, chaque segment est
        // visité au plus une fois par barre, l'agrégation reste linéaire.
        double weighted = 0;
        double weight = 0;
        int j = cursor;
        while (j < points.length && points[j - 1].distance < end) {
          final double segStart = points[j - 1].distance;
          final double segEnd = points[j].distance;
          final double overlap =
              (segEnd < end ? segEnd : end) -
              (segStart > start ? segStart : start);
          final double? grade = points[j].grade;
          if (overlap > 0 && grade != null) {
            weighted += grade * overlap;
            weight += overlap;
          }
          if (segEnd <= end) {
            cursor = j + 1;
            j = cursor;
          } else {
            break;
          }
        }

        bars.add(
          ElevationBar(
            start: start,
            end: end,
            elevation: _interpolate(points, (start + end) / 2),
            grade: weight > 0 ? weighted / weight : null,
          ),
        );
      }
    }

    return ElevationSamples(
      points: List<ElevationPoint>.unmodifiable(points),
      bars: List<ElevationBar>.unmodifiable(bars),
      totalDistance: total,
      minElevation: lo,
      maxElevation: hi,
    );
  }

  final List<ElevationPoint> points;
  final List<ElevationBar> bars;

  /// Longueur couverte par le profil, en mètres. C'est l'échelle de l'axe.
  final double totalDistance;
  final double minElevation;
  final double maxElevation;

  bool get isEmpty => bars.isEmpty;

  /// Hauteur relative d'une barre, dans `[0.10, 0.94]`.
  ///
  /// Le plancher de 10 % est voulu : une barre de hauteur nulle au point le
  /// plus bas ferait un trou dans l'histogramme, pas une vallée.
  double normalizedHeight(ElevationBar bar) {
    final double range = maxElevation - minElevation;
    final double t = range <= 0
        ? 0.0
        : ((bar.elevation - minElevation) / range).clamp(0.0, 1.0);
    return 0.10 + (0.94 - 0.10) * t;
  }

  /// Ce qu'affiche l'info-bulle à l'abscisse [distance].
  ///
  /// L'altitude est **interpolée** entre les deux points encadrants ; la pente
  /// est celle de la barre, qui est déjà une moyenne.
  ElevationReading? readingAt(double distance) {
    if (points.isEmpty) return null;
    final double d = distance.clamp(
      points.first.distance,
      points.last.distance,
    );
    return ElevationReading(
      distance: distance,
      elevation: _interpolate(points, d),
      grade: barAt(distance)?.grade,
    );
  }

  /// La barre qui couvre [distance], ou `null` si le profil est vide.
  ElevationBar? barAt(double distance) {
    if (bars.isEmpty) return null;
    final double start = bars.first.start;
    final double span = bars.last.end - start;
    if (span <= 0) return bars.first;
    final int index = ((distance - start) / span * bars.length).floor().clamp(
      0,
      bars.length - 1,
    );
    return bars[index];
  }
}

/// Altitude à l'abscisse [d], par dichotomie puis interpolation linéaire.
double _interpolate(List<ElevationPoint> points, double d) {
  if (d <= points.first.distance) return points.first.elevation;
  if (d >= points.last.distance) return points.last.elevation;

  int low = 0;
  int high = points.length - 1;
  while (high - low > 1) {
    final int mid = (low + high) ~/ 2;
    if (points[mid].distance <= d) {
      low = mid;
    } else {
      high = mid;
    }
  }
  final ElevationPoint a = points[low];
  final ElevationPoint b = points[high];
  final double gap = b.distance - a.distance;
  if (gap <= 0) return a.elevation;
  return a.elevation + (b.elevation - a.elevation) * (d - a.distance) / gap;
}
