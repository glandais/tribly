import 'package:flutter/widgets.dart';

class _TagKey extends ValueKey<String> {
  const _TagKey(String value) : super('tags_$value');
}

/// Le filtre par tag des listes d'équipe et sa feuille de choix.
class TagKeys {
  /// La puce « Tags » d'une barre de filtres.
  final filterChip = const _TagKey('filterChip');

  /// La ligne d'un tag dans la feuille de choix, par son id.
  ValueKey<String> pickerRow(String tagId) => _TagKey('pickerRow_$tagId');

  final pickerApply = const _TagKey('pickerApply');
  final pickerClear = const _TagKey('pickerClear');
}
