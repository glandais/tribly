// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/min_role.dart';
import '../models/publication_list_response.dart';
import '../models/publication_type.dart';

part 'publications_client.g.dart';

@RestApi()
abstract class PublicationsClient {
  factory PublicationsClient(Dio dio, {String? baseUrl}) = _PublicationsClient;

  /// List all publications.
  ///
  /// Get publications from all accessible teams (user's teams + public teams).
  ///
  /// [from] - Start date filter (ISO format).
  ///
  /// [minRole] - Only publications from teams where the user has at least this role. Yields nothing for an anonymous visitor.
  ///
  /// [page] - Page number.
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [size] - Page size.
  ///
  /// [to] - End date filter (ISO format).
  ///
  /// [type] - Types.
  @GET('/api/publications')
  Future<PublicationListResponse> listAllPublications({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('from') String? from,
    @Query('minRole') MinRole? minRole,
    @Query('search') String? search,
    @Query('to') String? to,
    @Query('type') PublicationType? type,
  });

  /// List publications.
  ///
  /// Get paginated list of publications for a team with optional filtering.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [from] - Start date filter (ISO format).
  ///
  /// [page] - Page number.
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [size] - Page size.
  ///
  /// [to] - End date filter (ISO format).
  ///
  /// [type] - Type.
  @GET('/api/teams/{teamSlug}/publications')
  Future<PublicationListResponse> listPublications({
    @Path('teamSlug') required String teamSlug,
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('from') String? from,
    @Query('search') String? search,
    @Query('to') String? to,
    @Query('type') PublicationType? type,
  });
}
