import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

/// Combien de jours en avant le carrousel « À venir » regarde.
const int kUpcomingWindowDays = 30;

/// Combien d'entrées il montre au plus.
const int kUpcomingLimit = 10;

/// Les sorties et voyages des trente prochains jours, **toutes équipes**.
///
/// Le filtrage sur les deux types se fait **côté client** : le paramètre `type`
/// de l'endpoint n'en accepte qu'un, et deux appels pour dix cartes coûteraient
/// plus cher que d'écarter les publications et les annonces à l'arrivée.
///
/// Comme la carte « Ma prochaine sortie », c'est un enrichissement : un échec
/// masque la rangée, il ne casse pas l'accueil.
final upcomingProvider = FutureProvider<List<PublicationDto>>((Ref ref) async {
  final DateTime now = DateTime.now().toUtc();
  final PublicationListResponse response = await ref
      .watch(publicationsClientProvider)
      .listAllPublications(
        from: now.toIso8601String(),
        to: now
            .add(const Duration(days: kUpcomingWindowDays))
            .toIso8601String(),
        status: Status.published,
        size: kUpcomingLimit,
        view: ListViewMode.compact,
      );

  return <PublicationDto>[
    for (final PublicationDto p in response.publications)
      if (p is PublicationDtoRide || p is PublicationDtoTrip) p,
  ];
});

/// L'action que propose la carte d'un carrousel.
///
/// Entièrement calculable côté client, **sans appel** : `registered`, `full` et
/// `groupCount` sont tous portés par la ligne de liste depuis la 1.3.0.
enum UpcomingAction {
  /// Badge « ✓ Inscrit », pas de bouton.
  registered,

  /// Bouton désactivé « Complet ».
  full,

  /// Un seul groupe : « Rejoindre », qui ouvre le détail en `autoJoin`.
  join,

  /// Plusieurs groupes : « Choisir un groupe », qui ouvre le détail.
  chooseGroup,
}

/// Dérive l'action d'une sortie de carrousel.
///
/// « Rejoindre » **ne peut pas inscrire directement** : la ligne de liste ne
/// porte pas `groups[]` (le serveur y passe `List.of()`), l'identifiant du
/// groupe unique est donc inconnu ici. Le bouton navigue vers l'écran 12 avec
/// `autoJoin`, qui déclenche l'inscription une fois le détail chargé — et
/// seulement s'il y a bien un seul groupe non plein. C'est un aller simple,
/// pas une boîte de dialogue.
UpcomingAction upcomingAction(PublicationDtoRide ride) {
  if (ride.registered) return UpcomingAction.registered;
  if (ride.full) return UpcomingAction.full;
  return ride.groupCount == 1
      ? UpcomingAction.join
      : UpcomingAction.chooseGroup;
}
