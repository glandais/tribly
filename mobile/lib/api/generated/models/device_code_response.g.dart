// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_code_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceCodeResponse _$DeviceCodeResponseFromJson(Map<String, dynamic> json) =>
    _DeviceCodeResponse(
      deviceCode: json['deviceCode'] as String,
      userCode: json['userCode'] as String,
      verificationUri: json['verificationUri'] as String,
      verificationUriComplete: json['verificationUriComplete'] as String,
      expiresIn: (json['expiresIn'] as num).toInt(),
      interval: (json['interval'] as num).toInt(),
    );

Map<String, dynamic> _$DeviceCodeResponseToJson(_DeviceCodeResponse instance) =>
    <String, dynamic>{
      'deviceCode': instance.deviceCode,
      'userCode': instance.userCode,
      'verificationUri': instance.verificationUri,
      'verificationUriComplete': instance.verificationUriComplete,
      'expiresIn': instance.expiresIn,
      'interval': instance.interval,
    };
