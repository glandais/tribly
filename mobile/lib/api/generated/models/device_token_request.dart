// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'device_token_request.freezed.dart';
part 'device_token_request.g.dart';

/// Device OAuth token request
@Freezed()
abstract class DeviceTokenRequest with _$DeviceTokenRequest {
  const factory DeviceTokenRequest({
    /// Grant type: 'urn:ietf:params:oauth:grant-type:device_code' or 'refresh_token'
    required String grantType,

    /// Device code (for device_code grant)
    String? deviceCode,

    /// Refresh token (for refresh_token grant)
    String? refreshToken,
  }) = _DeviceTokenRequest;

  factory DeviceTokenRequest.fromJson(Map<String, Object?> json) =>
      _$DeviceTokenRequestFromJson(json);
}
