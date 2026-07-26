import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../../core/pagination/pagination.dart';
import '../data/team_repository.dart';
import '../domain/member_filters.dart';

/// Filtres courants du trombinoscope d'une équipe.
final memberFiltersProvider = StateProvider.autoDispose
    .family<MemberFilters, String>(
      (ref, teamSlug) => MemberFilters(teamSlug: teamSlug),
    );

/// Le trombinoscope paginé pour un jeu de filtres.
final teamMembersProvider = StateNotifierProvider.autoDispose
    .family<TeamMembersNotifier, PagedListState<MemberDto>, MemberFilters>((
      ref,
      filters,
    ) {
      return TeamMembersNotifier(ref.watch(teamRepositoryProvider), filters);
    });

class TeamMembersNotifier extends PagedListNotifier<MemberDto> {
  final TeamRepository _repository;
  final MemberFilters _filters;

  TeamMembersNotifier(this._repository, this._filters);

  @override
  Future<PageResult<MemberDto>> fetchPage(int page) {
    return _repository.fetchMembers(
      filters: _filters,
      page: page,
      size: pageSize,
    );
  }

  @override
  Object? itemKey(MemberDto item) => item.id;
}
