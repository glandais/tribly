// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'climb_part_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ClimbPartDto _$ClimbPartDtoFromJson(Map<String, dynamic> json) =>
    _ClimbPartDto(
      startDistance: (json['startDistance'] as num).toInt(),
      endDistance: (json['endDistance'] as num).toInt(),
      elevationGain: (json['elevationGain'] as num).toInt(),
      grade: json['grade'] as num,
    );

Map<String, dynamic> _$ClimbPartDtoToJson(_ClimbPartDto instance) =>
    <String, dynamic>{
      'startDistance': instance.startDistance,
      'endDistance': instance.endDistance,
      'elevationGain': instance.elevationGain,
      'grade': instance.grade,
    };
