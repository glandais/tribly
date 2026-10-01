// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'tag_color.dart';
import 'tag_target.dart';

part 'tag_with_usage_dto.freezed.dart';
part 'tag_with_usage_dto.g.dart';

/// A team tag with its usage count
@Freezed()
abstract class TagWithUsageDto with _$TagWithUsageDto {
  const factory TagWithUsageDto({
    /// Tag ID (TSID)
    required String id,

    /// Label, at most 32 characters
    required String label,

    /// Colour family
    required String color,

    /// Kind of content the tag applies to
    required String type,

    /// Contents carrying the tag: rides, posts, trips, routes and ads out of the trash, plus ride templates — what a deletion would detach it from
    required int usageCount,
  }) = _TagWithUsageDto;

  factory TagWithUsageDto.fromJson(Map<String, Object?> json) =>
      _$TagWithUsageDtoFromJson(json);
}
