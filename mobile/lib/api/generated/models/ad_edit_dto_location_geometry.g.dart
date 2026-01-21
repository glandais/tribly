// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ad_edit_dto_location_geometry.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdEditDtoLocationGeometry _$AdEditDtoLocationGeometryFromJson(
  Map<String, dynamic> json,
) => _AdEditDtoLocationGeometry(
  type: AdEditDtoLocationGeometryTypeType.fromJson(json['type'] as String),
  coordinates: (json['coordinates'] as List<dynamic>)
      .map((e) => (e as num).toDouble())
      .toList(),
);

Map<String, dynamic> _$AdEditDtoLocationGeometryToJson(
  _AdEditDtoLocationGeometry instance,
) => <String, dynamic>{
  'type': instance.type.toJson(),
  'coordinates': instance.coordinates,
};
