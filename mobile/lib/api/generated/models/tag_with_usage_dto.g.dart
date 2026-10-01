// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'tag_with_usage_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TagWithUsageDto _$TagWithUsageDtoFromJson(Map<String, dynamic> json) =>
    _TagWithUsageDto(
      id: json['id'] as String,
      label: json['label'] as String,
      color: json['color'] as String,
      type: json['type'] as String,
      usageCount: (json['usageCount'] as num).toInt(),
    );

Map<String, dynamic> _$TagWithUsageDtoToJson(_TagWithUsageDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'label': instance.label,
      'color': instance.color,
      'type': instance.type,
      'usageCount': instance.usageCount,
    };
