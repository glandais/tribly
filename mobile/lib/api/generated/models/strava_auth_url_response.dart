// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'strava_auth_url_response.freezed.dart';
part 'strava_auth_url_response.g.dart';

/// Strava OAuth authorization URL response
@Freezed()
abstract class StravaAuthUrlResponse with _$StravaAuthUrlResponse {
  const factory StravaAuthUrlResponse({
    /// URL to redirect the user to for Strava authorization
    required String authorizationUrl,
  }) = _StravaAuthUrlResponse;

  factory StravaAuthUrlResponse.fromJson(Map<String, Object?> json) =>
      _$StravaAuthUrlResponseFromJson(json);
}
