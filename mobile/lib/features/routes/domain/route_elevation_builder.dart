import '../../../api/generated/export.dart';
import '../../../core/geo/polyline_index.dart';
import '../../../core/pdl/elevation/elevation_samples.dart';

/// La frontière entre le contrat et la bibliothèque de composants.
///
/// `core/pdl` ne connaît aucun DTO (contrat du module, §3) : c'est ici que la
/// géométrie d'un parcours devient un [ElevationSamples], et nulle part
/// ailleurs.
///
/// **Il n'y a plus d'endpoint de profil.** Les coordonnées du contrat sont en
/// `G3DM` — `[lon, lat, altitude, cumul]` — donc la géométrie porte déjà tout
/// ce qu'un profil contenait, à une résolution supérieure : ~90 m entre deux
/// sommets là où le profil rééchantillonnait à 300 points quelle que soit la
/// longueur du parcours. Un aller-retour réseau de moins, et surtout **plus
/// aucune réconciliation** : l'axe des distances du profil et celui de
/// [PolylineIndex] sont désormais le même tableau, alors qu'ils divergeaient
/// sur les parcours multi-tracés (le serveur concaténait les tracés sans le
/// pont haversine que l'index insère entre deux d'entre eux).
///
/// Renvoie `null` quand il n'y a rien à tracer — pas de géométrie, ou une
/// géométrie sans altitude. L'appelant ne rend alors pas de profil du tout ;
/// c'est le cas que l'endpoint serveur absorbait silencieusement.
ElevationSamples? elevationSamplesFromLines(
  Iterable<List<List<double>>> lines, {
  int targetBars = kElevationBarTarget,
}) {
  final List<ElevationPoint>? points = elevationPointsFromLines(lines);
  if (points == null) return null;

  return ElevationSamples.fromPoints(
    points,
    targetBars: targetBars,
    totalDistance: points.last.distance,
  );
}

/// Les points de profil bruts, distances **repartant de zéro**, avant toute
/// agrégation en barres.
///
/// Existe pour le profil global d'un voyage, qui doit décaler chaque étape de
/// la distance des précédentes puis agréger **une seule fois** sur l'ensemble :
/// agréger étape par étape donnerait des barres de largeurs différentes selon
/// la longueur de l'étape.
///
/// Renvoie `null` quand il n'y a pas de quoi tracer : moins de deux sommets,
/// ou une géométrie dont une coordonnée au moins n'a pas d'altitude.
List<ElevationPoint>? elevationPointsFromLines(
  Iterable<List<List<double>>> lines,
) {
  final PolylineIndex index = PolylineIndex.fromLineStrings(lines);
  if (index.length < 2 || !index.hasElevations) return null;

  final List<double> cumulative = index.cumulativeDistances;
  final List<double?> elevations = index.elevations;

  return <ElevationPoint>[
    for (int i = 0; i < cumulative.length; i++)
      ElevationPoint(
        distance: cumulative[i],
        elevation: elevations[i]!,
        // La pente du segment **se terminant** sur ce point ; le premier n'en
        // termine aucun. La longueur du segment est lue sur les cumuls de
        // l'index, donc suit exactement sa règle (mesure `M` quand elle est
        // là, haversine sinon, pont haversine entre deux tracés) — la calculer
        // à part la ferait dériver des couleurs de segments de la carte.
        grade: i == 0 ? null : _grade(cumulative, elevations, i),
      ),
  ];
}

double? _grade(List<double> cumulative, List<double?> elevations, int i) {
  final double run = cumulative[i] - cumulative[i - 1];
  // Sous le mètre, le rapport explose : deux sommets superposés donneraient
  // une pente de plusieurs centaines de pourcents.
  if (!run.isFinite || run < 1) return null;
  return (elevations[i]! - elevations[i - 1]!) / run * 100;
}

/// Le profil d'un parcours, tous ses tracés bout à bout dans l'ordre du
/// serveur — le même ordre que celui des montées ([RouteClimbs.allClimbs]) et
/// que celui du [PolylineIndex] du curseur.
extension RouteDetailElevation on RouteDetailDto {
  List<List<List<double>>> get lines => <List<List<double>>>[
    for (final TrackDto t in tracks) t.line.coordinates,
  ];

  ElevationSamples? elevationSamples({int targetBars = kElevationBarTarget}) =>
      elevationSamplesFromLines(lines, targetBars: targetBars);

  List<ElevationPoint>? get elevationPoints => elevationPointsFromLines(lines);
}
