// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'weather_condition.dart';

part 'weather_rain_alert_dto.freezed.dart';
part 'weather_rain_alert_dto.g.dart';

/// The first moment rain becomes likely: a probability of 50 % or more
@Freezed()
abstract class WeatherRainAlertDto with _$WeatherRainAlertDto {
  const factory WeatherRainAlertDto({
    /// Probability of precipitation then, % (0–100)
    required int probability,

    /// When — the passage at the checkpoint, or the hour
    required String time,

    /// The condition forecast then
    required String condition,

    /// Where, metres from the start of the leg. Absent in a ride's summary, which only knows the departure point.
    double? distance,
  }) = _WeatherRainAlertDto;

  factory WeatherRainAlertDto.fromJson(Map<String, Object?> json) =>
      _$WeatherRainAlertDtoFromJson(json);
}
