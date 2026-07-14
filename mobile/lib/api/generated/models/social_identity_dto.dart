// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'social_provider.dart';

part 'social_identity_dto.freezed.dart';
part 'social_identity_dto.g.dart';

/// A linked external identity (e.g. Strava)
@Freezed()
abstract class SocialIdentityDto with _$SocialIdentityDto {
  const factory SocialIdentityDto({
    /// Provider identifier
    required String provider,

    /// Display name of the provider
    required String displayName,

    /// When the identity was linked
    required String linkedAt,
  }) = _SocialIdentityDto;

  factory SocialIdentityDto.fromJson(Map<String, Object?> json) =>
      _$SocialIdentityDtoFromJson(json);
}
