// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'route_usage_dto.dart';

part 'route_usages_response.freezed.dart';
part 'route_usages_response.g.dart';

/// Rides and trips that use a route
@Freezed()
abstract class RouteUsagesResponse with _$RouteUsagesResponse {
  const factory RouteUsagesResponse({
    /// Usages of the route, most recent first
    required List<RouteUsageDto> usages,
  }) = _RouteUsagesResponse;

  factory RouteUsagesResponse.fromJson(Map<String, Object?> json) =>
      _$RouteUsagesResponseFromJson(json);
}
