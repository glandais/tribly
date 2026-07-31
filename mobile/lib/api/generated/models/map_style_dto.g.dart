// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'map_style_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MapStyleDto _$MapStyleDtoFromJson(Map<String, dynamic> json) => _MapStyleDto(
  id: json['id'] as String,
  label: json['label'] as String,
  group: json['group'] as String,
  url: json['url'] as String,
  darkVariant: json['darkVariant'] as String?,
);

Map<String, dynamic> _$MapStyleDtoToJson(_MapStyleDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'label': instance.label,
      'group': instance.group,
      'url': instance.url,
      'darkVariant': instance.darkVariant,
    };
