// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'tag_color.dart';

part 'tag_dto.freezed.dart';
part 'tag_dto.g.dart';

/// A team tag on a content
@Freezed()
abstract class TagDto with _$TagDto {
  const factory TagDto({
    /// Tag ID (TSID)
    required String id,

    /// Label, at most 32 characters
    required String label,

    /// Colour family
    required String color,
  }) = _TagDto;

  factory TagDto.fromJson(Map<String, Object?> json) => _$TagDtoFromJson(json);
}
