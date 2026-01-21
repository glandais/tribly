// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'garmin_token_response.freezed.dart';
part 'garmin_token_response.g.dart';

/// Garmin OAuth token response
@Freezed()
abstract class GarminTokenResponse with _$GarminTokenResponse {
  const factory GarminTokenResponse({
    /// Access token
    required String accessToken,

    /// Token type (always 'Bearer')
    required String tokenType,

    /// Token expiry in seconds
    required int expiresIn,

    /// Refresh token
    String? refreshToken,
  }) = _GarminTokenResponse;

  factory GarminTokenResponse.fromJson(Map<String, Object?> json) =>
      _$GarminTokenResponseFromJson(json);
}
