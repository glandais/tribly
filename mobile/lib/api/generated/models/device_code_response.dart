// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'device_code_response.freezed.dart';
part 'device_code_response.g.dart';

/// Device code response (RFC 8628)
@Freezed()
abstract class DeviceCodeResponse with _$DeviceCodeResponse {
  const factory DeviceCodeResponse({
    /// Device code for polling
    required String deviceCode,

    /// User code to display (e.g., 'ABCD12')
    required String userCode,

    /// Verification URL for user to visit
    required String verificationUri,

    /// Verification URL with user code embedded
    required String verificationUriComplete,

    /// Code expiry in seconds
    required int expiresIn,

    /// Minimum polling interval in seconds
    required int interval,
  }) = _DeviceCodeResponse;

  factory DeviceCodeResponse.fromJson(Map<String, Object?> json) =>
      _$DeviceCodeResponseFromJson(json);
}
