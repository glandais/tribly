// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/social_backfill_response.dart';

part 'admin_social_client.g.dart';

@RestApi()
abstract class AdminSocialClient {
  factory AdminSocialClient(Dio dio, {String? baseUrl}) = _AdminSocialClient;

  /// Backfill Strava identities.
  ///
  /// Create social-identity rows for migrated placeholder Strava accounts so they can log in with Strava. Idempotent.
  ///
  /// [domainId] - Target domain ID (defaults to the request's domain).
  @POST('/api/admin/social/backfill-strava')
  Future<SocialBackfillResponse> backfillStravaIdentities({
    @Query('domainId') String? domainId,
  });
}
