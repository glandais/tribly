// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'calendar_event_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CalendarEventDto _$CalendarEventDtoFromJson(Map<String, dynamic> json) =>
    _CalendarEventDto(
      id: json['id'] as String,
      title: json['title'] as String,
      start: json['start'] as String,
      allDay: json['allDay'] as bool,
      type: json['type'] as String,
      teamSlug: json['teamSlug'] as String,
      teamName: json['teamName'] as String,
      entitySlug: json['entitySlug'] as String,
      end: json['end'] as String?,
      tripSlug: json['tripSlug'] as String?,
    );

Map<String, dynamic> _$CalendarEventDtoToJson(_CalendarEventDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'start': instance.start,
      'allDay': instance.allDay,
      'type': instance.type,
      'teamSlug': instance.teamSlug,
      'teamName': instance.teamName,
      'entitySlug': instance.entitySlug,
      'end': instance.end,
      'tripSlug': instance.tripSlug,
    };
