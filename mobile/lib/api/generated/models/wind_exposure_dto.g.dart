// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'wind_exposure_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WindExposureDto _$WindExposureDtoFromJson(Map<String, dynamic> json) =>
    _WindExposureDto(
      head: (json['head'] as num).toDouble(),
      cross: (json['cross'] as num).toDouble(),
      tail: (json['tail'] as num).toDouble(),
    );

Map<String, dynamic> _$WindExposureDtoToJson(_WindExposureDto instance) =>
    <String, dynamic>{
      'head': instance.head,
      'cross': instance.cross,
      'tail': instance.tail,
    };
