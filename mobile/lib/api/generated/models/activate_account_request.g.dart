// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'activate_account_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ActivateAccountRequest _$ActivateAccountRequestFromJson(
  Map<String, dynamic> json,
) => _ActivateAccountRequest(
  token: json['token'] as String,
  password: json['password'] as String,
);

Map<String, dynamic> _$ActivateAccountRequestToJson(
  _ActivateAccountRequest instance,
) => <String, dynamic>{'token': instance.token, 'password': instance.password};
