// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'gps_o_auth_url_response.freezed.dart';
part 'gps_o_auth_url_response.g.dart';

/// OAuth authorization URL response
@Freezed()
abstract class GpsOAuthUrlResponse with _$GpsOAuthUrlResponse {
  const factory GpsOAuthUrlResponse({
    /// URL to redirect user for OAuth authorization
    required String authorizationUrl,
  }) = _GpsOAuthUrlResponse;

  factory GpsOAuthUrlResponse.fromJson(Map<String, Object?> json) =>
      _$GpsOAuthUrlResponseFromJson(json);
}
