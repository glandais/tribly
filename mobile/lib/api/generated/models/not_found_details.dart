// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'entity_type.dart';
import 'error_code.dart';
import 'error_details.dart';
import 'searched_by.dart';

part 'not_found_details.freezed.dart';
part 'not_found_details.g.dart';

@Freezed()
abstract class NotFoundDetails with _$NotFoundDetails {
  const factory NotFoundDetails({
    /// Type
    required String type,

    /// Entity type
    required String entityType,

    /// Search type
    required String searchedBy,

    /// id/slug
    required String id,
  }) = _NotFoundDetails;

  factory NotFoundDetails.fromJson(Map<String, Object?> json) =>
      _$NotFoundDetailsFromJson(json);
}
