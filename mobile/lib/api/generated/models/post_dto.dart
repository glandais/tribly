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

    /// Publication timestamp
    String? publishAt,

    /// Creation timestamp
    String? createdAt,
  }) = _PostDto;

  factory PostDto.fromJson(Map<String, Object?> json) =>
      _$PostDtoFromJson(json);
}
