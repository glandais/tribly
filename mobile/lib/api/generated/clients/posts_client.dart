// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/post_dto.dart';
import '../models/post_request.dart';
import '../models/slug_change_request.dart';

part 'posts_client.g.dart';

@RestApi()
abstract class PostsClient {
  factory PostsClient(Dio dio, {String? baseUrl}) = _PostsClient;

  /// Create post.
  ///
  /// Create a new post with optional groups.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/posts')
  Future<PostDto> createPost({
    @Path('teamSlug') required String teamSlug,
    @Body() required PostRequest body,
  });

  /// Update post.
  ///
  /// Update post information. Requires organizer permissions.
  ///
  /// [postSlug] - Post URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/posts/{postSlug}')
  Future<PostDto> updatePost({
    @Path('postSlug') required String postSlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required PostRequest body,
  });

  /// Get post details.
  ///
  /// Get detailed post information including groups.
  ///
  /// [postSlug] - Post URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/posts/{postSlug}')
  Future<PostDto> getPost({
    @Path('postSlug') required String postSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Delete post.
  ///
  /// Soft delete a post. Requires organizer permissions.
  ///
  /// [postSlug] - Post URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/posts/{postSlug}')
  Future<void> deletePost({
    @Path('postSlug') required String postSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Change post slug.
  ///
  /// Change post URL slug. Requires organizer permissions.
  ///
  /// [postSlug] - Current post URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/posts/{postSlug}/slug')
  Future<PostDto> changePostSlug({
    @Path('postSlug') required String postSlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required SlugChangeRequest body,
  });
}
