// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/ride_template_dto.dart';
import '../models/ride_template_list_response.dart';
import '../models/ride_template_request.dart';

part 'ride_templates_client.g.dart';

@RestApi()
abstract class RideTemplatesClient {
  factory RideTemplatesClient(Dio dio, {String? baseUrl}) =
      _RideTemplatesClient;

  /// List ride templates.
  ///
  /// Get paginated list of ride templates for a team.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [page] - Page number.
  ///
  /// [search] - Search by name.
  ///
  /// [size] - Page size.
  @GET('/api/teams/{teamSlug}/ride-templates')
  Future<RideTemplateListResponse> listTemplates({
    @Path('teamSlug') required String teamSlug,
    @Query('search') String? search,
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
  });

  /// Create ride template.
  ///
  /// Create a new ride template. Requires organizer permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/ride-templates')
  Future<RideTemplateDto> createTemplate({
    @Path('teamSlug') required String teamSlug,
    @Body() required RideTemplateRequest body,
  });

  /// Update ride template.
  ///
  /// Update ride template information. Requires organizer permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [templateSlug] - Template URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/ride-templates/{templateSlug}')
  Future<RideTemplateDto> updateTemplate({
    @Path('teamSlug') required String teamSlug,
    @Path('templateSlug') required String templateSlug,
    @Body() required RideTemplateRequest body,
  });

  /// Get ride template.
  ///
  /// Get detailed ride template information.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [templateSlug] - Template URL slug.
  @GET('/api/teams/{teamSlug}/ride-templates/{templateSlug}')
  Future<RideTemplateDto> getTemplate({
    @Path('teamSlug') required String teamSlug,
    @Path('templateSlug') required String templateSlug,
  });

  /// Delete ride template.
  ///
  /// Soft delete a ride template. Requires organizer permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [templateSlug] - Template URL slug.
  @DELETE('/api/teams/{teamSlug}/ride-templates/{templateSlug}')
  Future<void> deleteTemplate({
    @Path('teamSlug') required String teamSlug,
    @Path('templateSlug') required String templateSlug,
  });
}
