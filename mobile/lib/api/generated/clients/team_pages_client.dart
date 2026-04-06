// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/reorder_pages_request.dart';
import '../models/slug_change_request.dart';
import '../models/team_page_dto.dart';
import '../models/team_page_request.dart';
import '../models/team_page_summary_dto.dart';

part 'team_pages_client.g.dart';

@RestApi()
abstract class TeamPagesClient {
  factory TeamPagesClient(Dio dio, {String? baseUrl}) = _TeamPagesClient;

  /// List team pages.
  ///
  /// Get list of additional pages for a team.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/pages')
  Future<List<TeamPageSummaryDto>> listPages({
    @Path('teamSlug') required String teamSlug,
  });

  /// Create page.
  ///
  /// Create a new team page. Requires admin permissions. Maximum 3 additional pages per team.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/pages')
  Future<TeamPageDto> createPage({
    @Path('teamSlug') required String teamSlug,
    @Body() required TeamPageRequest body,
  });

  /// Reorder pages.
  ///
  /// Reorder team pages. Requires admin permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/pages/reorder')
  Future<List<TeamPageSummaryDto>> reorderPages({
    @Path('teamSlug') required String teamSlug,
    @Body() required ReorderPagesRequest body,
  });

  /// Update page.
  ///
  /// Update page information. Requires admin permissions.
  ///
  /// [pageSlug] - Page URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/pages/{pageSlug}')
  Future<TeamPageDto> updatePage({
    @Path('pageSlug') required String pageSlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required TeamPageRequest body,
  });

  /// Get page details.
  ///
  /// Get detailed page information.
  ///
  /// [pageSlug] - Page URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/pages/{pageSlug}')
  Future<TeamPageDto> getPage({
    @Path('pageSlug') required String pageSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Delete page.
  ///
  /// Delete a team page. Requires admin permissions.
  ///
  /// [pageSlug] - Page URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/pages/{pageSlug}')
  Future<void> deletePage({
    @Path('pageSlug') required String pageSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Change page slug.
  ///
  /// Change page URL slug. Requires admin permissions.
  ///
  /// [pageSlug] - Current page URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/pages/{pageSlug}/slug')
  Future<TeamPageDto> changePageSlug({
    @Path('pageSlug') required String pageSlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required SlugChangeRequest body,
  });

  /// Restore page.
  ///
  /// Restore a soft-deleted team page. Requires admin permissions.
  ///
  /// [pageSlug] - Page URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/pages/{pageSlug}/undelete')
  Future<TeamPageDto> undeletePage({
    @Path('pageSlug') required String pageSlug,
    @Path('teamSlug') required String teamSlug,
  });
}
