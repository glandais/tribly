// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'thumbnail_file.freezed.dart';
part 'thumbnail_file.g.dart';

/// One stored thumbnail
@Freezed()
abstract class ThumbnailFile with _$ThumbnailFile {
  const factory ThumbnailFile({
    /// Asset type
    required String type,

    /// Stored size in bytes, -1 when the file is missing
    required int bytes,
  }) = _ThumbnailFile;

  factory ThumbnailFile.fromJson(Map<String, Object?> json) =>
      _$ThumbnailFileFromJson(json);
}
