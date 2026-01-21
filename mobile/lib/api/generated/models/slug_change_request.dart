// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'slug_change_request.freezed.dart';
part 'slug_change_request.g.dart';

/// Slug change request
@Freezed()
abstract class SlugChangeRequest with _$SlugChangeRequest {
  const factory SlugChangeRequest({
    /// New slug (lowercase letters, numbers, and hyphens only)
    required String slug,
  }) = _SlugChangeRequest;

  factory SlugChangeRequest.fromJson(Map<String, Object?> json) =>
      _$SlugChangeRequestFromJson(json);
}
