import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

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

  /// Request OTP
  Future<MessageResponse> requestOtp(String email) {
    return _authClient.requestOtp(body: OtpRequest(email: email));
  }

  /// Verify OTP code
  Future<AuthResponse> verifyOtp(String email, String code) {
    return _authClient.verifyOtp(
      body: VerifyOtpRequest(email: email, code: code),
    );
  }

  /// Login with email and password
  Future<AuthResponse> loginWithPassword(String email, String password) {
    return _authClient.loginWithPassword(
      body: LoginRequest(email: email, password: password),
    );
  }

  /// Request password reset OTP
  Future<MessageResponse> forgotPassword(String email) {
    return _authClient.forgotPassword(
      body: ForgotPasswordRequest(email: email),
    );
  }

  /// Reset password with token from email link
  Future<AuthResponse> resetPassword(String token, String newPassword) {
    return _authClient.resetPassword(
      body: ResetPasswordRequest(token: token, newPassword: newPassword),
    );
  }

  /// Refresh access token
  /// Note: Uses Dio directly because the generated client doesn't support X-Refresh-Token header
  Future<AuthResponse> refreshToken(String refreshToken) async {
    final response = await _dio.post(
      '/api/auth/refresh',
      options: Options(headers: {'X-Refresh-Token': refreshToken}),
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

  // `logout-all` n'est pas ici : il exige le jeton d'accès, et ce dépôt parle
  // au client de base. Voir `AuthNotifier.logoutAll`.

  // Passkey endpoints

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

  // Les appels qui exigent le jeton d'accès — lister, ajouter, retirer une clé,
  // relire `/api/users/me` — ne sont pas ici : ce dépôt parle au client de
  // base, sans intercepteur, et un jeton passé à la main n'y est jamais
  // rafraîchi. Voir `PasskeyManagementRepository` et `usersClientProvider`.
}
