// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'place_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PlaceRequest _$PlaceRequestFromJson(Map<String, dynamic> json) =>
    _PlaceRequest(
      name: json['name'] as String,
      startPlace: json['startPlace'] as bool,
      endPlace: json['endPlace'] as bool,
      address: json['address'] as String?,
      link: json['link'] as String?,
      geometry: json['geometry'] == null
          ? null
          : PlaceRequestGeometry.fromJson(
              json['geometry'] as Map<String, dynamic>,
            ),
    );

Map<String, dynamic> _$PlaceRequestToJson(_PlaceRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'startPlace': instance.startPlace,
      'endPlace': instance.endPlace,
      'address': instance.address,
      'link': instance.link,
      'geometry': instance.geometry?.toJson(),
    };
