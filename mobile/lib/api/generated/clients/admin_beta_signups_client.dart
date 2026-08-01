// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/beta_signup_list_response.dart';

part 'admin_beta_signups_client.g.dart';

@RestApi()
abstract class AdminBetaSignupsClient {
  factory AdminBetaSignupsClient(Dio dio, {String? baseUrl}) =
      _AdminBetaSignupsClient;

  /// List beta sign-ups.
  ///
  /// Get a paginated list of everyone who signed up to be notified about a beta.
  ///
  /// [page] - Page number (0-indexed).
  ///
  /// [size] - Page size.
  @GET('/api/admin/beta-signups')
  Future<BetaSignupListResponse> listBetaSignups({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
  });
}
