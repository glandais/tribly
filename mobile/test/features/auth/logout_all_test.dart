import 'dart:convert';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/pedalons_api_client.dart';
import 'package:pedalons/features/auth/data/auth_repository.dart';
import 'package:pedalons/features/auth/data/secure_storage.dart';
import 'package:pedalons/features/auth/providers/auth_provider.dart';

/// MOB-32 — « Déconnecter tous les appareils » porte le jeton d'accès.
///
/// L'appel passait par le client de base, sans intercepteur d'authentification :
/// il partait sans `Authorization`, recevait un 401, et l'erreur avalée ne
/// déconnectait que ce téléphone. Les autres sessions restaient ouvertes.
void main() {
  late _Storage storage;
  late _Adapter adapter;
  late ProviderContainer container;

  setUp(() {
    storage = _Storage();
    adapter = _Adapter();
    container = ProviderContainer(
      overrides: [
        secureStorageProvider.overrideWithValue(storage),
        authRepositoryProvider.overrideWithValue(_FakeAuthRepository()),
      ],
    );
    addTearDown(container.dispose);
    final sub = container.listen(authProvider, (_, _) {});
    addTearDown(sub.close);
    container.read(dioProvider).httpClientAdapter = adapter;
    container.read(accessTokenHolderProvider.notifier).state = 'jeton-acces';
  });

  test(
    'logout-all part avec le Bearer, puis la session locale se ferme',
    () async {
      adapter.status = 204;

      await container.read(authProvider.notifier).logoutAll();

      final RequestOptions request = adapter.requests.single;
      expect(request.path, endsWith('/api/auth/logout-all'));
      expect(request.headers['Authorization'], 'Bearer jeton-acces');
      expect(storage.deleted, isTrue);
      expect(container.read(accessTokenHolderProvider), isNull);
    },
  );

  test('un refus du serveur lève et garde la session ouverte', () async {
    adapter.status = 403;

    await expectLater(
      container.read(authProvider.notifier).logoutAll(),
      throwsA(isA<DioException>()),
    );

    expect(storage.deleted, isFalse);
    expect(container.read(accessTokenHolderProvider), 'jeton-acces');
  });
}

class _Adapter implements HttpClientAdapter {
  int status = 204;
  final List<RequestOptions> requests = <RequestOptions>[];

  @override
  Future<ResponseBody> fetch(
    RequestOptions options,
    Stream<Uint8List>? requestStream,
    Future<void>? cancelFuture,
  ) async {
    requests.add(options);
    return ResponseBody.fromString(
      status == 204 ? '' : jsonEncode(<String, String>{'code': 'FORBIDDEN'}),
      status,
      headers: <String, List<String>>{
        Headers.contentTypeHeader: <String>['application/json'],
      },
    );
  }

  @override
  void close({bool force = false}) {}
}

class _Storage extends Fake implements SecureTokenStorage {
  bool deleted = false;

  @override
  Future<String?> getRefreshToken() async => 'jeton-rafraichissement';

  @override
  Future<void> deleteRefreshToken() async => deleted = true;
}

class _FakeAuthRepository extends Fake implements AuthRepository {}
