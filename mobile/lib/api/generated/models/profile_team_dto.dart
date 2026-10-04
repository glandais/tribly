// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_role.dart';

part 'profile_team_dto.freezed.dart';
part 'profile_team_dto.g.dart';

/// One of the current user's teams, with the user's role in it
@Freezed()
abstract class ProfileTeamDto with _$ProfileTeamDto {
  const factory ProfileTeamDto({
    /// Team URL slug
    required String slug,

    /// Team name
    required String name,

    /// The user's role in the team
    required String role,
  }) = _ProfileTeamDto;

  factory ProfileTeamDto.fromJson(Map<String, Object?> json) =>
      _$ProfileTeamDtoFromJson(json);
}
