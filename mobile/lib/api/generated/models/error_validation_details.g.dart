// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'error_validation_details.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ErrorValidationDetails _$ErrorValidationDetailsFromJson(
  Map<String, dynamic> json,
) => _ErrorValidationDetails(
  type: json['type'] as String,
  fieldErrors: (json['fieldErrors'] as List<dynamic>)
      .map((e) => FieldError.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$ErrorValidationDetailsToJson(
  _ErrorValidationDetails instance,
) => <String, dynamic>{
  'type': instance.type,
  'fieldErrors': instance.fieldErrors.map((e) => e.toJson()).toList(),
};
