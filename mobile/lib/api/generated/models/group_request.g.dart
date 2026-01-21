// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'group_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GroupRequest _$GroupRequestFromJson(Map<String, dynamic> json) =>
    _GroupRequest(
      name: json['name'] as String,
      id: json['id'] as String?,
      time: json['time'] as String?,
      averageSpeed: (json['averageSpeed'] as num?)?.toDouble(),
      maxParticipants: (json['maxParticipants'] as num?)?.toInt(),
      routeSlug: json['routeSlug'] as String?,
    );

Map<String, dynamic> _$GroupRequestToJson(_GroupRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'id': instance.id,
      'time': instance.time,
      'averageSpeed': instance.averageSpeed,
      'maxParticipants': instance.maxParticipants,
      'routeSlug': instance.routeSlug,
    };
