// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'geocode_result_dto.freezed.dart';
part 'geocode_result_dto.g.dart';

/// A place matching a geocoding query
@Freezed()
abstract class GeocodeResultDto with _$GeocodeResultDto {
  const factory GeocodeResultDto({
    /// Opaque identifier of the result, stable enough to key a list on
    required String id,

    /// Full human-readable name of the place
    required String displayName,

    /// Latitude in degrees (WGS 84)
    required double lat,

    /// Longitude in degrees (WGS 84)
    required double lon,
  }) = _GeocodeResultDto;

  factory GeocodeResultDto.fromJson(Map<String, Object?> json) =>
      _$GeocodeResultDtoFromJson(json);
}
