// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_detail_dto.dart';

part 'team_list_response.freezed.dart';
part 'team_list_response.g.dart';

/// Paginated team list response
@Freezed()
abstract class TeamListResponse with _$TeamListResponse {
  const factory TeamListResponse({
    /// List of teams
    required List<TeamDetailDto> teams,

    /// Total number of teams
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _TeamListResponse;

  factory TeamListResponse.fromJson(Map<String, Object?> json) =>
      _$TeamListResponseFromJson(json);
}
