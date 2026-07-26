// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'elevation_point_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ElevationPointDto _$ElevationPointDtoFromJson(Map<String, dynamic> json) =>
    _ElevationPointDto(
      distance: (json['distance'] as num).toDouble(),
      elevation: (json['elevation'] as num).toDouble(),
      grade: (json['grade'] as num).toDouble(),
    );

Map<String, dynamic> _$ElevationPointDtoToJson(_ElevationPointDto instance) =>
    <String, dynamic>{
      'distance': instance.distance,
      'elevation': instance.elevation,
      'grade': instance.grade,
    };
