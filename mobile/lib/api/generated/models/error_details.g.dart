// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'error_details.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ErrorDetailsValidation _$ErrorDetailsValidationFromJson(
  Map<String, dynamic> json,
) => ErrorDetailsValidation(
  fieldErrors: (json['fieldErrors'] as List<dynamic>)
      .map((e) => FieldError.fromJson(e as Map<String, dynamic>))
      .toList(),
  $type: json['type'] as String?,
);

Map<String, dynamic> _$ErrorDetailsValidationToJson(
  ErrorDetailsValidation instance,
) => <String, dynamic>{
  'fieldErrors': instance.fieldErrors.map((e) => e.toJson()).toList(),
  'type': instance.$type,
};

ErrorDetailsNotFound _$ErrorDetailsNotFoundFromJson(
  Map<String, dynamic> json,
) => ErrorDetailsNotFound(
  entityType: json['entityType'] as String,
  searchedBy: json['searchedBy'] as String,
  id: json['id'] as String,
  $type: json['type'] as String?,
);

Map<String, dynamic> _$ErrorDetailsNotFoundToJson(
  ErrorDetailsNotFound instance,
) => <String, dynamic>{
  'entityType': instance.entityType,
  'searchedBy': instance.searchedBy,
  'id': instance.id,
  'type': instance.$type,
};
