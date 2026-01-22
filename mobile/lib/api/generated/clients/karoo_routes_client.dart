// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/karoo_routes_response.dart';
import '../models/route_upload_response.dart';

part 'karoo_routes_client.g.dart';

@RestApi()
abstract class KarooRoutesClient {
  factory KarooRoutesClient(Dio dio, {String? baseUrl}) = _KarooRoutesClient;

  /// List routes.
  ///
  /// Get routes from user's teams, prioritizing upcoming rides.
  ///
  /// [lat] - User's latitude for proximity sorting.
  ///
  /// [lon] - User's longitude for proximity sorting.
  @GET('/api/karoo/routes')
  Future<KarooRoutesResponse> karooListRoutes({
    @Query('lat') double? lat,
    @Query('lon') double? lon,
  });

  /// Sync route to Hammerhead.
  ///
  /// Upload route GPX to user's Hammerhead cloud account.
  ///
  /// [routeSlug] - Route slug.
  ///
  /// [teamSlug] - Team slug.
  @POST('/api/karoo/routes/{teamSlug}/{routeSlug}/sync')
  Future<RouteUploadResponse> syncRoute({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
  });
}
