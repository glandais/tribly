// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'tag_color.dart';
import 'tag_target.dart';

part 'tag_create_request.freezed.dart';
part 'tag_create_request.g.dart';

/// Tag creation request
@Freezed()
abstract class TagCreateRequest with _$TagCreateRequest {
  const factory TagCreateRequest({
    /// Kind of content the tag applies to
    required String type,

    /// Label, trimmed; unique in the team and kind whatever the case, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
    required String label,

    /// Colour family
    required String color,
  }) = _TagCreateRequest;

  factory TagCreateRequest.fromJson(Map<String, Object?> json) =>
      _$TagCreateRequestFromJson(json);
}
