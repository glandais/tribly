// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_gps_credential_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UpdateGpsCredentialRequest _$UpdateGpsCredentialRequestFromJson(
  Map<String, dynamic> json,
) => _UpdateGpsCredentialRequest(
  clientId: json['clientId'] as String,
  clientSecret: json['clientSecret'] as String?,
  active: json['active'] as bool?,
);

Map<String, dynamic> _$UpdateGpsCredentialRequestToJson(
  _UpdateGpsCredentialRequest instance,
) => <String, dynamic>{
  'clientId': instance.clientId,
  'clientSecret': instance.clientSecret,
  'active': instance.active,
};
