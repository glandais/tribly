import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../data/ride_repository.dart';

/// Ce qui identifie une sortie : son équipe et son slug.
@immutable
class RideKey {
  const RideKey({required this.teamSlug, required this.rideSlug});

  final String teamSlug;
  final String rideSlug;

  @override
  bool operator ==(Object other) =>
      other is RideKey &&
      other.teamSlug == teamSlug &&
      other.rideSlug == rideSlug;

  @override
  int get hashCode => Object.hash(teamSlug, rideSlug);

  @override
  String toString() => 'RideKey($teamSlug/$rideSlug)';
}

/// Le détail d'une sortie, `groups[]` peuplées.
///
/// Point d'entrée **unique** du détail : l'écran 12, le contrôleur
/// d'inscription et le bloc « Ma prochaine sortie » de l'accueil (S11-1) le
/// partagent, si bien qu'ouvrir la sortie depuis l'accueil ne recharge rien.
///
/// Non `autoDispose` : le garder vivant est précisément ce qui rend ce partage
/// utile ; l'invalidation est explicite après une inscription ou un
/// pull-to-refresh.
final rideDetailProvider = FutureProvider.family<RideDto, RideKey>(
  (Ref ref, RideKey key) =>
      ref.watch(rideRepositoryProvider).getRide(key.teamSlug, key.rideSlug),
);

/// Une sortie a-t-elle eu lieu ?
///
/// Dérivé **client** faute de statut serveur : le contrat ne porte que
/// `PUBLISHED` / `DRAFT` / `CANCELLED`, jamais « terminée ». La comparaison se
/// fait donc sur l'horloge de l'appareil, dans le fuseau de l'appareil
/// (§1.0.3-11). C'est l'**unique** définition de « passée » ; les écrans 11, 12
/// et 13 la partagent plutôt que d'en recalculer chacun une variante.
extension RideTiming on RideDto {
  DateTime? get startsAt => DateTime.tryParse(dateTime)?.toLocal();

  bool get isPast {
    final DateTime? start = startsAt;
    return start != null && start.isBefore(DateTime.now());
  }

  bool get isCancelled => status == 'CANCELLED';

  /// Le groupe rejoint par l'utilisateur, ou `null`.
  ///
  /// Lu sur `registeredGroupId` : **jamais** en parcourant `participants[]`,
  /// qui est vide sans droit de lecture et donnait de faux négatifs.
  RideGroupDto? get registeredGroup {
    final String? id = registeredGroupId;
    if (id == null) return null;
    for (final RideGroupDto group in groups) {
      if (group.id == id) return group;
    }
    return null;
  }
}
