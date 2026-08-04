// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'calendar_event_type.dart';
import 'instant.dart';
import 'status.dart';

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

    /// Whether the current user is registered to this ride, or to the trip this stage belongs to. False for an anonymous caller.
    required bool registered,

    /// Publication status of the ride or stage
    required String status,

    /// Event end date/time
    String? end,

    /// Parent trip slug (for stages only)
    String? tripSlug,

    /// Name of the meeting place, null when the ride or stage has no start place
    String? startPlaceName,

    /// Distance in meters of the attached route, null when there is no route
    double? distance,

    /// Total elevation gain in meters of the attached route, null when there is no route
    double? elevationGain,

    /// Thumbnail image URL template (contains a {size} placeholder), light variant preferred. Falls back to the route's thumbnail when the ride or stage has none of its own. For a client that renders one picture and does not follow a colour scheme; prefer thumbnailLightUrl/thumbnailDarkUrl otherwise.
    String? thumbnailUrl,

    /// Light-scheme thumbnail image URL template (contains a {size} placeholder). Null when the event's picture exists only in a dark variant.
    String? thumbnailLightUrl,

    /// Dark-scheme thumbnail image URL template (contains a {size} placeholder). Null when the event's picture exists only in a light variant.
    String? thumbnailDarkUrl,

    /// Name of the ride group the current user joined. Null when not registered, and always null for trip stages, which have no groups.
    String? groupName,
  }) = _CalendarEventDto;

  factory CalendarEventDto.fromJson(Map<String, Object?> json) =>
      _$CalendarEventDtoFromJson(json);
}
