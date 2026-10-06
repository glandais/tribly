// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'departure_weather_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DepartureWeatherDto _$DepartureWeatherDtoFromJson(Map<String, dynamic> json) =>
    _DepartureWeatherDto(
      status: json['status'] as String,
      conditions: json['conditions'] == null
          ? null
          : WeatherConditionsDto.fromJson(
              json['conditions'] as Map<String, dynamic>,
            ),
      sunrise: json['sunrise'] as String?,
      sunset: json['sunset'] as String?,
      fetchedAt: json['fetchedAt'] as String?,
    );

Map<String, dynamic> _$DepartureWeatherDtoToJson(
  _DepartureWeatherDto instance,
) => <String, dynamic>{
  'status': instance.status,
  'conditions': instance.conditions?.toJson(),
  'sunrise': instance.sunrise,
  'sunset': instance.sunset,
  'fetchedAt': instance.fetchedAt,
};
