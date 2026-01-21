// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'router_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RouterResponse _$RouterResponseFromJson(Map<String, dynamic> json) =>
    _RouterResponse(
      route: GeoJsonLineString.fromJson(json['route'] as Map<String, dynamic>),
      dist: (json['dist'] as num?)?.toDouble(),
      ascend: (json['ascend'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$RouterResponseToJson(_RouterResponse instance) =>
    <String, dynamic>{
      'route': instance.route.toJson(),
      'dist': instance.dist,
      'ascend': instance.ascend,
    };
