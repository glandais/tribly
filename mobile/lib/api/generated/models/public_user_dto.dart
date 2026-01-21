// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'public_user_dto.freezed.dart';
part 'public_user_dto.g.dart';

/// Public user information (limited fields)
@Freezed()
abstract class PublicUserDto with _$PublicUserDto {
  const factory PublicUserDto({
    /// User ID (TSID)
    required String id,

    /// User display name
    required String displayName,

    /// User avatar URL
    String? avatarUrl,
  }) = _PublicUserDto;

  factory PublicUserDto.fromJson(Map<String, Object?> json) =>
      _$PublicUserDtoFromJson(json);
}
