// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'climb_category.dart';

part 'climb_dto.freezed.dart';
part 'climb_dto.g.dart';

/// Climb segment information
@Freezed()
abstract class ClimbDto with _$ClimbDto {
  const factory ClimbDto({
    /// Start distance from route start in meters
    required int startDistance,

    /// End distance from route start in meters
    required int endDistance,

    /// Elevation gain in meters
    required int elevationGain,

    /// Average gradient percentage
    required num averageGradient,

    /// Maximum gradient percentage
    required num maxGradient,

    /// Climb category (HC, 1, 2, 3, 4)
    String? category,
  }) = _ClimbDto;

  factory ClimbDto.fromJson(Map<String, Object?> json) =>
      _$ClimbDtoFromJson(json);
}
