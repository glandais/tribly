// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_route_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceRouteDto _$DeviceRouteDtoFromJson(Map<String, dynamic> json) =>
    _DeviceRouteDto(
      teamSlug: json['teamSlug'] as String,
      routeSlug: json['routeSlug'] as String,
      routeName: json['routeName'] as String,
      distance: (json['distance'] as num).toDouble(),
      elevationGain: (json['elevationGain'] as num).toDouble(),
      startLat: (json['startLat'] as num).toDouble(),
      startLon: (json['startLon'] as num).toDouble(),
      rideName: json['rideName'] as String?,
      groupName: json['groupName'] as String?,
      startDateTime: json['startDateTime'] as String?,
    );

Map<String, dynamic> _$DeviceRouteDtoToJson(_DeviceRouteDto instance) =>
    <String, dynamic>{
      'teamSlug': instance.teamSlug,
      'routeSlug': instance.routeSlug,
      'routeName': instance.routeName,
      'distance': instance.distance,
      'elevationGain': instance.elevationGain,
      'startLat': instance.startLat,
      'startLon': instance.startLon,
      'rideName': instance.rideName,
      'groupName': instance.groupName,
      'startDateTime': instance.startDateTime,
    };
