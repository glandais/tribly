// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/count_response.dart';
import '../models/list_view_mode.dart';
import '../models/min_role.dart';
import '../models/publication_list_response.dart';
import '../models/publication_type.dart';
import '../models/status.dart';

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
  /// [participating] - Only publications the current user is registered to (rides and trips). Yields nothing for an anonymous visitor.
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [size] - Page size.
  ///
  /// [status] - Only publications with this status. Narrows the visibility rules, never widens them.
  ///
  /// [to] - End date filter (ISO format).
  ///
  /// [type] - Types.
  ///
  /// [view] - How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets empty — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. Omitted, or FULL, is the previous behaviour, byte for byte.
  @GET('/api/publications')
  Future<PublicationListResponse> listAllPublications({
    @Query('page') int? page = 0,
    @Query('participating') bool? participating = false,
    @Query('size') int? size = 20,
    @Query('from') String? from,
    @Query('minRole') MinRole? minRole,
    @Query('search') String? search,
    @Query('status') Status? status,
    @Query('to') String? to,
    @Query('type') PublicationType? type,
    @Query('view') ListViewMode? view,
  });

  /// Count all publications.
  ///
  /// How many publications match the filters, with none of them read. Accepts exactly the same filters as the listing, minus pagination, so a count and the list it opens can never disagree.
  ///
  /// [from] - Start date filter (ISO format).
  ///
  /// [minRole] - Only publications from teams where the user has at least this role. Yields zero for an anonymous visitor.
  ///
  /// [participating] - Only publications the current user is registered to (rides and trips). Yields zero for an anonymous visitor.
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [status] - Only publications with this status. Narrows the visibility rules, never widens them.
  ///
  /// [to] - End date filter (ISO format).
  ///
  /// [type] - Types.
  @GET('/api/publications/count')
  Future<CountResponse> countAllPublications({
    @Query('participating') bool? participating = false,
    @Query('from') String? from,
    @Query('minRole') MinRole? minRole,
    @Query('search') String? search,
    @Query('status') Status? status,
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
  /// [participating] - Only publications the current user is registered to (rides and trips). Yields nothing for an anonymous visitor.
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [size] - Page size.
  ///
  /// [status] - Only publications with this status. Narrows the visibility rules, never widens them.
  ///
  /// [to] - End date filter (ISO format).
  ///
  /// [type] - Type.
  ///
  /// [view] - How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets empty — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. Omitted, or FULL, is the previous behaviour, byte for byte.
  @GET('/api/teams/{teamSlug}/publications')
  Future<PublicationListResponse> listPublications({
    @Path('teamSlug') required String teamSlug,
    @Query('page') int? page = 0,
    @Query('participating') bool? participating = false,
    @Query('size') int? size = 20,
    @Query('from') String? from,
    @Query('search') String? search,
    @Query('status') Status? status,
    @Query('to') String? to,
    @Query('type') PublicationType? type,
    @Query('view') ListViewMode? view,
  });

  /// Count publications.
  ///
  /// How many of the team's publications match the filters, with none of them read. Accepts exactly the same filters as the listing, minus pagination, so a count and the list it opens can never disagree.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [from] - Start date filter (ISO format).
  ///
  /// [participating] - Only publications the current user is registered to (rides and trips). Yields zero for an anonymous visitor.
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [status] - Only publications with this status. Narrows the visibility rules, never widens them.
  ///
  /// [to] - End date filter (ISO format).
  ///
  /// [type] - Type.
  @GET('/api/teams/{teamSlug}/publications/count')
  Future<CountResponse> countPublications({
    @Path('teamSlug') required String teamSlug,
    @Query('participating') bool? participating = false,
    @Query('from') String? from,
    @Query('search') String? search,
    @Query('status') Status? status,
    @Query('to') String? to,
    @Query('type') PublicationType? type,
  });
}
