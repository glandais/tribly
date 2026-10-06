// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'weather_checkpoint_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WeatherCheckpointDto _$WeatherCheckpointDtoFromJson(
  Map<String, dynamic> json,
) => _WeatherCheckpointDto(
  indexField: (json['index'] as num).toInt(),
  kind: json['kind'] as String,
  distance: (json['distance'] as num).toDouble(),
  time: json['time'] as String,
  elevation: (json['elevation'] as num?)?.toDouble(),
  weather: json['weather'] == null
      ? null
      : WeatherConditionsDto.fromJson(json['weather'] as Map<String, dynamic>),
  relativeWind: json['relativeWind'] as String?,
  headwind: (json['headwind'] as num?)?.toDouble(),
  relativeWindAngle: (json['relativeWindAngle'] as num?)?.toDouble(),
);

Map<String, dynamic> _$WeatherCheckpointDtoToJson(
  _WeatherCheckpointDto instance,
) => <String, dynamic>{
  'index': instance.indexField,
  'kind': instance.kind,
  'distance': instance.distance,
  'time': instance.time,
  'elevation': instance.elevation,
  'weather': instance.weather?.toJson(),
  'relativeWind': instance.relativeWind,
  'headwind': instance.headwind,
  'relativeWindAngle': instance.relativeWindAngle,
};
