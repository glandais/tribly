// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/beta_signup_request.dart';

part 'beta_signups_client.g.dart';

@RestApi()
abstract class BetaSignupsClient {
  factory BetaSignupsClient(Dio dio, {String? baseUrl}) = _BetaSignupsClient;

  /// Sign up for a beta program.
  ///
  /// Record interest in being added to a beta program (TestFlight, Play Console, Garmin Connect IQ) once one opens. Submitting the same email again has no effect.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/beta-signups')
  Future<void> signUpForBeta({
    @Body() required BetaSignupRequest body,
  });
}
