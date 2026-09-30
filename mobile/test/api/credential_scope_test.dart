import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/credential_scope.dart';
import 'package:pedalons/api/pedalons_api_client.dart';
import 'package:pedalons/config/app_config.dart';
import 'package:pedalons/core/widgets/authenticated_image.dart';

/// SEC-3 (audit H4) — le jeton d'accès ne part que vers l'origine de l'API.
///
/// Une image de markdown `![x](https://ailleurs/p.png)` était chargée avec le
/// `Bearer` de chaque lecteur : l'auteur du message recevait les jetons de
/// tous ceux qui ouvraient le fil.
void main() {
  group('carriesCredentials', () {
    const String api = 'https://www.pedalons.fr';

    bool carries(String url) =>
        carriesCredentials(Uri.parse(url), apiBaseUrl: api);

    test("l'origine de l'API porte le jeton", () {
      expect(
        carries('https://www.pedalons.fr/api/download/x/400.webp'),
        isTrue,
      );
      expect(carries('https://WWW.Pedalons.FR/api/users/me'), isTrue);
      expect(carries('https://www.pedalons.fr:443/api/users/me'), isTrue);
    });

    test('tout le reste part sans lui', () {
      for (final String url in <String>[
        'https://evil.example/p.png',
        // Même hôte, mais en clair : le jeton circulerait lisible.
        'http://www.pedalons.fr/api/users/me',
        'https://www.pedalons.fr:8443/api/users/me',
        'https://pedalons.fr/api/users/me',
        'https://www.pedalons.fr.evil.example/p.png',
        'https://evil.example/https://www.pedalons.fr/p.png',
        '//evil.example/p.png',
        '/api/users/me',
      ]) {
        expect(carries(url), isFalse, reason: url);
      }
    });
  });

  group('authHeadersFor', () {
    test("une image de l'API reçoit le Bearer", () {
      expect(
        authHeadersFor(resolveImageUrl('/api/download/a/{size}.webp'), 'j'),
        <String, String>{'Authorization': 'Bearer j'},
      );
    });

    test("une image d'ailleurs ne le reçoit pas", () {
      expect(
        authHeadersFor(resolveImageUrl('https://evil.example/p.png'), 'j'),
        isNull,
      );
    });

    test('sans jeton, aucun en-tête', () {
      expect(authHeadersFor('${AppConfig.apiBaseUrl}/api/x', null), isNull);
    });
  });

  group('le client authentifié', () {
    late ProviderContainer container;
    late _Adapter adapter;

    setUp(() {
      container = ProviderContainer();
      adapter = _Adapter();
      container.read(dioProvider).httpClientAdapter = adapter;
      container.read(accessTokenHolderProvider.notifier).state = 'jeton-acces';
    });

    tearDown(() => container.dispose());

    test("pose le Bearer sur l'API", () async {
      await container.read(dioProvider).get<dynamic>('/api/users/me');

      expect(
        adapter.requests.single.headers['Authorization'],
        'Bearer jeton-acces',
      );
    });

    test('ne le pose pas sur une URL absolue vers un autre hôte', () async {
      await container
          .read(dioProvider)
          .get<dynamic>('https://evil.example/fichier.gpx');

      final RequestOptions request = adapter.requests.single;
      expect(request.uri.host, 'evil.example');
      expect(request.headers.containsKey('Authorization'), isFalse);
    });

    test(
      "un 401 venu d'ailleurs ne déclenche aucun rafraîchissement",
      () async {
        adapter.status = 401;

        await expectLater(
          container.read(dioProvider).get<dynamic>('https://evil.example/x'),
          throwsA(isA<DioException>()),
        );

        // Ni nouvel essai (il porterait le jeton neuf là-bas), ni session
        // effacée : le jeton d'accès est intact.
        expect(adapter.requests, hasLength(1));
        expect(container.read(accessTokenHolderProvider), 'jeton-acces');
      },
    );
  });
}

class _Adapter implements HttpClientAdapter {
  final List<RequestOptions> requests = <RequestOptions>[];
  int status = 200;

  @override
  Future<ResponseBody> fetch(
    RequestOptions options,
    Stream<Uint8List>? requestStream,
    Future<void>? cancelFuture,
  ) async {
    requests.add(options);
    return ResponseBody.fromString(
      '{}',
      status,
      headers: <String, List<String>>{
        Headers.contentTypeHeader: <String>['application/json'],
      },
    );
  }

  @override
  void close({bool force = false}) {}
}
