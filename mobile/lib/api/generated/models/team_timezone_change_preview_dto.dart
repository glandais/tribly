// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_timezone_change_item_dto.dart';

part 'team_timezone_change_preview_dto.freezed.dart';
part 'team_timezone_change_preview_dto.g.dart';

/// Preview of a change of the team's zone; nothing is written
@Freezed()
abstract class TeamTimezoneChangePreviewDto
    with _$TeamTimezoneChangePreviewDto {
  const factory TeamTimezoneChangePreviewDto({
    /// Team's current zone
    required String from,

    /// Zone asked for
    required String to,

    /// How many upcoming place-less events keep their wall time
    required int upcomingCount,

    /// How many past place-less events keep their instant, relabelled
    required int pastCount,

    /// The first upcoming ones, soonest first, at most 10
    required List<TeamTimezoneChangeItemDto> upcoming,
  }) = _TeamTimezoneChangePreviewDto;

  factory TeamTimezoneChangePreviewDto.fromJson(Map<String, Object?> json) =>
      _$TeamTimezoneChangePreviewDtoFromJson(json);
}
