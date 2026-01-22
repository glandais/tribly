// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_route_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceRouteDto _$DeviceRouteDtoFromJson(Map<String, dynamic> json) =>
    _DeviceRouteDto(
      teamSlug: json['teamSlug'] as String,
      routeSlug: json['routeSlug'] as String,
      name: json['name'] as String,
      distance: (json['distance'] as num).toDouble(),
      elevationGain: (json['elevationGain'] as num).toDouble(),
      label: json['label'] as String?,
      rideDateTime: json['rideDateTime'] as String?,
      startLat: (json['startLat'] as num?)?.toDouble(),
      startLon: (json['startLon'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$DeviceRouteDtoToJson(_DeviceRouteDto instance) =>
    <String, dynamic>{
      'teamSlug': instance.teamSlug,
      'routeSlug': instance.routeSlug,
      'name': instance.name,
      'distance': instance.distance,
      'elevationGain': instance.elevationGain,
      'label': instance.label,
      'rideDateTime': instance.rideDateTime,
      'startLat': instance.startLat,
      'startLon': instance.startLon,
    };
