// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'garmin_token_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GarminTokenResponse _$GarminTokenResponseFromJson(Map<String, dynamic> json) =>
    _GarminTokenResponse(
      accessToken: json['accessToken'] as String,
      tokenType: json['tokenType'] as String,
      expiresIn: (json['expiresIn'] as num).toInt(),
      refreshToken: json['refreshToken'] as String?,
    );

Map<String, dynamic> _$GarminTokenResponseToJson(
  _GarminTokenResponse instance,
) => <String, dynamic>{
  'accessToken': instance.accessToken,
  'tokenType': instance.tokenType,
  'expiresIn': instance.expiresIn,
  'refreshToken': instance.refreshToken,
};
