import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'user_preferences_provider.dart';

/// Clé `shared_preferences` du réglage « Envoyer automatiquement les rapports
/// d'erreur ». Lue aussi par `ErrorReporter`, qui vit hors de Riverpod.
const String kAutoErrorReportsKey = 'prefs.autoErrorReports';

/// Le réglage, mémorisé **sur l'appareil** et allumé par défaut.
///
/// Il n'existe pas au contrat, et n'a pas à y être : il décide de ce que *cet
/// appareil* envoie de lui-même, pas de ce que le compte accepte.
final NotifierProvider<AutoErrorReportsNotifier, bool>
autoErrorReportsProvider = NotifierProvider<AutoErrorReportsNotifier, bool>(
  AutoErrorReportsNotifier.new,
);

class AutoErrorReportsNotifier extends Notifier<bool> {
  @override
  bool build() =>
      ref.watch(sharedPreferencesProvider).getBool(kAutoErrorReportsKey) ??
      true;

  Future<void> set(bool enabled) async {
    state = enabled;
    await ref
        .read(sharedPreferencesProvider)
        .setBool(kAutoErrorReportsKey, enabled);
  }
}
