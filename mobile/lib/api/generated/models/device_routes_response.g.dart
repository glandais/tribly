// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_routes_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceRoutesResponse _$DeviceRoutesResponseFromJson(
  Map<String, dynamic> json,
) => _DeviceRoutesResponse(
  routes: (json['routes'] as List<dynamic>)
      .map((e) => DeviceRouteDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$DeviceRoutesResponseToJson(
  _DeviceRoutesResponse instance,
) => <String, dynamic>{
  'routes': instance.routes.map((e) => e.toJson()).toList(),
};
