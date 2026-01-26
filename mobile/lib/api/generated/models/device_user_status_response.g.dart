// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_user_status_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceUserStatusResponse _$DeviceUserStatusResponseFromJson(
  Map<String, dynamic> json,
) => _DeviceUserStatusResponse(
  connectedGpsServices: (json['connectedGpsServices'] as List<dynamic>)
      .map((e) => GpsServiceType.fromJson(e as String))
      .toList(),
);

Map<String, dynamic> _$DeviceUserStatusResponseToJson(
  _DeviceUserStatusResponse instance,
) => <String, dynamic>{
  'connectedGpsServices': instance.connectedGpsServices
      .map((e) => e.toJson())
      .toList(),
};
