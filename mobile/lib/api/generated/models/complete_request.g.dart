// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'complete_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CompleteRequest _$CompleteRequestFromJson(Map<String, dynamic> json) =>
    _CompleteRequest(
      userCode: json['userCode'] as String,
      confirmed: json['confirmed'] as bool,
    );

Map<String, dynamic> _$CompleteRequestToJson(_CompleteRequest instance) =>
    <String, dynamic>{
      'userCode': instance.userCode,
      'confirmed': instance.confirmed,
    };
