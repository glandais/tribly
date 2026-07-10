// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'bounds_dto.dart';

part 'route_bounds_response.freezed.dart';
part 'route_bounds_response.g.dart';

/// Bounding box of the routes matching a filter set
@Freezed()
abstract class RouteBoundsResponse with _$RouteBoundsResponse {
  const factory RouteBoundsResponse({
    /// Bounding box, or null when no route matches
    BoundsDto? bounds,
  }) = _RouteBoundsResponse;

  factory RouteBoundsResponse.fromJson(Map<String, Object?> json) =>
      _$RouteBoundsResponseFromJson(json);
}
