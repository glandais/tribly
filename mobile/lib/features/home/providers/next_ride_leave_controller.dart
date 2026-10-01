import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';
import 'package:flutter_riverpod/misc.dart' show KeepAliveLink;

import '../../../api/generated/export.dart';
import '../../../core/utils/api_error_handler.dart';
import '../../rides/data/ride_repository.dart';
import '../../rides/providers/participation_changes.dart';
import '../../rides/providers/ride_detail_provider.dart';
import '../../rides/providers/ride_registration_controller.dart';

/// L'état du bouton « Quitter » de « Ma prochaine sortie ».
@immutable
class NextRideLeaveState {
  const NextRideLeaveState({this.pendingGroupId, this.failure});

  /// Le groupe en cours de désinscription, s'il y en a un.
  final String? pendingGroupId;

  /// Le dernier échec, rendu en bandeau au-dessus de la carte.
  final RegistrationFailure? failure;
}

/// Quitter sa prochaine sortie **depuis l'accueil**, sans charger la sortie.
///
/// La carte se dessine depuis la ligne de liste et son `registeredGroup`
/// (`docs/LEDGER_*.md API-4`). Passer par `rideRegistrationProvider` la
/// remettrait sous le détail : ce contrôleur-là écoute `rideDetailProvider`
/// dès sa création, et c'est précisément le `getRide` que la ligne rend
/// inutile. Ne pas y revenir.
///
/// Une désinscription n'a besoin de rien d'autre que l'identifiant du groupe :
/// pas d'exclusivité à vérifier, pas de bascule optimiste (la carte disparaît
/// au succès, quand [notifyParticipationChanged] relit la participation).
final nextRideLeaveProvider = StateNotifierProvider.autoDispose
    .family<NextRideLeaveController, NextRideLeaveState, RideKey>(
      (Ref ref, RideKey key) => NextRideLeaveController(ref, key),
    );

class NextRideLeaveController extends StateNotifier<NextRideLeaveState> {
  NextRideLeaveController(this._ref, this._key)
    : super(const NextRideLeaveState());

  final Ref _ref;
  final RideKey _key;

  void dismissFailure() => state = const NextRideLeaveState();

  Future<void> leave(RideDto ride, RideGroupDto group) async {
    if (state.pendingGroupId != null) return;
    state = NextRideLeaveState(pendingGroupId: group.id);
    // La carte disparaît au succès : le contrôleur doit survivre à l'appel.
    final KeepAliveLink alive = _ref.keepAlive();
    try {
      await _ref
          .read(rideRepositoryProvider)
          .leaveGroup(_key.teamSlug, _key.rideSlug, group.id);
      state = const NextRideLeaveState();
      // Le détail, s'il a été ouvert, est périmé ; sinon l'invalidation ne
      // coûte rien.
      _ref.invalidate(rideDetailProvider(_key));
      notifyParticipationChanged(
        _ref,
        publicationId: ride.id,
        registered: false,
      );
    } catch (error, stackTrace) {
      final ApiError resolved = resolveApiError(error, stackTrace);
      state = NextRideLeaveState(
        failure: RegistrationFailure(
          reason: RegistrationFailureReason.generic,
          groupId: group.id,
          groupName: group.name,
          message: resolved.message,
        ),
      );
    } finally {
      alive.close();
    }
  }
}
