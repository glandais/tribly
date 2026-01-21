// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_user_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminUserDto _$AdminUserDtoFromJson(Map<String, dynamic> json) =>
    _AdminUserDto(
      id: json['id'] as String,
      email: json['email'] as String,
      displayName: json['displayName'] as String,
      domainId: json['domainId'] as String,
      domainName: json['domainName'] as String,
      emailVerified: json['emailVerified'] as bool,
      createdAt: json['createdAt'] as String,
      platformRole: json['platformRole'] as String?,
      lastLoginAt: json['lastLoginAt'] as String?,
    );

Map<String, dynamic> _$AdminUserDtoToJson(_AdminUserDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'email': instance.email,
      'displayName': instance.displayName,
      'domainId': instance.domainId,
      'domainName': instance.domainName,
      'emailVerified': instance.emailVerified,
      'createdAt': instance.createdAt,
      'platformRole': instance.platformRole,
      'lastLoginAt': instance.lastLoginAt,
    };
