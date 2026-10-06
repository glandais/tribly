// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_weather_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideWeatherDto _$RideWeatherDtoFromJson(Map<String, dynamic> json) =>
    _RideWeatherDto(
      status: json['status'] as String,
      departure: DepartureWeatherDto.fromJson(
        json['departure'] as Map<String, dynamic>,
      ),
      legs: (json['legs'] as List<dynamic>)
          .map((e) => WeatherLegDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      attribution: WeatherAttributionDto.fromJson(
        json['attribution'] as Map<String, dynamic>,
      ),
      availableFrom: json['availableFrom'] as String?,
      fetchedAt: json['fetchedAt'] as String?,
    );

Map<String, dynamic> _$RideWeatherDtoToJson(_RideWeatherDto instance) =>
    <String, dynamic>{
      'status': instance.status,
      'departure': instance.departure.toJson(),
      'legs': instance.legs.map((e) => e.toJson()).toList(),
      'attribution': instance.attribution.toJson(),
      'availableFrom': instance.availableFrom,
      'fetchedAt': instance.fetchedAt,
    };
