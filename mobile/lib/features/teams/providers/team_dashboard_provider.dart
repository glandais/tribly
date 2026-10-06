import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../data/team_repository.dart';

/// Le tableau de bord d'une équipe, lu d'un seul appel.
///
/// `autoDispose` : il ne vaut que tant qu'on le regarde. Une inscription faite
/// depuis la fiche d'une sortie le recharge (`notifyParticipationChanged`
/// l'invalide) : « Vos prochaines sorties » ne se déduit pas côté client. Le
/// geste « tirer pour rafraîchir » relit le reste.
///
/// Le rôle qui découpe les sections est **celui du serveur**
/// (`TeamDashboardDto.role`) : l'écran n'en déduit aucun lui-même, il rend ce
/// qui arrive et masque ce qui vient `null`.
final teamDashboardProvider = FutureProvider.autoDispose
    .family<TeamDashboardDto, String>((Ref ref, String slug) {
      return ref.watch(teamRepositoryProvider).getDashboard(slug);
    });
