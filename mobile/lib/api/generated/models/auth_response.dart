// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'user_dto.dart';

part 'auth_response.freezed.dart';
part 'auth_response.g.dart';

/// Authentication response
@Freezed()
abstract class AuthResponse with _$AuthResponse {
  const factory AuthResponse({
    /// JWT access token
    String? accessToken,

    /// Token expiry in seconds
    int? expiresIn,

    /// Authenticated user
    UserDto? user,

    /// Refresh token (for mobile clients)
    String? refreshToken,
  }) = _AuthResponse;

  factory AuthResponse.fromJson(Map<String, Object?> json) =>
      _$AuthResponseFromJson(json);
}
