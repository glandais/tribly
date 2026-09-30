// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'verify_response.freezed.dart';
part 'verify_response.g.dart';

/// User code verification response
@Freezed()
abstract class VerifyResponse with _$VerifyResponse {
  const factory VerifyResponse({
    /// User code
    required String userCode,

    /// Which kind of device asks (e.g. 'karoo', 'garmin'), to name it on the confirmation screen
    required String clientId,

    /// When the device asked for the code: a code the user did not request themselves a moment ago stands out
    required String requestedAt,

    /// Whether authorization is already completed
    bool? authorized,
  }) = _VerifyResponse;

  factory VerifyResponse.fromJson(Map<String, Object?> json) =>
      _$VerifyResponseFromJson(json);
}
