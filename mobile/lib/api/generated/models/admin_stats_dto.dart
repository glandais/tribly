// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'admin_stats_dto.freezed.dart';
part 'admin_stats_dto.g.dart';

/// Admin dashboard statistics
@Freezed()
abstract class AdminStatsDto with _$AdminStatsDto {
  const factory AdminStatsDto({
    /// Total number of domains
    required int totalDomains,

    /// Total number of teams
    required int totalTeams,

    /// Total number of users
    required int totalUsers,
  }) = _AdminStatsDto;

  factory AdminStatsDto.fromJson(Map<String, Object?> json) =>
      _$AdminStatsDtoFromJson(json);
}
