// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'route_bounds_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RouteBoundsResponse _$RouteBoundsResponseFromJson(Map<String, dynamic> json) =>
    _RouteBoundsResponse(
      bounds: json['bounds'] == null
          ? null
          : BoundsDto.fromJson(json['bounds'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$RouteBoundsResponseToJson(
  _RouteBoundsResponse instance,
) => <String, dynamic>{'bounds': instance.bounds?.toJson()};
