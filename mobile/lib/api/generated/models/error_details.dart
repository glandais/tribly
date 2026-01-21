// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'entity_type.dart';
import 'error_code.dart';
import 'error_validation_details.dart';
import 'field_error.dart';
import 'not_found_details.dart';
import 'searched_by.dart';

part 'error_details.freezed.dart';
part 'error_details.g.dart';

/// Error details
@Freezed(unionKey: 'type')
sealed class ErrorDetails with _$ErrorDetails {
  @FreezedUnionValue('VALIDATION')
  const factory ErrorDetails.validation({
    /// Field Errors
    required List<FieldError> fieldErrors,
  }) = ErrorDetailsValidation;

  @FreezedUnionValue('NOT_FOUND')
  const factory ErrorDetails.notFound({
    /// Entity type
    required String entityType,

    /// Search type
    required String searchedBy,

    /// id/slug
    required String id,
  }) = ErrorDetailsNotFound;

  factory ErrorDetails.fromJson(Map<String, Object?> json) =>
      _$ErrorDetailsFromJson(json);
}
