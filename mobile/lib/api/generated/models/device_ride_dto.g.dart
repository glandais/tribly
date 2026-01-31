// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_ride_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceRideDto _$DeviceRideDtoFromJson(Map<String, dynamic> json) =>
    _DeviceRideDto(
      teamSlug: json['teamSlug'] as String,
      rideSlug: json['rideSlug'] as String,
      rideName: json['rideName'] as String,
      entries: (json['entries'] as List<dynamic>)
          .map((e) => DeviceRideEntryDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      startDateTime: json['startDateTime'] as String?,
    );

Map<String, dynamic> _$DeviceRideDtoToJson(_DeviceRideDto instance) =>
    <String, dynamic>{
      'teamSlug': instance.teamSlug,
      'rideSlug': instance.rideSlug,
      'rideName': instance.rideName,
      'entries': instance.entries.map((e) => e.toJson()).toList(),
      'startDateTime': instance.startDateTime,
    };
