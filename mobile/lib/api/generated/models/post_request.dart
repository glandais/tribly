// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'status.dart';
import 'visibility.dart';

part 'post_request.freezed.dart';
part 'post_request.g.dart';

/// Post request
@Freezed()
abstract class PostRequest with _$PostRequest {
  const factory PostRequest({
    /// Post name
    required String name,

    /// Post description
    required MediaDto media,

    /// Post date/time
    required String dateTime,

    /// Post status
    required String status,

    /// Visibility level
    required String visibility,

    /// Publication timestamp (for scheduled publishing)
    String? publishAt,
  }) = _PostRequest;

  factory PostRequest.fromJson(Map<String, Object?> json) =>
      _$PostRequestFromJson(json);
}
