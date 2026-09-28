import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../rides/providers/ride_detail_provider.dart';

/// Ma prochaine participation, avec le groupe que j'ai rejoint.
@immutable
class NextRide {
  const NextRide({required this.ride, required this.group});

  final RideDto ride;

  /// Le groupe rejoint. `null` si l'API ne le rend pas — la carte se replie
  /// alors sur les informations de la sortie plutôt que d'inventer un horaire.
  final RideGroupDto? group;
}

/// « Ma prochaine sortie », détail du groupe compris.
///
/// Deux appels, **dans un seul provider** :
///
/// 1. `GET /api/users/me/participations` — **l'endpoint qui rend ce bloc
///    possible** : sans lui, il faudrait parcourir toutes les équipes et toutes
///    leurs sorties pour retrouver celle où l'on est inscrit. `size: 1` : on ne
///    veut que la plus proche ; `view: COMPACT` : la ligne suffit pour
///    identifier la sortie, le détail suit.
/// 2. **Un seul `getRide`**, qui passe par [rideDetailProvider] : taper « Voir
///    la sortie » n'entraîne donc aucun rechargement, l'écran 12 lit la même
///    entrée de cache.
///
/// L'identité de la sortie n'est **pas** mise en cache à part : elle l'a été,
/// dans un provider privé que rien ne pouvait invalider — ni le pull-to-refresh
/// de l'accueil (qui reconstruisait ce provider-ci en relisant l'ancienne
/// identité), ni une inscription. Invalider [nextRideProvider] suffit
/// désormais à tout reprendre ; c'est ce que fait
/// `notifyParticipationChanged`.
///
/// Un échec **masque le bloc** sans propager d'erreur : l'accueil est
/// consultable sans lui, et le brief refuse qu'un enrichissement casse un
/// écran.
final nextRideProvider = FutureProvider<NextRide?>((Ref ref) async {
  final PublicationListResponse response = await ref
      .watch(usersClientProvider)
      .listMyParticipations(
        from: DateTime.now().toUtc().toIso8601String(),
        status: Status.published,
        size: 1,
        view: ListViewMode.compact,
      );

  RideDto? summary;
  for (final PublicationDto publication in response.publications) {
    // Les voyages remontent aussi dans les participations ; ce bloc-ci parle
    // de sorties. Le carrousel « À venir » montre les deux.
    if (publication is PublicationDtoRide) {
      summary = _asRide(publication);
      break;
    }
  }
  if (summary == null) return null;

  final RideDto detail = await ref.watch(
    rideDetailProvider(
      RideKey(teamSlug: summary.team.slug, rideSlug: summary.slug),
    ).future,
  );

  // `groups[]` est vide sur une ligne de liste (`RideDto.fromListItem` passe
  // `List.of()` côté serveur) : c'est le détail qui porte le groupe, et
  // `registeredGroupId` qui dit lequel.
  return NextRide(ride: detail, group: detail.registeredGroup);
});

/// Reconstruit un `RideDto` depuis une ligne de fil.
///
/// Volontairement minimal : cette valeur ne sert qu'à connaître `(teamSlug,
/// slug)`. Tout le reste vient du détail, une ligne de tête.
RideDto _asRide(PublicationDtoRide p) => RideDto(
  type: 'RIDE',
  team: p.team,
  id: p.id,
  slug: p.slug,
  name: p.name,
  media: p.media,
  dateTime: p.dateTime,
  status: p.status,
  visibility: p.visibility,
  participantCount: p.participantCount,
  groupCount: p.groupCount,
  groups: p.groups,
  topParticipants: p.topParticipants,
  deleted: p.deleted,
  registered: p.registered,
  registeredGroupId: p.registeredGroupId,
  full: p.full,
);
