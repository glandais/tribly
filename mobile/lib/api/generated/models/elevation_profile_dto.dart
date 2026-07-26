// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'elevation_point_dto.dart';

part 'elevation_profile_dto.freezed.dart';
part 'elevation_profile_dto.g.dart';

/// Sampled elevation profile of a route, ready to draw
@Freezed()
abstract class ElevationProfileDto with _$ElevationProfileDto {
  const factory ElevationProfileDto({
    /// Route ID (TSID)
    required String routeId,

    /// Route slug
    required String slug,

    /// Distance covered by the profile, in meters
    required double distance,

    /// Lowest elevation of the profile, in meters
    required double minElevation,

    /// Highest elevation of the profile, in meters
    required double maxElevation,

    /// Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.
    required int samples,

    /// Profile points, by increasing distance
    required List<ElevationPointDto> points,
  }) = _ElevationProfileDto;

  factory ElevationProfileDto.fromJson(Map<String, Object?> json) =>
      _$ElevationProfileDtoFromJson(json);
}
