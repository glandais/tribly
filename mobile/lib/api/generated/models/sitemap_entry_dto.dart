// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'sitemap_entry_type.dart';

part 'sitemap_entry_dto.freezed.dart';
part 'sitemap_entry_dto.g.dart';

/// One indexable page. The API returns slugs, not URLs: the path is localised and belongs to the client's route table.
@Freezed()
abstract class SitemapEntryDto with _$SitemapEntryDto {
  const factory SitemapEntryDto({
    /// Which page this is
    required String type,

    /// Slug of the team the page belongs to
    required String teamSlug,

    /// Last modification of the page's content
    required String lastModified,

    /// Slug of the trip a TRIP_STAGE belongs to; null for every other type
    String? tripSlug,

    /// Slug of the page itself; null for TEAM and TEAM_ABOUT
    String? slug,
  }) = _SitemapEntryDto;

  factory SitemapEntryDto.fromJson(Map<String, Object?> json) =>
      _$SitemapEntryDtoFromJson(json);
}
