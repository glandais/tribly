// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/auth_response.dart';
import '../models/strava_auth_url_response.dart';
import '../models/strava_session_request.dart';

part 'strava_authentication_client.g.dart';

@RestApi()
abstract class StravaAuthenticationClient {
  factory StravaAuthenticationClient(Dio dio, {String? baseUrl}) =
      _StravaAuthenticationClient;

  /// Strava OAuth callback.
  ///
  /// Handles the Strava OAuth redirect and hands off to the frontend.
  @GET('/api/auth/strava/callback')
  Future<void> stravaCallback({
    @Query('code') String? code,
    @Query('error') String? error,
    @Query('state') String? state,
  });

  /// Get Strava link URL.
  ///
  /// Get the Strava OAuth authorization URL to link Strava to the current account.
  @GET('/api/auth/strava/connect')
  Future<StravaAuthUrlResponse> getStravaConnectUrl();

  /// Unlink Strava.
  ///
  /// Remove the Strava identity from the account.
  @DELETE('/api/auth/strava/identity')
  Future<void> unlinkStrava();

  /// Get Strava login URL.
  ///
  /// Get the Strava OAuth authorization URL to log in / recover an account.
  @GET('/api/auth/strava/login-url')
  Future<StravaAuthUrlResponse> getStravaLoginUrl();

  /// Exchange Strava login code for a session.
  ///
  /// Exchange the one-time code from the Strava callback for an authenticated session.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/strava/session')
  Future<AuthResponse> createStravaSession({
    @Body() required StravaSessionRequest body,
    @Header('X-Forwarded-For') String? xForwardedFor,
    @Header('X-Real-IP') String? xRealIp,
  });
}
