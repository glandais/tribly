// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

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

    /// When this entry leaves, as an absolute instant (UTC): the group's time read at the ride's departure point local time, on the ride's local date; the ride's own startDateTime for the ride-level route and for a group without a time. Devices render it in their own zone.
    required String startDateTime,

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
