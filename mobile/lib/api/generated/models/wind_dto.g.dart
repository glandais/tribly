// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'wind_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WindDto _$WindDtoFromJson(Map<String, dynamic> json) => _WindDto(
  speed: (json['speed'] as num).toDouble(),
  direction: (json['direction'] as num).toDouble(),
  compass: json['compass'] as String,
  gusts: (json['gusts'] as num?)?.toDouble(),
);

Map<String, dynamic> _$WindDtoToJson(_WindDto instance) => <String, dynamic>{
  'speed': instance.speed,
  'direction': instance.direction,
  'compass': instance.compass,
  'gusts': instance.gusts,
};
