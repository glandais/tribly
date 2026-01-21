// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_page_summary_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamPageSummaryDto _$TeamPageSummaryDtoFromJson(Map<String, dynamic> json) =>
    _TeamPageSummaryDto(
      id: json['id'] as String,
      title: json['title'] as String,
      slug: json['slug'] as String,
      visibility: json['visibility'] as String,
      order: (json['order'] as num).toInt(),
    );

Map<String, dynamic> _$TeamPageSummaryDtoToJson(_TeamPageSummaryDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'slug': instance.slug,
      'visibility': instance.visibility,
      'order': instance.order,
    };
