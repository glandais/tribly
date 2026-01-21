// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'route_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RouteListResponse _$RouteListResponseFromJson(Map<String, dynamic> json) =>
    _RouteListResponse(
      routes: (json['routes'] as List<dynamic>)
          .map((e) => RouteDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$RouteListResponseToJson(_RouteListResponse instance) =>
    <String, dynamic>{
      'routes': instance.routes.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
