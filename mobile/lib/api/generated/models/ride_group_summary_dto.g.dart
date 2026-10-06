// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_group_summary_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideGroupSummaryDto _$RideGroupSummaryDtoFromJson(Map<String, dynamic> json) =>
    _RideGroupSummaryDto(
      id: json['id'] as String,
      name: json['name'] as String,
      countParticipants: (json['countParticipants'] as num).toInt(),
      full: json['full'] as bool,
      sortOrder: (json['sortOrder'] as num).toInt(),
      time: json['time'] as String?,
      averageSpeed: (json['averageSpeed'] as num?)?.toDouble(),
      maxParticipants: (json['maxParticipants'] as num?)?.toInt(),
      routeSlug: json['routeSlug'] as String?,
      distance: (json['distance'] as num?)?.toDouble(),
      elevationGain: (json['elevationGain'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$RideGroupSummaryDtoToJson(
  _RideGroupSummaryDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'countParticipants': instance.countParticipants,
  'full': instance.full,
  'sortOrder': instance.sortOrder,
  'time': instance.time,
  'averageSpeed': instance.averageSpeed,
  'maxParticipants': instance.maxParticipants,
  'routeSlug': instance.routeSlug,
  'distance': instance.distance,
  'elevationGain': instance.elevationGain,
};
