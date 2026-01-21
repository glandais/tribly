// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'comment_request.freezed.dart';
part 'comment_request.g.dart';

/// Comment creation request
@Freezed()
abstract class CommentRequest with _$CommentRequest {
  const factory CommentRequest({
    /// Comment content
    required String content,

    /// Parent comment ID for replies (optional)
    String? parentId,
  }) = _CommentRequest;

  factory CommentRequest.fromJson(Map<String, Object?> json) =>
      _$CommentRequestFromJson(json);
}
