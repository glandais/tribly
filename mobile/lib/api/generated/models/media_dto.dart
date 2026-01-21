// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'assets_dto.dart';

part 'media_dto.freezed.dart';
part 'media_dto.g.dart';

@Freezed()
abstract class MediaDto with _$MediaDto {
  const factory MediaDto({
    /// Markdown
    required String markdown,

    /// Assets
    required AssetsDto assets,
  }) = _MediaDto;

  factory MediaDto.fromJson(Map<String, Object?> json) =>
      _$MediaDtoFromJson(json);
}
