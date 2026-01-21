// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'place_detail_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PlaceDetailDto _$PlaceDetailDtoFromJson(Map<String, dynamic> json) =>
    _PlaceDetailDto(
      id: json['id'] as String,
      name: json['name'] as String,
      startPlace: json['startPlace'] as bool,
      endPlace: json['endPlace'] as bool,
      address: json['address'] as String?,
      link: json['link'] as String?,
      geometry: json['geometry'] == null
          ? null
          : GeoJsonPoint.fromJson(json['geometry'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$PlaceDetailDtoToJson(_PlaceDetailDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'startPlace': instance.startPlace,
      'endPlace': instance.endPlace,
      'address': instance.address,
      'link': instance.link,
      'geometry': instance.geometry?.toJson(),
    };
