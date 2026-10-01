// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'tag_color.dart';

part 'tag_update_request.freezed.dart';
part 'tag_update_request.g.dart';

/// Tag update request — absent fields are unchanged
@Freezed()
abstract class TagUpdateRequest with _$TagUpdateRequest {
  const factory TagUpdateRequest({
    /// New label, trimmed, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
    String? label,

    /// New colour family
    String? color,
  }) = _TagUpdateRequest;

  factory TagUpdateRequest.fromJson(Map<String, Object?> json) =>
      _$TagUpdateRequestFromJson(json);
}
