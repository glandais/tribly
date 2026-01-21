// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/gps_o_auth_url_response.dart';
import '../models/gps_service_type.dart';
import '../models/route_upload_response.dart';

part 'gps_services_client.g.dart';

@RestApi()
abstract class GpsServicesClient {
  factory GpsServicesClient(Dio dio, {String? baseUrl}) = _GpsServicesClient;

  /// Get available GPS services.
  ///
  /// Get list of GPS service types configured for this domain.
  @GET('/api/gps/available')
  Future<List<GpsServiceType>> getAvailableServices();

  /// OAuth callback.
  ///
  /// Handles OAuth callback from GPS service and redirects to frontend.
  ///
  /// [serviceType] - GPS service type.
  @GET('/api/gps/callback/{serviceType}')
  Future<void> handleCallback({
    @Path('serviceType') required GpsServiceType serviceType,
    @Query('code') String? code,
    @Query('error') String? error,
    @Query('state') String? state,
  });

  /// Get OAuth authorization URL.
  ///
  /// Get the OAuth authorization URL to connect a GPS service.
  ///
  /// [serviceType] - GPS service type.
  @GET('/api/gps/connect/{serviceType}')
  Future<GpsOAuthUrlResponse> getConnectUrl({
    @Path('serviceType') required GpsServiceType serviceType,
  });

  /// Disconnect GPS service.
  ///
  /// Disconnect a connected GPS service.
  ///
  /// [serviceType] - GPS service type.
  @DELETE('/api/gps/disconnect/{serviceType}')
  Future<void> disconnect({
    @Path('serviceType') required GpsServiceType serviceType,
  });

  /// Upload route to GPS service.
  ///
  /// Upload a route to a connected GPS service.
  ///
  /// [routeSlug] - Route URL slug.
  ///
  /// [serviceType] - GPS service type.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/gps/upload/{serviceType}/{teamSlug}/{routeSlug}')
  Future<RouteUploadResponse> uploadRoute({
    @Path('routeSlug') required String routeSlug,
    @Path('serviceType') required GpsServiceType serviceType,
    @Path('teamSlug') required String teamSlug,
  });
}
