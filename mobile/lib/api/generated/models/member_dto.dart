// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'public_user_dto.dart';
import 'team_publication_dto.dart';
import 'team_role.dart';

part 'member_dto.freezed.dart';
part 'member_dto.g.dart';

/// Team member information
@Freezed()
abstract class MemberDto with _$MemberDto {
  const factory MemberDto({
    /// Team
    required TeamPublicationDto team,

    /// Membership ID (TSID)
    required String id,

    /// User
    required PublicUserDto user,

    /// Member role. Null when the caller is not entitled to it: an organiser reading the roster of a team that has not opened its member directory gets the names and nothing else.
    String? role,

    /// When the user joined the team
    String? joinedAt,
  }) = _MemberDto;

  factory MemberDto.fromJson(Map<String, Object?> json) =>
      _$MemberDtoFromJson(json);
}
