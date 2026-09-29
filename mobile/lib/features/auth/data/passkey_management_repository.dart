import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

/// Les clés d'accès du membre connecté : les lister, en ajouter, en retirer.
///
/// Passe par le client **authentifié** : son intercepteur pose le `Bearer` et
/// rafraîchit le jeton d'accès sur un 401. Ces appels vivaient dans
/// [AuthRepository], sur le client de base, avec un jeton passé à la main —
/// qui, expiré, faisait échouer la liste des clés ou leur ajout en 401 au lieu
/// d'être rafraîchi.
///
/// À lire au moment de l'appel (`ref.read`) et pas à surveiller depuis l'arbre
/// d'authentification : [apiClientProvider] est invalidé à chaque changement
/// d'identité.
final passkeyManagementRepositoryProvider =
    Provider<PasskeyManagementRepository>((ref) {
      return PasskeyManagementRepository(
        ref.watch(dioProvider),
        ref.watch(apiClientProvider).passkeys,
      );
    });

class PasskeyManagementRepository {
  PasskeyManagementRepository(this._dio, this._client);

  final Dio _dio;
  final PasskeysClient _client;

  Future<List<PasskeyDto>> listPasskeys() => _client.listPasskeys();

  Future<void> deletePasskey(String id) => _client.deletePasskey(id: id);

  /// Les options WebAuthn d'enregistrement.
  ///
  /// Par Dio directement : le client généré rend `void` pour cet endpoint.
  Future<Map<String, dynamic>> getRegistrationOptions() async {
    final Response<dynamic> response = await _dio.get(
      '/api/auth/passkeys/registration-options',
    );
    return response.data as Map<String, dynamic>;
  }

  Future<PasskeyDto> registerPasskey(
    Map<String, dynamic> credential, {
    String? deviceName,
  }) => _client.registerPasskey(body: credential, deviceName: deviceName);
}
