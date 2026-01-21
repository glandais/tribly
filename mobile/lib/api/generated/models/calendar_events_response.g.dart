// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'calendar_events_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CalendarEventsResponse _$CalendarEventsResponseFromJson(
  Map<String, dynamic> json,
) => _CalendarEventsResponse(
  events: (json['events'] as List<dynamic>)
      .map((e) => CalendarEventDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$CalendarEventsResponseToJson(
  _CalendarEventsResponse instance,
) => <String, dynamic>{
  'events': instance.events.map((e) => e.toJson()).toList(),
};
