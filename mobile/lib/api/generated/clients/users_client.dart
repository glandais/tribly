// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'dart:convert';
import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/public_user_dto.dart';
import '../models/update_user_request.dart';
import '../models/user_dto.dart';

part 'users_client.g.dart';

@RestApi()
abstract class UsersClient {
  factory UsersClient(Dio dio, {String? baseUrl}) = _UsersClient;

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
