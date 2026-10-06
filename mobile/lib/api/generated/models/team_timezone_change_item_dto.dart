// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'timezone_change_entity_type.dart';

part 'team_timezone_change_item_dto.freezed.dart';
part 'team_timezone_change_item_dto.g.dart';

/// An upcoming place-less event that keeps its wall time in the new zone
@Freezed()
abstract class TeamTimezoneChangeItemDto with _$TeamTimezoneChangeItemDto {
  const factory TeamTimezoneChangeItemDto({
    /// Kind of event
    required String type,

    /// Event ID (TSID)
    required String id,

    /// Event URL slug
    required String slug,

    /// Event title; a stage's own name
    required String title,

    /// Start, as stored today: read it in the team's current zone for the wall time it keeps
    required String dateTime,

    /// For a stage, the title of its trip
    String? tripTitle,
  }) = _TeamTimezoneChangeItemDto;

  factory TeamTimezoneChangeItemDto.fromJson(Map<String, Object?> json) =>
      _$TeamTimezoneChangeItemDtoFromJson(json);
}
