// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'beta_signup_request.freezed.dart';
part 'beta_signup_request.g.dart';

/// A request to be notified and added to a beta program once one opens
@Freezed()
abstract class BetaSignupRequest with _$BetaSignupRequest {
  const factory BetaSignupRequest({
    /// Email to contact once a beta is open
    required String email,
  }) = _BetaSignupRequest;

  factory BetaSignupRequest.fromJson(Map<String, Object?> json) =>
      _$BetaSignupRequestFromJson(json);
}
