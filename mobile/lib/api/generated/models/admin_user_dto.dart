// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'platform_role.dart';

part 'admin_user_dto.freezed.dart';
part 'admin_user_dto.g.dart';

/// Admin user view with domain info
@Freezed()
abstract class AdminUserDto with _$AdminUserDto {
  const factory AdminUserDto({
    /// User ID (TSID)
    required String id,

    /// User email address
    required String email,

    /// User display name
    required String displayName,

    /// Domain ID this user belongs to
    required String domainId,

    /// Domain hostname
    required String domainName,

    /// Whether email is verified
    required bool emailVerified,

    /// User creation timestamp
    required String createdAt,

    /// Platform role (null if regular user)
    String? platformRole,

    /// Last login timestamp
    String? lastLoginAt,
  }) = _AdminUserDto;

  factory AdminUserDto.fromJson(Map<String, Object?> json) =>
      _$AdminUserDtoFromJson(json);
}
