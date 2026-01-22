// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_token_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DeviceTokenResponse _$DeviceTokenResponseFromJson(Map<String, dynamic> json) =>
    _DeviceTokenResponse(
      accessToken: json['accessToken'] as String,
      tokenType: json['tokenType'] as String,
      expiresIn: (json['expiresIn'] as num).toInt(),
      refreshToken: json['refreshToken'] as String?,
    );

Map<String, dynamic> _$DeviceTokenResponseToJson(
  _DeviceTokenResponse instance,
) => <String, dynamic>{
  'accessToken': instance.accessToken,
  'tokenType': instance.tokenType,
  'expiresIn': instance.expiresIn,
  'refreshToken': instance.refreshToken,
};
