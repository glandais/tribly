// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_ride_entry_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceRideEntryDto _$DeviceRideEntryDtoFromJson(Map<String, dynamic> json) =>
    _DeviceRideEntryDto(
      routeSlug: json['routeSlug'] as String,
      routeName: json['routeName'] as String,
      distance: (json['distance'] as num).toDouble(),
      elevationGain: (json['elevationGain'] as num).toDouble(),
      groupName: json['groupName'] as String?,
      startLat: (json['startLat'] as num?)?.toDouble(),
      startLon: (json['startLon'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$DeviceRideEntryDtoToJson(_DeviceRideEntryDto instance) =>
    <String, dynamic>{
      'routeSlug': instance.routeSlug,
      'routeName': instance.routeName,
      'distance': instance.distance,
      'elevationGain': instance.elevationGain,
      'groupName': instance.groupName,
      'startLat': instance.startLat,
      'startLon': instance.startLon,
    };
