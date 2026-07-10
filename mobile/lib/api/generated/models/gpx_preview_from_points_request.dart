// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_point.dart';

part 'gpx_preview_from_points_request.freezed.dart';
part 'gpx_preview_from_points_request.g.dart';

/// GPX preview creation request from planner points
@Freezed()
abstract class GpxPreviewFromPointsRequest with _$GpxPreviewFromPointsRequest {
  const factory GpxPreviewFromPointsRequest({
    /// Preview name
    required String name,

    /// Points from frontend routing
    required List<GeoPoint> points,
  }) = _GpxPreviewFromPointsRequest;

  factory GpxPreviewFromPointsRequest.fromJson(Map<String, Object?> json) =>
      _$GpxPreviewFromPointsRequestFromJson(json);
}
