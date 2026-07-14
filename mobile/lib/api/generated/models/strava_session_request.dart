// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'strava_session_request.freezed.dart';
part 'strava_session_request.g.dart';

/// Exchange a one-time Strava login code for a session
@Freezed()
abstract class StravaSessionRequest with _$StravaSessionRequest {
  const factory StravaSessionRequest({
    /// One-time login code from the Strava callback
    required String code,
  }) = _StravaSessionRequest;

  factory StravaSessionRequest.fromJson(Map<String, Object?> json) =>
      _$StravaSessionRequestFromJson(json);
}
