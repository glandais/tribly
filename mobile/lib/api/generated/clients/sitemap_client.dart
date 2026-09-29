// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/sitemap_dto.dart';

part 'sitemap_client.g.dart';

@RestApi()
abstract class SitemapClient {
  factory SitemapClient(Dio dio, {String? baseUrl}) = _SitemapClient;

  /// Get the indexable pages of this site.
  ///
  /// Public teams and their public content, as an anonymous visitor sees it, for the site the request arrived on (a pinned host lists its one team). Classified ads and routes are never listed. Anonymous by construction: the caller's session does not widen it.
  @GET('/api/sitemap')
  Future<SitemapDto> getSitemap();
}
