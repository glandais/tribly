// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'place_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PlaceListResponse _$PlaceListResponseFromJson(Map<String, dynamic> json) =>
    _PlaceListResponse(
      places: (json['places'] as List<dynamic>)
          .map((e) => PlaceDetailDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$PlaceListResponseToJson(_PlaceListResponse instance) =>
    <String, dynamic>{
      'places': instance.places.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
