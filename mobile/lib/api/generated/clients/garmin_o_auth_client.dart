// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/callback_response.dart';
import '../models/garmin_callback_request.dart';
import '../models/garmin_token_request.dart';
import '../models/garmin_token_response.dart';

part 'garmin_o_auth_client.g.dart';

@RestApi()
abstract class GarminOAuthClient {
  factory GarminOAuthClient(Dio dio, {String? baseUrl}) = _GarminOAuthClient;

  /// Start OAuth authorization.
  ///
  /// Redirects to frontend login page for Garmin OAuth flow.
  ///
  /// [clientId] - OAuth client ID (must be 'garmin-connect-iq').
  ///
  /// [redirectUri] - Redirect URI (connectiq://oauth).
  ///
  /// [responseType] - Response type (must be 'code').
  ///
  /// [state] - State parameter for CSRF protection.
  @GET('/api/garmin/oauth/authorize')
  Future<void> authorize({
    @Query('client_id') String? clientId,
    @Query('redirect_uri') String? redirectUri,
    @Query('response_type') String? responseType,
    @Query('state') String? state,
  });

  /// Complete OAuth flow from frontend.
  ///
  /// Receives auth from frontend and returns redirect URL with auth code.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/garmin/oauth/complete')
  Future<CallbackResponse> complete({
    @Body() required GarminCallbackRequest body,
  });

  /// Exchange code for tokens.
  ///
  /// Exchange authorization code or refresh token for access tokens.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/garmin/oauth/token')
  Future<GarminTokenResponse> token({
    @Body() required GarminTokenRequest body,
  });
}
