import 'package:flutter/widgets.dart';

class _LegalPageKey extends ValueKey<String> {
  const _LegalPageKey(String value) : super('legalPage_$value');
}

/// Les pages légales : conditions d'utilisation, confidentialité.
class LegalPageKeys {
  final backButton = const _LegalPageKey('backButton');

  /// Le texte de la page, une fois chargé.
  final content = const _LegalPageKey('content');
}
