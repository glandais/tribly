// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'garmin_token_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GarminTokenRequest _$GarminTokenRequestFromJson(Map<String, dynamic> json) =>
    _GarminTokenRequest(
      grantType: json['grantType'] as String,
      code: json['code'] as String?,
      refreshToken: json['refreshToken'] as String?,
    );

Map<String, dynamic> _$GarminTokenRequestToJson(_GarminTokenRequest instance) =>
    <String, dynamic>{
      'grantType': instance.grantType,
      'code': instance.code,
      'refreshToken': instance.refreshToken,
    };
