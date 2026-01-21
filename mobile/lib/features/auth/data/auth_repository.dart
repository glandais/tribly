import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/tribly_api_client.dart';

/// Provider for auth repository - uses baseDioProvider to avoid circular dependency
final authRepositoryProvider = Provider<AuthRepository>((ref) {
  return AuthRepository(
    ref.watch(baseDioProvider),
    ref.watch(authenticationClientProvider),
    ref.watch(passkeysClientProvider),
  );
});

/// Repository for authentication API calls
class AuthRepository {
  final Dio _dio;
  final AuthenticationClient _authClient;
  final PasskeysClient _passkeysClient;

  AuthRepository(this._dio, this._authClient, this._passkeysClient);

  /// Register a new user
  Future<MessageResponse> register(RegisterRequest request) {
    return _authClient.register(body: request);
  }

  /// Verify email with token
  Future<AuthResponse> verifyEmail(String token) {
    return _authClient.verifyEmail(body: VerifyTokenRequest(token: token));
  }

  /// Request magic link
  Future<MessageResponse> requestMagicLink(String email) {
    return _authClient.requestMagicLink(body: MagicLinkRequest(email: email));
  }

  /// Verify magic link token
  Future<AuthResponse> verifyMagicLink(String token) {
    return _authClient.verifyMagicLink(body: VerifyTokenRequest(token: token));
  }

  /// Refresh access token
  /// Note: Uses Dio directly because the generated client doesn't support X-Refresh-Token header
  Future<AuthResponse> refreshToken(String refreshToken) async {
    final response = await _dio.post(
      '/api/auth/refresh',
      options: Options(
        headers: {'X-Refresh-Token': refreshToken},
      ),
    );
    return AuthResponse.fromJson(response.data as Map<String, Object?>);
  }

  /// Logout
  /// Note: Uses Dio directly for X-Refresh-Token header support
  Future<void> logout(String? refreshToken) async {
    await _dio.post(
      '/api/auth/logout',
      options: refreshToken != null
          ? Options(headers: {'X-Refresh-Token': refreshToken})
          : null,
    );
  }

  /// Logout from all devices
  Future<void> logoutAll() {
    return _authClient.logoutAll();
  }

  // Passkey endpoints

  /// Get passkey registration options (requires auth - use token)
  /// Note: Uses Dio directly because the generated client returns void
  Future<Map<String, dynamic>> getPasskeyRegistrationOptions(
      String accessToken) async {
    final response = await _dio.get(
      '/api/auth/passkeys/registration-options',
      options: Options(headers: {'Authorization': 'Bearer $accessToken'}),
    );
    return response.data as Map<String, dynamic>;
  }

  /// Register a passkey (requires auth - use token)
  /// Note: Uses Dio directly for custom auth header
  Future<PasskeyDto> registerPasskey(
    Map<String, dynamic> credential,
    String accessToken, {
    String? deviceName,
  }) async {
    final response = await _dio.post(
      '/api/auth/passkeys/register',
      data: credential,
      queryParameters: deviceName != null ? {'deviceName': deviceName} : null,
      options: Options(headers: {'Authorization': 'Bearer $accessToken'}),
    );
    return PasskeyDto.fromJson(response.data as Map<String, Object?>);
  }

  /// Get passkey authentication options
  /// Note: Uses Dio directly because the generated client returns void
  Future<Map<String, dynamic>> getPasskeyAuthenticationOptions({
    String? email,
  }) async {
    final response = await _dio.post(
      '/api/auth/passkeys/authentication-options',
      data: email != null ? {'email': email} : {},
    );
    return response.data as Map<String, dynamic>;
  }

  /// Authenticate with passkey
  Future<AuthResponse> authenticateWithPasskey(
    Map<String, dynamic> credential,
  ) {
    return _passkeysClient.authenticate(body: credential);
  }

  /// List user's passkeys (requires auth - use token)
  /// Note: Uses Dio directly for custom auth header
  Future<List<PasskeyDto>> listPasskeys(String accessToken) async {
    final response = await _dio.get(
      '/api/auth/passkeys',
      options: Options(headers: {'Authorization': 'Bearer $accessToken'}),
    );
    return (response.data as List)
        .map((e) => PasskeyDto.fromJson(e as Map<String, Object?>))
        .toList();
  }

  /// Delete a passkey (requires auth - use token)
  /// Note: Uses Dio directly for custom auth header
  Future<void> deletePasskey(String id, String accessToken) async {
    await _dio.delete(
      '/api/auth/passkeys/$id',
      options: Options(headers: {'Authorization': 'Bearer $accessToken'}),
    );
  }

  /// Get current user (requires auth - use token)
  /// Note: Uses Dio directly for custom auth header
  Future<UserDto> getMe(String accessToken) async {
    final response = await _dio.get(
      '/api/users/me',
      options: Options(headers: {'Authorization': 'Bearer $accessToken'}),
    );
    return UserDto.fromJson(response.data as Map<String, Object?>);
  }
}
