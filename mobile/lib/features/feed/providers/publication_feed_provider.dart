import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';

/// Identifies one publication feed: a scope (a team, or the whole home feed)
/// and the filters applied to it.
typedef PublicationFeedKey = ({
  String? teamSlug,
  PublicationType? type,
  String? search,
});

/// Type filter currently applied to a feed, keyed by scope.
///
/// The home feed and a team feed keep their own selection.
final publicationFeedTypeProvider = StateProvider.autoDispose
    .family<PublicationType?, String?>((ref, teamSlug) => null);

/// Recherche plein texte du fil, par portée.
///
/// Le fil était le seul écran de liste sans champ de recherche alors que
/// `GET /api/publications` accepte `search` depuis toujours (F-DE-4). C'est
/// aussi ce qui donne au fil un **vide filtré** distinct de son vide absolu,
/// et donc une sortie de secours (« Effacer la recherche », F-DE-9).
final publicationFeedSearchProvider = StateProvider.autoDispose
    .family<String?, String?>((ref, teamSlug) => null);

/// The paginated publication feed for a scope + type pair.
final publicationFeedProvider = StateNotifierProvider.autoDispose
    .family<
      PublicationFeedNotifier,
      PagedListState<PublicationDto>,
      PublicationFeedKey
    >((ref, key) {
      return PublicationFeedNotifier(
        ref.watch(publicationsClientProvider),
        key,
      );
    });

class PublicationFeedNotifier extends PagedListNotifier<PublicationDto> {
  final PublicationsClient _client;
  final PublicationFeedKey _key;

  PublicationFeedNotifier(this._client, this._key);

  @override
  Future<PageResult<PublicationDto>> fetchPage(int page) async {
    final teamSlug = _key.teamSlug;
    final response = teamSlug == null
        ? await _client.listAllPublications(
            page: page,
            size: pageSize,
            type: _key.type,
            search: _key.search,
          )
        : await _client.listPublications(
            teamSlug: teamSlug,
            page: page,
            size: pageSize,
            type: _key.type,
            search: _key.search,
          );
    return PageResult(items: response.publications, total: response.total);
  }

  @override
  Object? itemKey(PublicationDto item) => item.id;
}
