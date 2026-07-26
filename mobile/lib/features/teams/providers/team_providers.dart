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
