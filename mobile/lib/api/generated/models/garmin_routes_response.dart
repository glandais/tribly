// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'garmin_route_dto.dart';

part 'garmin_routes_response.freezed.dart';
part 'garmin_routes_response.g.dart';

/// List of routes for Garmin device
@Freezed()
abstract class GarminRoutesResponse with _$GarminRoutesResponse {
  const factory GarminRoutesResponse({
    /// Routes list
    required List<GarminRouteDto> routes,
  }) = _GarminRoutesResponse;

  factory GarminRoutesResponse.fromJson(Map<String, Object?> json) =>
      _$GarminRoutesResponseFromJson(json);
}
