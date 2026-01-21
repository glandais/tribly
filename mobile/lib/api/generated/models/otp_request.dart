// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'otp_request.freezed.dart';
part 'otp_request.g.dart';

/// OTP request
@Freezed()
abstract class OtpRequest with _$OtpRequest {
  const factory OtpRequest({
    /// Email address
    required String email,
  }) = _OtpRequest;

  factory OtpRequest.fromJson(Map<String, Object?> json) =>
      _$OtpRequestFromJson(json);
}
