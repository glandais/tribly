// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'client_error_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ClientErrorDto _$ClientErrorDtoFromJson(Map<String, dynamic> json) =>
    _ClientErrorDto(
      type: json['type'] as String,
      message: json['message'] as String,
      stack: json['stack'] as String?,
    );

Map<String, dynamic> _$ClientErrorDtoToJson(_ClientErrorDto instance) =>
    <String, dynamic>{
      'type': instance.type,
      'message': instance.message,
      'stack': instance.stack,
    };
