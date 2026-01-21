// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'verify_otp_request.freezed.dart';
part 'verify_otp_request.g.dart';

/// OTP verification request
@Freezed()
abstract class VerifyOtpRequest with _$VerifyOtpRequest {
  const factory VerifyOtpRequest({
    /// Email address
    required String email,

    /// 6-digit OTP code
    required String code,
  }) = _VerifyOtpRequest;

  factory VerifyOtpRequest.fromJson(Map<String, Object?> json) =>
      _$VerifyOtpRequestFromJson(json);
}
