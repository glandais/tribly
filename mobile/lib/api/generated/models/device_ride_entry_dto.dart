// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'device_ride_entry_dto.freezed.dart';
part 'device_ride_entry_dto.g.dart';

/// Route entry within a ride for device applications
@Freezed()
abstract class DeviceRideEntryDto with _$DeviceRideEntryDto {
  const factory DeviceRideEntryDto({
    /// Route slug
    required String routeSlug,

    /// Route name
    required String routeName,

    /// Distance in meters
    required double distance,

    /// Elevation gain in meters
    required double elevationGain,

    /// Group name (null for ride-level route)
    String? groupName,

    /// Start latitude
    double? startLat,

    /// Start longitude
    double? startLon,
  }) = _DeviceRideEntryDto;

  factory DeviceRideEntryDto.fromJson(Map<String, Object?> json) =>
      _$DeviceRideEntryDtoFromJson(json);
}
