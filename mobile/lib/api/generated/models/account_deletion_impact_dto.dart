// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_publication_dto.dart';

part 'account_deletion_impact_dto.freezed.dart';
part 'account_deletion_impact_dto.g.dart';

/// What deleting the current user's account would do to their teams
@Freezed()
abstract class AccountDeletionImpactDto with _$AccountDeletionImpactDto {
  const factory AccountDeletionImpactDto({
    /// Whether the deletion is refused: the user is the only admin of at least one team that has other members (SOLE_TEAM_ADMIN), or of a team migrated from biketeam (SOLE_MIGRATED_TEAM_ADMIN)
    required bool blocked,

    /// Teams the user is the only admin of while other members remain; they must name another admin, or delete the team, before deleting their account. Excludes the teams listed in migratedTeams
    required List<TeamPublicationDto> blockingTeams,

    /// Teams the user administers and is the only member of; they are deleted with the account. Excludes the teams listed in migratedTeams
    required List<TeamPublicationDto> deletedTeams,

    /// Teams the user is the only admin of, with or without other members, that were migrated from biketeam: their old biketeam addresses redirect to them. Each one refuses the deletion until another admin is named (or, for a platform admin, the switch-over is cancelled on biketeam and the team deleted)
    required List<TeamPublicationDto> migratedTeams,
  }) = _AccountDeletionImpactDto;

  factory AccountDeletionImpactDto.fromJson(Map<String, Object?> json) =>
      _$AccountDeletionImpactDtoFromJson(json);
}
