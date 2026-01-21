// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'garmin_token_request.freezed.dart';
part 'garmin_token_request.g.dart';

/// Garmin OAuth token exchange request
@Freezed()
abstract class GarminTokenRequest with _$GarminTokenRequest {
  const factory GarminTokenRequest({
    /// Grant type (authorization_code or refresh_token)
    required String grantType,

    /// Authorization code (for authorization_code grant)
    String? code,

    /// Refresh token (for refresh_token grant)
    String? refreshToken,
  }) = _GarminTokenRequest;

  factory GarminTokenRequest.fromJson(Map<String, Object?> json) =>
      _$GarminTokenRequestFromJson(json);
}
