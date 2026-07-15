// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'climb_part_dto.freezed.dart';
part 'climb_part_dto.g.dart';

/// Climb part information
@Freezed()
abstract class ClimbPartDto with _$ClimbPartDto {
  const factory ClimbPartDto({
    /// Start distance from route start in meters
    required int startDistance,

    /// End distance from route start in meters
    required int endDistance,

    /// Elevation gain in meters
    required int elevationGain,

    /// Gradient percentage
    required num grade,
  }) = _ClimbPartDto;

  factory ClimbPartDto.fromJson(Map<String, Object?> json) =>
      _$ClimbPartDtoFromJson(json);
}
