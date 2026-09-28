import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/auth/data/auth_repository.dart';
import 'package:pedalons/features/auth/domain/auth_state.dart';
import 'package:pedalons/features/auth/presentation/pages/verify_email_page.dart';
import 'package:pedalons/features/auth/providers/auth_provider.dart';
import 'package:pedalons/keys.dart';

/// Le lien de vérification ouvre une page qui appelle l'API d'elle-même.
///
/// `AuthNotifier.verifyEmail` pose `isLoading` **avant** son premier `await` :
/// lancé depuis `initState`, il modifiait un provider pendant que l'arbre se
/// construisait — l'assertion de Riverpod « Tried to modify a provider while
/// the widget tree was building » faisait échouer tout test qui ouvrait la
/// page, et le parcours d'inscription n'avait pas de test de bout en bout.
class _FailingAuthRepository implements AuthRepository {
  final List<String> tokens = <String>[];

  @override
  Future<AuthResponse> verifyEmail(String token) async {
    tokens.add(token);
    throw Exception('lien expiré');
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

/// L'app écoute l'état d'authentification au-dessus de chaque page (routeur,
/// shell) : sans écouteur, un changement d'état ne notifie personne et
/// l'assertion ne se déclenche pas.
class _AuthListener extends ConsumerWidget {
  const _AuthListener({required this.child});

  final Widget child;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    ref.watch(authProvider.select((AuthState s) => s.isLoading));
    return child;
  }
}

void main() {
  Future<void> open(
    WidgetTester tester,
    _FailingAuthRepository repository, {
    String? token,
  }) async {
    await tester.pumpWidget(
      ProviderScope(
        overrides: [authRepositoryProvider.overrideWithValue(repository)],
        child: MaterialApp(
          home: _AuthListener(child: VerifyEmailPage(token: token)),
        ),
      ),
    );
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  testWidgets(
    'la vérification part après la première image, sans modifier un provider '
    'pendant la construction',
    (WidgetTester tester) async {
      final _FailingAuthRepository repository = _FailingAuthRepository();

      await open(tester, repository, token: 't0k3n');

      expect(tester.takeException(), isNull);
      expect(repository.tokens, <String>['t0k3n']);
      expect(find.byKey(keys.login.verifyError), findsOneWidget);
      expect(find.byKey(keys.login.verifyBackToLoginButton), findsOneWidget);
    },
  );

  testWidgets('sans jeton, la page le dit sans appeler l\'API', (
    WidgetTester tester,
  ) async {
    final _FailingAuthRepository repository = _FailingAuthRepository();

    await open(tester, repository);

    expect(tester.takeException(), isNull);
    expect(repository.tokens, isEmpty);
    expect(find.byKey(keys.login.verifyError), findsOneWidget);
  });
}
