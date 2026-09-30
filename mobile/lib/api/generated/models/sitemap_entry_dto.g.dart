// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sitemap_entry_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_SitemapEntryDto _$SitemapEntryDtoFromJson(Map<String, dynamic> json) =>
    _SitemapEntryDto(
      type: json['type'] as String,
      teamSlug: json['teamSlug'] as String,
      lastModified: json['lastModified'] as String,
      tripSlug: json['tripSlug'] as String?,
      slug: json['slug'] as String?,
    );

Map<String, dynamic> _$SitemapEntryDtoToJson(_SitemapEntryDto instance) =>
    <String, dynamic>{
      'type': instance.type,
      'teamSlug': instance.teamSlug,
      'lastModified': instance.lastModified,
      'tripSlug': instance.tripSlug,
      'slug': instance.slug,
    };
