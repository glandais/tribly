// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sitemap_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_SitemapDto _$SitemapDtoFromJson(Map<String, dynamic> json) => _SitemapDto(
  entries: (json['entries'] as List<dynamic>)
      .map((e) => SitemapEntryDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$SitemapDtoToJson(_SitemapDto instance) =>
    <String, dynamic>{
      'entries': instance.entries.map((e) => e.toJson()).toList(),
    };
