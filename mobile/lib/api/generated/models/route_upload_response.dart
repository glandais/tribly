// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'route_upload_response.freezed.dart';
part 'route_upload_response.g.dart';

/// Result of uploading a route to a GPS service
@Freezed()
abstract class RouteUploadResponse with _$RouteUploadResponse {
  const factory RouteUploadResponse({
    /// Whether the upload was successful
    required bool success,

    /// Error message if upload failed
    String? message,

    /// External route ID on the GPS service
    String? externalRouteId,
  }) = _RouteUploadResponse;

  factory RouteUploadResponse.fromJson(Map<String, Object?> json) =>
      _$RouteUploadResponseFromJson(json);
}
