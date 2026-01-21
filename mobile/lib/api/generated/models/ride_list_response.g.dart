// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideListResponse _$RideListResponseFromJson(Map<String, dynamic> json) =>
    _RideListResponse(
      rides: (json['rides'] as List<dynamic>)
          .map((e) => RideDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$RideListResponseToJson(_RideListResponse instance) =>
    <String, dynamic>{
      'rides': instance.rides.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
