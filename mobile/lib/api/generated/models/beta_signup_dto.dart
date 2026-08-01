// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'beta_signup_dto.freezed.dart';
part 'beta_signup_dto.g.dart';

/// A beta program sign-up, as seen by an admin
@Freezed()
abstract class BetaSignupDto with _$BetaSignupDto {
  const factory BetaSignupDto({
    /// Sign-up ID
    required String id,

    /// Email
    required String email,

    /// When the sign-up was submitted
    required String createdAt,
  }) = _BetaSignupDto;

  factory BetaSignupDto.fromJson(Map<String, Object?> json) =>
      _$BetaSignupDtoFromJson(json);
}
