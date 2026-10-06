// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_weather_summary_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideWeatherSummaryDto _$RideWeatherSummaryDtoFromJson(
  Map<String, dynamic> json,
) => _RideWeatherSummaryDto(
  status: json['status'] as String,
  availableFrom: json['availableFrom'] as String?,
  weatherCode: (json['weatherCode'] as num?)?.toInt(),
  condition: json['condition'] as String?,
  daylight: json['daylight'] as bool?,
  temperature: (json['temperature'] as num?)?.toDouble(),
  temperatureMin: (json['temperatureMin'] as num?)?.toDouble(),
  temperatureMax: (json['temperatureMax'] as num?)?.toDouble(),
  maxPrecipitationProbability: (json['maxPrecipitationProbability'] as num?)
      ?.toInt(),
  wind: json['wind'] == null
      ? null
      : WindDto.fromJson(json['wind'] as Map<String, dynamic>),
  rainAlert: json['rainAlert'] == null
      ? null
      : WeatherRainAlertDto.fromJson(json['rainAlert'] as Map<String, dynamic>),
  timezone: json['timezone'] as String?,
);

Map<String, dynamic> _$RideWeatherSummaryDtoToJson(
  _RideWeatherSummaryDto instance,
) => <String, dynamic>{
  'status': instance.status,
  'availableFrom': instance.availableFrom,
  'weatherCode': instance.weatherCode,
  'condition': instance.condition,
  'daylight': instance.daylight,
  'temperature': instance.temperature,
  'temperatureMin': instance.temperatureMin,
  'temperatureMax': instance.temperatureMax,
  'maxPrecipitationProbability': instance.maxPrecipitationProbability,
  'wind': instance.wind?.toJson(),
  'rainAlert': instance.rainAlert?.toJson(),
  'timezone': instance.timezone,
};
