// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'visibility.dart';

part 'admin_team_dto.freezed.dart';
part 'admin_team_dto.g.dart';

/// Admin team view with domain info
@Freezed()
abstract class AdminTeamDto with _$AdminTeamDto {
  const factory AdminTeamDto({
    /// Team ID (TSID)
    required String id,

    /// Team name
    required String name,

    /// Team URL slug
    required String slug,

    /// Domain ID this team belongs to
    required String domainId,

    /// Domain hostname
    required String domainName,

    /// Team visibility
    required String visibility,

    /// Is team soft-deleted
    required bool deleted,

    /// Number of members
    required int memberCount,

    /// Team creation timestamp
    required String createdAt,
  }) = _AdminTeamDto;

  factory AdminTeamDto.fromJson(Map<String, Object?> json) =>
      _$AdminTeamDtoFromJson(json);
}
