// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'tile_token_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TileTokenDto _$TileTokenDtoFromJson(Map<String, dynamic> json) =>
    _TileTokenDto(
      token: json['token'] as String,
      expiresAt: json['expiresAt'] as String,
      expiresIn: (json['expiresIn'] as num).toInt(),
    );

Map<String, dynamic> _$TileTokenDtoToJson(_TileTokenDto instance) =>
    <String, dynamic>{
      'token': instance.token,
      'expiresAt': instance.expiresAt,
      'expiresIn': instance.expiresIn,
    };
