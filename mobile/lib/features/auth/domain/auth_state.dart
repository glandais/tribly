import 'package:freezed_annotation/freezed_annotation.dart';

import '../../../api/generated/export.dart';

part 'auth_state.freezed.dart';

/// Authentication state
/// Mirrors frontend/src/store/authStore.ts
@Freezed()
abstract class AuthState with _$AuthState {
  const AuthState._();

  const factory AuthState({
    /// Currently authenticated user
    UserDto? user,

    /// JWT access token (stored in memory only)
    String? accessToken,

    /// Whether the user has any registered passkeys
    @Default(false) bool hasPasskeys,

    /// Whether the auth state has been initialized
    @Default(false) bool isInitialized,

    /// Whether initialization is in progress
    @Default(false) bool isInitializing,

    /// Whether an auth operation is in progress
    @Default(false) bool isLoading,

    /// Last error message
    String? error,
  }) = _AuthState;

  /// Whether the user is authenticated
  bool get isAuthenticated => user != null && accessToken != null;
}
