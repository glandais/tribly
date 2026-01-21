// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UserDto _$UserDtoFromJson(Map<String, dynamic> json) => _UserDto(
  id: json['id'] as String,
  email: json['email'] as String,
  displayName: json['displayName'] as String,
  avatarUrl: json['avatarUrl'] as String?,
  createdAt: json['createdAt'] as String?,
  unitSystem: json['unitSystem'] as String?,
  platformRole: json['platformRole'] as String?,
  connectedServices: (json['connectedServices'] as List<dynamic>?)
      ?.map((e) => GpsServiceConnectionDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$UserDtoToJson(_UserDto instance) => <String, dynamic>{
  'id': instance.id,
  'email': instance.email,
  'displayName': instance.displayName,
  'avatarUrl': instance.avatarUrl,
  'createdAt': instance.createdAt,
  'unitSystem': instance.unitSystem,
  'platformRole': instance.platformRole,
  'connectedServices': instance.connectedServices
      ?.map((e) => e.toJson())
      .toList(),
};
