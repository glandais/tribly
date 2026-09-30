// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'verify_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_VerifyResponse _$VerifyResponseFromJson(Map<String, dynamic> json) =>
    _VerifyResponse(
      userCode: json['userCode'] as String,
      clientId: json['clientId'] as String,
      requestedAt: json['requestedAt'] as String,
      authorized: json['authorized'] as bool?,
    );

Map<String, dynamic> _$VerifyResponseToJson(_VerifyResponse instance) =>
    <String, dynamic>{
      'userCode': instance.userCode,
      'clientId': instance.clientId,
      'requestedAt': instance.requestedAt,
      'authorized': instance.authorized,
    };
