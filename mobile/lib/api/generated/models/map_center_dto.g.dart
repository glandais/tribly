// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'map_center_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MapCenterDto _$MapCenterDtoFromJson(Map<String, dynamic> json) =>
    _MapCenterDto(
      lat: (json['lat'] as num).toDouble(),
      lon: (json['lon'] as num).toDouble(),
      zoom: (json['zoom'] as num).toDouble(),
    );

Map<String, dynamic> _$MapCenterDtoToJson(_MapCenterDto instance) =>
    <String, dynamic>{
      'lat': instance.lat,
      'lon': instance.lon,
      'zoom': instance.zoom,
    };
