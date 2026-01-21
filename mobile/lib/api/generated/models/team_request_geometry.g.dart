// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_request_geometry.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamRequestGeometry _$TeamRequestGeometryFromJson(Map<String, dynamic> json) =>
    _TeamRequestGeometry(
      type: TeamRequestGeometryTypeType.fromJson(json['type'] as String),
      coordinates: (json['coordinates'] as List<dynamic>)
          .map((e) => (e as num).toDouble())
          .toList(),
    );

Map<String, dynamic> _$TeamRequestGeometryToJson(
  _TeamRequestGeometry instance,
) => <String, dynamic>{
  'type': instance.type.toJson(),
  'coordinates': instance.coordinates,
};
