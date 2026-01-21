// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_group_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideGroupDto _$RideGroupDtoFromJson(Map<String, dynamic> json) =>
    _RideGroupDto(
      id: json['id'] as String,
      name: json['name'] as String,
      countParticipants: (json['countParticipants'] as num).toInt(),
      participants: (json['participants'] as List<dynamic>)
          .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      sortOrder: (json['sortOrder'] as num).toInt(),
      time: json['time'] as String?,
      routeSlug: json['routeSlug'] as String?,
      averageSpeed: (json['averageSpeed'] as num?)?.toDouble(),
      maxParticipants: (json['maxParticipants'] as num?)?.toInt(),
    );

Map<String, dynamic> _$RideGroupDtoToJson(_RideGroupDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'countParticipants': instance.countParticipants,
      'participants': instance.participants.map((e) => e.toJson()).toList(),
      'sortOrder': instance.sortOrder,
      'time': instance.time,
      'routeSlug': instance.routeSlug,
      'averageSpeed': instance.averageSpeed,
      'maxParticipants': instance.maxParticipants,
    };
