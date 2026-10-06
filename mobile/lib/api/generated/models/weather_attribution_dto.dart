// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'weather_attribution_dto.freezed.dart';
part 'weather_attribution_dto.g.dart';

/// Who the forecast comes from. Its licence (CC BY 4.0) asks every display to credit it: show the name, linked to the url.
@Freezed()
abstract class WeatherAttributionDto with _$WeatherAttributionDto {
  const factory WeatherAttributionDto({
    /// Name to display, e.g. "Open-Meteo.com"
    required String name,

    /// Link of the credit
    required String url,
  }) = _WeatherAttributionDto;

  factory WeatherAttributionDto.fromJson(Map<String, Object?> json) =>
      _$WeatherAttributionDtoFromJson(json);
}
