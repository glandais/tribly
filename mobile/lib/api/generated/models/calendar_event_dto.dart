// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'calendar_event_type.dart';
import 'instant.dart';

part 'calendar_event_dto.freezed.dart';
part 'calendar_event_dto.g.dart';

/// Calendar event data
@Freezed()
abstract class CalendarEventDto with _$CalendarEventDto {
  const factory CalendarEventDto({
    /// Event ID (TSID)
    required String id,

    /// Event title
    required String title,

    /// Event start date/time
    required String start,

    /// Is all-day event
    required bool allDay,

    /// Event type
    required String type,

    /// Team slug
    required String teamSlug,

    /// Team name
    required String teamName,

    /// Entity slug (ride or stage)
    required String entitySlug,

    /// Event end date/time
    String? end,

    /// Parent trip slug (for stages only)
    String? tripSlug,
  }) = _CalendarEventDto;

  factory CalendarEventDto.fromJson(Map<String, Object?> json) =>
      _$CalendarEventDtoFromJson(json);
}
