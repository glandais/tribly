// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_token_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceTokenRequest _$DeviceTokenRequestFromJson(Map<String, dynamic> json) =>
    _DeviceTokenRequest(
      grantType: json['grantType'] as String,
      deviceCode: json['deviceCode'] as String?,
      refreshToken: json['refreshToken'] as String?,
    );

Map<String, dynamic> _$DeviceTokenRequestToJson(_DeviceTokenRequest instance) =>
    <String, dynamic>{
      'grantType': instance.grantType,
      'deviceCode': instance.deviceCode,
      'refreshToken': instance.refreshToken,
    };
