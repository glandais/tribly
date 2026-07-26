import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../data/team_repository.dart';

/// The one source of a team's detail, and therefore the one source of its
/// loading and error state.
///
/// It used to live in `team_detail_page.dart`, a page routed nowhere, while the
/// router watched it through a wrapper of its own — which is how a team section
/// ended up with two owners of the same error. The provider now belongs to the
/// feature, and a single page (`TeamHomePage`) watches it.
final teamDetailProvider = FutureProvider.family<TeamDetailDto, String>((
  ref,
  slug,
) async {
  final repository = ref.watch(teamRepositoryProvider);
  return repository.getTeam(slug);
});

/// Les équipes dont l'utilisateur est membre.
///
/// Vivait dans `teams_page.dart`. Elle en sort parce qu'elle n'est plus la
/// donnée d'un écran mais un **fait transverse** : l'écran 12 s'en sert pour
/// savoir, avant tout appel, s'il doit proposer de rejoindre un groupe ou
/// afficher le bandeau « Rejoignez cette équipe ».
final myTeamsProvider = FutureProvider<List<TeamDetailDto>>((ref) async {
  final repository = ref.watch(teamRepositoryProvider);
  return repository.getMyTeams();
});

/// L'utilisateur est-il membre de [teamSlug] ?
///
/// `null` tant qu'on ne sait pas — la liste n'est pas chargée, ou l'appel a
/// échoué. Le nul est **significatif** : il ne faut ni promettre l'inscription,
/// ni afficher un bandeau « rejoignez cette équipe » à quelqu'un qui en est
/// membre. En cas de doute, l'écran laisse l'API trancher au premier appel et
/// traite un 403 `FORBIDDEN` comme la réponse définitive.
final teamMembershipProvider = Provider.family<bool?, String>((
  Ref ref,
  String teamSlug,
) {
  return ref
      .watch(myTeamsProvider)
      .whenOrNull(
        data: (List<TeamDetailDto> teams) =>
            teams.any((TeamDetailDto t) => t.slug == teamSlug),
      );
});
