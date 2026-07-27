import 'dart:math' as math;
import 'dart:ui';

import '../../../core/geo/polyline_index.dart';
import '../../../core/theme/pdl_tokens.dart';

/// La couleur de chaque segment d'un tracé, dérivée de sa **pente**.
///
/// La source est la géométrie elle-même, pas le profil altimétrique : les
/// coordonnées GeoJSON du contrat sont en `G3DM`, `[lon, lat, altitude,
/// cumul]`, et la pente d'un segment se lit donc exactement entre ses deux
/// sommets. C'est aussi la source du profil altimétrique
/// (`route_elevation_builder.dart`), et les deux doivent le rester : deux
/// règles de calcul de longueur donneraient des bandes de couleur décalées des
/// barres du profil.
///
/// **Pente signée, colorisation non signée.** L'échelle du §1.1.5 va du vert au
/// violet sur `0 → 18 %` : une descente à −8 % est peinte comme une montée à
/// 8 %, ce qui est le rendu voulu — c'est la difficulté du relief qu'on montre,
/// pas son sens.
///
/// Un segment dont l'altitude ou la longueur manque prend [slopeNeutral] : on
/// ne devine pas une pente.
List<Color> slopeColorsForLines(Iterable<List<List<double>>> lines) {
  final List<Color> colors = <Color>[];
  for (final List<List<double>> line in lines) {
    for (int i = 1; i < line.length; i++) {
      colors.add(_segmentColor(line[i - 1], line[i]));
    }
  }
  return colors;
}

Color _segmentColor(List<double> from, List<double> to) {
  if (from.length < 3 || to.length < 3) return slopeNeutral;

  final double rise = to[2] - from[2];
  final double run = _runMeters(from, to);
  // Sous le mètre, le rapport explose : deux sommets superposés donneraient
  // une pente de plusieurs centaines de pourcents.
  if (run < 1) return slopeNeutral;

  return slopeColor((rise / run * 100).abs());
}

/// La longueur d'un segment : le cumul `M` quand il est là, la haversine
/// sinon. C'est la même règle que [PolylineIndex], et il faut qu'elle le reste.
double _runMeters(List<double> from, List<double> to) {
  if (from.length >= 4 && to.length >= 4) {
    final double delta = to[3] - from[3];
    if (delta.isFinite && delta > 0) return delta;
  }
  return haversineMeters(LngLat(from[0], from[1]), LngLat(to[0], to[1]));
}

/// La pente moyenne d'un intervalle, en pourcent — utilitaire de test et de
/// libellé, pas de rendu.
double averageGradePercent(double riseMeters, double runMeters) =>
    runMeters <= 0 ? 0 : riseMeters / runMeters * 100;

/// L'altitude interpolée à une distance cumulée, pour l'info-bulle du réticule
/// quand elle est alimentée par la géométrie plutôt que par le profil.
double? elevationAt(Iterable<List<List<double>>> lines, double meters) {
  double cumulative = 0;
  for (final List<List<double>> line in lines) {
    for (int i = 1; i < line.length; i++) {
      final List<double> a = line[i - 1];
      final List<double> b = line[i];
      final double run = _runMeters(a, b);
      if (meters <= cumulative + run) {
        if (a.length < 3 || b.length < 3) return null;
        final double t = run <= 0
            ? 0
            : ((meters - cumulative) / run).clamp(0.0, 1.0);
        return a[2] + (b[2] - a[2]) * t;
      }
      cumulative += run;
    }
  }
  return null;
}

/// Le nombre de segments d'un ensemble de lignes — le contrat que
/// `PdlMapTrack.segmentColors` doit respecter.
int segmentCount(Iterable<List<List<double>>> lines) => lines.fold(
  0,
  (int sum, List<List<double>> l) => sum + math.max(0, l.length - 1),
);
