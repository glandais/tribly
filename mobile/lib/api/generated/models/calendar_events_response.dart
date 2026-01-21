// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'calendar_event_dto.dart';

part 'calendar_events_response.freezed.dart';
part 'calendar_events_response.g.dart';

/// Calendar events response
@Freezed()
abstract class CalendarEventsResponse with _$CalendarEventsResponse {
  const factory CalendarEventsResponse({
    /// List of calendar events
    required List<CalendarEventDto> events,
  }) = _CalendarEventsResponse;

  factory CalendarEventsResponse.fromJson(Map<String, Object?> json) =>
      _$CalendarEventsResponseFromJson(json);
}
