// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'map_terrain_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MapTerrainDto _$MapTerrainDtoFromJson(Map<String, dynamic> json) =>
    _MapTerrainDto(
      url: json['url'] as String,
      maxZoom: (json['maxZoom'] as num).toInt(),
    );

Map<String, dynamic> _$MapTerrainDtoToJson(_MapTerrainDto instance) =>
    <String, dynamic>{'url': instance.url, 'maxZoom': instance.maxZoom};
