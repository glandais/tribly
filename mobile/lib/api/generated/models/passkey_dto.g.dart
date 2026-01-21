// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'passkey_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PasskeyDto _$PasskeyDtoFromJson(Map<String, dynamic> json) => _PasskeyDto(
  id: json['id'] as String?,
  credentialId: json['credentialId'] as String?,
  deviceName: json['deviceName'] as String?,
  transports: (json['transports'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList(),
  createdAt: json['createdAt'] as String?,
  lastUsedAt: json['lastUsedAt'] as String?,
);

Map<String, dynamic> _$PasskeyDtoToJson(_PasskeyDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'credentialId': instance.credentialId,
      'deviceName': instance.deviceName,
      'transports': instance.transports,
      'createdAt': instance.createdAt,
      'lastUsedAt': instance.lastUsedAt,
    };
