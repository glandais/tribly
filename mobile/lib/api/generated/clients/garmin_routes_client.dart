// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/garmin_routes_response.dart';

part 'garmin_routes_client.g.dart';

@RestApi()
abstract class GarminRoutesClient {
  factory GarminRoutesClient(Dio dio, {String? baseUrl}) = _GarminRoutesClient;

  /// List routes for user.
  ///
  /// Get routes for authenticated user. Prioritizes routes from upcoming rides, then latest routes from user's teams.
  ///
  /// [lat] - User's current latitude (for proximity sorting).
  ///
  /// [lon] - User's current longitude (for proximity sorting).
  @GET('/api/garmin/routes')
  Future<GarminRoutesResponse> getRoutes({
    @Query('lat') double? lat,
    @Query('lon') double? lon,
  });

  /// Download FIT file.
  ///
  /// Download route as FIT file for Garmin GPS.
  ///
  /// [routeSlug] - Route URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/garmin/routes/{teamSlug}/{routeSlug}/fit')
  Future<void> downloadFit({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
  });
}
