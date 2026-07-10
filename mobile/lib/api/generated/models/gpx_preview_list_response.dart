// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'gpx_preview_summary_dto.dart';

part 'gpx_preview_list_response.freezed.dart';
part 'gpx_preview_list_response.g.dart';

/// Paginated GPX preview list response
@Freezed()
abstract class GpxPreviewListResponse with _$GpxPreviewListResponse {
  const factory GpxPreviewListResponse({
    /// List of GPX previews
    required List<GpxPreviewSummaryDto> previews,

    /// Total number of previews
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _GpxPreviewListResponse;

  factory GpxPreviewListResponse.fromJson(Map<String, Object?> json) =>
      _$GpxPreviewListResponseFromJson(json);
}
