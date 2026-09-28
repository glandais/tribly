import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../data/trip_repository.dart';

/// Ce qui identifie un voyage : son équipe et son slug.
@immutable
class TripKey {
  const TripKey({required this.teamSlug, required this.tripSlug});

  final String teamSlug;
  final String tripSlug;

  @override
  bool operator ==(Object other) =>
      other is TripKey &&
      other.teamSlug == teamSlug &&
      other.tripSlug == tripSlug;

  @override
  int get hashCode => Object.hash(teamSlug, tripSlug);

  @override
  String toString() => 'TripKey($teamSlug/$tripSlug)';
}

/// Le détail d'un voyage, `stages[]` peuplées.
///
/// Point d'entrée **unique** : l'écran 24, l'écran 25 — qui n'a pas d'endpoint
/// dédié et n'en a pas besoin, son rail d'étapes veut de toute façon la liste
/// complète — et le contrôleur de participation le partagent. Passer d'une
/// étape à l'autre ne recharge donc rien.
///
/// `autoDispose`, contrairement au détail d'une sortie : ce partage n'a besoin
/// que des écrans ouverts. L'écran 25 se pose **sur** l'écran 24 (un lien
/// profond reconstruit la pile, `ancestorsForDeepLink`), qui garde donc le
/// détail vivant tant qu'on passe d'une étape à l'autre ; une fois le voyage
/// refermé, rien ne le retient. Gardé pour la vie de l'app, un voyage annulé
/// ou modifié par l'organisateur restait affiché tel quel jusqu'au prochain
/// lancement. Le tirer-pour-rafraîchir de l'écran 24 l'invalide aussi.
final tripDetailProvider = FutureProvider.autoDispose.family<TripDto, TripKey>(
  (Ref ref, TripKey key) =>
      ref.watch(tripRepositoryProvider).getTrip(key.teamSlug, key.tripSlug),
);

/// Les dérivés temporels d'un voyage.
///
/// Comme pour les sorties, « passé » est un dérivé **client** : le contrat ne
/// porte que `PUBLISHED` / `DRAFT` / `CANCELLED`. Un voyage se juge sur sa
/// **date de fin** quand il en a une — `endDate` est nouveau en 1.5.0 et c'est
/// précisément ce qu'il rend possible : une traversée de sept jours n'est pas
/// « passée » le lendemain de son départ.
///
/// Fuseau de l'appareil, jamais d'UTC affiché (§1.0.3-11).
extension TripTiming on TripDto {
  DateTime? get startsAt => DateTime.tryParse(dateTime)?.toLocal();

  DateTime? get endsAt {
    final String? raw = endDate;
    return raw == null ? null : DateTime.tryParse(raw)?.toLocal();
  }

  bool get isPast {
    final DateTime? last = endsAt ?? startsAt;
    return last != null && last.isBefore(DateTime.now());
  }

  bool get isCancelled => status == 'CANCELLED';

  /// Les étapes dans l'ordre d'affichage.
  ///
  /// `stageIndex` est le rang **imprimable** livré en 1.5.0 ; `sortOrder` est un
  /// rang persistant qui peut avoir des trous. Le tri se fait donc sur le
  /// premier, et la couleur d'une étape s'indexe sur `stageIndex - 1`, pas sur
  /// sa position dans la liste reçue.
  List<TripStageDto> get orderedStages {
    final List<TripStageDto> ordered = List<TripStageDto>.of(stages)
      ..sort((TripStageDto a, TripStageDto b) {
        final int byIndex = a.stageIndex.compareTo(b.stageIndex);
        return byIndex != 0 ? byIndex : a.sortOrder.compareTo(b.sortOrder);
      });
    return ordered;
  }
}

extension TripStageTiming on TripStageDto {
  DateTime? get startsAt => DateTime.tryParse(dateTime)?.toLocal();

  /// Le rang à partir de zéro — l'index de palette de l'étape.
  int get paletteIndex => stageIndex - 1;
}
