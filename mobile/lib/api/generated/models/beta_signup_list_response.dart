// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'beta_signup_dto.dart';

part 'beta_signup_list_response.freezed.dart';
part 'beta_signup_list_response.g.dart';

/// Paginated beta sign-up list response
@Freezed()
abstract class BetaSignupListResponse with _$BetaSignupListResponse {
  const factory BetaSignupListResponse({
    /// List of sign-ups
    required List<BetaSignupDto> signups,

    /// Total number of sign-ups
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _BetaSignupListResponse;

  factory BetaSignupListResponse.fromJson(Map<String, Object?> json) =>
      _$BetaSignupListResponseFromJson(json);
}
