// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'climb_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ClimbDto _$ClimbDtoFromJson(Map<String, dynamic> json) => _ClimbDto(
  startDistance: (json['startDistance'] as num).toInt(),
  endDistance: (json['endDistance'] as num).toInt(),
  elevationGain: (json['elevationGain'] as num).toInt(),
  averageGradient: json['averageGradient'] as num,
  maxGradient: json['maxGradient'] as num,
  parts: (json['parts'] as List<dynamic>)
      .map((e) => ClimbPartDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  category: json['category'] as String?,
);

Map<String, dynamic> _$ClimbDtoToJson(_ClimbDto instance) => <String, dynamic>{
  'startDistance': instance.startDistance,
  'endDistance': instance.endDistance,
  'elevationGain': instance.elevationGain,
  'averageGradient': instance.averageGradient,
  'maxGradient': instance.maxGradient,
  'parts': instance.parts.map((e) => e.toJson()).toList(),
  'category': instance.category,
};
