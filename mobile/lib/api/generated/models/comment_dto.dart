// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'comment_dto.dart';
import 'instant.dart';
import 'public_user_dto.dart';

part 'comment_dto.freezed.dart';
part 'comment_dto.g.dart';

/// Comment data
@Freezed()
abstract class CommentDto with _$CommentDto {
  const factory CommentDto({
    /// Comment ID (TSID)
    required String id,

    /// Comment content. Empty when the comment is deleted — see the deleted flag.
    required String content,

    /// Comment author
    required PublicUserDto author,

    /// Creation timestamp
    required String createdAt,

    /// Replies to this comment
    required List<CommentDto> replies,

    /// How many replies this comment has. Equal to replies.size() when the whole thread is embedded; a client that loads threads on demand uses it to decide whether ?parentId= is worth a call. Always 0 on a reply — threading is one level deep.
    required int replyCount,

    /// True for the comment of a deleted account that others had answered. It stays only to carry its replies: the content is empty, and clients render a placeholder with neither author nor actions.
    required bool deleted,

    /// Parent comment ID (for replies)
    String? parentId,
  }) = _CommentDto;

  factory CommentDto.fromJson(Map<String, Object?> json) =>
      _$CommentDtoFromJson(json);
}
