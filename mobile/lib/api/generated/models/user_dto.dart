// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'gps_service_connection_dto.dart';
import 'instant.dart';
import 'platform_role.dart';
import 'unit_system.dart';

part 'user_dto.freezed.dart';
part 'user_dto.g.dart';

/// User profile data
@Freezed()
abstract class UserDto with _$UserDto {
  const factory UserDto({
    /// User ID (TSID)
    required String id,

    /// User email address
    required String email,

    /// User display name
    required String displayName,

    /// User avatar URL
    String? avatarUrl,

    /// Account creation timestamp
    String? createdAt,

    /// Preferred unit system (metric or imperial)
    String? unitSystem,

    /// Platform role (null if regular user)
    String? platformRole,

    /// Connected GPS services
    List<GpsServiceConnectionDto>? connectedServices,
  }) = _UserDto;

  factory UserDto.fromJson(Map<String, Object?> json) =>
      _$UserDtoFromJson(json);
}
