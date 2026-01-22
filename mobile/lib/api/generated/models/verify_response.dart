// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'verify_response.freezed.dart';
part 'verify_response.g.dart';

/// User code verification response
@Freezed()
abstract class VerifyResponse with _$VerifyResponse {
  const factory VerifyResponse({
    /// User code
    required String userCode,

    /// Whether authorization is already completed
    bool? authorized,
  }) = _VerifyResponse;

  factory VerifyResponse.fromJson(Map<String, Object?> json) =>
      _$VerifyResponseFromJson(json);
}
