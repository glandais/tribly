// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'waypoint_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WaypointDto _$WaypointDtoFromJson(Map<String, dynamic> json) => _WaypointDto(
  geometry: GeoJsonPoint.fromJson(json['geometry'] as Map<String, dynamic>),
  name: json['name'] as String?,
);

Map<String, dynamic> _$WaypointDtoToJson(_WaypointDto instance) =>
    <String, dynamic>{
      'geometry': instance.geometry.toJson(),
      'name': instance.name,
    };
