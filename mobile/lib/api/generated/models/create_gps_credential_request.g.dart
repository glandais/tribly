// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_gps_credential_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CreateGpsCredentialRequest _$CreateGpsCredentialRequestFromJson(
  Map<String, dynamic> json,
) => _CreateGpsCredentialRequest(
  serviceType: json['serviceType'] as String,
  clientId: json['clientId'] as String,
  clientSecret: json['clientSecret'] as String?,
  active: json['active'] as bool?,
);

Map<String, dynamic> _$CreateGpsCredentialRequestToJson(
  _CreateGpsCredentialRequest instance,
) => <String, dynamic>{
  'serviceType': instance.serviceType,
  'clientId': instance.clientId,
  'clientSecret': instance.clientSecret,
  'active': instance.active,
};
