// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'place_request_geometry.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PlaceRequestGeometry _$PlaceRequestGeometryFromJson(
  Map<String, dynamic> json,
) => _PlaceRequestGeometry(
  type: PlaceRequestGeometryTypeType.fromJson(json['type'] as String),
  coordinates: (json['coordinates'] as List<dynamic>)
      .map((e) => (e as num).toDouble())
      .toList(),
);

Map<String, dynamic> _$PlaceRequestGeometryToJson(
  _PlaceRequestGeometry instance,
) => <String, dynamic>{
  'type': instance.type.toJson(),
  'coordinates': instance.coordinates,
};
