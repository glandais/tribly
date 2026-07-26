// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'elevation_point_dto.freezed.dart';
part 'elevation_point_dto.g.dart';

/// One point of an elevation profile
@Freezed()
abstract class ElevationPointDto with _$ElevationPointDto {
  const factory ElevationPointDto({
    /// Cumulative distance from the start of the route, in meters
    required double distance,

    /// Elevation above sea level, in meters
    required double elevation,

    /// Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.
    required double grade,
  }) = _ElevationPointDto;

  factory ElevationPointDto.fromJson(Map<String, Object?> json) =>
      _$ElevationPointDtoFromJson(json);
}
