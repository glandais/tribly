// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'thumbnail_file.dart';
import 'thumbnail_owner_kind.dart';
import 'thumbnail_regeneration_outcome.dart';
import 'thumbnail_regeneration_reason.dart';

part 'thumbnail_owner_report.freezed.dart';
part 'thumbnail_owner_report.g.dart';

/// A route, ride or trip whose thumbnails were (or would be) redrawn
@Freezed()
abstract class ThumbnailOwnerReport with _$ThumbnailOwnerReport {
  const factory ThumbnailOwnerReport({
    /// Entity ID
    required String id,

    /// Entity kind
    required String kind,

    /// Team slug
    required String teamSlug,

    /// Entity slug
    required String slug,

    /// Why it was selected
    required List<ThumbnailRegenerationReason> reasons,

    /// Its thumbnails before
    required List<ThumbnailFile> before,

    /// What happened
    required String outcome,

    /// Its thumbnails after, absent on a dry run
    List<ThumbnailFile>? after,

    /// Error message when the regeneration itself failed
    String? error,
  }) = _ThumbnailOwnerReport;

  factory ThumbnailOwnerReport.fromJson(Map<String, Object?> json) =>
      _$ThumbnailOwnerReportFromJson(json);
}
