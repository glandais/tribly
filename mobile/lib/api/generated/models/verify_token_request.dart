// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'verify_token_request.freezed.dart';
part 'verify_token_request.g.dart';

/// Token verification request
@Freezed()
abstract class VerifyTokenRequest with _$VerifyTokenRequest {
  const factory VerifyTokenRequest({
    /// Verification token
    required String token,
  }) = _VerifyTokenRequest;

  factory VerifyTokenRequest.fromJson(Map<String, Object?> json) =>
      _$VerifyTokenRequestFromJson(json);
}
