import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../data/route_repository.dart';

/// Ce qui identifie un parcours : son équipe et son slug.
@immutable
class RouteKey {
  const RouteKey({required this.teamSlug, required this.routeSlug});

  final String teamSlug;
  final String routeSlug;

  @override
  bool operator ==(Object other) =>
      other is RouteKey &&
      other.teamSlug == teamSlug &&
      other.routeSlug == routeSlug;

  @override
  int get hashCode => Object.hash(teamSlug, routeSlug);

  @override
  String toString() => 'RouteKey($teamSlug/$routeSlug)';
}

/// Le détail d'un parcours pour sa fiche.
///
/// **`simplify: 5` / `points: 3000`.** Ces deux paramètres existent au contrat
/// et n'étaient pas envoyés : la fiche téléchargeait la géométrie brute, soit
/// plusieurs mégaoctets sur un parcours de 150 km, pour dessiner un tracé de
/// 390 px de large. Cinq mètres de tolérance sont invisibles à l'écran ; le
/// plafond de sommets borne le pire cas.
///
/// Un **seul** appel, pas de « simplifié puis complet » : deux passes
/// feraient clignoter le tracé pour un gain que l'œil ne voit pas.
final routeDetailProvider = FutureProvider.family<RouteDetailDto, RouteKey>((
  Ref ref,
  RouteKey key,
) {
  return ref
      .watch(routeRepositoryProvider)
      .getRouteDetail(key.teamSlug, key.routeSlug, simplify: 5, points: 3000);
});

/// Où ce parcours est employé : sorties, publications, étapes de voyage.
///
/// `GET …/usages` **existe depuis longtemps et n'avait jamais été appelé** par
/// le mobile. C'est cet écran qui l'active.
///
/// `autoDispose` : la liste n'a de sens que sur la fiche ouverte.
final routeUsagesProvider =
    FutureProvider.family<List<RouteUsageDto>, RouteKey>((
      Ref ref,
      RouteKey key,
    ) async {
      final RouteUsagesResponse response = await ref
          .watch(routesClientProvider)
          .getRouteUsages(teamSlug: key.teamSlug, routeSlug: key.routeSlug);
      return response.usages;
    }, isAutoDispose: true);

/// Les montées d'un parcours, **toutes pistes confondues, dans l'ordre**.
///
/// Le serveur concatène les pistes pour calculer le profil ; le client doit
/// concaténer dans le **même ordre**, sans quoi les distances des montées ne
/// coïncideraient pas avec l'axe du profil ni avec `PolylineIndex`.
extension RouteClimbs on RouteDetailDto {
  List<ClimbDto> get allClimbs => <ClimbDto>[
    for (final TrackDto t in tracks) ...t.climbs,
  ];

  bool get hasGeometry =>
      tracks.any((TrackDto t) => t.line.coordinates.isNotEmpty);
}
