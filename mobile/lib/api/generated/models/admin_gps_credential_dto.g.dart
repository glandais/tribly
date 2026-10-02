// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_gps_credential_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminGpsCredentialDto _$AdminGpsCredentialDtoFromJson(
  Map<String, dynamic> json,
) => _AdminGpsCredentialDto(
  id: json['id'] as String,
  serviceType: json['serviceType'] as String,
  clientId: json['clientId'] as String,
  oauthVersion: json['oauthVersion'] as String,
  active: json['active'] as bool,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$AdminGpsCredentialDtoToJson(
  _AdminGpsCredentialDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'serviceType': instance.serviceType,
  'clientId': instance.clientId,
  'oauthVersion': instance.oauthVersion,
  'active': instance.active,
  'createdAt': instance.createdAt,
};
