// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'elevation_profile_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ElevationProfileDto _$ElevationProfileDtoFromJson(Map<String, dynamic> json) =>
    _ElevationProfileDto(
      routeId: json['routeId'] as String,
      slug: json['slug'] as String,
      distance: (json['distance'] as num).toDouble(),
      minElevation: (json['minElevation'] as num).toDouble(),
      maxElevation: (json['maxElevation'] as num).toDouble(),
      samples: (json['samples'] as num).toInt(),
      points: (json['points'] as List<dynamic>)
          .map((e) => ElevationPointDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$ElevationProfileDtoToJson(
  _ElevationProfileDto instance,
) => <String, dynamic>{
  'routeId': instance.routeId,
  'slug': instance.slug,
  'distance': instance.distance,
  'minElevation': instance.minElevation,
  'maxElevation': instance.maxElevation,
  'samples': instance.samples,
  'points': instance.points.map((e) => e.toJson()).toList(),
};
