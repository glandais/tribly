import 'dart:async';

import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/routes/data/tile_token_repository.dart';

/// Un client de tuiles gréé à la main — le projet n'embarque ni mockito ni
/// mocktail, et l'interface n'a qu'une méthode.
class _FakeTilesClient implements TilesClient {
  _FakeTilesClient({this.expiresIn = 900, this.expiresAt});

  int calls = 0;
  int expiresIn;

  /// Volontairement réglable à une valeur aberrante : c'est ce qui prouve que
  /// le dépôt ne s'en sert pas.
  String? expiresAt;

  Completer<void>? gate;

  @override
  Future<TileTokenDto> mint() async {
    calls++;
    final int n = calls;
    if (gate != null) await gate!.future;
    return TileTokenDto(
      token: 'token-$n',
      expiresAt:
          expiresAt ??
          DateTime.now().add(Duration(seconds: expiresIn)).toIso8601String(),
      expiresIn: expiresIn,
    );
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

void main() {
  group('TileTokenRepository', () {
    test('le premier appel frappe un jeton', () async {
      final _FakeTilesClient client = _FakeTilesClient();
      final TileTokenRepository repo = TileTokenRepository(client);

      expect(await repo.current(), 'token-1');
      expect(client.calls, 1);
    });

    test('un second appel dans la fenêtre ne refrappe pas', () async {
      final _FakeTilesClient client = _FakeTilesClient();
      final TileTokenRepository repo = TileTokenRepository(client);

      await repo.current();
      expect(await repo.current(), 'token-1');
      expect(client.calls, 1);
    });

    test('passé 80 % de la durée de vie, il refrappe', () async {
      // expiresIn de 0 s ⇒ l'échéance de renouvellement est déjà passée.
      final _FakeTilesClient client = _FakeTilesClient(expiresIn: 0);
      final TileTokenRepository repo = TileTokenRepository(client);

      await repo.current();
      expect(await repo.current(), 'token-2');
      expect(client.calls, 2);
    });

    test('deux appels concurrents partagent un seul appel réseau', () async {
      final _FakeTilesClient client = _FakeTilesClient()
        ..gate = Completer<void>();
      final TileTokenRepository repo = TileTokenRepository(client);

      final Future<String> a = repo.current();
      final Future<String> b = repo.current();
      client.gate!.complete();

      expect(await a, 'token-1');
      expect(await b, 'token-1');
      expect(client.calls, 1);
    });

    test('clear() force une nouvelle frappe', () async {
      final _FakeTilesClient client = _FakeTilesClient();
      final TileTokenRepository repo = TileTokenRepository(client);

      await repo.current();
      repo.clear();

      expect(await repo.current(), 'token-2');
      expect(client.calls, 2);
    });

    test('needsRenewal est vrai avant la première frappe', () {
      expect(TileTokenRepository(_FakeTilesClient()).needsRenewal, isTrue);
    });

    /// L'horloge d'un téléphone peut être fausse de plusieurs minutes sans que
    /// personne ne s'en aperçoive. Le dépôt programme son renouvellement sur
    /// `expiresIn`, jamais sur `expiresAt` : un `expiresAt` aberrant ne doit
    /// donc rien changer.
    test('l\'échéance se calcule sur expiresIn, pas sur expiresAt', () async {
      final _FakeTilesClient client = _FakeTilesClient(
        expiresIn: 900,
        expiresAt: '1970-01-01T00:00:00Z',
      );
      final TileTokenRepository repo = TileTokenRepository(client);

      await repo.current();

      expect(repo.needsRenewal, isFalse);
      expect(await repo.current(), 'token-1');
      expect(client.calls, 1);
    });
  });
}
