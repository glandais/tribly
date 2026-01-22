// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'device_token_response.freezed.dart';
part 'device_token_response.g.dart';

/// Device OAuth token response
@Freezed()
abstract class DeviceTokenResponse with _$DeviceTokenResponse {
  const factory DeviceTokenResponse({
    /// Access token
    required String accessToken,

    /// Token type (always 'Bearer')
    required String tokenType,

    /// Token expiry in seconds
    required int expiresIn,

    /// Refresh token
    String? refreshToken,
  }) = _DeviceTokenResponse;

  factory DeviceTokenResponse.fromJson(Map<String, Object?> json) =>
      _$DeviceTokenResponseFromJson(json);
}
