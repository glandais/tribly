// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'device_route_dto.dart';

part 'device_routes_response.freezed.dart';
part 'device_routes_response.g.dart';

/// Response containing routes for device applications
@Freezed()
abstract class DeviceRoutesResponse with _$DeviceRoutesResponse {
  const factory DeviceRoutesResponse({
    /// List of routes
    required List<DeviceRouteDto> routes,
  }) = _DeviceRoutesResponse;

  factory DeviceRoutesResponse.fromJson(Map<String, Object?> json) =>
      _$DeviceRoutesResponseFromJson(json);
}
