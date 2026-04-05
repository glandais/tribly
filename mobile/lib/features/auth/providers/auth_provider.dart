import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/utils/api_error_handler.dart';
import '../data/auth_repository.dart';
import '../data/secure_storage.dart';
import '../domain/auth_state.dart';

/// Provider for auth state
final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier(
    ref.watch(authRepositoryProvider),
    ref.watch(secureStorageProvider),
    ref,
  );
});

/// Auth state notifier
/// Mirrors frontend/src/store/authStore.ts
class AuthNotifier extends StateNotifier<AuthState> {
  final AuthRepository _repository;
  final SecureTokenStorage _storage;
  final Ref _ref;

  AuthNotifier(this._repository, this._storage, this._ref)
      : super(const AuthState());

  /// Get the current access token
  String? get accessToken => state.accessToken;

  /// Sync token to the holder for interceptor use
  void _syncTokenToHolder(String? token) {
    _ref.read(accessTokenHolderProvider.notifier).state = token;
  }

  /// Initialize auth state by attempting to refresh from stored token
  Future<void> initialize() async {
    if (state.isInitialized || state.isInitializing) return;

    state = state.copyWith(isInitializing: true, error: null);

    try {
      final refreshToken = await _storage.getRefreshToken();
      if (refreshToken != null) {
        final response = await _repository.refreshToken(refreshToken);
        await _handleAuthSuccess(response);
      }
    } catch (e) {
      // Only clear stored token if the server explicitly rejected it (401/403).
      // On network errors, keep the token so we can retry next launch.
      final isAuthError = e is DioException &&
          e.response != null &&
          (e.response!.statusCode == 401 || e.response!.statusCode == 403);
      if (isAuthError) {
        await _storage.deleteRefreshToken();
      }
      _syncTokenToHolder(null);
    } finally {
      state = state.copyWith(
        isInitialized: true,
        isInitializing: false,
      );
    }
  }

  /// Handle successful authentication
  Future<void> _handleAuthSuccess(AuthResponse response) async {
    // Save refresh token if provided
    if (response.refreshToken != null) {
      await _storage.saveRefreshToken(response.refreshToken!);
    }

    // Sync token to holder for interceptor
    _syncTokenToHolder(response.accessToken);

    // Fetch full user profile (includes connectedServices, etc.)
    UserDto? user = response.user;
    if (response.accessToken != null) {
      try {
        user = await _ref.read(usersClientProvider).getMe();
      } on DioException catch (e) {
        debugPrint('[AuthNotifier] getMe failed after auth: ${e.message}');
        // Intentional fallback to JWT user on network/server error
      } catch (e) {
        debugPrint('[AuthNotifier] Unexpected error in getMe: $e');
      }
    }

    // Check if user has passkeys
    bool hasPasskeys = false;
    if (response.accessToken != null) {
      try {
        final passkeys = await _repository.listPasskeys(response.accessToken!);
        hasPasskeys = passkeys.isNotEmpty;
      } on DioException catch (e) {
        debugPrint('[AuthNotifier] listPasskeys failed after auth: ${e.message}');
        // Non-fatal: user can still log in without passkey info
      } catch (e) {
        debugPrint('[AuthNotifier] Unexpected error in listPasskeys: $e');
      }
    }

    state = state.copyWith(
      user: user,
      accessToken: response.accessToken,
      hasPasskeys: hasPasskeys,
      isLoading: false,
      error: null,
    );
  }

  /// Register a new user
  Future<MessageResponse> register({
    required String email,
    required String displayName,
    required String password,
  }) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.register(
        RegisterRequest(email: email, displayName: displayName, password: password),
      );
      state = state.copyWith(isLoading: false);
      return response;
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Login with email and password
  Future<void> loginWithPassword(String email, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.loginWithPassword(email, password);
      await _handleAuthSuccess(response);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Request password reset OTP
  Future<MessageResponse> requestPasswordReset(String email) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.forgotPassword(email);
      state = state.copyWith(isLoading: false);
      return response;
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Reset password with OTP code
  Future<void> resetPassword(
    String email,
    String code,
    String newPassword,
  ) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.resetPassword(email, code, newPassword);
      await _handleAuthSuccess(response);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Verify email with token
  Future<void> verifyEmail(String token) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.verifyEmail(token);
      await _handleAuthSuccess(response);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Request OTP code
  Future<MessageResponse> requestOtp(String email) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.requestOtp(email);
      state = state.copyWith(isLoading: false);
      return response;
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Verify OTP code
  Future<void> verifyOtp(String email, String code) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.verifyOtp(email, code);
      await _handleAuthSuccess(response);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Authenticate with passkey
  Future<void> authenticateWithPasskey(Map<String, dynamic> credential) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.authenticateWithPasskey(credential);
      await _handleAuthSuccess(response);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: getErrorMessage(e));
      rethrow;
    }
  }

  /// Set access token (called by auth interceptor on refresh)
  void setAccessToken(String token) {
    state = state.copyWith(accessToken: token);
    _syncTokenToHolder(token);
  }

  /// Set user (called by auth interceptor on refresh)
  void setUser(UserDto user) {
    state = state.copyWith(user: user);
  }

  /// Update hasPasskeys flag
  void setHasPasskeys(bool value) {
    state = state.copyWith(hasPasskeys: value);
  }

  /// Logout
  Future<void> logout() async {
    try {
      final refreshToken = await _storage.getRefreshToken();
      await _repository.logout(refreshToken);
    } catch (_) {
      // Ignore logout errors
    } finally {
      await _storage.deleteRefreshToken();
      _syncTokenToHolder(null);
      state = const AuthState(isInitialized: true);
    }
  }

  /// Logout from all devices
  Future<void> logoutAll() async {
    try {
      await _repository.logoutAll();
    } catch (_) {
      // Ignore logout errors
    } finally {
      await _storage.deleteRefreshToken();
      _syncTokenToHolder(null);
      state = const AuthState(isInitialized: true);
    }
  }

  /// Clear error
  void clearError() {
    state = state.copyWith(error: null);
  }
}
