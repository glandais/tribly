// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'trip_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TripListResponse _$TripListResponseFromJson(Map<String, dynamic> json) =>
    _TripListResponse(
      trips: (json['trips'] as List<dynamic>)
          .map((e) => TripDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$TripListResponseToJson(_TripListResponse instance) =>
    <String, dynamic>{
      'trips': instance.trips.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
