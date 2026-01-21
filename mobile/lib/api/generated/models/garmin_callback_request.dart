// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'garmin_callback_request.freezed.dart';
part 'garmin_callback_request.g.dart';

/// Garmin OAuth callback from frontend
@Freezed()
abstract class GarminCallbackRequest with _$GarminCallbackRequest {
  const factory GarminCallbackRequest({
    /// User's access token from frontend auth
    required String accessToken,

    /// Original redirect URI from authorize request
    required String redirectUri,

    /// State parameter from authorize request
    String? state,
  }) = _GarminCallbackRequest;

  factory GarminCallbackRequest.fromJson(Map<String, Object?> json) =>
      _$GarminCallbackRequestFromJson(json);
}
