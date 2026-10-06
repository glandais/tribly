// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'weather_rain_alert_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WeatherRainAlertDto _$WeatherRainAlertDtoFromJson(Map<String, dynamic> json) =>
    _WeatherRainAlertDto(
      probability: (json['probability'] as num).toInt(),
      time: json['time'] as String,
      condition: json['condition'] as String,
      distance: (json['distance'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$WeatherRainAlertDtoToJson(
  _WeatherRainAlertDto instance,
) => <String, dynamic>{
  'probability': instance.probability,
  'time': instance.time,
  'condition': instance.condition,
  'distance': instance.distance,
};
