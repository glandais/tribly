// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ad_dto_location_geometry.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdDtoLocationGeometry _$AdDtoLocationGeometryFromJson(
  Map<String, dynamic> json,
) => _AdDtoLocationGeometry(
  type: AdDtoLocationGeometryTypeType.fromJson(json['type'] as String),
  coordinates: (json['coordinates'] as List<dynamic>)
      .map((e) => (e as num).toDouble())
      .toList(),
);

Map<String, dynamic> _$AdDtoLocationGeometryToJson(
  _AdDtoLocationGeometry instance,
) => <String, dynamic>{
  'type': instance.type.toJson(),
  'coordinates': instance.coordinates,
};
