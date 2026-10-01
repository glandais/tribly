// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'tag_create_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TagCreateRequest _$TagCreateRequestFromJson(Map<String, dynamic> json) =>
    _TagCreateRequest(
      type: json['type'] as String,
      label: json['label'] as String,
      color: json['color'] as String,
    );

Map<String, dynamic> _$TagCreateRequestToJson(_TagCreateRequest instance) =>
    <String, dynamic>{
      'type': instance.type,
      'label': instance.label,
      'color': instance.color,
    };
