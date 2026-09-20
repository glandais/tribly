// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'push_device_registration.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PushDeviceRegistration _$PushDeviceRegistrationFromJson(
  Map<String, dynamic> json,
) => _PushDeviceRegistration(
  token: json['token'] as String,
  platform: json['platform'] as String,
  deviceName: json['deviceName'] as String?,
  appVersion: json['appVersion'] as String?,
);

Map<String, dynamic> _$PushDeviceRegistrationToJson(
  _PushDeviceRegistration instance,
) => <String, dynamic>{
  'token': instance.token,
  'platform': instance.platform,
  'deviceName': instance.deviceName,
  'appVersion': instance.appVersion,
};
