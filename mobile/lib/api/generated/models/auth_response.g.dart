// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'auth_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AuthResponse _$AuthResponseFromJson(Map<String, dynamic> json) =>
    _AuthResponse(
      accessToken: json['accessToken'] as String?,
      expiresIn: (json['expiresIn'] as num?)?.toInt(),
      user: json['user'] == null
          ? null
          : UserDto.fromJson(json['user'] as Map<String, dynamic>),
      refreshToken: json['refreshToken'] as String?,
    );

Map<String, dynamic> _$AuthResponseToJson(_AuthResponse instance) =>
    <String, dynamic>{
      'accessToken': instance.accessToken,
      'expiresIn': instance.expiresIn,
      'user': instance.user?.toJson(),
      'refreshToken': instance.refreshToken,
    };
