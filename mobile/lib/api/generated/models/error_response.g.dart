// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'error_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ErrorResponse _$ErrorResponseFromJson(Map<String, dynamic> json) =>
    _ErrorResponse(
      code: json['code'] as String,
      errorDetails: json['errorDetails'] == null
          ? null
          : ErrorDetails.fromJson(json['errorDetails'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$ErrorResponseToJson(_ErrorResponse instance) =>
    <String, dynamic>{
      'code': instance.code,
      'errorDetails': instance.errorDetails?.toJson(),
    };
