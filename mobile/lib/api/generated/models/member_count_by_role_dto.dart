// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'member_count_by_role_dto.freezed.dart';
part 'member_count_by_role_dto.g.dart';

/// Number of members of the team per role. The three figures add up to the team's memberCount.
@Freezed()
abstract class MemberCountByRoleDto with _$MemberCountByRoleDto {
  const factory MemberCountByRoleDto({
    /// Members with the ADMIN role
    required int admins,

    /// Members with the ORGANIZER role
    required int organizers,

    /// Members with the MEMBER role
    required int members,
  }) = _MemberCountByRoleDto;

  factory MemberCountByRoleDto.fromJson(Map<String, Object?> json) =>
      _$MemberCountByRoleDtoFromJson(json);
}
