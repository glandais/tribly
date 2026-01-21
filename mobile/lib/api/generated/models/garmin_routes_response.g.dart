// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'garmin_routes_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GarminRoutesResponse _$GarminRoutesResponseFromJson(
  Map<String, dynamic> json,
) => _GarminRoutesResponse(
  routes: (json['routes'] as List<dynamic>)
      .map((e) => GarminRouteDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$GarminRoutesResponseToJson(
  _GarminRoutesResponse instance,
) => <String, dynamic>{
  'routes': instance.routes.map((e) => e.toJson()).toList(),
};
