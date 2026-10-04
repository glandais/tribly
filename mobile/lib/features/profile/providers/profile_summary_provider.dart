import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../data/profile_repository.dart';

/// L'état de chaque sujet du profil, pour les lignes d'état de la vue
/// d'ensemble : `GET /api/users/me/profile-summary`, un seul appel à coût SQL
/// fixe. Les préférences d'affichage, `contactableByMembers` et les services
/// connectés restent dans `UserDto` (l'utilisateur de `authProvider`) : le
/// résumé ne les répète pas.
///
/// `null` hors session. Pas d'`autoDispose` : le profil est un onglet, ses
/// lignes restent affichées. Ce qui change un compteur l'invalide — une
/// inscription (`notifyParticipationChanged`), un blocage
/// (`refreshAfterBlockChange`), et le retour d'une sous-page du profil.
final profileSummaryProvider = FutureProvider<ProfileSummaryDto?>((ref) async {
  final String? token = ref.watch(accessTokenHolderProvider);
  if (token == null) return null;
  return ref.read(profileRepositoryProvider).profileSummary();
});
