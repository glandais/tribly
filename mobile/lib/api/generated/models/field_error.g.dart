// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'field_error.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_FieldError _$FieldErrorFromJson(Map<String, dynamic> json) => _FieldError(
  field: json['field'] as String,
  message: json['message'] as String,
  rejectedValue: json['rejectedValue'],
);

Map<String, dynamic> _$FieldErrorToJson(_FieldError instance) =>
    <String, dynamic>{
      'field': instance.field,
      'message': instance.message,
      'rejectedValue': instance.rejectedValue,
    };
