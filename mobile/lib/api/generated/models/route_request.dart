// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_point.dart';
import 'media_dto.dart';
import 'surface_type.dart';
import 'visibility.dart';

part 'route_request.freezed.dart';
part 'route_request.g.dart';

/// Route update request
@Freezed()
abstract class RouteRequest with _$RouteRequest {
  const factory RouteRequest({
    /// Route name
    required String name,

    /// Media
    required MediaDto media,

    /// Surface type
    required String surfaceType,

    /// Whether the route is publicly visible
    required String visibility,

    /// Points from frontend routing
    List<GeoPoint>? points,
  }) = _RouteRequest;

  factory RouteRequest.fromJson(Map<String, Object?> json) =>
      _$RouteRequestFromJson(json);
}
