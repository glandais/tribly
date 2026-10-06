// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'trip_weather_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TripWeatherDto _$TripWeatherDtoFromJson(Map<String, dynamic> json) =>
    _TripWeatherDto(
      status: json['status'] as String,
      stages: (json['stages'] as List<dynamic>)
          .map((e) => TripStageWeatherDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      attribution: WeatherAttributionDto.fromJson(
        json['attribution'] as Map<String, dynamic>,
      ),
      availableFrom: json['availableFrom'] as String?,
      fetchedAt: json['fetchedAt'] as String?,
    );

Map<String, dynamic> _$TripWeatherDtoToJson(_TripWeatherDto instance) =>
    <String, dynamic>{
      'status': instance.status,
      'stages': instance.stages.map((e) => e.toJson()).toList(),
      'attribution': instance.attribution.toJson(),
      'availableFrom': instance.availableFrom,
      'fetchedAt': instance.fetchedAt,
    };
