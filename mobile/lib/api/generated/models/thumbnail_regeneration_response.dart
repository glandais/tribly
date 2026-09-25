// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'thumbnail_owner_report.dart';

part 'thumbnail_regeneration_response.freezed.dart';
part 'thumbnail_regeneration_response.g.dart';

/// Outcome of a thumbnail regeneration, one entry per entity
@Freezed()
abstract class ThumbnailRegenerationResponse
    with _$ThumbnailRegenerationResponse {
  const factory ThumbnailRegenerationResponse({
    /// Whether this was a dry run (nothing redrawn)
    required bool dryRun,

    /// Number of entities matching the criteria, before the limit
    required int matched,

    /// Entities processed, at most the limit
    required List<ThumbnailOwnerReport> entities,
  }) = _ThumbnailRegenerationResponse;

  factory ThumbnailRegenerationResponse.fromJson(Map<String, Object?> json) =>
      _$ThumbnailRegenerationResponseFromJson(json);
}
