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
      registered: json['registered'] as bool,
      status: json['status'] as String,
      end: json['end'] as String?,
      tripSlug: json['tripSlug'] as String?,
      startPlaceName: json['startPlaceName'] as String?,
      distance: (json['distance'] as num?)?.toDouble(),
      elevationGain: (json['elevationGain'] as num?)?.toDouble(),
      thumbnailUrl: json['thumbnailUrl'] as String?,
      groupName: json['groupName'] as String?,
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
      'registered': instance.registered,
      'status': instance.status,
      'end': instance.end,
      'tripSlug': instance.tripSlug,
      'startPlaceName': instance.startPlaceName,
      'distance': instance.distance,
      'elevationGain': instance.elevationGain,
      'thumbnailUrl': instance.thumbnailUrl,
      'groupName': instance.groupName,
    };
