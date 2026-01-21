// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'garmin_route_dto.freezed.dart';
part 'garmin_route_dto.g.dart';

/// Route information for Garmin device
@Freezed()
abstract class GarminRouteDto with _$GarminRouteDto {
  const factory GarminRouteDto({
    /// Team slug
    required String teamSlug,

    /// Route slug
    required String routeSlug,

    /// Route name
    required String name,

    /// Distance in meters
    required double distance,

    /// Elevation gain in meters
    required double elevationGain,

    /// Label (e.g., 'Rapides - Sam 18 Jan 09:00')
    String? label,

    /// Ride date/time if from a ride
    String? rideDateTime,

    /// Start latitude
    double? startLat,

    /// Start longitude
    double? startLon,

    /// Distance from user in meters (if GPS provided)
    double? distanceFromUser,
  }) = _GarminRouteDto;

  factory GarminRouteDto.fromJson(Map<String, Object?> json) =>
      _$GarminRouteDtoFromJson(json);
}
