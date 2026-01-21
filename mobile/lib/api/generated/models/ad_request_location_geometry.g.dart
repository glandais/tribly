// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ad_request_location_geometry.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdRequestLocationGeometry _$AdRequestLocationGeometryFromJson(
  Map<String, dynamic> json,
) => _AdRequestLocationGeometry(
  type: AdRequestLocationGeometryTypeType.fromJson(json['type'] as String),
  coordinates: (json['coordinates'] as List<dynamic>)
      .map((e) => (e as num).toDouble())
      .toList(),
);

Map<String, dynamic> _$AdRequestLocationGeometryToJson(
  _AdRequestLocationGeometry instance,
) => <String, dynamic>{
  'type': instance.type.toJson(),
  'coordinates': instance.coordinates,
};
