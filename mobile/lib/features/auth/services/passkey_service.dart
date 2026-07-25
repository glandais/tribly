import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:passkeys/authenticator.dart';
import 'package:passkeys/types.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../config/app_config.dart';
import '../data/auth_repository.dart';
import '../providers/auth_provider.dart';

/// Provider for passkey service
final passkeyServiceProvider = Provider<PasskeyService>((ref) {
  return PasskeyService(ref.watch(authRepositoryProvider), ref);
});

/// Service for WebAuthn/Passkey operations
/// Mirrors frontend/src/lib/passkeys.ts
class PasskeyService {
  final AuthRepository _repository;
  final Ref _ref;
  final PasskeyAuthenticator _authenticator = PasskeyAuthenticator();

  PasskeyService(this._repository, this._ref);

  /// Check if passkeys are supported on this device
  Future<bool> isSupported() async {
    try {
      // Using deprecated but still functional method
      // ignore: deprecated_member_use
      return await _authenticator.canAuthenticate();
    } catch (_) {
      return false;
    }
  }

  /// Authenticate with a passkey
  Future<void> authenticate({String? email}) async {
    // Get authentication options from server
    final options = await _repository.getPasskeyAuthenticationOptions(
      email: email,
    );

    // Create authentication request
    final request = AuthenticateRequestType(
      relyingPartyId: options['rpId'] as String? ?? AppConfig.webAuthnRpId,
      challenge: options['challenge'] as String,
      mediation: MediationType.Optional,
      preferImmediatelyAvailableCredentials: true,
      timeout: options['timeout'] as int?,
      userVerification: options['userVerification'] as String?,
      allowCredentials: _parseAllowCredentials(options['allowCredentials']),
    );

    // Perform authentication with device
    final response = await _authenticator.authenticate(request);

    // Send assertion to server
    final credential = {
      'id': response.id,
      'rawId': response.rawId,
      'type': 'public-key',
      'response': {
        'clientDataJSON': response.clientDataJSON,
        'authenticatorData': response.authenticatorData,
        'signature': response.signature,
        'userHandle': response.userHandle,
      },
    };

    // Authenticate with backend
    final authNotifier = _ref.read(authProvider.notifier);
    await authNotifier.authenticateWithPasskey(credential);
  }

  /// Register a new passkey for the current user
  Future<PasskeyDto> register({String? deviceName}) async {
    // Get current access token
    final accessToken = _ref.read(accessTokenHolderProvider);
    if (accessToken == null) {
      throw Exception('User must be authenticated to register a passkey');
    }

    // Get registration options from server
    final options = await _repository.getPasskeyRegistrationOptions(
      accessToken,
    );

    final user = options['user'] as Map<String, dynamic>;
    final rp = options['rp'] as Map<String, dynamic>?;
    final pubKeyCredParams =
        options['pubKeyCredParams'] as List<dynamic>? ?? [];
    final authenticatorSelection =
        options['authenticatorSelection'] as Map<String, dynamic>?;

    // Create registration request
    final request = RegisterRequestType(
      relyingParty: RelyingPartyType(
        id: rp?['id'] as String? ?? AppConfig.webAuthnRpId,
        name: rp?['name'] as String? ?? 'Pédalons',
      ),
      user: UserType(
        id: user['id'] as String,
        name: user['name'] as String,
        displayName: user['displayName'] as String,
      ),
      challenge: options['challenge'] as String,
      timeout: options['timeout'] as int?,
      pubKeyCredParams: pubKeyCredParams.map((e) {
        final param = e as Map<String, dynamic>;
        return PubKeyCredParamType(
          type: param['type'] as String,
          alg: param['alg'] as int,
        );
      }).toList(),
      authSelectionType: authenticatorSelection != null
          ? AuthenticatorSelectionType(
              authenticatorAttachment:
                  authenticatorSelection['authenticatorAttachment']
                      as String? ??
                  'platform',
              residentKey:
                  authenticatorSelection['residentKey'] as String? ??
                  'required',
              requireResidentKey:
                  authenticatorSelection['requireResidentKey'] as bool? ?? true,
              userVerification:
                  authenticatorSelection['userVerification'] as String? ??
                  'preferred',
            )
          : AuthenticatorSelectionType(
              authenticatorAttachment: 'platform',
              residentKey: 'required',
              requireResidentKey: true,
              userVerification: 'preferred',
            ),
      attestation: options['attestation'] as String?,
      excludeCredentials:
          _parseExcludeCredentials(options['excludeCredentials']) ?? [],
    );

    // Perform registration with device
    final response = await _authenticator.register(request);

    // Send attestation to server
    final credential = {
      'id': response.id,
      'rawId': response.rawId,
      'type': 'public-key',
      'response': {
        'clientDataJSON': response.clientDataJSON,
        'attestationObject': response.attestationObject,
      },
      'authenticatorAttachment': 'platform',
    };

    // Register with backend
    final passkey = await _repository.registerPasskey(
      credential,
      accessToken,
      deviceName: deviceName,
    );

    // Update auth state
    final authNotifier = _ref.read(authProvider.notifier);
    authNotifier.setHasPasskeys(true);

    return passkey;
  }

  List<CredentialType>? _parseAllowCredentials(dynamic allowCredentials) {
    if (allowCredentials == null) return null;
    final list = allowCredentials as List<dynamic>;
    if (list.isEmpty) return null;
    return list.map((e) {
      final cred = e as Map<String, dynamic>;
      return CredentialType(
        type: cred['type'] as String? ?? 'public-key',
        id: cred['id'] as String,
        transports:
            (cred['transports'] as List<dynamic>?)
                ?.map((t) => t as String)
                .toList() ??
            [],
      );
    }).toList();
  }

  List<CredentialType>? _parseExcludeCredentials(dynamic excludeCredentials) {
    if (excludeCredentials == null) return null;
    final list = excludeCredentials as List<dynamic>;
    if (list.isEmpty) return null;
    return list.map((e) {
      final cred = e as Map<String, dynamic>;
      return CredentialType(
        type: cred['type'] as String? ?? 'public-key',
        id: cred['id'] as String,
        transports:
            (cred['transports'] as List<dynamic>?)
                ?.map((t) => t as String)
                .toList() ??
            [],
      );
    }).toList();
  }
}
