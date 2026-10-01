// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'tag_deleted_dto.freezed.dart';
part 'tag_deleted_dto.g.dart';

/// Result of a tag deletion
@Freezed()
abstract class TagDeletedDto with _$TagDeletedDto {
  const factory TagDeletedDto({
    /// Contents the tag was detached from, counted like usageCount (trashed contents lose it too, uncounted)
    required int detachedCount,
  }) = _TagDeletedDto;

  factory TagDeletedDto.fromJson(Map<String, Object?> json) =>
      _$TagDeletedDtoFromJson(json);
}
