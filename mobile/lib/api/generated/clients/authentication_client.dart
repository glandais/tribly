// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/auth_response.dart';
import '../models/email_change_request.dart';
import '../models/forgot_password_request.dart';
import '../models/login_request.dart';
import '../models/message_response.dart';
import '../models/otp_request.dart';
import '../models/register_request.dart';
import '../models/reset_password_request.dart';
import '../models/verify_otp_request.dart';
import '../models/verify_token_request.dart';

part 'authentication_client.g.dart';

@RestApi()
abstract class AuthenticationClient {
  factory AuthenticationClient(Dio dio, {String? baseUrl}) =
      _AuthenticationClient;

  /// Request email change.
  ///
  /// Set/change the account's real email (e.g. recover a migrated Strava account). Sends a verification link to the new address.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/email/change-request')
  Future<MessageResponse> requestEmailChange({
    @Body() required EmailChangeRequest body,
  });

  /// Request password reset.
  ///
  /// Send a 6-digit code to the user's email to reset their password.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/forgot-password')
  Future<MessageResponse> forgotPassword({
    @Body() required ForgotPasswordRequest body,
  });

  /// Login with password.
  ///
  /// Authenticate using email and password.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/login')
  Future<AuthResponse> loginWithPassword({
    @Body() required LoginRequest body,
    @Header('X-Forwarded-For') String? xForwardedFor,
    @Header('X-Real-IP') String? xRealIp,
  });

  /// Logout.
  ///
  /// Logout and invalidate the refresh token.
  @POST('/api/auth/logout')
  Future<void> logout({
    @Header('X-Refresh-Token') String? xRefreshToken,
  });

  /// Logout all sessions.
  ///
  /// Logout from all devices by invalidating all refresh tokens.
  @POST('/api/auth/logout-all')
  Future<void> logoutAll();

  /// Request OTP.
  ///
  /// Send a 6-digit OTP code to the user's email for passwordless login.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/otp')
  Future<MessageResponse> requestOtp({
    @Body() required OtpRequest body,
  });

  /// Verify OTP.
  ///
  /// Verify OTP code and authenticate.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/otp/verify')
  Future<AuthResponse> verifyOtp({
    @Body() required VerifyOtpRequest body,
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
    @Header('X-Refresh-Token') String? xRefreshToken,
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

  /// Reset password.
  ///
  /// Verify the reset token and set a new password.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/auth/reset-password')
  Future<AuthResponse> resetPassword({
    @Body() required ResetPasswordRequest body,
    @Header('X-Forwarded-For') String? xForwardedFor,
    @Header('X-Real-IP') String? xRealIp,
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
