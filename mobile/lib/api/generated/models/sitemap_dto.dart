// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'sitemap_entry_dto.dart';

part 'sitemap_dto.freezed.dart';
part 'sitemap_dto.g.dart';

/// Every page of the site a search engine may index: the public content of public teams, without classified ads. Capped at 50,000 entries, the sitemap protocol's limit; newest first within each type.
@Freezed()
abstract class SitemapDto with _$SitemapDto {
  const factory SitemapDto({
    /// Indexable pages
    required List<SitemapEntryDto> entries,
  }) = _SitemapDto;

  factory SitemapDto.fromJson(Map<String, Object?> json) =>
      _$SitemapDtoFromJson(json);
}
