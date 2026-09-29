import 'package:flutter/widgets.dart';

import '../../api/generated/export.dart';

class _FeedKey extends ValueKey<String> {
  const _FeedKey(String value) : super('feed_$value');
}

/// Le fil de publications, sur l'accueil comme dans une équipe.
class FeedKeys {
  /// La carte d'une publication du fil, par son slug.
  ValueKey<String> card(String slug) => _FeedKey('card_$slug');

  /// L'état vide d'une recherche qui ne trouve rien.
  final filteredEmptyState = const _FeedKey('filteredEmptyState');

  final searchField = const _FeedKey('searchField');

  /// La puce d'un type de publication ; `null` pour « Tout ».
  ValueKey<String> typeChip(PublicationType? type) =>
      _FeedKey('typeChip_${type?.json ?? 'all'}');
}
