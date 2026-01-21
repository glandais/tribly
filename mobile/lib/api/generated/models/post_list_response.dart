// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'post_dto.dart';

part 'post_list_response.freezed.dart';
part 'post_list_response.g.dart';

/// Paginated post list response
@Freezed()
abstract class PostListResponse with _$PostListResponse {
  const factory PostListResponse({
    /// List of posts
    required List<PostDto> posts,

    /// Total number of posts
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _PostListResponse;

  factory PostListResponse.fromJson(Map<String, Object?> json) =>
      _$PostListResponseFromJson(json);
}
