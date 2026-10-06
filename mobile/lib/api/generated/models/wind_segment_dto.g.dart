// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'wind_segment_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WindSegmentDto _$WindSegmentDtoFromJson(Map<String, dynamic> json) =>
    _WindSegmentDto(
      fromDistance: (json['fromDistance'] as num).toDouble(),
      toDistance: (json['toDistance'] as num).toDouble(),
      relativeWind: json['relativeWind'] as String,
      headwind: (json['headwind'] as num).toDouble(),
    );

Map<String, dynamic> _$WindSegmentDtoToJson(_WindSegmentDto instance) =>
    <String, dynamic>{
      'fromDistance': instance.fromDistance,
      'toDistance': instance.toDistance,
      'relativeWind': instance.relativeWind,
      'headwind': instance.headwind,
    };
