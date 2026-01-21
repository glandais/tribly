// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'error_code.dart';
import 'error_details.dart';
import 'field_error.dart';

part 'error_validation_details.freezed.dart';
part 'error_validation_details.g.dart';

@Freezed()
abstract class ErrorValidationDetails with _$ErrorValidationDetails {
  const factory ErrorValidationDetails({
    /// Type
    required String type,

    /// Field Errors
    required List<FieldError> fieldErrors,
  }) = _ErrorValidationDetails;

  factory ErrorValidationDetails.fromJson(Map<String, Object?> json) =>
      _$ErrorValidationDetailsFromJson(json);
}
