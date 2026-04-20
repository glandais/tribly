// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/ad_dto.dart';
import '../models/ad_edit_dto.dart';
import '../models/ad_list_response.dart';
import '../models/ad_request.dart';
import '../models/ad_type.dart';
import '../models/slug_change_request.dart';

part 'ads_client.g.dart';

@RestApi()
abstract class AdsClient {
  factory AdsClient(Dio dio, {String? baseUrl}) = _AdsClient;

  /// List ads.
  ///
  /// Get paginated list of ads for a team with optional filtering.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [adType] - Filter by ad type.
  ///
  /// [from] - Start date filter (ISO format).
  ///
  /// [page] - Page number.
  ///
  /// [search] - Search by name/description.
  ///
  /// [size] - Page size.
  ///
  /// [to] - End date filter (ISO format).
  @GET('/api/teams/{teamSlug}/classifieds')
  Future<AdListResponse> listAds({
    @Path('teamSlug') required String teamSlug,
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('adType') AdType? adType,
    @Query('from') String? from,
    @Query('search') String? search,
    @Query('to') String? to,
  });

  /// Create ad.
  ///
  /// Create a new ad. Any team member can create ads.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/classifieds')
  Future<AdDto> createAd({
    @Path('teamSlug') required String teamSlug,
    @Body() required AdRequest body,
  });

  /// Update ad.
  ///
  /// Update ad information. Only the creator or an admin can update.
  ///
  /// [slug] - Ad URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/classifieds/{slug}')
  Future<AdDto> updateAd({
    @Path('slug') required String slug,
    @Path('teamSlug') required String teamSlug,
    @Body() required AdRequest body,
  });

  /// Get ad details.
  ///
  /// Get detailed ad information.
  ///
  /// [slug] - Ad URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/classifieds/{slug}')
  Future<AdDto> getAd({
    @Path('slug') required String slug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Delete ad.
  ///
  /// Soft delete an ad. Only the creator or an admin can delete.
  ///
  /// [slug] - Ad URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/classifieds/{slug}')
  Future<void> deleteAd({
    @Path('slug') required String slug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Get ad details for edit.
  ///
  /// Get detailed ad information.
  ///
  /// [slug] - Ad URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/classifieds/{slug}/edit')
  Future<AdEditDto> getAdEdit({
    @Path('slug') required String slug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Change ad slug.
  ///
  /// Change ad URL slug. Requires admin permissions.
  ///
  /// [slug] - Current ad URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/classifieds/{slug}/slug')
  Future<AdDto> changeAdSlug({
    @Path('slug') required String slug,
    @Path('teamSlug') required String teamSlug,
    @Body() required SlugChangeRequest body,
  });

  /// Restore ad.
  ///
  /// Restore a soft-deleted ad. Only the creator or an admin can restore.
  ///
  /// [slug] - Ad URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/classifieds/{slug}/undelete')
  Future<AdEditDto> undeleteAd({
    @Path('slug') required String slug,
    @Path('teamSlug') required String teamSlug,
  });
}
