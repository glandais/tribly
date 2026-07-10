// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bounds_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BoundsDto _$BoundsDtoFromJson(Map<String, dynamic> json) => _BoundsDto(
  minLon: (json['minLon'] as num).toDouble(),
  minLat: (json['minLat'] as num).toDouble(),
  maxLon: (json['maxLon'] as num).toDouble(),
  maxLat: (json['maxLat'] as num).toDouble(),
);

Map<String, dynamic> _$BoundsDtoToJson(_BoundsDto instance) =>
    <String, dynamic>{
      'minLon': instance.minLon,
      'minLat': instance.minLat,
      'maxLon': instance.maxLon,
      'maxLat': instance.maxLat,
    };
