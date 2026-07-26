import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

/// La clé d'un profil altimétrique : un parcours et une finesse d'échantillonnage.
///
/// Le nombre d'échantillons fait partie de la clé parce qu'il fait partie de la
/// réponse : deux largeurs d'écran demandent deux profils différents. Il est
/// **quantifié** par [ElevationProfileKey.forWidth] pour qu'une rotation de
/// quelques pixels ne crée pas une entrée de cache par largeur possible.
@immutable
class ElevationProfileKey {
  const ElevationProfileKey({
    required this.teamSlug,
    required this.routeSlug,
    required this.samples,
  });

  /// Règle unique de demande (§1.0.3-5 du plan) : `2 × largeur logique`, borné
  /// à `[60, 300]`. Deux barres par pixel suffisent — le rendu agrège ensuite
  /// vers ~76 barres — et le serveur borne de toute façon à `2..1000`.
  ///
  /// La valeur est arrondie au multiple de 20 supérieur : sans cela, chaque
  /// largeur d'appareil produirait sa propre entrée de cache pour un même
  /// parcours, et changer d'orientation retéléchargerait le profil.
  factory ElevationProfileKey.forWidth({
    required String teamSlug,
    required String routeSlug,
    required double logicalWidth,
  }) {
    final int raw = (logicalWidth * 2).round().clamp(60, 300);
    final int quantised = ((raw + 19) ~/ 20 * 20).clamp(60, 300);
    return ElevationProfileKey(
      teamSlug: teamSlug,
      routeSlug: routeSlug,
      samples: quantised,
    );
  }

  final String teamSlug;
  final String routeSlug;
  final int samples;

  @override
  bool operator ==(Object other) =>
      other is ElevationProfileKey &&
      other.teamSlug == teamSlug &&
      other.routeSlug == routeSlug &&
      other.samples == samples;

  @override
  int get hashCode => Object.hash(teamSlug, routeSlug, samples);

  @override
  String toString() => 'ElevationProfileKey($teamSlug/$routeSlug @$samples)';
}

/// Le profil altimétrique d'un parcours.
///
/// **Ce n'est plus un calcul depuis `tracks[].line`** : la géométrie complète
/// pèse plusieurs mégaoctets et n'a aucune raison d'être téléchargée pour
/// dessiner un histogramme de 76 barres.
///
/// `autoDispose` : un profil est lié à un écran ouvert. Le garder en mémoire
/// après fermeture retiendrait autant de listes de points qu'on a consulté de
/// parcours dans la session.
final elevationProfileProvider =
    FutureProvider.family<ElevationProfileDto, ElevationProfileKey>(
      (Ref ref, ElevationProfileKey key) => ref
          .watch(routesClientProvider)
          .getRouteElevationProfile(
            teamSlug: key.teamSlug,
            routeSlug: key.routeSlug,
            samples: key.samples,
          ),
      isAutoDispose: true,
    );
