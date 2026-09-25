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
    /// Whether the deletion is refused (SOLE_TEAM_ADMIN): the user is the only admin of at least one team that has other members
    required bool blocked,

    /// Teams the user is the only admin of while other members remain; they must name another admin, or delete the team, before deleting their account
    required List<TeamPublicationDto> blockingTeams,

    /// Teams the user administers and is the only member of; they are deleted with the account
    required List<TeamPublicationDto> deletedTeams,
  }) = _AccountDeletionImpactDto;

  factory AccountDeletionImpactDto.fromJson(Map<String, Object?> json) =>
      _$AccountDeletionImpactDtoFromJson(json);
}
