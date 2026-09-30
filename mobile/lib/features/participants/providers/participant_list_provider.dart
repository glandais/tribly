import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../../core/pagination/pagination.dart';
import '../data/participant_repository.dart';
import '../domain/participant_query.dart';

/// La liste paginée des participants pour une source et une recherche.
///
/// Auto-disposée : fermer la feuille la libère.
final participantListProvider = StateNotifierProvider.autoDispose
    .family<
      ParticipantListNotifier,
      PagedListState<PublicUserDto>,
      ParticipantQuery
    >((ref, query) {
      return ParticipantListNotifier(
        ref.watch(participantRepositoryProvider),
        query,
      );
    });

class ParticipantListNotifier extends PagedListNotifier<PublicUserDto> {
  ParticipantListNotifier(this._repository, this._query) : super(pageSize: 50);

  final ParticipantRepository _repository;
  final ParticipantQuery _query;

  @override
  Future<PageResult<PublicUserDto>> fetchPage(int page) =>
      _repository.fetchPage(_query, page: page, size: pageSize);

  @override
  Object? itemKey(PublicUserDto item) => item.id;
}
