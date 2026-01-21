// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_user_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PublicUserDto _$PublicUserDtoFromJson(Map<String, dynamic> json) =>
    _PublicUserDto(
      id: json['id'] as String,
      displayName: json['displayName'] as String,
      avatarUrl: json['avatarUrl'] as String?,
    );

Map<String, dynamic> _$PublicUserDtoToJson(_PublicUserDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'displayName': instance.displayName,
      'avatarUrl': instance.avatarUrl,
    };
