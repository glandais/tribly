// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_detail_dto_geometry.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamDetailDtoGeometry _$TeamDetailDtoGeometryFromJson(
  Map<String, dynamic> json,
) => _TeamDetailDtoGeometry(
  type: TeamDetailDtoGeometryTypeType.fromJson(json['type'] as String),
  coordinates: (json['coordinates'] as List<dynamic>)
      .map((e) => (e as num).toDouble())
      .toList(),
);

Map<String, dynamic> _$TeamDetailDtoGeometryToJson(
  _TeamDetailDtoGeometry instance,
) => <String, dynamic>{
  'type': instance.type.toJson(),
  'coordinates': instance.coordinates,
};
