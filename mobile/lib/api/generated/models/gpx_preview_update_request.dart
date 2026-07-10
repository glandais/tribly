// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_point.dart';

part 'gpx_preview_update_request.freezed.dart';
part 'gpx_preview_update_request.g.dart';

/// GPX preview update request
@Freezed()
abstract class GpxPreviewUpdateRequest with _$GpxPreviewUpdateRequest {
  const factory GpxPreviewUpdateRequest({
    /// Preview name
    required String name,

    /// Points from frontend routing
    List<GeoPoint>? points,
  }) = _GpxPreviewUpdateRequest;

  factory GpxPreviewUpdateRequest.fromJson(Map<String, Object?> json) =>
      _$GpxPreviewUpdateRequestFromJson(json);
}
