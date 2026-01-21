// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'geo_json_point.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GeoJsonPoint _$GeoJsonPointFromJson(Map<String, dynamic> json) =>
    _GeoJsonPoint(
      type: GeoJsonPointTypeType.fromJson(json['type'] as String),
      coordinates: (json['coordinates'] as List<dynamic>)
          .map((e) => (e as num).toDouble())
          .toList(),
    );

Map<String, dynamic> _$GeoJsonPointToJson(_GeoJsonPoint instance) =>
    <String, dynamic>{
      'type': instance.type.toJson(),
      'coordinates': instance.coordinates,
    };
