// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'routes_bulk_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RoutesBulkResponse _$RoutesBulkResponseFromJson(Map<String, dynamic> json) =>
    _RoutesBulkResponse(
      routes: (json['routes'] as List<dynamic>)
          .map((e) => RouteDetailDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      extent: json['extent'] == null
          ? null
          : BoundsDto.fromJson(json['extent'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$RoutesBulkResponseToJson(_RoutesBulkResponse instance) =>
    <String, dynamic>{
      'routes': instance.routes.map((e) => e.toJson()).toList(),
      'extent': instance.extent?.toJson(),
    };
