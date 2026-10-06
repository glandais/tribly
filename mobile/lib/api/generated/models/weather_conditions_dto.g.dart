// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'weather_conditions_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WeatherConditionsDto _$WeatherConditionsDtoFromJson(
  Map<String, dynamic> json,
) => _WeatherConditionsDto(
  time: json['time'] as String,
  weatherCode: (json['weatherCode'] as num).toInt(),
  condition: json['condition'] as String,
  daylight: json['daylight'] as bool,
  temperature: (json['temperature'] as num).toDouble(),
  apparentTemperature: (json['apparentTemperature'] as num).toDouble(),
  precipitation: (json['precipitation'] as num).toDouble(),
  wind: WindDto.fromJson(json['wind'] as Map<String, dynamic>),
  precipitationProbability: (json['precipitationProbability'] as num?)?.toInt(),
);

Map<String, dynamic> _$WeatherConditionsDtoToJson(
  _WeatherConditionsDto instance,
) => <String, dynamic>{
  'time': instance.time,
  'weatherCode': instance.weatherCode,
  'condition': instance.condition,
  'daylight': instance.daylight,
  'temperature': instance.temperature,
  'apparentTemperature': instance.apparentTemperature,
  'precipitation': instance.precipitation,
  'wind': instance.wind.toJson(),
  'precipitationProbability': instance.precipitationProbability,
};
