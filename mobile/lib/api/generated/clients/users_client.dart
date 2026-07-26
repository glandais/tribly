// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'dart:convert';
import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/list_view_mode.dart';
import '../models/public_user_dto.dart';
import '../models/publication_list_response.dart';
import '../models/status.dart';
import '../models/update_user_request.dart';
import '../models/user_dto.dart';
import '../models/user_export_dto.dart';
import '../models/user_preferences_request.dart';

part 'users_client.g.dart';

@RestApi()
abstract class UsersClient {
  factory UsersClient(Dio dio, {String? baseUrl}) = _UsersClient;

  /// Download a personal data export.
  ///
  /// Download a prepared data export archive using the token from the notification email.
  ///
  /// [token] - Download token from the notification email.
  @GET('/api/export/download/{token}')
  Future<void> downloadDataExport({
    @Path('token') required String token,
  });

  /// Update current user.
  ///
  /// Update the current user's profile.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/users/me')
  Future<UserDto> updateMe({
    @Body() required UpdateUserRequest body,
  });

  /// Get current user.
  ///
  /// Get the current authenticated user's profile.
  @GET('/api/users/me')
  Future<UserDto> getMe();

  /// Delete current user.
  ///
  /// Delete the current user's account.
  @DELETE('/api/users/me')
  Future<void> deleteCurrentUser();

  /// Upload user avatar.
  ///
  /// Upload a new avatar image for the current user. Image will be resized to 256x256.
  ///
  /// [file] - Name not received - field will be skipped.
  @MultiPart()
  @POST('/api/users/me/avatar')
  Future<UserDto> uploadAvatar({
    @Part(name: 'file') MultipartFile? file,
  });

  /// Delete user avatar.
  ///
  /// Remove the current user's avatar.
  @DELETE('/api/users/me/avatar')
  Future<UserDto> deleteAvatar();

  /// Request a personal data export.
  ///
  /// Queue a GDPR export of the current user's data. The archive is built in the background and a download link is emailed when it is ready. Limited to one export per hour.
  @POST('/api/users/me/export')
  Future<UserExportDto> requestExport();

  /// Get the latest data export.
  ///
  /// Status of the current user's most recent export request, if any.
  @GET('/api/users/me/export')
  Future<UserExportDto> getLatestExport();

  /// Get a data export.
  ///
  /// Status of one of the current user's export requests.
  ///
  /// [exportId] - Export job identifier.
  @GET('/api/users/me/export/{exportId}')
  Future<UserExportDto> getExport({
    @Path('exportId') required String exportId,
  });

  /// List my participations.
  ///
  /// The rides and trips the current user is registered to, soonest first. Only publications the user may still see are returned: leaving a team removes its outings from this list.
  ///
  /// [from] - Start date filter (ISO format).
  ///
  /// [page] - Page number.
  ///
  /// [size] - Page size.
  ///
  /// [status] - Only publications with this status.
  ///
  /// [to] - End date filter (ISO format).
  ///
  /// [view] - How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets empty — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. Omitted, or FULL, is the previous behaviour, byte for byte.
  @GET('/api/users/me/participations')
  Future<PublicationListResponse> listMyParticipations({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('from') String? from,
    @Query('status') Status? status,
    @Query('to') String? to,
    @Query('view') ListViewMode? view,
  });

  /// Update display preferences.
  ///
  /// Set the current user's unit system, colour scheme and language. A partial update: fields omitted (or sent null) are left unchanged. These live on the server so that a member who picks imperial units on their phone sees them on the web too, and so that reinstalling the app does not lose them.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/users/me/preferences')
  Future<UserDto> updateMyPreferences({
    @Body() required UserPreferencesRequest body,
  });

  /// Search users.
  ///
  /// Search users by display name.
  ///
  /// [limit] - Maximum results (max 20).
  ///
  /// [q] - Search query.
  @GET('/api/users/search')
  Future<List<PublicUserDto>> searchUsers({
    @Query('q') String? q,
    @Query('limit') int? limit = 10,
  });
}
