// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'wind_exposure_dto.freezed.dart';
part 'wind_exposure_dto.g.dart';

/// How much of a leg rides against, across and with the wind, metres. All zero when no stretch has weather.
@Freezed()
abstract class WindExposureDto with _$WindExposureDto {
  const factory WindExposureDto({
    /// Metres with a HEAD wind
    required double head,

    /// Metres with a CROSS wind
    required double cross,

    /// Metres with a TAIL wind
    required double tail,
  }) = _WindExposureDto;

  factory WindExposureDto.fromJson(Map<String, Object?> json) =>
      _$WindExposureDtoFromJson(json);
}
