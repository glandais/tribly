// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_template_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideTemplateListResponse _$RideTemplateListResponseFromJson(
  Map<String, dynamic> json,
) => _RideTemplateListResponse(
  templates: (json['templates'] as List<dynamic>)
      .map((e) => RideTemplateDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$RideTemplateListResponseToJson(
  _RideTemplateListResponse instance,
) => <String, dynamic>{
  'templates': instance.templates.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
