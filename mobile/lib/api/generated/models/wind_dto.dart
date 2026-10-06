// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'compass_point.dart';

part 'wind_dto.freezed.dart';
part 'wind_dto.g.dart';

/// Wind at a place and an hour, 10 m above ground
@Freezed()
abstract class WindDto with _$WindDto {
  const factory WindDto({
    /// Mean wind speed, km/h
    required double speed,

    /// Direction the wind comes FROM, degrees clockwise from north (0 = from the north, 90 = from the east)
    required double direction,

    /// direction on the eight-point rose, still the direction the wind comes FROM
    required String compass,

    /// Gusts, km/h. Absent when the model gives none.
    double? gusts,
  }) = _WindDto;

  factory WindDto.fromJson(Map<String, Object?> json) =>
      _$WindDtoFromJson(json);
}
