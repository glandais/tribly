// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'dart:convert';
import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/gps_service_type.dart';
import '../models/gpx_preview_dto.dart';
import '../models/gpx_preview_update_request.dart';
import '../models/route_dto.dart';
import '../models/route_request.dart';
import '../models/route_upload_response.dart';

part 'gpx_previews_client.g.dart';

@RestApi()
abstract class GpxPreviewsClient {
  factory GpxPreviewsClient(Dio dio, {String? baseUrl}) = _GpxPreviewsClient;

  /// Analyse a GPX file.
  ///
  /// Uploads a GPX file, runs the elevation and climb pipeline, and stores the result under an unguessable identifier for 30 days.
  ///
  /// [gpxFile] - Name not received - field will be skipped.
  @MultiPart()
  @POST('/api/gpx-previews')
  Future<GpxPreviewDto> createPreview({
    @Part(name: 'gpxFile') MultipartFile? gpxFile,
  });

  /// Update an analysed GPX file.
  ///
  /// Rename the preview and, when a new GPX file or planner points are provided, replay the pipeline to replace its track. Only the creator may edit.
  ///
  /// [previewId] - Public preview identifier.
  ///
  /// [preview] - Name not received - field will be skipped.
  ///
  /// [gpxFile] - Name not received - field will be skipped.
  @MultiPart()
  @PUT('/api/gpx-previews/{previewId}')
  Future<GpxPreviewDto> updatePreview({
    @Path('previewId') required String previewId,
    @Part(name: 'preview') GpxPreviewUpdateRequest? preview,
    @Part(name: 'gpxFile') MultipartFile? gpxFile,
  });

  /// Get an analysed GPX file.
  ///
  /// Anyone holding the link may read the preview: the unguessable identifier is what grants access. Creating and deleting require an account.
  ///
  /// [previewId] - Public preview identifier.
  @GET('/api/gpx-previews/{previewId}')
  Future<GpxPreviewDto> getPreview({
    @Path('previewId') required String previewId,
  });

  /// Delete an analysed GPX file.
  ///
  /// Only the creator may delete.
  ///
  /// [previewId] - Public preview identifier.
  @DELETE('/api/gpx-previews/{previewId}')
  Future<void> deletePreview({
    @Path('previewId') required String previewId,
  });

  /// Send an analysed GPX file to a GPS service.
  ///
  /// Uploads the preview to the current user's connected Garmin or Hammerhead.
  ///
  /// [previewId] - Public preview identifier.
  ///
  /// [serviceType] - GPS service type.
  @POST('/api/gpx-previews/{previewId}/gps/{serviceType}')
  Future<RouteUploadResponse> uploadToGpsService({
    @Path('previewId') required String previewId,
    @Path('serviceType') required GpsServiceType serviceType,
  });

  /// Save an analysed GPX file as a route.
  ///
  /// Creates a team route from the preview's original upload.
  ///
  /// [previewId] - Public preview identifier.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/gpx-previews/{previewId}/routes/{teamSlug}')
  Future<RouteDto> createRouteFromPreview({
    @Path('previewId') required String previewId,
    @Path('teamSlug') required String teamSlug,
    @Body() required RouteRequest body,
  });
}
