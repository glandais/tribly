import 'dart:convert';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/interceptors/auth_interceptor.dart';
import 'package:pedalons/api/pedalons_api_client.dart';
import 'package:pedalons/features/auth/data/passkey_management_repository.dart';

/// MOB-32 — les appels d'authentification qui exigent le jeton d'accès le
/// portent, et un jeton expiré y est rafraîchi.
///
/// Ils passaient un jeton à la main sur le client de base : expiré, il faisait
/// échouer la liste des clés, leur ajout ou leur retrait en 401. Et même sur
/// le client authentifié, l'intercepteur refusait de rafraîchir quoi que ce
/// soit sous `/api/auth/`.
void main() {
  group('refreshesOn401', () {
    test('les endpoints publics ne rafraîchissent pas', () {
      for (final String path in <String>[
        '/api/auth/login',
        '/api/auth/refresh',
        '/api/auth/otp/verify',
        '/api/auth/passkeys/authenticate',
        '/api/auth/passkeys/authentication-options',
      ]) {
        expect(refreshesOn401(path), isFalse, reason: path);
      }
    });

    test('ceux qui exigent le jeton rafraîchissent', () {
      for (final String path in <String>[
        '/api/auth/logout-all',
        '/api/auth/passkeys',
        '/api/auth/passkeys/abc123',
        '/api/auth/passkeys/registration-options',
        '/api/auth/passkeys/register',
        '/api/users/me',
      ]) {
        expect(refreshesOn401(path), isTrue, reason: path);
      }
    });
  });

  test('la liste des clés part avec le Bearer du porte-jeton', () async {
    final ProviderContainer container = ProviderContainer();
    addTearDown(container.dispose);
    final _Adapter adapter = _Adapter();
    container.read(dioProvider).httpClientAdapter = adapter;
    container.read(accessTokenHolderProvider.notifier).state = 'jeton-acces';

    final passkeys = await container
        .read(passkeyManagementRepositoryProvider)
        .listPasskeys();

    expect(passkeys, isEmpty);
    final RequestOptions request = adapter.requests.single;
    expect(request.path, endsWith('/api/auth/passkeys'));
    expect(request.headers['Authorization'], 'Bearer jeton-acces');
  });
}

class _Adapter implements HttpClientAdapter {
  final List<RequestOptions> requests = <RequestOptions>[];

  @override
  Future<ResponseBody> fetch(
    RequestOptions options,
    Stream<Uint8List>? requestStream,
    Future<void>? cancelFuture,
  ) async {
    requests.add(options);
    return ResponseBody.fromString(
      jsonEncode(<Object>[]),
      200,
      headers: <String, List<String>>{
        Headers.contentTypeHeader: <String>['application/json'],
      },
    );
  }

  @override
  void close({bool force = false}) {}
}
