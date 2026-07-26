import '../../../api/generated/export.dart';
import '../providers/ride_detail_provider.dart';

/// L'action offerte par une carte de groupe.
///
/// Six états, **tous dérivés de champs serveur** : `registered` et `full` sont
/// calculés par l'API depuis la 1.3.0. Rien ici ne parcourt `participants[]`,
/// qui est vide sans droit de lecture et tronqué au-delà de cinq entrées —
/// c'était la cause de « Participer » proposé à un inscrit, puis du 409 qui
/// suivait.
enum RideGroupAction {
  /// Aucune action : sortie passée ou annulée, ou utilisateur non membre.
  none,

  /// `PdlButton(fill, sm)` « Rejoindre ».
  join,

  /// `PdlButton(outline, sm)` « Quitter » — l'utilisateur est dans **ce**
  /// groupe.
  leave,

  /// `PdlButton(sm)` désactivé « Complet ».
  full,
}

/// Dérive l'action d'un groupe, sans heuristique.
///
/// [isMember] traduit l'appartenance à l'équipe : un non-membre ne voit aucun
/// bouton, un bandeau prend sa place à l'échelle de l'écran.
RideGroupAction rideGroupAction({
  required RideDto ride,
  required RideGroupDto group,
  required bool isMember,
}) {
  // Une sortie annulée ne propose rien, **pas même « Quitter »** : elle n'a
  // plus lieu, se désinscrire n'a plus de sens.
  if (ride.isCancelled || ride.isPast) return RideGroupAction.none;
  if (group.registered) return RideGroupAction.leave;
  if (!isMember) return RideGroupAction.none;
  if (group.full) return RideGroupAction.full;
  return RideGroupAction.join;
}
