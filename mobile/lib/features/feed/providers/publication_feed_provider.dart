import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';

/// Ce qui identifie un jeu de résultats du fil : une portée et ses filtres.
///
/// **Tous les filtres font partie de la clé**, et c'est la propriété qui
/// compte : changer un filtre construit un notifier neuf plutôt que de muter
/// l'actuel, ce qui interdit à deux jeux de résultats de se mélanger dans la
/// même liste.
typedef PublicationFeedKey = ({
  String? teamSlug,
  PublicationType? type,
  String? search,
  MinRole? minRole,
});

/// Filtre de type courant, par portée. Le fil d'accueil et un fil d'équipe
/// gardent chacun leur sélection.
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

/// Portée du fil d'accueil : toutes mes équipes, ou seulement celles où je
/// tiens au moins tel rôle.
///
/// `null` signifie « toutes ». Sans objet sur un fil d'équipe, où la portée est
/// l'écran lui-même.
final publicationFeedScopeProvider = StateProvider.autoDispose
    .family<MinRole?, String?>((ref, teamSlug) => null);

/// Le fil paginé d'une portée et de ses filtres.
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

/// Combien de publications répondent à ces filtres.
///
/// Un appel séparé, parce que l'en-tête doit annoncer le total **avant** que
/// tout soit chargé : « 1 248 publications » se dit dès la première page.
final publicationFeedCountProvider = FutureProvider.autoDispose
    .family<int, PublicationFeedKey>((Ref ref, PublicationFeedKey key) async {
      final PublicationsClient client = ref.watch(publicationsClientProvider);
      final String? search = _trimmed(key.search);
      final CountResponse response = key.teamSlug == null
          ? await client.countAllPublications(
              type: key.type,
              search: search,
              minRole: key.minRole,
            )
          : await client.countPublications(
              teamSlug: key.teamSlug!,
              type: key.type,
              search: search,
            );
      return response.total;
    });

String? _trimmed(String? value) {
  final String? v = value?.trim();
  return (v == null || v.isEmpty) ? null : v;
}

class PublicationFeedNotifier extends PagedListNotifier<PublicationDto> {
  PublicationFeedNotifier(this._client, this._key);

  final PublicationsClient _client;
  final PublicationFeedKey _key;

  @override
  Future<PageResult<PublicationDto>> fetchPage(int page) async {
    final String? teamSlug = _key.teamSlug;
    final PublicationListResponse response = teamSlug == null
        ? await _client.listAllPublications(
            page: page,
            size: pageSize,
            type: _key.type,
            search: _trimmed(_key.search),
            minRole: _key.minRole,
            // **Vue compacte.** Sans elle, chaque page embarque vingt corps
            // markdown complets pour rendre deux lignes d'extrait. `excerpt`
            // et `thumbnailUrl` sont présents dans les deux vues ; ce sont eux
            // que la carte lit.
            view: ListViewMode.compact,
          )
        : await _client.listPublications(
            teamSlug: teamSlug,
            page: page,
            size: pageSize,
            type: _key.type,
            search: _trimmed(_key.search),
            view: ListViewMode.compact,
          );
    return PageResult<PublicationDto>(
      items: response.publications,
      total: response.total,
    );
  }

  @override
  Object? itemKey(PublicationDto item) => item.id;
}
