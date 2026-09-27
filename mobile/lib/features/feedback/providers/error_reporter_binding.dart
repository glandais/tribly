import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../core/logging/error_reporter.dart';
import '../../auth/domain/auth_state.dart';
import '../../auth/providers/auth_provider.dart';
import '../data/feedback_repository.dart';

/// Branche [ErrorReporter] sur la session : l'envoi s'ouvre quand le membre
/// est connecté — ce qui vide la file en attente — et se ferme à la
/// déconnexion.
///
/// Comme `pushAuthorizationProvider`, il n'existe que si quelqu'un le tient :
/// `main.dart` l'écoute dès le démarrage.
final Provider<void> errorReporterBindingProvider = Provider<void>((Ref ref) {
  final ErrorReporter reporter = ref.watch(errorReporterProvider);
  ref.listen<bool>(
    authProvider.select((AuthState s) => s.isInitialized && s.isAuthenticated),
    (bool? previous, bool authenticated) {
      if (authenticated) {
        // Le dépôt se relit à chaque envoi : le client authentifié est
        // reconstruit à chaque changement d'identité.
        unawaited(
          reporter.attach(
            (ErrorReportRequest request) =>
                ref.read(feedbackRepositoryProvider).reportError(request),
          ),
        );
      } else {
        reporter.detach();
      }
    },
    fireImmediately: true,
  );
});
