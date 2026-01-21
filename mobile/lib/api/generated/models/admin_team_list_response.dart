// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'admin_team_dto.dart';

part 'admin_team_list_response.freezed.dart';
part 'admin_team_list_response.g.dart';

/// Paginated admin team list response
@Freezed()
abstract class AdminTeamListResponse with _$AdminTeamListResponse {
  const factory AdminTeamListResponse({
    /// List of teams
    required List<AdminTeamDto> teams,

    /// Total number of teams
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _AdminTeamListResponse;

  factory AdminTeamListResponse.fromJson(Map<String, Object?> json) =>
      _$AdminTeamListResponseFromJson(json);
}
