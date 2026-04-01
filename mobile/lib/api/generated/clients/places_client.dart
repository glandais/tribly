// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/place_detail_dto.dart';
import '../models/place_list_response.dart';
import '../models/place_request.dart';

part 'places_client.g.dart';

@RestApi()
abstract class PlacesClient {
  factory PlacesClient(Dio dio, {String? baseUrl}) = _PlacesClient;

  /// List places.
  ///
  /// Get all places for a team.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [filterEnd] - Filter to end places only.
  ///
  /// [filterStart] - Filter to start places only.
  ///
  /// [page] - Page number.
  ///
  /// [search] - Search by name or address.
  ///
  /// [size] - Page size.
  @GET('/api/teams/{teamSlug}/places')
  Future<PlaceListResponse> listPlaces({
    @Path('teamSlug') required String teamSlug,
    @Query('search') String? search,
    @Query('filterEnd') bool? filterEnd = false,
    @Query('filterStart') bool? filterStart = false,
    @Query('page') int? page = 0,
    @Query('size') int? size = 50,
  });

  /// Create place.
  ///
  /// Create a new place for the team.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/places')
  Future<PlaceDetailDto> createPlace({
    @Path('teamSlug') required String teamSlug,
    @Body() required PlaceRequest body,
  });

  /// Update place.
  ///
  /// Update an existing place.
  ///
  /// [placeId] - Place ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/places/{placeId}')
  Future<PlaceDetailDto> updatePlace({
    @Path('placeId') required String placeId,
    @Path('teamSlug') required String teamSlug,
    @Body() required PlaceRequest body,
  });

  /// Get place details.
  ///
  /// Get a specific place by ID.
  ///
  /// [placeId] - Place ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/places/{placeId}')
  Future<PlaceDetailDto> getPlace({
    @Path('placeId') required String placeId,
    @Path('teamSlug') required String teamSlug,
  });

  /// Delete place.
  ///
  /// Soft delete a place.
  ///
  /// [placeId] - Place ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/places/{placeId}')
  Future<void> deletePlace({
    @Path('placeId') required String placeId,
    @Path('teamSlug') required String teamSlug,
  });
}
