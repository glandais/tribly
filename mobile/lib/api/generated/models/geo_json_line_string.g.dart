// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'geo_json_line_string.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GeoJsonLineString _$GeoJsonLineStringFromJson(Map<String, dynamic> json) =>
    _GeoJsonLineString(
      type: GeoJsonLineStringTypeType.fromJson(json['type'] as String),
      coordinates: (json['coordinates'] as List<dynamic>)
          .map(
            (e) =>
                (e as List<dynamic>).map((e) => (e as num).toDouble()).toList(),
          )
          .toList(),
    );

Map<String, dynamic> _$GeoJsonLineStringToJson(_GeoJsonLineString instance) =>
    <String, dynamic>{
      'type': instance.type.toJson(),
      'coordinates': instance.coordinates,
    };
