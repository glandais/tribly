// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/auth_response.dart';
import '../models/magic_link_request.dart';
import '../models/message_response.dart';
import '../models/register_request.dart';
import '../models/verify_token_request.dart';

part 'authentication_client.g.dart';

@RestApi()
abstract class AuthenticationClient {
  factory AuthenticationClient(Dio dio, {String? baseUrl}) =
      _AuthenticationClient;

  /// Logout.
  ///
  /// Logout and invalidate the refresh token.
  @POST('/api/auth/logout')
  Future<void> logout();

  /// Logout all sessions.
  ///
  /// Logout from all devices by invalidating all refresh tokens.
  @POST('/api/auth/logout-all')
  Future<void> logoutAll();

  /// Request magic link.
  ///
  /// Send a magic link to the user's email for passwordless login.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/magic-link')
  Future<MessageResponse> requestMagicLink({
    @Body() required MagicLinkRequest body,
  });

  /// Verify magic link.
  ///
  /// Verify magic link token and authenticate.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/magic-link/verify')
  Future<AuthResponse> verifyMagicLink({
    @Body() required VerifyTokenRequest body,
    @Header('X-Forwarded-For') String? xForwardedFor,
    @Header('X-Real-IP') String? xRealIp,
  });

  /// Refresh access token.
  ///
  /// Get a new access token using the refresh token cookie.
  @POST('/api/auth/refresh')
  Future<AuthResponse> refresh({
    @Header('X-Forwarded-For') String? xForwardedFor,
    @Header('X-Real-IP') String? xRealIp,
  });

  /// Register new user.
  ///
  /// Register a new user. A verification email will be sent.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/register')
  Future<MessageResponse> register({
    @Body() required RegisterRequest body,
  });

  /// Verify email.
  ///
  /// Verify email address and complete registration.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/verify-email')
  Future<AuthResponse> verifyEmail({
    @Body() required VerifyTokenRequest body,
    @Header('X-Forwarded-For') String? xForwardedFor,
    @Header('X-Real-IP') String? xRealIp,
  });
}
