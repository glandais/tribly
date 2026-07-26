// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'comment_dto.dart';

part 'comment_list_response.freezed.dart';
part 'comment_list_response.g.dart';

/// List of comments response
@Freezed()
abstract class CommentListResponse with _$CommentListResponse {
  const factory CommentListResponse({
    /// List of comments (top-level only, with nested replies)
    required List<CommentDto> items,

    /// Total count including replies
    required int total,

    /// How many items exist in the mode that was asked for: top-level comments normally, or replies of the requested parentId. This is what page/size iterate over.
    required int itemTotal,

    /// Page number of items (0-indexed)
    required int page,

    /// Page size applied to items. Equals itemTotal when the caller passed no pagination parameter, since the whole tree is then returned.
    required int size,
  }) = _CommentListResponse;

  factory CommentListResponse.fromJson(Map<String, Object?> json) =>
      _$CommentListResponseFromJson(json);
}
