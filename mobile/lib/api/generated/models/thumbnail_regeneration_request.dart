// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'thumbnail_regeneration_request.freezed.dart';
part 'thumbnail_regeneration_request.g.dart';

/// Which map thumbnails to redraw from the geometry already stored. The criteria combine: the date window and the size threshold narrow the thumbnails that exist, the 'missing' flag adds the entities that have none. At least one criterion is required.
@Freezed()
abstract class ThumbnailRegenerationRequest
    with _$ThumbnailRegenerationRequest {
  const factory ThumbnailRegenerationRequest({
    /// List what would be redrawn, without redrawing anything
    required bool dryRun,

    /// Only thumbnails drawn at or after this instant
    String? renderedFrom,

    /// Only thumbnails drawn before this instant
    String? renderedTo,

    /// Only thumbnails whose stored file is smaller than this many bytes, or has no file. A map drawn without its background weighs a few kB, a real one tens.
    int? suspectBelowBytes,

    /// Also redraw the routes, rides and trips that have a route to draw but lack a light or dark thumbnail — what a failed render leaves behind
    bool? includeMissing,

    /// Redraw at most this many entities (default 100)
    int? limit,
  }) = _ThumbnailRegenerationRequest;

  factory ThumbnailRegenerationRequest.fromJson(Map<String, Object?> json) =>
      _$ThumbnailRegenerationRequestFromJson(json);
}
