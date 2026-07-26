// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'publication_dto.dart';
import 'publication_type.dart';
import 'status.dart';
import 'team_publication_dto.dart';
import 'visibility.dart';

part 'post_dto.freezed.dart';
part 'post_dto.g.dart';

/// Post summary data
@Freezed()
abstract class PostDto with _$PostDto {
  const factory PostDto({
    /// Type
    required String type,

    /// Team
    required TeamPublicationDto team,

    /// Publication ID (TSID)
    required String id,

    /// Publication URL slug
    required String slug,

    /// Publication name
    required String name,

    /// Publication media
    required MediaDto media,

    /// Publication date/time
    required String dateTime,

    /// Publication status
    required String status,

    /// Visibility level
    required String visibility,

    /// Whether the post is soft-deleted
    required bool deleted,

    /// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
    String? excerpt,

    /// URL template of the post's first image, the one a card shows. Saves a compact row from carrying media.assets just to find a picture.
    String? thumbnailUrl,

    /// Publication timestamp
    String? publishAt,

    /// Creation timestamp
    String? createdAt,

    /// Number of comments, replies included. Absent when the caller may not read the comments of this post — comments are members-only, so an outsider is told nothing, not even zero.
    int? commentCount,
  }) = _PostDto;

  factory PostDto.fromJson(Map<String, Object?> json) =>
      _$PostDtoFromJson(json);
}
